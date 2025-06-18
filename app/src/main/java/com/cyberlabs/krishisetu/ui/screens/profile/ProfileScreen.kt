package com.cyberlabs.krishisetu.ui.screens.profile

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.cyberlabs.krishisetu.R
import com.cyberlabs.krishisetu.authentication.AuthViewModel
import com.cyberlabs.krishisetu.profile.ProfileViewModel
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel(),
    navController: NavController,
    onImagePicked: (File) -> Unit = {}
) {
    val context = LocalContext.current
    val user by viewModel.user
    val profilePicUrl by viewModel.profilePicUrl.collectAsState()
    var isEditingPhone by remember { mutableStateOf(false) }
    var phoneInput by remember { mutableStateOf(user?.phone ?: "") }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let {
                val file = uriToFile(context, it)
                if (file != null && user?.id != null) {
                    onImagePicked(file)
                    viewModel.uploadAndSetProfilePic(file, user!!.id)
                }
            }
        }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(24.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(16.dp))

            // Profile Picture
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(128.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray.copy(alpha = 0.2f))
                    .clickable { imagePickerLauncher.launch("image/*") }
            ) {
                AsyncImage(
                    model = profilePicUrl,
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(R.drawable.baseline_account_circle_24)
                )
            }

            Spacer(Modifier.height(12.dp))

            Text(
                text = user?.name ?: "Unnamed",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = user?.role?.name?.lowercase()?.replace('_', ' ', true)?.capitalize() ?: "Unknown Role",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )

            Spacer(Modifier.height(16.dp))

            ProfileField(label = "Email", value = user?.email ?: "No email")

            Spacer(Modifier.height(8.dp))

            if (isEditingPhone) {
                OutlinedTextField(
                    value = phoneInput,
                    onValueChange = { phoneInput = it },
                    label = { Text("Phone") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = {
                        isEditingPhone = false
                        phoneInput = user?.phone ?: ""
                    }) { Text("Cancel") }

                    Button(onClick = {
                        viewModel.updatePhone(phoneInput)
                        isEditingPhone = false
                    }) { Text("Save") }
                }
            } else {
                ProfileField(label = "Phone", value = user?.phone ?: "No phone")
                Spacer(Modifier.height(4.dp))
                TextButton(onClick = { isEditingPhone = true }) {
                    Text("Edit Phone", style = MaterialTheme.typography.bodySmall)
                }
            }

            Spacer(Modifier.weight(1f))

            OutlinedButton(
                onClick = {
                    authViewModel.signOut()
                    navController.navigate("signIn") { popUpTo("profile") { inclusive = true } }
                },
                border = BorderStroke(1.dp, Color.Red),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Text("Sign Out", color = Color.Red)
            }
        }
    }
}

@Composable
fun ProfileField(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
        Text(text = value, style = MaterialTheme.typography.bodyMedium)
    }
}

private fun uriToFile(context: Context, uri: Uri): File? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val tempFile = File.createTempFile("temp_image", ".jpg", context.cacheDir)
        val outputStream = FileOutputStream(tempFile)

        inputStream?.use { input -> outputStream.use { input.copyTo(it) } }
        tempFile
    } catch (e: Exception) {
        Log.e("uriToFile", "Error converting URI to File", e)
        null
    }
}
