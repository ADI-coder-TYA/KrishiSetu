package com.cyberlabs.krishisetu.ui.screens.crops

import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil3.compose.AsyncImage
import com.cyberlabs.krishisetu.crops.CropUploadViewModel
import java.io.InputStream

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun CropUploadScreen(
    viewModel: CropUploadViewModel = hiltViewModel(),
    navController: NavController = rememberNavController()
) {
    val context = LocalContext.current
    val (selectedImageUri, setSelectedImageUri) = remember { mutableStateOf<Uri?>(null) }
    val (inputStream, setInputStream) = remember { mutableStateOf<InputStream?>(null) }
    val (fileExtension, setFileExt) = remember { mutableStateOf(".jpg") }

    val allFieldsField =
        listOf(
            viewModel.uploadState.title,
            viewModel.uploadState.description,
            viewModel.uploadState.price,
            viewModel.uploadState.quantity,
            viewModel.uploadState.location
        ).all { it.isNotBlank() }

    val pickMedia = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
    ) { uri ->
        uri?.let {
            setSelectedImageUri(it)
            context.contentResolver.openInputStream(uri)?.let { stream ->
                val extension = MimeTypeMap.getSingleton()
                    .getExtensionFromMimeType(context.contentResolver.getType(uri)) ?: "jpg"
                setInputStream(stream)
                setFileExt(".$extension")
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.uploadSuccessful.collect {
            setInputStream(null)
            setFileExt(".jpg")
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Upload Crop"
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.navigateUp()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                OutlinedTextField(value = viewModel.uploadState.title, onValueChange = {
                    viewModel.updateTitle(it)
                }, label = { Text("Crop Title") })
                Spacer(Modifier.height(12.dp))
            }
            item {
                OutlinedTextField(value = viewModel.uploadState.description, onValueChange = {
                    viewModel.updateDescription(it)
                }, label = { Text("Description") })
                Spacer(Modifier.height(12.dp))
            }
            item {
                OutlinedTextField(value = viewModel.uploadState.price, onValueChange = {
                    viewModel.updatePrice(it)
                }, label = { Text("Price per unit") })
                Spacer(Modifier.height(12.dp))
            }
            item {
                OutlinedTextField(value = viewModel.uploadState.quantity, onValueChange = {
                    viewModel.updateQuantity(it)
                }, label = { Text("Quantity available") })
                Spacer(Modifier.height(12.dp))
            }
            item {
                OutlinedTextField(value = viewModel.uploadState.location, onValueChange = {
                    viewModel.updateLocation(it)
                }, label = { Text("Location") })
                Spacer(Modifier.height(12.dp))
            }
            item {
                Button(onClick = {
                    pickMedia
                        .launch(
                            PickVisualMediaRequest(
                                ActivityResultContracts.PickVisualMedia.ImageOnly
                            )
                        )
                }) {
                    Text("Upload Image")
                }
                Spacer(Modifier.height(16.dp))
            }
            item {
                selectedImageUri?.let { uri ->
                    AsyncImage(
                        model = uri,
                        contentDescription = "Selected Crop Image",
                        modifier = Modifier
                            .size(200.dp),
                        contentScale = ContentScale.Crop
                    )
                }
                Spacer(Modifier.height(16.dp))
            }
            item {
                Button(
                    enabled = viewModel.currentUserId != null
                            &&
                            allFieldsField
                            &&
                            inputStream != null,
                    onClick = {
                        inputStream?.let {
                            viewModel.uploadCrop(it, fileExtension)
                        }
                    }) {
                    Text("Submit Crop")
                }
            }
        }
    }
}
