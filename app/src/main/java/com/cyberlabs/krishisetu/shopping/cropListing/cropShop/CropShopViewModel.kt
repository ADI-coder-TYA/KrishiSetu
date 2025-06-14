package com.cyberlabs.krishisetu.shopping.cropListing.cropShop

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amplifyframework.api.graphql.model.ModelQuery
import com.amplifyframework.core.Amplify
import com.amplifyframework.datastore.generated.model.User
import com.cyberlabs.krishisetu.authentication.AmplifyAuthRepository
import com.cyberlabs.krishisetu.crops.CropData
import com.cyberlabs.krishisetu.crops.CropRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject

@HiltViewModel
class CropShopViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val cropRepository: CropRepository,
    private val authRepository: AmplifyAuthRepository
) : ViewModel() {
    private val cropId: String = checkNotNull(savedStateHandle["cropId"])

    private val _cropData = mutableStateOf<CropData?>(null)
    val cropData: State<CropData?> = _cropData

    private val _currUserId = mutableStateOf<String?>(null)
    val currUserId: State<String?> = _currUserId

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _errorMsg = mutableStateOf<String?>(null)
    val errorMsg: State<String?> = _errorMsg

    init {
        fetchUserId()
        fetchCropData()
    }

    private fun fetchUserId() {
        viewModelScope.launch {
            _currUserId.value = authRepository.getCurrUserID()
        }
    }

    private fun fetchCropData() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMsg.value = null
            try {
                val cropEntity = cropRepository.getCrop(cropId)
                val imageUrl = cropRepository.getImageUrl(cropEntity.imageUrl)
                _cropData.value = CropData(
                    cropId = cropEntity.id,
                    title = cropEntity.title,
                    imageUrl = imageUrl,
                    quantityAvailable = cropEntity.quantityAvailable,
                    price = cropEntity.price,
                    description = cropEntity.description,
                    farmer = cropEntity.farmer,
                    location = cropEntity.location
                )
                _errorMsg.value = null
            } catch (e: Exception) {
                _errorMsg.value = e.message ?: "Something went wrong"
            } finally {
                _isLoading.value = false
            }
        }
    }
}