package com.cyberlabs.krishisetu.shopping.order

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amplifyframework.api.graphql.model.ModelMutation
import com.amplifyframework.api.graphql.model.ModelQuery
import com.amplifyframework.core.Amplify
import com.amplifyframework.datastore.generated.model.Order
import com.amplifyframework.datastore.generated.model.OrderStatus
import com.cyberlabs.krishisetu.authentication.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

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
        Amplify.API.query(
            ModelQuery.list(Order::class.java, Order.BUYER.eq(currentUserId)),
            { response ->
                if (response.hasData()) {
                    _orders.value = response.data.items.filterNotNull()
                } else if (response.hasErrors()) {
                    Log.e("OrdersViewModel", "GraphQL errors: ${response.errors}")
                }
            },
            { Log.e("OrdersViewModel", "Failed to fetch orders", it) }
        )
    }

    fun cancelOrder(order: Order) {
        viewModelScope.launch {
            // Implement logic to reject the order
            val updatedOrder = order.copyOfBuilder()
                .orderStatus(OrderStatus.CANCELLED)
                .build()

            Amplify.API.mutate(
                ModelMutation.update(updatedOrder),
                { response ->
                    if (response.hasData()) {
                        Log.i(
                            "OrdersViewModel",
                            "Order cancelled successfully, response: ${response.data}"
                        )
                    } else if (response.hasErrors()) {
                        Log.e(
                            "OrdersViewModel",
                            "Failed to cancel order, errors: ${response.errors}"
                        )
                    }
                }, { apiError ->
                    Log.e("OrdersViewModel", "API error in cancellation: ${apiError.message}")
                }
            )
            fetchOrders(authRepository.getCurrUserID())
        }
    }

}
