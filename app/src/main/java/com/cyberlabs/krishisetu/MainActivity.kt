package com.cyberlabs.krishisetu

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.amplifyframework.core.Amplify
import com.cyberlabs.krishisetu.authentication.AuthViewModel
import com.cyberlabs.krishisetu.ui.theme.KrishiSetuTheme
import com.cyberlabs.krishisetu.util.navigation.AppNavHost
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val vm by viewModels<AuthViewModel>()
    private val userLogged = MutableStateFlow<Boolean?>(null) // null = loading state

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        checkUserSession() // <-- Call early

        setContent {
            val isReady by MyApp.amplifyReady.collectAsState(initial = false)
            val isLoggedIn by userLogged.collectAsState()

            KrishiSetuTheme {
                if (!isReady || isLoggedIn == null) {
                    // Amplify not ready OR user session not determined
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    val startDestination = if (isLoggedIn == true) "home" else "signUp"
                    AppNavHost(vm, rememberNavController(), startDestination = startDestination)
                }
            }
        }
    }

    private fun checkUserSession() {
        Amplify.Auth.getCurrentUser(
            { user ->
                Log.i("MainActivity", "Logged in as ${user.username}")
                userLogged.value = true
            },
            { error ->
                Log.e("MainActivity", "No logged-in user: $error")
                userLogged.value = false
            }
        )
    }
}
