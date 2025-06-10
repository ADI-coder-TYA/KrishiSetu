package com.cyberlabs.krishisetu.authentication

import com.amplifyframework.auth.AuthUserAttribute
import com.cyberlabs.krishisetu.states.UserState

interface AuthRepository {
    suspend fun signUp(userState: UserState, password: String): AuthResult
    suspend fun confirmSignUp(email: String, code: String): AuthResult
    suspend fun signIn(email: String, password: String): AuthResult
    suspend fun signOut(): AuthResult
    suspend fun fetchUserAttributes(): Result<List<AuthUserAttribute>>
    suspend fun getCurrUserID(): String
}