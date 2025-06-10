package com.cyberlabs.krishisetu.shopping.cart

import android.util.Log
import com.amplifyframework.api.graphql.model.ModelMutation
import com.amplifyframework.api.graphql.model.ModelQuery
import com.amplifyframework.core.Amplify
import com.amplifyframework.core.model.temporal.Temporal
import com.amplifyframework.datastore.generated.model.Cart
import com.amplifyframework.datastore.generated.model.CartItem
import com.amplifyframework.datastore.generated.model.Crop
import com.amplifyframework.datastore.generated.model.User
import java.time.OffsetDateTime
import java.util.UUID
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class CartRepositoryImpl @Inject constructor() : CartRepository {

    override suspend fun getCart(userId: String): Cart = suspendCoroutine { cont ->
        Amplify.API.query(
            ModelQuery.list(Cart::class.java, Cart.BUYER.eq(userId)),
            { resp ->
                when {
                    resp.hasErrors() -> {
                        cont.resumeWithException(RuntimeException(resp.errors.joinToString { it.message }))
                    }

                    resp.hasData() -> {
                        val carts = resp.data.items
                        if (carts.count() > 0) {
                            Log.i("CartRepository", "Cart found: $carts")
                            Log.i("CartRepository", "Cart first found: ${carts.first()}")
                            Log.i("CartRepository", "Cart Items: ${carts.first().items.toList()}")
                            cont.resume(carts.first())
                        } else {
                            // No cart → fetch the user object
                            Amplify.API.query(
                                ModelQuery[User::class.java, userId],
                                { userResp ->
                                    when {
                                        userResp.hasErrors() ->
                                            cont.resumeWithException(RuntimeException(userResp.errors.joinToString { it.message }))

                                        userResp.data != null -> {
                                            val newCart = Cart.builder()
                                                .createdAt(
                                                    Temporal.DateTime(
                                                        OffsetDateTime.now().toString()
                                                    )
                                                )
                                                .updatedAt(
                                                    Temporal.DateTime(
                                                        OffsetDateTime.now().toString()
                                                    )
                                                )
                                                .buyer(userResp.data)       // or .buyer(userId) if your schema wants a string
                                                .id(UUID.randomUUID().toString())
                                                .build()
                                            // create the new cart
                                            Amplify.API.mutate(
                                                ModelMutation.create(newCart),
                                                { mResp ->
                                                    if (mResp.hasErrors())
                                                        cont.resumeWithException(
                                                            RuntimeException(
                                                                mResp.errors.joinToString { it.message })
                                                        )
                                                    else if (mResp.data != null)
                                                        cont.resume(mResp.data)
                                                    else
                                                        cont.resumeWithException(RuntimeException("Create returned no data"))
                                                },
                                                { err -> cont.resumeWithException(err) }
                                            )
                                        }

                                        else -> {
                                            cont.resumeWithException(RuntimeException("User not found"))
                                        }
                                    }
                                },
                                { apiErr -> cont.resumeWithException(apiErr) }
                            )
                        }
                    }

                    else -> {
                        cont.resumeWithException(RuntimeException("Unexpected: no data & no errors"))
                    }
                }
            },
            { err -> cont.resumeWithException(err) }
        )
    }

    override suspend fun addItemToCart(
        cart: Cart,
        item: Crop,
        quantity: Int
    ): Boolean {
        return try {
            val existingCartItem = getCartItemByCrop(cart, item)
            if (existingCartItem != null) {
                increaseQuantity(existingCartItem, quantity)
            } else {
                val now = Temporal.DateTime(OffsetDateTime.now().toString())
                val cartItem = CartItem.builder()
                    .buyerId(cart.buyer.id)
                    .quantity(quantity)
                    .priceAtAdd(item.price)
                    .addedAt(now)
                    .createdAt(now)
                    .updatedAt(now)
                    .cart(cart)
                    .crop(item)
                    .id(UUID.randomUUID().toString())
                    .build()

                Log.i("CartRepository", "Adding new item to cart: $cartItem") //Log correct values, no null fields

                suspendCoroutine { continuation ->
                    Amplify.API.mutate(
                        ModelMutation.create(cartItem),
                        { response ->
                            if (response.hasErrors()) {
                                Log.e("CartRepository", "Error adding item: ${response.errors}")
                                continuation.resume(false)
                            } else if (response.hasData()) {
                                Log.i("CartRepository", "Item added to cart successfully")
                                continuation.resume(true)
                            }
                        },
                        { apiError ->
                            Log.e("CartRepository", "API error in addItemToCart", apiError)
                            continuation.resumeWithException(apiError)
                        }
                    )
                }
            }
        } catch (e: Exception) {
            Log.e("CartRepository", "Exception in addItemToCart", e)
            false
        }
    }


    override suspend fun removeItemFromCart(item: CartItem): Boolean =
        suspendCoroutine { continuation ->
            Amplify.API.mutate(
                ModelMutation.delete(item),
                { response ->
                    if (response.hasErrors()) {
                        Log.e(
                            "CartRepository",
                            "Response error: ${response.errors.joinToString { it.message }}",
                        )
                        continuation.resumeWithException(RuntimeException("Response error in deleting item"))
                    } else if (response.hasData()) {
                        Log.i("CartRepository", "Item removed from cart successfully")
                        continuation.resume(true)
                    } else {
                        Log.e("CartRepository", "No item data and no errors (unexplained)")
                        continuation.resume(false)
                    }
                }, { apiError ->
                    Log.e("CartRepository", "API error in deleting item", apiError)
                    continuation.resumeWithException(apiError)
                }
            )
        }

    suspend fun getCartItemByCrop(cart: Cart, crop: Crop): CartItem? {
        return suspendCoroutine { continuation ->
            val predicate = CartItem.CART.eq(cart.id).and(CartItem.CROP.eq(crop.id))
            Amplify.API.query(
                ModelQuery.list(CartItem::class.java, predicate),
                { response ->
                    val item = response.data.firstOrNull()
                    Log.i("CartRepository", "Existing item check: $item")
                    continuation.resume(item)
                },
                { apiError ->
                    Log.e("CartRepository", "API error fetching cart item by crop", apiError)
                    continuation.resumeWithException(apiError)
                }
            )
        }
    }

    override suspend fun increaseQuantity(cartItem: CartItem, quantityToAdd: Int): Boolean {
        val updatedItem = cartItem.copyOfBuilder()
            .quantity(cartItem.quantity + quantityToAdd)
            .updatedAt(Temporal.DateTime(OffsetDateTime.now().toString()))
            .build()

        Log.i("CartRepository", "Updating quantity: $updatedItem")

        return suspendCoroutine { continuation ->
            Amplify.API.mutate(
                ModelMutation.update(updatedItem),
                { response ->
                    if (response.hasErrors()) {
                        Log.e("CartRepository", "Error updating item: ${response.errors}")
                        continuation.resume(false)

                    } else {
                        Log.i("CartRepository", "Quantity updated successfully")
                        continuation.resume(true)
                    }
                },
                { apiError ->
                    Log.e("CartRepository", "API error in increaseQuantity()", apiError)
                    continuation.resumeWithException(apiError)
                }
            )
        }
    }

    override suspend fun decreaseQuantity(cartItem: CartItem, quantityToDecrease: Int): Boolean {
        val newQuantity = cartItem.quantity - quantityToDecrease

        return if (newQuantity <= 0) {
            Log.i("CartRepository", "Quantity is zero or less, removing item: $cartItem")
            suspendCoroutine { continuation ->
                Amplify.API.mutate(
                    ModelMutation.delete(cartItem),
                    { response ->
                        if (response.hasData()) {
                            Log.i("CartRepository", "Item removed from cart successfully")
                            continuation.resume(true)
                        } else {
                            Log.e("CartRepository", "Error removing item: ${response.errors}")
                            continuation.resume(false)
                        }
                    },
                    { apiError ->
                        Log.e("CartRepository", "API error on delete", apiError)
                        continuation.resumeWithException(apiError)
                    }
                )
            }
        } else {
            val updatedItem = cartItem.copyOfBuilder()
                .quantity(newQuantity)
                .updatedAt(Temporal.DateTime(OffsetDateTime.now().toString()))
                .build()

            Log.i("CartRepository", "Decreasing quantity: $updatedItem")

            suspendCoroutine { continuation ->
                Amplify.API.mutate(
                    ModelMutation.update(updatedItem),
                    { response ->
                        if (response.hasData()) {
                            Log.i("CartRepository", "Quantity decreased successfully")
                            continuation.resume(true)
                        } else {
                            Log.e("CartRepository", "Error decreasing quantity: ${response.errors}")
                            continuation.resume(false)
                        }
                    },
                    { apiError ->
                        Log.e("CartRepository", "API error on decreaseQuantity()", apiError)
                        continuation.resumeWithException(apiError)
                    }
                )
            }
        }
    }

}
