package com.cyberlabs.krishisetu.profile

import android.util.Log
import com.amplifyframework.api.graphql.model.ModelMutation
import com.amplifyframework.api.graphql.model.ModelQuery
import com.amplifyframework.core.Amplify
import com.amplifyframework.datastore.generated.model.User
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class ProfileRepository @Inject constructor() {

    suspend fun updateUserProfilePicture(userId: String, imageUrl: String, onSuccess: (String) -> Unit): Boolean =
        suspendCancellableCoroutine { continuation ->
            Amplify.API.query(
                ModelQuery[User::class.java, userId],
                { response ->
                    val user = response.data
                    if (user != null) {
                        val updatedUser = user.copyOfBuilder()
                            .profilePicture(imageUrl)
                            .build()

                        Log.i("ProfileRepository", "Updated User: $updatedUser")

                        Amplify.API.mutate(
                            ModelMutation.update(updatedUser),
                            { mutationResponse ->
                                if (mutationResponse.hasData()) {
                                    Log.i(
                                        "ProfileRepository",
                                        "Update Successful, ${response.data}"
                                    )
                                    onSuccess(imageUrl)
                                    continuation.resume(true) { cause, _, _ ->
                                        Log.e(
                                            "ProfileRepository",
                                            "Update Cancelled, cause : ${cause.message}"
                                        )
                                    }
                                } else if (mutationResponse.hasErrors()) {
                                    val message =
                                        mutationResponse.errors.joinToString { it.message }
                                    continuation.resumeWithException(Exception("Mutation error: $message"))
                                } else {
                                    continuation.resumeWithException(Exception("Unknown error occurred"))
                                }
                            },
                            { error ->
                                continuation.resumeWithException(error)
                            }
                        )
                    } else {
                        continuation.resumeWithException(Exception("User not found"))
                    }
                },
                { error ->
                    continuation.resumeWithException(error)
                }
            )
        }


    fun uploadProfilePic(
        userId: String,
        file: File,
        onComplete: (String?) -> Unit
    ) {
        val key = "public/profile_pics/$userId.jpg"
        Amplify.Storage.uploadFile(
            key,
            file,
            {
                onComplete(key)
            },
            { onComplete(null) }
        )
    }

    suspend fun getImageUrl(key: String): String = suspendCoroutine { continuation ->
        Amplify.Storage.getUrl(
            key,
            { result -> continuation.resume(result.url.toString()) },
            { error -> continuation.resumeWithException(error) }
        )
    }

}