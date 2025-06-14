package com.cyberlabs.krishisetu.shopping.cart

import android.util.Log
import com.amplifyframework.api.graphql.model.ModelMutation
import com.amplifyframework.api.graphql.model.ModelQuery
import com.amplifyframework.core.Amplify
import com.amplifyframework.core.model.temporal.Temporal
import com.amplifyframework.datastore.generated.model.CartItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.OffsetDateTime
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class CartRepositoryImpl @Inject constructor() : CartRepository {

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    override val cartItems: StateFlow<List<CartItem>> get() = _cartItems

    override suspend fun fetchCartItems(buyerId: String) {
        try {
            val request = ModelQuery.list(
                CartItem::class.java,
                CartItem.USER.eq(buyerId)
            )

            val items = suspendCoroutine<List<CartItem>> { continuation ->
                Amplify.API.query(
                    request,
                    { response ->
                        if (response.hasErrors()) {
                            Log.e("CartRepository", "Error fetching cart items: ${response.errors}")
                            continuation.resume(emptyList())
                        } else if (response.hasData()) {
                            val result = response.data.items.toList()
                            continuation.resume(result)
                        } else {
                            Log.e("CartRepository", "No data and no errors")
                            continuation.resume(emptyList())
                        }
                    },
                    { error ->
                        Log.e("CartRepository", "API error in fetchCartItems", error)
                        continuation.resume(emptyList())
                    }
                )
            }

            _cartItems.value = items
        } catch (e: Exception) {
            Log.e("CartRepository", "Exception fetching cart items", e)
        }
    }

    override suspend fun addCartItem(item: CartItem) {
        try {
            suspendCoroutine<Unit> { continuation ->
                val request = ModelMutation.create(item)
                Amplify.API.mutate(
                    request,
                    { response ->
                        if (response.hasErrors()) {
                            Log.e("CartRepository", "Error adding cart item: ${response.errors}")
                        } else if (response.hasData()) {
                            Log.i("CartRepository", "Item added: ${response.data}")
                        } else {
                            Log.e("CartRepository", "Add failed: No data, no errors")
                        }
                        continuation.resume(Unit)
                    },
                    { error ->
                        Log.e("CartRepository", "API error adding cart item", error)
                        continuation.resume(Unit)
                    }
                )
            }

            fetchCartItems(item.user.id)
        } catch (e: Exception) {
            Log.e("CartRepository", "Exception adding cart item", e)
        }
    }

    override suspend fun deleteCartItem(item: CartItem) {
        try {
            suspendCoroutine<Unit> { continuation ->
                val request = ModelMutation.delete(item)
                Amplify.API.mutate(
                    request,
                    { response ->
                        if (response.hasErrors()) {
                            Log.e("CartRepository", "Error deleting cart item: ${response.errors}")
                        } else if (response.hasData()) {
                            Log.i("CartRepository", "Item deleted: ${response.data}")
                        } else {
                            Log.e("CartRepository", "Delete failed: No data, no errors")
                        }
                        continuation.resume(Unit)
                    },
                    { error ->
                        Log.e("CartRepository", "API error deleting cart item", error)
                        continuation.resume(Unit)
                    }
                )
            }

            fetchCartItems(item.user.id)
        } catch (e: Exception) {
            Log.e("CartRepository", "Exception deleting cart item", e)
        }
    }

    override suspend fun clearCart(buyerId: String) {
        try {
            val currentItems = cartItems.value.filter { it.user.id == buyerId }
            for (item in currentItems) {
                deleteCartItem(item)
            }
        } catch (e: Exception) {
            Log.e("CartRepository", "Exception clearing cart", e)
        }
    }

    override suspend fun increaseQuantity(item: CartItem, delta: Int) {
        try {
            val updated = item.copyOfBuilder()
                .quantity(item.quantity + delta)
                .updatedAt(Temporal.DateTime(OffsetDateTime.now().toString()))
                .build()

            suspendCoroutine<Unit> { continuation ->
                Amplify.API.mutate(
                    ModelMutation.update(updated),
                    { response ->
                        if (response.hasErrors()) {
                            Log.e("CartRepo", "Increase qty errors: ${response.errors}")
                        } else if (response.hasData()) {
                            Log.i("CartRepo", "Quantity increased: ${response.data}")
                        } else {
                            Log.e("CartRepo", "Increase failed: No data, no errors")
                        }
                        continuation.resume(Unit)
                    },
                    { error ->
                        Log.e("CartRepo", "API error increasing quantity", error)
                        continuation.resume(Unit)
                    }
                )
            }

            fetchCartItems(item.user.id)
        } catch (e: Exception) {
            Log.e("CartRepository", "Exception increasing quantity", e)
        }
    }

    override suspend fun decreaseQuantity(item: CartItem, delta: Int) {
        try {
            val newQty = item.quantity - delta
            if (newQty <= 0) {
                deleteCartItem(item)
            } else {
                val updated = item.copyOfBuilder()
                    .quantity(newQty)
                    .updatedAt(Temporal.DateTime(OffsetDateTime.now().toString()))
                    .build()

                suspendCoroutine<Unit> { continuation ->
                    Amplify.API.mutate(
                        ModelMutation.update(updated),
                        { response ->
                            if (response.hasErrors()) {
                                Log.e("CartRepo", "Decrease qty errors: ${response.errors}")
                            } else if (response.hasData()) {
                                Log.i("CartRepo", "Quantity decreased: ${response.data}")
                            } else {
                                Log.e("CartRepo", "Decrease failed: No data, no errors")
                            }
                            continuation.resume(Unit)
                        },
                        { error ->
                            Log.e("CartRepo", "API error decreasing quantity", error)
                            continuation.resume(Unit)
                        }
                    )
                }

                fetchCartItems(item.user.id)
            }
        } catch (e: Exception) {
            Log.e("CartRepository", "Exception decreasing quantity", e)
        }
    }
}
