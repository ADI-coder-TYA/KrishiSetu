package com.cyberlabs.krishisetu.authentication

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amplifyframework.api.graphql.model.ModelMutation
import com.amplifyframework.auth.AuthUserAttribute
import com.amplifyframework.core.Amplify
import com.amplifyframework.core.model.temporal.Temporal
import com.amplifyframework.datastore.generated.model.User
import com.amplifyframework.datastore.generated.model.UserRole
import com.cyberlabs.krishisetu.states.UserState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.OffsetDateTime
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repo: AuthRepository
) : ViewModel() {

    private var _userState by mutableStateOf<UserState>(UserState())
    val userState: UserState
        get() = _userState
    private val _userAttributes = MutableStateFlow<List<AuthUserAttribute>?>(null)
    val userAttributes: StateFlow<List<AuthUserAttribute>?> = _userAttributes

    init {
        fetchUserAttributes()
    }

    fun updateEmail(email: String) {
        _userState = _userState.copy(email = email.lowercase())
    }

    fun updateName(name: String) {
        _userState = _userState.copy(name = name)
    }

    fun updatePhone(phone: String) {
        _userState = _userState.copy(phone = phone)
    }

    fun updateRole(role: UserRole) {
        _userState = _userState.copy(role = role)
    }

    var password by mutableStateOf("")
    var code by mutableStateOf("")

    var isLoading by mutableStateOf(false)
    var errorMsg by mutableStateOf<String?>(null)
    var authComplete by mutableStateOf(false)

    fun signUp() {
        viewModelScope.launch {
            isLoading = true
            errorMsg = null

            val res = userState.let {
                repo.signUp(
                    userState = it,
                    password = password
                )
            }

            res.let {
                when (it) {
                    is AuthResult.Success -> authComplete = it.complete
                    is AuthResult.Failure -> errorMsg = it.exception.message
                }
            }
            isLoading = false
        }
    }

    fun confirmSignUp(onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            errorMsg = null

            when (val res = repo.confirmSignUp(userState.email, code)) {
                is AuthResult.Success -> {
                    authComplete = res.complete
                    if (authComplete) {
                        // 👇 Sign in the user to get Cognito `sub`
                        when (val signInRes = repo.signIn(userState.email, password)) {
                            is AuthResult.Success -> {
                                if (signInRes.complete) {
                                    fetchUserAttributes()
                                    onSuccess()
                                    // ✅ Now safely get the Cognito userId (sub)
                                    Amplify.Auth.getCurrentUser({
                                        val cognitoId = it.userId

                                        val newUser = userState.let {
                                            User.builder()
                                                .name(it.name)
                                                .email(it.email)
                                                .ownerId(cognitoId)
                                                .role(it.role)
                                                .phone("+91${it.phone}")
                                                .id(cognitoId)
                                                .build()
                                        }

                                        newUser?.let {
                                            Amplify.API.mutate(
                                                ModelMutation.create(it),
                                                { response ->
                                                    if (response.hasErrors()) {
                                                        Log.e("AuthVM", "❌ Mutation error: ${response.errors.first().message}")
                                                    } else {
                                                        Log.i("AuthVM", "✅ Mutation succeeded: ${response.data}")
                                                    }
                                                },
                                                { err -> Log.e("AuthVM", "User Save Failed", err) }
                                            )
                                        }

                                    }) {
                                        Log.e("AuthVM", "Failed to get user", it)
                                    }
                                }
                            }

                            is AuthResult.Failure -> {
                                errorMsg = signInRes.exception.message
                            }
                        }
                    }
                }

                is AuthResult.Failure -> {
                    errorMsg = res.exception.message
                }
            }

            isLoading = false
        }
    }


    fun signIn(onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading = true; errorMsg = null
            when (val res = repo.signIn(userState.email, password)) {
                is AuthResult.Success -> {
                    authComplete = res.complete
                    if (authComplete) {
                        fetchUserAttributes()
                        onSuccess() // 🎯 callback fired only when successful
                    }
                }

                is AuthResult.Failure -> errorMsg = res.exception.message
            }
            isLoading = false
        }
    }

    fun signOut() {
        viewModelScope.launch {
            isLoading = true; errorMsg = null
            when (val res = repo.signOut()) {
                is AuthResult.Success -> authComplete = res.complete
                is AuthResult.Failure -> errorMsg = res.exception.message
            }
            isLoading = false
        }
    }

    fun fetchUserAttributes() {
        viewModelScope.launch {
            val res = repo.fetchUserAttributes()
            _userAttributes.value = res.getOrNull()
        }
    }
}
