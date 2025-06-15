package com.cyberlabs.krishisetu.ui.screens.crops

import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil3.compose.AsyncImage
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.cyberlabs.krishisetu.R
import com.cyberlabs.krishisetu.crops.CropUploadViewModel
import com.cyberlabs.krishisetu.util.navigation.TopBar
import kotlinx.coroutines.delay
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
    val (showSuccessAnim, setShowSuccessAnim) = remember { mutableStateOf(false) }

    val touchedFields = remember { mutableStateMapOf<String, Boolean>() }
    val uploadState = viewModel.uploadState
    val allFieldsFilled = listOf(
        uploadState.title, uploadState.description,
        uploadState.price, uploadState.quantity, uploadState.location
    ).all { it.isNotBlank() }

    val pickMedia = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let {
            setSelectedImageUri(it)
            context.contentResolver.openInputStream(it)?.let { stream ->
                val ext = MimeTypeMap.getSingleton()
                    .getExtensionFromMimeType(context.contentResolver.getType(it)) ?: "jpg"
                setInputStream(stream)
                setFileExt(".$ext")
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.uploadSuccessful.collect { success ->
            setShowSuccessAnim(true)
            delay(2000)
            setShowSuccessAnim(false)
            navController.navigateUp()
        }
    }

    Scaffold(
        topBar = { TopBar("Upload Crop", navController, true) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    pickMedia.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                containerColor = Color(0xFF2C6F30),
                contentColor = Color.White,
                shape = CircleShape,
                elevation = FloatingActionButtonDefaults.elevation(6.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.outline_add_photo_alternate_24),
                    contentDescription = "Pick Image"
                )
            }
        }
    ) { innerPadding ->

        Box(modifier = Modifier.fillMaxSize()) {

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 20.dp, vertical = 10.dp)
                    .imePadding(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                fun inputField(
                    key: String,
                    value: String,
                    onValueChange: (String) -> Unit,
                    label: String,
                    isNumeric: Boolean = false
                ) {
                    val isTouched = touchedFields[key] == true
                    val showError = isTouched && value.isBlank()

                    item {
                        Column {
                            OutlinedTextField(
                                value = value,
                                onValueChange = {
                                    if (!isNumeric || it.all(Char::isDigit)) {
                                        onValueChange(it)
                                        touchedFields[key] = true
                                    }
                                },
                                label = {
                                    Row {
                                        Text(label)
                                        Text("*", color = Color.Red)
                                    }
                                },
                                singleLine = true,
                                isError = showError,
                                modifier = Modifier.fillMaxWidth(),
                                colors = TextFieldDefaults.colors().copy(
                                    focusedIndicatorColor = Color(0xFF2C6F30),
                                    focusedLabelColor = Color(0xFF2C6F30),
                                    cursorColor = Color(0xFF2C6F30)
                                )
                            )
                            if (showError) {
                                Text(
                                    text = "$label is required",
                                    color = Color.Red,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                                )
                            }
                        }
                    }
                }

                inputField("title", uploadState.title, viewModel::updateTitle, "Crop Title")
                inputField(
                    "description",
                    uploadState.description,
                    viewModel::updateDescription,
                    "Description"
                )
                inputField("price", uploadState.price, viewModel::updatePrice, "Price per kg", true)
                inputField(
                    "quantity",
                    uploadState.quantity,
                    viewModel::updateQuantity,
                    "Quantity available",
                    true
                )
                inputField("location", uploadState.location, viewModel::updateLocation, "Location")

                selectedImageUri?.let { uri ->
                    item {
                        AsyncImage(
                            model = uri,
                            contentDescription = "Selected Crop Image",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(16.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                item {
                    Button(
                        onClick = {
                            inputStream?.let {
                                viewModel.uploadCrop(it, fileExtension)
                            }
                        },
                        enabled = allFieldsFilled && inputStream != null && viewModel.currentUserId != null,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2C6F30),
                            disabledContainerColor = Color.LightGray
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                    ) {
                        Icon(Icons.Default.Done, contentDescription = null, tint = Color.White)
                        Spacer(Modifier.width(8.dp))
                        Text("Submit Crop", color = Color.White, fontWeight = FontWeight.Medium)
                    }
                }
            }

            if (showSuccessAnim) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.6f))
                        .clickable(enabled = false) {},
                    contentAlignment = Alignment.Center
                ) {
                    LottieAnimation(
                        composition = rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.success_anim)).value,
                        iterations = 1,
                        modifier = Modifier.size(200.dp)
                    )
                }
            }
        }
    }
}
