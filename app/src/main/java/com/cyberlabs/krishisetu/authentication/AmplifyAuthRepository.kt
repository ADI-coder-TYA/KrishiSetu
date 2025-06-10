package com.cyberlabs.krishisetu.authentication

import android.util.Log
import com.amplifyframework.auth.AuthException
import com.amplifyframework.auth.AuthUserAttribute
import com.amplifyframework.auth.AuthUserAttributeKey
import com.amplifyframework.auth.options.AuthSignUpOptions
import com.amplifyframework.auth.result.AuthSignInResult
import com.amplifyframework.auth.result.AuthSignUpResult
import com.amplifyframework.core.Amplify
import com.cyberlabs.krishisetu.states.UserState
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class AmplifyAuthRepository @Inject constructor() : AuthRepository {

    /**
     * Sign up a new user in Cognito, storing name, email, phone, and role.
     */
    override suspend fun signUp(
        userState: UserState,
        password: String
    ): AuthResult = suspendCancellableCoroutine { cont ->
        val options = AuthSignUpOptions.builder()
            .userAttribute(AuthUserAttributeKey.name(), userState.name)
            .userAttribute(AuthUserAttributeKey.email(), userState.email)
            .userAttribute(AuthUserAttributeKey.phoneNumber(), "+91${userState.phone}")
            .userAttribute(
                AuthUserAttributeKey.custom("custom:role"),
                userState.role.name
            )
            .build()

        Amplify.Auth.signUp(
            userState.email,
            password,
            options,
            { result: AuthSignUpResult ->
                cont.resume(AuthResult.Success(result.isSignUpComplete))
            },
            { error: AuthException ->
                cont.resume(AuthResult.Failure(error))
            }
        )
    }

    /**
     * Confirm the sign-up code sent to the user.
     */
    override suspend fun confirmSignUp(
        email: String,
        code: String
    ): AuthResult = suspendCancellableCoroutine { cont ->
        Amplify.Auth.confirmSignUp(
            email,
            code,
            { result: AuthSignUpResult ->
                cont.resume(AuthResult.Success(result.isSignUpComplete))
            },
            { error: AuthException ->
                cont.resume(AuthResult.Failure(error))
            }
        )
    }

    /**
     * Sign in the user. Leaves attribute loading to the ViewModel.
     */
    override suspend fun signIn(
        email: String,
        password: String
    ): AuthResult = suspendCancellableCoroutine { cont ->
        Amplify.Auth.signIn(
            email,
            password,
            { result: AuthSignInResult ->
                cont.resume(AuthResult.Success(result.isSignedIn))
            },
            { error: AuthException ->
                cont.resume(AuthResult.Failure(error))
            }
        )
    }

    /**
     * Sign out the current user.
     */
    override suspend fun signOut(): AuthResult = suspendCancellableCoroutine { cont ->
        Amplify.Auth.signOut {
            cont.resume(AuthResult.Success(true))
        }
    }

    /**
     * Fetch all the Cognito attributes for the currently signed in user.
     * The ViewModel can then map these into a UserState subclass.
     */
    override suspend fun fetchUserAttributes(): Result<List<AuthUserAttribute>> =
        suspendCancellableCoroutine { cont ->
            Amplify.Auth.fetchUserAttributes(
                { attrs -> cont.resume(Result.success(attrs)) },
                { error -> cont.resume(Result.failure(error)) }
            )
        }

    override suspend fun getCurrUserID() = suspendCoroutine { continuation ->
        Amplify.Auth.getCurrentUser(
            {
                continuation.resume(it.userId)
            }, {
                Log.e("CropShopVM", "Failed to fetch user", it)
                continuation.resumeWithException(it)
            }
        )
    }
}
