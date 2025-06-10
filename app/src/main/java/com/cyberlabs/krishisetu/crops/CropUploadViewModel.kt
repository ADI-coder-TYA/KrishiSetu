package com.cyberlabs.krishisetu.crops

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.amplifyframework.api.graphql.model.ModelMutation
import com.amplifyframework.api.graphql.model.ModelQuery
import com.amplifyframework.core.Amplify
import com.amplifyframework.core.model.temporal.Temporal
import com.amplifyframework.datastore.generated.model.Crop
import com.amplifyframework.datastore.generated.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.time.OffsetDateTime
import java.util.UUID
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@HiltViewModel
class CropUploadViewModel @Inject constructor(
    private val application: Application
) : AndroidViewModel(application) {
    private var _uploadState by mutableStateOf(CropUploadScreenState())
    val uploadState: CropUploadScreenState
        get() = _uploadState

    var currentUserId by mutableStateOf<String?>(null)
        private set

    private val _uploadSuccessful = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val uploadSuccessful: SharedFlow<Unit> = _uploadSuccessful

    init {
        fetchCurrentUserId()
    }

    fun updateTitle(title: String) {
        _uploadState = _uploadState.copy(title = title)
    }

    fun updateDescription(description: String) {
        _uploadState = _uploadState.copy(description = description)
    }

    fun updatePrice(price: String) {
        _uploadState = _uploadState.copy(price = price)
    }

    fun updateQuantity(quantity: String) {
        _uploadState = _uploadState.copy(quantity = quantity)
    }

    fun updateLocation(location: String) {
        _uploadState = _uploadState.copy(location = location)
    }

    fun fetchCurrentUserId() {
        Amplify.Auth.getCurrentUser(
            { user ->
                currentUserId = user.userId
            },
            { error -> Log.e("CropUploadViewModel", "Error getting current user", error) }
        )
    }

    fun uploadCrop(inputStream: InputStream, extension: String) {
        viewModelScope.launch {
            val title = uploadState.title
            val desc = uploadState.description
            val price = uploadState.price
            val quantity = uploadState.quantity
            val location = uploadState.location

            if (title.isBlank() || desc.isBlank() || price.isBlank() || quantity.isBlank()
                || location.isBlank()
            ) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        application.applicationContext,
                        "Please fill in all fields",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                return@launch
            }
            val userId = currentUserId ?: run {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        application.applicationContext,
                        "User not logged in",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                return@launch
            }

            val fileName = "public/crops/${UUID.randomUUID()}$extension"
            val uploadSuccess = uploadImageToS3(inputStream, fileName)

            if (!uploadSuccess) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        application.applicationContext,
                        "Failed to upload image",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                return@launch
            }

            val farmer = queryUserById(userId)
            if (farmer == null) {
                Toast.makeText(
                    application.applicationContext,
                    "Failed to fetch farmer",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e("UploadCrop", "Failed to fetch farmer")
                deleteImageFromS3(fileName)
                return@launch
            }

            val crop = Crop.builder()
                .title(title)
                .price(price.toDouble())
                .quantityAvailable(quantity.toInt())
                .createdAt(Temporal.DateTime(OffsetDateTime.now().toString()))
                .updatedAt(Temporal.DateTime(OffsetDateTime.now().toString()))
                .description(desc)
                .imageUrl(fileName)
                .location(location)
                .farmer(farmer)
                .build()

            val saved = saveCropViaApi(crop) // Changed function name

            withContext(Dispatchers.Main) {
                if (saved) {
                    _uploadSuccessful.tryEmit(Unit)
                    _uploadState = CropUploadScreenState()
                    Toast.makeText(
                        application.applicationContext,
                        "Crop uploaded and saved successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.i("UploadCrop", "Crop uploaded and saved successfully")
                } else {
                    Toast.makeText(
                        application.applicationContext,
                        "Failed to save crop",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e("UploadCrop", "Failed to save crop")
                    deleteImageFromS3(fileName)
                }
            }
        }
    }

    private suspend fun uploadImageToS3(inputStream: InputStream, key: String): Boolean {
        return suspendCoroutine { cont ->
            Amplify.Storage.uploadInputStream(
                key,
                inputStream,
                {
                    Log.i("UploadCrop", "Upload complete")
                    cont.resume(true)
                    try {
                        inputStream.close()
                    } catch (e: Exception) {
                        Log.e("UploadCrop", "Error closing input stream: ${e.localizedMessage}")
                    }
                },
                { error ->
                    Log.e("UploadCrop", "Upload error: ${error.localizedMessage}")
                    cont.resume(false)
                    try {
                        inputStream.close()
                    } catch (e: Exception) {
                        Log.e("UploadCrop", "Error closing input stream: ${e.localizedMessage}")
                    }
                }
            )
        }
    }

    // Changed from saveDataStore to saveCropViaApi to reflect API usage
    private suspend fun saveCropViaApi(crop: Crop): Boolean =
        suspendCoroutine { cont ->
            Amplify.API.mutate(
                ModelMutation.create(crop),
                { response ->
                    if (response.hasData()) {
                        Log.i("CropVM", "API Save: Success, ${response.data}")
                        cont.resume(true)
                    } else if (response.hasErrors()) {
                        Log.e("CropVM", "API Save Errors: ${response.errors.joinToString { it.message }}")
                        cont.resume(false)
                    } else {
                        Log.e("CropVM", "API Save: No data and no errors (unexplained)")
                        cont.resume(false)
                    }
                },
                { err ->
                    Log.e("CropVM", "API Save Error: ${err.message.toString()}")
                    cont.resume(false)
                }
            )
        }

    // Changed from queryUserById to queryUserByIdViaApi to reflect API usage
    private suspend fun queryUserById(id: String): User? = suspendCancellableCoroutine { continuation ->
        Amplify.API.query(
            ModelQuery.get(User::class.java, id),
            { response ->
                if (response.hasData()) {
                    val user = response.data
                    if (user != null) {
                        Log.i("UploadCrop", "API Query User: Success")
                        continuation.resume(user)
                    } else {
                        Log.i("UploadCrop", "API Query User: User not found for ID: $id")
                        continuation.resume(null)
                    }
                } else if (response.hasErrors()) {
                    Log.e("UploadCrop", "API Query User Errors: ${response.errors.joinToString { it.message }}")
                    continuation.resume(null)
                } else {
                    Log.e("UploadCrop", "API Query User: No data and no errors (unexplained)")
                    continuation.resume(null)
                }
            },
            { error ->
                Log.e("UploadCrop", "API Query User Error: ${error.localizedMessage}")
                continuation.resume(null)
            }
        )
    }

    fun deleteImageFromS3(imageKey: String) {
        Amplify.Storage.remove(
            imageKey,
            { result -> Log.i("DeleteImage", "Successfully deleted: ${result.key}") },
            { error -> Log.e("DeleteImage", "Delete failed: ${error.localizedMessage}") }
        )
    }

}

data class CropUploadScreenState(
    val title: String = "",
    val description: String = "",
    val price: String = "",
    val quantity: String = "",
    val location: String = ""
)