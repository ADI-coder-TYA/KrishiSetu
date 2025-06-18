package com.cyberlabs.krishisetu.ui.screens.authentication

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.cyberlabs.krishisetu.authentication.AuthViewModel

@Composable
fun ConfirmScreen(vm: AuthViewModel, onNext: () -> Unit) {
    Scaffold(
        containerColor = Color.White
    ) { innerPadding ->
        Column(
            Modifier.padding(16.dp).padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Confirm Sign Up", style = MaterialTheme.typography.headlineMedium)
            OutlinedTextField(
                value = vm.code,
                onValueChange = { vm.code = it },
                label = { Text("Confirmation Code") }
            )
            vm.errorMsg?.let { Text(it, color = Color.Red) }
            Button(onClick = { vm.confirmSignUp { onNext() } }, enabled = !vm.isLoading) {
                if (vm.isLoading) CircularProgressIndicator(Modifier.size(24.dp)) else Text("Confirm")
            }
        }
    }
}