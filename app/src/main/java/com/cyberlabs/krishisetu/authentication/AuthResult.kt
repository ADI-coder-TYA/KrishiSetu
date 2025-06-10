package com.cyberlabs.krishisetu.authentication

import com.amplifyframework.auth.AuthException

sealed class AuthResult {
    data class Success(val complete: Boolean): AuthResult()
    data class Failure(val exception: AuthException): AuthResult()
}