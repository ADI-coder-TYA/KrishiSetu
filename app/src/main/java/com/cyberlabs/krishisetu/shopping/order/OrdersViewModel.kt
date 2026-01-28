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
import com.amplifyframework.datastore.generated.model.Order
import com.amplifyframework.datastore.generated.model.OrderStatus
import com.cyberlabs.krishisetu.authentication.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@HiltViewModel
class OrdersViewModel @Inject constructor(
    private val authRepository: AuthRepository,
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
                // FIXED: Using safeListQuery
                val items = safeListQuery(
                    ModelQuery.list(Order::class.java, Order.BUYER.eq(currentUserId))
                )
                // Sorting by newest first (descending) is usually better for order history
                _orders.value = items.sortedByDescending { it.createdAt }
            } catch (e: Exception) {
                Log.e("OrdersViewModel", "Failed to fetch orders", e)
            }
        }
    }

    fun cancelOrder(order: Order) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // 1. Prepare Update
                val updatedOrder = order.copyOfBuilder()
                    .orderStatus(OrderStatus.CANCELLED)
                    .build()

                // 2. Perform Mutation
                safeMutate(ModelMutation.update(updatedOrder))
                Log.i("OrdersViewModel", "Order cancelled successfully")

                // 3. Refresh List
                fetchOrders(authRepository.getCurrUserID())

            } catch (e: Exception) {
                Log.e("OrdersViewModel", "Failed to cancel order", e)
            }
        }
    }

    // --- FIXED COROUTINE WRAPPERS (Same as FarmerViewModel) ---

    /**
     * Helper to safely execute a mutation (Create/Update/Delete).
     * @param request The GraphQL Request (e.g., ModelMutation.create(item))
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
     * Helper to safely execute a list query.
     * @param request The GraphQL Request (e.g., ModelQuery.list(Item::class.java))
     * @return A list of non-null items.
     */
    private suspend fun <T : Model> safeListQuery(request: GraphQLRequest<PaginatedResult<T>>): List<T> {
        return suspendCancellableCoroutine { continuation ->
            Amplify.API.query(
                request,
                { response ->
                    if (response.hasData()) {
                        // Extract items and filter nulls safely
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
