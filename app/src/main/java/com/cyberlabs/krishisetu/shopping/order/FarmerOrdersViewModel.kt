package com.cyberlabs.krishisetu.shopping.order

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amplifyframework.api.graphql.model.ModelMutation
import com.amplifyframework.api.graphql.model.ModelQuery
import com.amplifyframework.core.Amplify
import com.amplifyframework.core.model.temporal.Temporal
import com.amplifyframework.datastore.generated.model.Delivery
import com.amplifyframework.datastore.generated.model.DeliveryStatus
import com.amplifyframework.datastore.generated.model.Order
import com.amplifyframework.datastore.generated.model.OrderStatus
import com.amplifyframework.datastore.generated.model.Purchase
import com.amplifyframework.datastore.generated.model.User
import com.amplifyframework.datastore.generated.model.UserRole
import com.cyberlabs.krishisetu.authentication.AuthRepository
import com.cyberlabs.krishisetu.states.PersonalDeliveryAgentDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.OffsetDateTime
import java.util.UUID
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@HiltViewModel
class FarmerOrdersViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders

    init {
        viewModelScope.launch {
            val currentUserId = authRepository.getCurrUserID()
            fetchOrders(currentUserId)
        }
    }

    private fun fetchOrders(currentUserId: String) {
        if (currentUserId == "null") return
        Amplify.API.query(
            ModelQuery.list(Order::class.java, Order.FARMER.eq(currentUserId)),
            { response ->
                _orders.value = response.data.items.filterNotNull()
            },
            { Log.e("FarmerOrdersViewModel", "Failed to fetch orders", it) }
        )
    }

    fun acceptOrder(
        order: Order,
        deliveryAgentEmail: String?,
        isPersonal: Boolean,
        personalDetails: PersonalDeliveryAgentDetails?
    ) {
        viewModelScope.launch {
            // Implement logic to accept the order
            val updatedOrder = order.copyOfBuilder()
                .orderStatus(OrderStatus.ACCEPTED)
                .build()
            val newPurchase = Purchase.builder()
                .quantity(order.quantity)
                .totalAmount(order.bargainedPrice.toDouble())
                .createdAt(Temporal.DateTime(OffsetDateTime.now().toString()))
                .updatedAt(Temporal.DateTime(OffsetDateTime.now().toString()))
                .farmer(order.farmer)
                .id(UUID.randomUUID().toString())
                .buyer(order.buyer)
                .crop(order.crop)
                .build()
            val newDelivery = if (isPersonal) {
                Delivery.builder()
                    .deliveryAddress(order.deliveryAddress)
                    .deliveryStatus(DeliveryStatus.PENDING)
                    .createdAt(Temporal.DateTime(OffsetDateTime.now().toString()))
                    .updatedAt(Temporal.DateTime(OffsetDateTime.now().toString()))
                    .personalAgentName(personalDetails!!.name)
                    .personalAgentPhone(personalDetails.phone)
                    .personalAgentEmail(personalDetails.email)
                    .id(UUID.randomUUID().toString())
                    .farmer(newPurchase.farmer)
                    .purchase(newPurchase)
                    .build()
            } else {
                val agent = queryAgentByEmail(deliveryAgentEmail!!)
                if (agent != null) {
                    Delivery.builder()
                        .deliveryAddress(order.deliveryAddress)
                        .deliveryStatus(DeliveryStatus.PENDING)
                        .createdAt(Temporal.DateTime(OffsetDateTime.now().toString()))
                        .updatedAt(Temporal.DateTime(OffsetDateTime.now().toString()))
                        .agent(agent)
                        .farmer(order.farmer)
                        .purchase(newPurchase)
                        .id(UUID.randomUUID().toString())
                        .build()
                } else null
            }

            if (newDelivery != null) {

                Amplify.API.mutate(
                    ModelMutation.update(updatedOrder),
                    { response ->
                        if (response.hasData()) {
                            Log.i(
                                "FarmerOrdersViewModel",
                                "Order accepted successfully, response: ${response.data}"
                            )
                            Amplify.API.mutate(
                                ModelMutation.create(newDelivery),
                                { response ->
                                    if (response.hasData()) {
                                        Log.i(
                                            "FarmerOrdersViewModel",
                                            "Delivery created successfully, response: ${response.data}"
                                        )
                                        Amplify.API.mutate(
                                            ModelMutation.create(newPurchase),
                                            { response ->
                                                if (response.hasData()) {
                                                    Log.i(
                                                        "FarmerOrdersViewModel",
                                                        "Purchase created successfully, response: ${response.data}"
                                                    )
                                                } else if (response.hasErrors()) {
                                                    Log.e(
                                                        "FarmerOrdersViewModel",
                                                        "Failed to create purchase, errors: ${response.errors}"
                                                    )
                                                }
                                            }, { apiError ->
                                                Log.e(
                                                    "FarmerOrdersViewModel",
                                                    "API error: ${apiError.message}"
                                                )
                                            }
                                        )
                                    } else if (response.hasErrors()) {
                                        Log.e(
                                            "FarmerOrdersViewModel",
                                            "Failed to create delivery, errors: ${response.errors}"
                                        )
                                    }
                                }, { apiError ->
                                    Log.e("FarmerOrdersViewModel", "API error: ${apiError.message}")
                                }
                            )
                        } else if (response.hasErrors()) {
                            Log.e(
                                "FarmerOrdersViewModel",
                                "Failed to accept order, errors: ${response.errors}"
                            )
                        }
                    }, { apiError ->
                        Log.e("FarmerOrdersViewModel", "API error: ${apiError.message}")
                    }
                )
            }
            fetchOrders(authRepository.getCurrUserID())
        }
    }

    fun rejectOrder(order: Order) {
        viewModelScope.launch {
            // Implement logic to reject the order
            val updatedOrder = order.copyOfBuilder()
                .orderStatus(OrderStatus.REJECTED)
                .build()

            Amplify.API.mutate(
                ModelMutation.update(updatedOrder),
                { response ->
                    if (response.hasData()) {
                        Log.i(
                            "OrdersViewModel",
                            "Order rejected successfully, response: ${response.data}"
                        )
                    } else if (response.hasErrors()) {
                        Log.e(
                            "OrdersViewModel",
                            "Failed to reject order, errors: ${response.errors}"
                        )
                    }
                }, { apiError ->
                    Log.e("OrdersViewModel", "API error: ${apiError.message}")
                }
            )
            fetchOrders(authRepository.getCurrUserID())
        }
    }

    private suspend fun queryAgentByEmail(email: String): User? = suspendCoroutine { continuation ->
        Amplify.API.query(
            ModelQuery.list(User::class.java, User.EMAIL.eq(email)),
            { response ->
                if (response.hasData()) {
                    val agent = response.data.items.firstOrNull {
                        it.role == UserRole.DELIVERY_AGENT
                    }
                    if (agent != null) {
                        continuation.resume(agent)
                    } else {
                        Log.e("FarmerOrdersViewModel", "Agent not found for email: $email")
                        continuation.resume(null)
                    }
                } else {
                    Log.e(
                        "FarmerOrdersViewModel",
                        "Response error while fetching agent: ${response.errors}"
                    )
                    continuation.resume(null)
                }
            }, { apiError ->
                Log.e(
                    "FarmerOrdersViewModel",
                    "API error while fetching agent: ${apiError.message}"
                )
                continuation.resume(null)
            }
        )
    }

}
