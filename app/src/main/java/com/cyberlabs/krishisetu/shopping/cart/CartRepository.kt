package com.cyberlabs.krishisetu.shopping.cart

import com.amplifyframework.datastore.generated.model.Cart
import com.amplifyframework.datastore.generated.model.CartItem
import com.amplifyframework.datastore.generated.model.Crop

interface CartRepository {
    suspend fun getCart(userId: String): Cart
    suspend fun addItemToCart(cart: Cart, item: Crop, quantity: Int): Boolean
    suspend fun removeItemFromCart(item: CartItem): Boolean
    suspend fun increaseQuantity(cartItem: CartItem, quantityToAdd: Int): Boolean
    suspend fun decreaseQuantity(cartItem: CartItem, quantityToDecrease: Int): Boolean
}
