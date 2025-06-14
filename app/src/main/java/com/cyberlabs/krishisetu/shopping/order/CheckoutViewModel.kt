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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.OffsetDateTime
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CheckoutViewModel @Inject constructor() : ViewModel() {

    var buyerName by mutableStateOf("")
    var phoneNumber by mutableStateOf("")
    var address by mutableStateOf("")

    var paymentMethod by mutableStateOf("COD") // Options: "COD", "UPI"

    var isPlacingOrder by mutableStateOf(false)
    var orderPlacedSuccessfully by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    fun placeOrder(
        finalOrder: List<Pair<CartItem, Int>>,
        deliveryAddress: String,
        deliveryPincode: String,
        deliveryPhone: String
    ) {
        viewModelScope.launch {
            try {
                isPlacingOrder = true
                errorMessage = null
                finalOrder.forEach { pair ->
                    val item = pair.first
                    val bargainedPrice = pair.second
                    val currentTime = Temporal.DateTime(OffsetDateTime.now().toString())
                    val expiresAt = Temporal.DateTime(OffsetDateTime.now().plusDays(1).toString())

                    val newOrder = Order.builder()
                        .quantity(item.quantity)
                        .bargainedPrice(bargainedPrice)
                        .realPrice(item.crop.price.toInt())
                        .deliveryAddress(deliveryAddress)
                        .deliveryPhone(deliveryPhone)
                        .deliveryPincode(deliveryPincode)
                        .orderStatus(OrderStatus.PENDING)
                        .createdAt(currentTime)
                        .updatedAt(currentTime)
                        .expiresAt(expiresAt)
                        .id(UUID.randomUUID().toString())
                        .crop(item.crop)
                        .farmer(item.crop.farmer)
                        .buyer(item.user)
                        .build()

                    Amplify.API.mutate(
                        ModelMutation.create(newOrder),
                        { response ->
                            if (response.hasData()) {
                                Log.i(
                                    "CheckoutViewModel",
                                    "Order placed successfully: ${response.data}"
                                )
                            } else if (response.hasErrors()) {
                                Log.e(
                                    "CheckoutViewModel",
                                    "Failed to place order: ${response.errors}"
                                )
                            }
                        }, { apiError ->
                            Log.e("CheckoutViewModel", "Failed to place order: ${apiError.message}")
                        }
                    )

                    orderPlacedSuccessfully = true
                }
            } catch (e: Exception) {
                errorMessage = "Failed to place order: ${e.message}"
            } finally {
                isPlacingOrder = false
                delay(1000)
                resetOrderState()
            }
        }
    }

    fun resetOrderState() {
        orderPlacedSuccessfully = false
        errorMessage = null
        isPlacingOrder = false
    }
}
