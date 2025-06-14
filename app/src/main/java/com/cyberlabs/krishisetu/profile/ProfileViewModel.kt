package com.cyberlabs.krishisetu.profile

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amplifyframework.api.graphql.model.ModelQuery
import com.amplifyframework.core.Amplify
import com.amplifyframework.datastore.generated.model.User
import com.cyberlabs.krishisetu.authentication.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _userId = mutableStateOf<String?>(null)
    val userId: State<String?> = _userId

    private val _user = mutableStateOf<User?>(null)
    val user: State<User?> = _user

    private val _profilePicUrl = MutableStateFlow<String?>(null)
    val profilePicUrl: StateFlow<String?> = _profilePicUrl

    init {
        fetchUserId()
    }

    fun updateProfilePicUrl(url: String) {
        _profilePicUrl.value = url
    }

    fun fetchUserId() {
        viewModelScope.launch {
            _userId.value = authRepository.getCurrUserID()
            _user.value = queryUserById(userId.value!!)
            _profilePicUrl.value = user.value?.profilePicture
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
                            continuation.resume(user, {})
                        } else {
                            Log.i("UploadCartItem", "API Query User: User not found for ID: $id")
                            continuation.resume(null, {})
                        }
                    } else if (response.hasErrors()) {
                        Log.e(
                            "UploadCartItem",
                            "API Query User Errors: ${response.errors.joinToString { it.message }}"
                        )
                        continuation.resume(null, {})
                    } else {
                        Log.e(
                            "UploadCartItem",
                            "API Query User: No data and no errors (unexplained)"
                        )
                        continuation.resume(null, {})
                    }
                },
                { error ->
                    Log.e("UploadCartItem", "API Query User Error: ${error.localizedMessage}")
                    continuation.resume(null, {})
                }
            )
        }

    fun uploadAndSetProfilePic(
        imageFile: File,
        id: String
    ) {
        viewModelScope.launch {
            profileRepository.uploadProfilePic(id, imageFile) { key ->
                viewModelScope.launch {
                    if (key != null) {
                        val imageUrl = profileRepository.getImageUrl(key)
                        Log.i("ProfileViewModel", "Profile picture uploaded to S3 successfully")
                        Log.i("ProfileViewModel", "Profile picture URL: $imageUrl")
                        Log.i("ProfileViewModel", "User ID: $id")
                        profileRepository.updateUserProfilePicture(
                            id,
                            imageUrl
                        ) {
                            updateProfilePicUrl(imageUrl)
                        }
                    } else {
                        Log.e("ProfileViewModel", "Failed to upload profile picture to S3")
                    }
                }
            }
        }
    }
}
