package com.cyberlabs.krishisetu.crops

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amplifyframework.datastore.generated.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CropViewModel @Inject constructor(
    private val cropRepository: CropRepository
) : ViewModel() {
    private val _crops = mutableStateOf<List<CropData>>(emptyList())
    val crops: State<List<CropData>> get() = _crops

    private val _isLoading = mutableStateOf(true)
    val isLoading: State<Boolean> get() = _isLoading

    private val _errorMsg = mutableStateOf<String?>(null)
    val errorMsg: State<String?> get() = _errorMsg

    init {
        fetchCrops()
    }


    private fun fetchCrops() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val cropEntities = cropRepository.getAllCrops()
                val cropDataList = cropEntities.map { entity ->
                    val imageUrl = cropRepository.getImageUrl(entity.imageUrl)
                    CropData(
                        cropId = entity.id,
                        title = entity.title,
                        description = entity.description,
                        price = entity.price,
                        quantityAvailable = entity.quantityAvailable,
                        imageUrl = imageUrl,
                        location = entity.location,
                        farmer = entity.farmer
                    )
                }
                _crops.value = cropDataList
                _errorMsg.value = null
            } catch (e: Exception) {
                _errorMsg.value = e.message ?: "Something went wrong"
            } finally {
                _isLoading.value = false
            }
        }
    }
}

data class CropData(
    val cropId: String,
    val title: String,
    val description: String,
    val price: Double,
    val quantityAvailable: Int,
    val imageUrl: String,
    val location: String,
    val farmer: User
)
