package com.cyberlabs.krishisetu.shopping.order

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amplifyframework.api.graphql.GraphQLRequest
import com.amplifyframework.api.graphql.PaginatedResult
import com.amplifyframework.api.graphql.model.ModelMutation
import com.amplifyframework.api.graphql.model.ModelQuery
import com.amplifyframework.core.Amplify
import com.amplifyframework.core.model.Model
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.time.OffsetDateTime
import java.util.UUID
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

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

        viewModelScope.launch(Dispatchers.IO) {
            try {
                // FIXED: Now calling safeListQuery correctly
                val items = safeListQuery(
                    ModelQuery.list(Order::class.java, Order.FARMER.eq(currentUserId))
                )
                _orders.value = items.sortedByDescending { it.createdAt }
            } catch (e: Exception) {
                Log.e("FarmerOrdersViewModel", "Failed to fetch orders", e)
            }
        }
    }

    fun acceptOrder(
        order: Order,
        deliveryAgentEmail: String?,
        isPersonal: Boolean,
        personalDetails: PersonalDeliveryAgentDetails?
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // 1. Check Stock
                if (order.crop.quantityAvailable - order.quantity < 0) {
                    Log.e("FarmerOrdersViewModel", "Not enough stock available")
                    return@launch
                }

                // 2. Update Order Status
                val updatedOrder = order.copyOfBuilder()
                    .orderStatus(OrderStatus.ACCEPTED)
                    .build()
                safeMutate(ModelMutation.update(updatedOrder))

                // 3. Create Purchase Record
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
                safeMutate(ModelMutation.create(newPurchase))

                // 4. Update Crop Quantity
                val updatedCrop = order.crop.copyOfBuilder()
                    .quantityAvailable(order.crop.quantityAvailable - order.quantity)
                    .build()
                safeMutate(ModelMutation.update(updatedCrop))

                // 5. Create Delivery
                val newDelivery = buildDeliveryObject(
                    order,
                    newPurchase,
                    isPersonal,
                    personalDetails,
                    deliveryAgentEmail
                )

                if (newDelivery != null) {
                    safeMutate(ModelMutation.create(newDelivery))
                    Log.i("FarmerOrdersViewModel", "Order Acceptance Flow Complete")
                } else {
                    Log.e("FarmerOrdersViewModel", "Failed to build Delivery object")
                }

                // 6. Refresh List
                fetchOrders(authRepository.getCurrUserID())

            } catch (e: Exception) {
                Log.e("FarmerOrdersViewModel", "Error in acceptOrder flow: ${e.message}")
            }
        }
    }

    fun rejectOrder(order: Order) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val updatedOrder = order.copyOfBuilder()
                    .orderStatus(OrderStatus.REJECTED)
                    .build()

                safeMutate(ModelMutation.update(updatedOrder))
                Log.i("FarmerOrdersViewModel", "Order rejected successfully")

                fetchOrders(authRepository.getCurrUserID())
            } catch (e: Exception) {
                Log.e("FarmerOrdersViewModel", "Error rejecting order", e)
            }
        }
    }

    // --- Private Helper Logic for Delivery Construction ---

    private suspend fun buildDeliveryObject(
        order: Order,
        purchase: Purchase,
        isPersonal: Boolean,
        personalDetails: PersonalDeliveryAgentDetails?,
        deliveryAgentEmail: String?
    ): Delivery? {
        val builder = Delivery.builder()
            .deliveryAddress(order.deliveryAddress)
            .deliveryStatus(DeliveryStatus.PENDING)
            .deliveryPhone(order.deliveryPhone)
            .deliveryPincode(order.deliveryPincode)
            .deliveryQuantity(order.quantity)
            .createdAt(Temporal.DateTime(OffsetDateTime.now().toString()))
            .updatedAt(Temporal.DateTime(OffsetDateTime.now().toString()))
            .id(UUID.randomUUID().toString())
            .farmer(purchase.farmer)
            .purchase(purchase)
            .buyer(purchase.buyer)

        return if (isPersonal && personalDetails != null) {
            builder
                .personalAgentName(personalDetails.name)
                .personalAgentPhone(personalDetails.phone)
                .personalAgentEmail(personalDetails.email)
                .agent(purchase.farmer)
                .build()
        } else if (!isPersonal && deliveryAgentEmail != null) {
            val agent = findAgentByEmail(deliveryAgentEmail)
            if (agent != null) {
                builder
                    .agent(agent)
                    .personalAgentName(agent.name)
                    .personalAgentEmail(agent.email)
                    .personalAgentPhone(agent.phone)
                    .build()
            } else {
                Log.e("FarmerOrdersViewModel", "Agent not found: $deliveryAgentEmail")
                null
            }
        } else {
            null
        }
    }

    private suspend fun findAgentByEmail(email: String): User? {
        return try {
            // FIXED: Using safeListQuery
            val users = safeListQuery(ModelQuery.list(User::class.java, User.EMAIL.eq(email)))
            users.firstOrNull { it.role == UserRole.DELIVERY_AGENT }
        } catch (e: Exception) {
            null
        }
    }

    // --- FIXED COROUTINE WRAPPERS ---

    /**
     * FIXED: Parameter type changed to GraphQLRequest<T>
     */
    private suspend fun <T : Model> safeMutate(request: GraphQLRequest<T>): T {
        return suspendCancellableCoroutine { continuation ->
            Amplify.API.mutate(
                request,
                { response ->
                    if (response.hasData()) {
                        continuation.resume(response.data)
                    } else if (response.hasErrors()) {
                        val errorMsg = response.errors.joinToString { it.message }
                        continuation.resumeWithException(Exception("GraphQL Error: $errorMsg"))
                    } else {
                        continuation.resumeWithException(Exception("Unknown GraphQL error"))
                    }
                },
                { apiException ->
                    continuation.resumeWithException(apiException)
                }
            )
        }
    }

    /**
     * FIXED: Parameter type changed to GraphQLRequest<PaginatedResult<T>>
     * AND return type handles the List extraction.
     */
    private suspend fun <T : Model> safeListQuery(request: GraphQLRequest<PaginatedResult<T>>): List<T> {
        return suspendCancellableCoroutine { continuation ->
            Amplify.API.query(
                request,
                { response ->
                    if (response.hasData()) {
                        // Extract items from PaginatedResult and filter nulls
                        val items = response.data.items.filterNotNull().toList()
                        continuation.resume(items)
                    } else if (response.hasErrors()) {
                        val errorMsg = response.errors.joinToString { it.message }
                        continuation.resumeWithException(Exception("GraphQL Query Error: $errorMsg"))
                    } else {
                        continuation.resumeWithException(Exception("Unknown Query error"))
                    }
                },
                { apiException ->
                    continuation.resumeWithException(apiException)
                }
            )
        }
    }
}
