package com.cyberlabs.krishisetu.shopping.cart

import com.amplifyframework.datastore.generated.model.CartItem
import kotlinx.coroutines.flow.StateFlow

interface CartRepository {
    val cartItems: StateFlow<List<CartItem>>
    suspend fun fetchCartItems(buyerId: String)
    suspend fun addCartItem(item: CartItem)
    suspend fun deleteCartItem(item: CartItem)
    suspend fun clearCart(buyerId: String)
    suspend fun increaseQuantity(item: CartItem, delta: Int)
    suspend fun decreaseQuantity(item: CartItem, delta: Int)
}
