package com.cyberlabs.krishisetu.shopping.cart

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amplifyframework.datastore.generated.model.Cart
import com.amplifyframework.datastore.generated.model.CartItem
import com.amplifyframework.datastore.generated.model.Crop
import com.cyberlabs.krishisetu.authentication.AuthRepository
import com.cyberlabs.krishisetu.crops.CropRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val cartRepository: CartRepository,
    private val cropRepository: CropRepository
) : ViewModel() {

    private val _userCart = mutableStateOf<Cart?>(null)
    val userCart: State<Cart?> = _userCart

    private val _currUserId = mutableStateOf<String?>(null)
    val currUserId: State<String?> = _currUserId

    // Always emits an Int (0 if cart is empty or loading)
    val totalPrice: State<Int> = derivedStateOf {
        _userCart.value?.items?.sumOf { it.quantity * it.priceAtAdd.toInt() } ?: 0
    }

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _errorMsg = mutableStateOf<String?>(null)
    val errorMsg: State<String?> = _errorMsg

    private val _imageUrls = MutableStateFlow<Map<String, String?>>(emptyMap())
    val imageUrls: StateFlow<Map<String, String?>> = _imageUrls

    init {
        fetchCart()
    }

    private fun fetchCart() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // 1. Properly await the suspend call
                val userId = authRepository.getCurrUserID()
                if (userId.isEmpty()) {
                    _errorMsg.value = "User not signed in"
                    return@launch
                }
                _currUserId.value = userId

                // 2. Fetch cart only after we have a non-null ID
                _userCart.value = cartRepository.getCart(userId)
                Log.i("CartViewModel", "Cart fetched: ${_userCart.value?.id}")
                Log.i("CartViewModel", "Cart items: ${_userCart.value?.items?.toList()}")
            } catch (e: Exception) {
                // Preserve the error for the UI to show
                _errorMsg.value = e.message ?: "Something went wrong fetching cart"
                Log.e("CartViewModel", "Error fetching cart", e)
            } finally {
                _isLoading.value = false
            }
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

    fun addItemToCart(itemId: String, quantity: Int) {
        viewModelScope.launch {
            try {
                _errorMsg.value = null
                val item = cropRepository.getCrop(itemId)
                val cart = _userCart.value
                if (cart == null) {
                    _errorMsg.value = "Cart not initialized"
                    return@launch
                }

                Log.i("CartViewModel", "Adding item to cart: $item")
                Log.i("CartViewModel", "Cart: $cart")

                val added = cartRepository.addItemToCart(cart, item, quantity)
                if (!added) {
                    _errorMsg.value = "Failed to add item to cart"
                }
                // refresh after mutation
                fetchCart()
            } catch (e: Exception) {
                _errorMsg.value = e.message ?: "Something went wrong adding item"
                Log.e("CartViewModel", "Error adding item", e)
            }
        }
    }

    fun removeItemFromCart(cartItem: CartItem) {
        viewModelScope.launch {
            try {
                _errorMsg.value = null
                // Ensure your repository accepts CartItem
                val removed = cartRepository.removeItemFromCart(cartItem)
                if (!removed) {
                    _errorMsg.value = "Failed to remove item from cart"
                }
                // refresh after mutation
                fetchCart()
            } catch (e: Exception) {
                _errorMsg.value = e.message ?: "Something went wrong removing item"
                Log.e("CartViewModel", "Error removing item", e)
            }
        }
    }

    fun increaseQuantity(cartItem: CartItem) {
        viewModelScope.launch {
            try {
                _errorMsg.value = null
                val success = cartRepository.increaseQuantity(cartItem, 1)
                if (!success) {
                    _errorMsg.value = "Failed to increase quantity"
                }
                fetchCart()
            } catch (e: Exception) {
                _errorMsg.value = e.message ?: "Something went wrong increasing quantity"
            }
        }
    }

    fun decreaseQuantity(cartItem: CartItem) {
        viewModelScope.launch {
            try {
                _errorMsg.value = null
                val success = cartRepository.decreaseQuantity(cartItem, 1)
                if (!success) {
                    _errorMsg.value = "Failed to decrease quantity"
                }
                fetchCart()
            } catch (e: Exception) {
                _errorMsg.value = e.message ?: "Something went wrong decreasing quantity"
            }
        }
    }

    /** Optional: clear entire cart **/
    fun clearCart() {
        viewModelScope.launch {
            try {
                _errorMsg.value = null
                val cart = _userCart.value ?: return@launch
                val successes = cart.items.map { cartItem ->
                    cartRepository.removeItemFromCart(cartItem)
                }
                if (successes.any { !it }) {
                    _errorMsg.value = "Some items failed to remove"
                }
                fetchCart()
            } catch (e: Exception) {
                _errorMsg.value = e.message ?: "Something went wrong clearing cart"
            }
        }
    }
}
