package com.cyberlabs.krishisetu.ui.screens.authentication

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.amplifyframework.core.Amplify
import com.cyberlabs.krishisetu.R
import com.cyberlabs.krishisetu.authentication.AuthViewModel

@Composable
fun SignInScreen(vm: AuthViewModel, onSignedIn: () -> Unit, onSignUp: () -> Unit) {

    var userLogged by remember { mutableStateOf<Boolean?>(null) } // null = loading

    LaunchedEffect(Unit) {
        Amplify.Auth.getCurrentUser(
            { user ->
                Log.i("MainActivity", "Logged in as ${user.username}")
                userLogged = true
            },
            { error ->
                Log.e("MainActivity", "No logged-in user: $error")
                userLogged = false
            }
        )
    }

    when (userLogged) {
        null -> {
            // Loading UI
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        true -> {
            // Already logged in
            LaunchedEffect(Unit) {
                onSignedIn()
            }
        }

        false -> {
            Scaffold(
                containerColor = Color.White
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(R.drawable.app_logo),
                        contentDescription = "App Logo",
                        modifier = Modifier
                            .padding(12.dp)
                            .size(128.dp)
                            .clip(CircleShape)
                    )
                    Column(
                        Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Sign In", style = MaterialTheme.typography.headlineMedium)
                        OutlinedTextField(
                            value = vm.userState.email,
                            onValueChange = { vm.updateEmail(it) },
                            label = { Text("Email") },
                            colors = OutlinedTextFieldDefaults.colors().copy(
                                focusedLabelColor = Color.Black,
                                focusedIndicatorColor = Color.Black,
                                cursorColor = Color.Black,
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black
                            )
                        )
                        OutlinedTextField(
                            vm.password,
                            { vm.password = it },
                            label = { Text("Password") },
                            colors = OutlinedTextFieldDefaults.colors().copy(
                                focusedLabelColor = Color.Black,
                                focusedIndicatorColor = Color.Black,
                                cursorColor = Color.Black,
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black
                            ),
                            visualTransformation = PasswordVisualTransformation()
                        )
                        vm.errorMsg?.let { Text(it, color = Color.Red) }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = {
                                    vm.signIn(onSignedIn)
                                },
                                enabled = !vm.isLoading
                            ) {
                                if (vm.isLoading) CircularProgressIndicator(Modifier.size(24.dp)) else Text(
                                    "Sign In"
                                )
                            }
                        }
                    }
                    TextButton(
                        onClick = { onSignUp() }
                    ) {
                        Text(
                            color = MaterialTheme.colorScheme.inversePrimary,
                            text = "Do not have an account? Sign Up"
                        )
                    }
                }
            }
        }
    }
}
