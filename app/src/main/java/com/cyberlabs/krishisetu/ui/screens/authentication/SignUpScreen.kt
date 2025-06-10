package com.cyberlabs.krishisetu.ui.screens.authentication

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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.amplifyframework.datastore.generated.model.UserRole
import com.cyberlabs.krishisetu.R
import com.cyberlabs.krishisetu.authentication.AuthViewModel

@Composable
fun SignUpScreen(vm: AuthViewModel, onNext: () -> Unit, onSignIn: () -> Unit) {
    val allFieldFilled = listOf(
        vm.userState.email,
        vm.userState.name,
        vm.userState.phone,
        vm.userState.role,
        vm.password
    ).all { it.toString().isNotBlank() }

    Scaffold(
        containerColor = Color.White,
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = innerPadding.calculateTopPadding()),
                verticalArrangement = Arrangement.spacedBy(8.dp),
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
                Text(
                    text = "Sign Up",
                    style = MaterialTheme.typography.headlineMedium
                )
                OutlinedTextField(
                    value = vm.userState.name,
                    onValueChange = { vm.updateName(it) },
                    label = { Text("Full Name") },
                    colors = OutlinedTextFieldDefaults.colors().copy(
                        focusedLabelColor = Color.Black,
                        focusedIndicatorColor = Color.Black,
                        cursorColor = Color.Black,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black
                    )
                )
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
                    value = vm.password,
                    onValueChange = { vm.password = it },
                    label = { Text("Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    colors = OutlinedTextFieldDefaults.colors().copy(
                        focusedLabelColor = Color.Black,
                        focusedIndicatorColor = Color.Black,
                        cursorColor = Color.Black,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black
                    )
                )
                vm.errorMsg?.let { Text(it, color = Color.Red) }
                OutlinedTextField(
                    value = vm.userState.phone,
                    onValueChange = { vm.updatePhone(it) },
                    label = { Text("Phone Number") },
                    colors = OutlinedTextFieldDefaults.colors().copy(
                        focusedLabelColor = Color.Black,
                        focusedIndicatorColor = Color.Black,
                        cursorColor = Color.Black,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black
                    )
                )
                RoleButtons { selectedOption ->
                    vm.updateRole(
                        when (selectedOption) {
                            "Buyer" -> UserRole.BUYER
                            "Delivery Agent" -> UserRole.DELIVERY_AGENT
                            else -> UserRole.FARMER
                        }
                    )
                }
                Button(
                    onClick = {
                        vm.signUp();
                        if (vm.errorMsg == null) {
                            onNext()
                        }
                    },
                    enabled = !vm.isLoading && allFieldFilled,
                    colors = ButtonDefaults.buttonColors().copy(
                        containerColor = Color.Black,
                        contentColor = Color.White,
                        disabledContainerColor = Color.Gray,
                        disabledContentColor = Color.DarkGray
                    )
                ) {
                    if (vm.isLoading) CircularProgressIndicator(Modifier.size(24.dp)) else Text("Next")
                }
                TextButton(
                    onClick = { onSignIn() }
                ) {
                    Text(
                        color = MaterialTheme.colorScheme.inversePrimary,
                        text = "Already have an account? Sign In"
                    )
                }
            }
        }
    }
}

@Composable
fun RoleButtons(onSelected: (String) -> Unit) {
    val radioList = listOf("Farmer", "Buyer", "Delivery Agent")
    val selectedOption = remember { mutableStateOf(radioList[0]) }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        radioList.forEach { text ->
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    colors = RadioButtonDefaults.colors().copy(
                        selectedColor = Color(0xFFC2993A),
                        unselectedColor = Color.Black
                    ),
                    selected = text == selectedOption.value,
                    onClick = {
                        selectedOption.value = text
                        onSelected(selectedOption.value)
                    }
                )
                Text(text)
            }
        }
    }
}