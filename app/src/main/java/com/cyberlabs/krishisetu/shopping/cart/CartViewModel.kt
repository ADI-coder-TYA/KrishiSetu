package com.cyberlabs.krishisetu.shopping.cart

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amplifyframework.api.graphql.model.ModelQuery
import com.amplifyframework.core.Amplify
import com.amplifyframework.core.model.temporal.Temporal
import com.amplifyframework.datastore.generated.model.CartItem
import com.amplifyframework.datastore.generated.model.Crop
import com.amplifyframework.datastore.generated.model.User
import com.cyberlabs.krishisetu.authentication.AuthRepository
import com.cyberlabs.krishisetu.crops.CropRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.time.OffsetDateTime
import java.util.UUID
import javax.inject.Inject
import kotlin.coroutines.resume

@HiltViewModel
class CartViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val cartRepository: CartRepository,
    private val cropRepository: CropRepository
) : ViewModel() {

    val cartItems: StateFlow<List<CartItem>> = cartRepository.cartItems

    val totalPrice: StateFlow<Int> = cartItems
        .map { it.sumOf { item -> item.priceAtAdd.toInt() * item.quantity } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)


    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _imageUrls = MutableStateFlow<Map<String, String?>>(emptyMap())
    val imageUrls: StateFlow<Map<String, String?>> = _imageUrls

    private val _buyerId = MutableStateFlow<String?>(null)
    val buyerId: StateFlow<String?> = _buyerId

    init {
        viewModelScope.launch {
            val id = authRepository.getCurrUserID()
            _buyerId.value = id
            loadCartItems(id)
        }
    }

    fun loadCartItems(buyerId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            cartRepository.fetchCartItems(buyerId)
            _isLoading.value = false
        }
    }

    fun loadImage(crop: Crop) {
        viewModelScope.launch {
            val url = try {
                cropRepository.getImageUrl(crop.imageUrl)
            } catch (e: Exception) {
                Log.e("CartViewModel", "Error fetching image", e)
                null
            }
            _imageUrls.update { it + (crop.id to url) }
        }
    }

    fun removeItemFromCart(item: CartItem) {
        viewModelScope.launch {
            _isLoading.value = true
            cartRepository.deleteCartItem(item)
            _isLoading.value = false
        }
    }

    fun clearCart(buyerId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            cartRepository.clearCart(buyerId)
            _isLoading.value = false
        }
    }

    fun increaseQuantity(item: CartItem, delta: Int = 1) {
        viewModelScope.launch {
            _isLoading.value = true
            cartRepository.increaseQuantity(item, delta)
            _isLoading.value = false
        }
    }

    fun decreaseQuantity(item: CartItem, delta: Int = 1) {
        viewModelScope.launch {
            _isLoading.value = true
            cartRepository.decreaseQuantity(item, delta)
            _isLoading.value = false
        }
    }

    fun addItemToCart(buyerId: String, cropId: String, quantity: Int, priceAtAdd: Double) {
        viewModelScope.launch {
            val existingItem = cartItems.value.find { it.crop.id == cropId }
            if (existingItem != null) {
                increaseQuantity(existingItem, quantity)
                return@launch
            }

            val crop = cropRepository.getCrop(cropId)
            val user = queryUserById(buyerId)

            if (user != null) {
                val item = CartItem.builder()
                    .quantity(quantity)
                    .priceAtAdd(priceAtAdd)
                    .addedAt(Temporal.DateTime(OffsetDateTime.now().toString()))
                    .createdAt(Temporal.DateTime(OffsetDateTime.now().toString()))
                    .updatedAt(Temporal.DateTime(OffsetDateTime.now().toString()))
                    .crop(crop)
                    .id(UUID.randomUUID().toString())
                    .user(user)
                    .build()

                cartRepository.addCartItem(item)
            }
        }
    }

    private suspend fun queryUserById(id: String): User? =
        suspendCancellableCoroutine { continuation ->
            Amplify.API.query(
                ModelQuery[User::class.java, id],
                { response ->
                    if (response.hasData()) {
                        val user = response.data
                        if (user != null) {
                            Log.i("UploadCartItem", "API Query User: Success")
                            continuation.resume(user)
                        } else {
                            Log.i("UploadCartItem", "API Query User: User not found for ID: $id")
                            continuation.resume(null)
                        }
                    } else if (response.hasErrors()) {
                        Log.e(
                            "UploadCartItem",
                            "API Query User Errors: ${response.errors.joinToString { it.message }}"
                        )
                        continuation.resume(null)
                    } else {
                        Log.e(
                            "UploadCartItem",
                            "API Query User: No data and no errors (unexplained)"
                        )
                        continuation.resume(null)
                    }
                },
                { error ->
                    Log.e("UploadCartItem", "API Query User Error: ${error.localizedMessage}")
                    continuation.resume(null)
                }
            )
        }

}
