package com.cyberlabs.krishisetu.shopping.order

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amplifyframework.api.graphql.model.ModelMutation
import com.amplifyframework.core.Amplify
import com.amplifyframework.core.model.temporal.Temporal
import com.amplifyframework.datastore.generated.model.CartItem
import com.amplifyframework.datastore.generated.model.Order
import com.amplifyframework.datastore.generated.model.OrderStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.time.OffsetDateTime
import java.util.UUID
import javax.inject.Inject
import kotlin.coroutines.resume

@HiltViewModel
class CheckoutViewModel @Inject constructor() : ViewModel() {

    var isPlacingOrder by mutableStateOf(false)

    // Temporary state to hold order details during Razorpay flow (between Screen and Activity)
    private var pendingCartItems: List<Pair<CartItem, Int>>? = null
    private var pendingAddress: String? = null
    private var pendingPincode: String? = null
    private var pendingPhone: String? = null

    private val LAMBDA_URL =
        "https://j8nz8blba1.execute-api.ap-south-1.amazonaws.com/default/krishisetu_createRazorpayOrder"

    // -------------------------
    // PHASE 1: Pre-Payment (Lambda Call)
    // -------------------------

    /**
     * Calls your AWS Lambda function to generate a secure Razorpay Order ID.
     * This is critical for security so the amount cannot be tampered with on the client.
     */
    suspend fun fetchRazorpayOrderId(amountInRupees: Int): String? {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL(LAMBDA_URL)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.doOutput = true
                connection.setRequestProperty("Content-Type", "application/json")

                // Send Amount (in paise) to Lambda
                val payload = JSONObject()
                payload.put("amount", amountInRupees * 100)

                connection.outputStream.use { os ->
                    os.write(payload.toString().toByteArray())
                }

                // Read Response
                val responseText = connection.inputStream.bufferedReader().use { it.readText() }
                val jsonResponse = JSONObject(responseText)

                // Return the 'id' from Razorpay (e.g., "order_EKyx...")
                if (jsonResponse.has("id")) {
                    jsonResponse.getString("id")
                } else {
                    Log.e("CheckoutVM", "Lambda Error: $responseText")
                    null
                }
            } catch (e: Exception) {
                Log.e("CheckoutVM", "Network Error fetching Order ID", e)
                null
            }
        }
    }

    // -------------------------
    // PHASE 2: Order Preparation
    // -------------------------

    // FLOW 1: Cash on Delivery (Direct)
    fun placeOrder(
        finalOrder: List<Pair<CartItem, Int>>,
        deliveryAddress: String,
        deliveryPincode: String,
        deliveryPhone: String,
        paymentMode: String,
        onSuccess: () -> Unit,
        onFail: () -> Unit
    ) {
        viewModelScope.launch {
            isPlacingOrder = true
            val success = processBatchOrders(
                finalOrder,
                deliveryAddress,
                deliveryPincode,
                deliveryPhone,
                paymentMode,
                paymentStatus = "PENDING",
                paymentId = null
            )
            isPlacingOrder = false
            if (success) onSuccess() else onFail()
        }
    }

    // FLOW 2: Online Payment - Step A (Prepare Data)
    fun prepareOnlineOrder(
        finalOrder: List<Pair<CartItem, Int>>,
        deliveryAddress: String,
        deliveryPincode: String,
        deliveryPhone: String
    ) {
        // Store data in ViewModel while user is on Razorpay screen
        pendingCartItems = finalOrder
        pendingAddress = deliveryAddress
        pendingPincode = deliveryPincode
        pendingPhone = deliveryPhone
        Log.d("CheckoutVM", "Online Order Prepared. Items: ${finalOrder.size}")
    }

    // FLOW 2: Online Payment - Step B (Confirm after Success)
    fun confirmOnlineOrder(razorpayPaymentID: String, onSuccess: () -> Unit, onFail: () -> Unit) {
        viewModelScope.launch {
            Log.d("CheckoutVM", "Confirming Order. Pending Items: $pendingCartItems")
            isPlacingOrder = true

            if (pendingCartItems != null) {
                val success = processBatchOrders(
                    pendingCartItems!!,
                    pendingAddress!!,
                    pendingPincode!!,
                    pendingPhone!!,
                    paymentMode = "ONLINE",
                    paymentStatus = "PAID",
                    paymentId = razorpayPaymentID
                )

                // Clear temp state to prevent duplicate orders
                pendingCartItems = null
                pendingAddress = null
                pendingPincode = null
                pendingPhone = null

                isPlacingOrder = false
                if (success) onSuccess() else onFail()
            } else {
                Log.e("CheckoutVM", "FAIL: pendingCartItems is NULL! ViewModel state was lost.")
                isPlacingOrder = false
                onFail()
            }
        }
    }

    // -------------------------
    // PHASE 3: Backend Creation (Shared Logic)
    // -------------------------

    private suspend fun processBatchOrders(
        items: List<Pair<CartItem, Int>>,
        address: String,
        pincode: String,
        phone: String,
        paymentMode: String,
        paymentStatus: String,
        paymentId: String?
    ): Boolean {
        return try {
            // Process all items in parallel using async
            val jobs = items.map { pair ->
                viewModelScope.async(Dispatchers.IO) {
                    createOrderInBackend(
                        pair,
                        address,
                        pincode,
                        phone,
                        paymentMode,
                        paymentStatus,
                        paymentId
                    )
                }
            }
            // Wait for all to finish and return true only if ALL succeeded
            jobs.awaitAll().all { it }
        } catch (e: Exception) {
            Log.e("CheckoutViewModel", "Batch processing error", e)
            false
        }
    }

    private suspend fun createOrderInBackend(
        pair: Pair<CartItem, Int>,
        address: String,
        pincode: String,
        phone: String,
        paymentMode: String,
        paymentStatus: String,
        paymentId: String?
    ): Boolean = suspendCancellableCoroutine { continuation ->

        val item = pair.first
        val bargainedPrice = pair.second
        val currentTime = Temporal.DateTime(OffsetDateTime.now().toString())
        val expiresAt = Temporal.DateTime(OffsetDateTime.now().plusDays(1).toString())

        val newOrder = Order.builder()
            .quantity(item.quantity)
            .bargainedPrice(bargainedPrice)
            .realPrice(item.crop.price.toInt())
            .deliveryAddress(address)
            .deliveryPhone(phone)
            .deliveryPincode(pincode)
            .orderStatus(OrderStatus.PENDING)
            .createdAt(currentTime)
            .updatedAt(currentTime)
            .expiresAt(expiresAt)
            .id(UUID.randomUUID().toString())
            .crop(item.crop)
            .farmer(item.crop.farmer)
            .buyer(item.user)
            // Fields for Payment Tracking
            .paymentMode(paymentMode)
            .paymentStatus(paymentStatus)
            .paymentId(paymentId)
            .build()

        Amplify.API.mutate(
            ModelMutation.create(newOrder),
            { response ->
                if (response.hasData()) {
                    continuation.resume(true)
                } else {
                    Log.e("CheckoutVM", "Amplify Error: ${response.errors}")
                    continuation.resume(false)
                }
            },
            { error ->
                Log.e("CheckoutVM", "Amplify API Fail: ${error.message}")
                continuation.resume(false)
            }
        )
    }
}
