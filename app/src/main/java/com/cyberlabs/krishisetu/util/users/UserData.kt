package com.cyberlabs.krishisetu.util.users

import android.util.Log
import com.amplifyframework.api.ApiException
import com.amplifyframework.api.graphql.GraphQLRequest
import com.amplifyframework.api.graphql.model.ModelQuery
import com.amplifyframework.auth.AuthUserAttributeKey
import com.amplifyframework.core.Amplify
import com.amplifyframework.core.model.Model
import com.amplifyframework.core.model.query.predicate.QueryPredicate
import com.amplifyframework.datastore.generated.model.User
import com.amplifyframework.datastore.generated.model.UserRole
import com.cyberlabs.krishisetu.authentication.AuthViewModel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

fun userRoleFlow(vm: AuthViewModel): Flow<UserRole?> =
    vm.userAttributes.map { attrs ->
        val role = attrs?.find { it.key == AuthUserAttributeKey.custom("custom:role") }?.value
        when (role) {
            "FARMER" -> UserRole.FARMER
            "BUYER" -> UserRole.BUYER
            "DELIVERY" -> UserRole.DELIVERY_AGENT
            else -> null
        }
    }

fun userNameFlow(userId: String): Flow<String?> = callbackFlow {
    // Construct a GraphQL request to get a User by ID
    // Assuming your GraphQL schema has a query like 'getUser(id: ID!): User'
    val request: GraphQLRequest<User> = ModelQuery[User::class.java, userId]

    Amplify.API.query(
        request,
        { response ->
            if (response.hasData()) {
                val user = response.data
                trySend(user?.name) // user will be null if not found
            } else if (response.hasErrors()) {
                val errorMessages = response.errors.joinToString { it.message }
                Log.e("UserAPIName", "GraphQL errors: $errorMessages")
                trySend(null)
            } else {
                Log.e("UserAPIName", "No data or errors in API response for ID: $userId")
                trySend(null)
            }
            close()
        },
        { error ->
            Log.e("UserAPIName", "Failed to fetch user via API", error)
            trySend(null)
            close()
        }
    )
    awaitClose { /* No specific cleanup needed for a single API query */ }
}

fun userProfilePicUrlFlow(userId: String): Flow<String?> = callbackFlow {
    // Construct a GraphQL request to get a User by ID
    // Assuming your GraphQL schema has a query like 'getUser(id: ID!): User'
    val request: GraphQLRequest<User> = ModelQuery[User::class.java, userId]

    Amplify.API.query(
        request,
        { response ->
            if (response.hasData()) {
                val user = response.data
                trySend(user?.profilePicture) // user will be null if not found
            } else if (response.hasErrors()) {
                val errorMessages = response.errors.joinToString { it.message }
                Log.e("UserAPIProfilePicUrl", "GraphQL errors: $errorMessages")
                trySend(null)
            } else {
                Log.e("UserAPIProfilePicUrl", "No data or errors in API response for ID: $userId")
                trySend(null)
            }
            close()
        },
        { error ->
            Log.e("UserAPIProfilePicUrl", "Failed to fetch user via API", error)
            trySend(null)
            close()
        }
    )
    awaitClose { /* No specific cleanup needed for a single API query */ }
}

fun userPhoneFlow(userId: String): Flow<String?> = callbackFlow {
    // Construct a GraphQL request to get a User by ID
    // Assuming your GraphQL schema has a query like 'getUser(id: ID!): User'
    val request: GraphQLRequest<User> = ModelQuery[User::class.java, userId]

    Amplify.API.query(
        request,
        { response ->
            if (response.hasData()) {
                val user = response.data
                trySend(user?.phone) // user will be null if not found
            } else if (response.hasErrors()) {
                val errorMessages = response.errors.joinToString { it.message }
                Log.e("UserAPIPhone", "GraphQL errors: $errorMessages")
                trySend(null)
            } else {
                Log.e("UserAPIPhone", "No data or errors in API response for ID: $userId")
                trySend(null)
            }
            close()
        },
        { error ->
            Log.e("UserAPIPhone", "Failed to fetch user via API", error)
            trySend(null)
            close()
        }
    )
    awaitClose { /* No specific cleanup needed for a single API query */ }
}

suspend inline fun <reified T : Model> querySuspend(
    predicate: QueryPredicate
): List<T> = suspendCoroutine { cont ->
    val request = ModelQuery.list(T::class.java, predicate)

    Amplify.API.query(
        request,
        { response ->
            if (response.hasData()) {
                cont.resume(response.data.items.toList())
            } else if (response.hasErrors()) {
                val errorMessages = response.errors.joinToString { it.message }
                response
                cont.resumeWithException(
                    ApiException(
                        "GraphQL errors: $errorMessages",
                        "See the errors and rectify them please: API error, querySuspendFunction"
                    )
                )
            } else {
                // No data and no errors, can happen if no items match the predicate
                cont.resume(emptyList())
            }
        },
        { error ->
            cont.resumeWithException(error)
        }
    )
}
