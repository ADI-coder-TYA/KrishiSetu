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
import java.time.OffsetDateTime
import java.util.UUID
import javax.inject.Inject
import kotlin.coroutines.resume

@HiltViewModel
class CheckoutViewModel @Inject constructor() : ViewModel() {

    var isPlacingOrder by mutableStateOf(false)

    // Temporary state to hold order details during Razorpay flow
    private var pendingCartItems: List<Pair<CartItem, Int>>? = null
    private var pendingAddress: String? = null
    private var pendingPincode: String? = null
    private var pendingPhone: String? = null

    // -------------------------
    // FLOW 1: Cash on Delivery
    // -------------------------
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

    // -------------------------
    // FLOW 2: Online Payment
    // -------------------------

    // Step 1: Called BEFORE Razorpay opens
    fun prepareOnlineOrder(
        finalOrder: List<Pair<CartItem, Int>>,
        deliveryAddress: String,
        deliveryPincode: String,
        deliveryPhone: String
    ) {
        pendingCartItems = finalOrder
        pendingAddress = deliveryAddress
        pendingPincode = deliveryPincode
        pendingPhone = deliveryPhone
    }

    // Step 2: Called AFTER Razorpay returns success
    fun confirmOnlineOrder(razorpayPaymentID: String, onSuccess: () -> Unit, onFail: () -> Unit) {
        viewModelScope.launch {
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

                // Clear temp state
                pendingCartItems = null
                pendingAddress = null

                isPlacingOrder = false
                if (success) onSuccess() else onFail()
            } else {
                isPlacingOrder = false
                onFail()
            }
        }
    }

    // -------------------------
    // Shared Logic
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
            // Process all items in parallel
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
            // Wait for all to finish and check if all were successful
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
            // NEW FIELDS (Ensure these exist in your Schema)
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
                    Log.e("CheckoutVM", "Error: ${response.errors}")
                    continuation.resume(false)
                }
            },
            { error ->
                Log.e("CheckoutVM", "API Fail: ${error.message}")
                continuation.resume(false)
            }
        )
    }
}
