package com.cyberlabs.krishisetu.crops

import android.util.Log
import com.amplifyframework.api.graphql.model.ModelQuery
import com.amplifyframework.core.Amplify
import com.amplifyframework.datastore.generated.model.Crop
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class CropRepository @Inject constructor() {

    suspend fun getCrop(cropId: String): Crop = suspendCoroutine { continuation ->
        Amplify.API.query(
            ModelQuery[Crop::class.java, cropId],
            { response ->
                if (response.hasData()) {
                    val cropEntity = response.data
                    Log.i("Crop Repository", "Successfully fetched crop with ID: $cropId")
                    continuation.resume(cropEntity)
                } else if (response.hasErrors()) {
                    val errors = response.errors.joinToString { it.message }
                    Log.e("Crop Repository", "Crop Query failed with errors: $errors")
                    continuation.resumeWithException(RuntimeException("Query failed: $errors"))
                } else {
                    Log.e("Crop Repository", "Query: No crop data and no errors (unexplained)")
                }
            },
            { apiError ->
                Log.e("Crop Repository", "API Crop Query failed", apiError)
                continuation.resumeWithException(apiError)
            }
        )
    }

    suspend fun getAllCrops(): List<Crop> = suspendCoroutine { continuation ->

        Amplify.Auth.getCurrentUser(
            { authUser ->
                // Use Amplify.API.query instead of Amplify.DataStore.query
                // Filter by the current user's ID
                Amplify.API.query(
                    ModelQuery.list(Crop::class.java, Crop.FARMER.eq(authUser.userId)),
                    { response ->
                        if (response.hasData()) {
                            val cropEntities = response.data.items.toList()
                            Log.i(
                                "Crop Repository",
                                "Successfully fetched crops via API for user: ${authUser.userId}"
                            )
                            continuation.resume(cropEntities)
                        } else if (response.hasErrors()) {
                            val errors = response.errors.joinToString { it.message }
                            Log.e("Crop Repository", "API Crop Query failed with errors: $errors")
                            continuation.resumeWithException(RuntimeException("API Query failed: $errors"))
                        } else {
                            Log.e(
                                "Crop Repository",
                                "API Crop Query: No data and no errors (unexplained)"
                            )
                            continuation.resume(emptyList()) // Or throw an exception if this state is unexpected
                        }
                    },
                    { apiError ->
                        Log.e("Crop Repository", "API Crop Query failed", apiError)
                        continuation.resumeWithException(apiError)
                    }
                )
            }) { authError ->
            Log.e("Crop Repository", "Failed to fetch current user", authError)
            continuation.resumeWithException(authError)
        }
    }

    suspend fun getImageUrl(key: String): String = suspendCoroutine { continuation ->
        Amplify.Storage.getUrl(
            key,
            { result -> continuation.resume(result.url.toString()) },
            { error -> continuation.resumeWithException(error) }
        )
    }
}
