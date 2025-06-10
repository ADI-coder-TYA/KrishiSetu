package com.cyberlabs.krishisetu.ui.screens.authentication

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.cyberlabs.krishisetu.R
import com.cyberlabs.krishisetu.authentication.AuthViewModel

@Composable
fun SignInScreen(vm: AuthViewModel, onSignedIn: () -> Unit, onSignUp: () -> Unit) {
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
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
                Button(
                    onClick = {
                        vm.signIn(onSignedIn)
                    },
                    enabled = !vm.isLoading
                ) {
                    if (vm.isLoading) CircularProgressIndicator(Modifier.size(24.dp)) else Text("Sign In")
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