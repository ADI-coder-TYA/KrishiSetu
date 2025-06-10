package com.cyberlabs.krishisetu.ui.screens.crops

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.cyberlabs.krishisetu.R
import com.cyberlabs.krishisetu.crops.CropViewModel
import com.cyberlabs.krishisetu.util.navigation.FarmerBottomBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CropsScreen(
    navController: NavController,
    viewModel: CropViewModel = hiltViewModel()
) {
    val crops by viewModel.crops
    val isLoading by viewModel.isLoading
    val errorMsg by viewModel.errorMsg

    Scaffold(
        topBar = {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.elevatedCardElevation(
                    defaultElevation = 4.dp
                ),
                shape = RectangleShape
            ) {
                CenterAlignedTopAppBar(
                    title = {
                        Row(
                            modifier = Modifier.fillMaxHeight(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.app_logo),
                                contentDescription = "Logo",
                                modifier = Modifier.clip(CircleShape)
                            )
                            Text(
                                text = "कृषिसेतु",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = {
                                //TODO: Navigate to profile
                            }
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_person_24),
                                contentDescription = "Profile"
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                //TODO: Navigate to settings
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings"
                            )
                        }
                    }
                )
            }
        },
        bottomBar = {
            FarmerBottomBar(navController, 2)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Spacer(Modifier.height(24.dp))
            Button(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                contentPadding = PaddingValues(16.dp),
                colors = ButtonDefaults.buttonColors().copy(
                    containerColor = Color(0xFF2C6F30)
                ),
                onClick = {
                    navController.navigate("cropUpload")
                }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        tint = Color.White,
                        contentDescription = null
                    )
                    Text(
                        text = "Upload Crop",
                        color = Color.White
                    )
                }
            }
            Text(
                text = "My Crops",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.padding(16.dp)
            )
            when {
                isLoading -> {
                    Box(modifier = Modifier.fillMaxSize()) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                }

                errorMsg != null -> {
                    Text("Error: $errorMsg", color = Color.Red)
                }

                else -> {
                    LazyColumn {
                        crops.chunked(2).forEach { chunk ->
                            item {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp)
                                ) {
                                    chunk.forEach { crop ->
                                        CropCard(
                                            modifier = Modifier.weight(1f).clickable(
                                                onClick = {
                                                    navController.navigate("cropShop/${crop.cropId}")
                                                }
                                            ),
                                            cropName = crop.title,
                                            cropDescription = crop.description,
                                            pricePerKg = crop.price.toString(),
                                            quantity = crop.quantityAvailable.toString(),
                                            location = crop.location,
                                            imageUrl = crop.imageUrl
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun CropCard(
    modifier: Modifier = Modifier,
    cropName: String = "Alphonso Mango",
    cropDescription: String = "Sweet-flavoured organically grown alphonso mangoes, rich in colour and taste.",
    pricePerKg: String = "100",
    quantity: String = "20",
    location: String = "2nd Cross, 2nd Stage, Jeevanahalli, Bengaluru, Karnataka, 560005",
    imageUrl: String = ""
) {
    ElevatedCard(
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.elevatedCardElevation(4.dp),
        modifier = modifier
            .fillMaxWidth()
    ) {
        Column {
            AsyncImage(
                model = imageUrl,
                contentDescription = "Crop Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
                placeholder = painterResource(R.drawable.app_logo), // Optional fallback
                error = painterResource(R.drawable.baseline_crop_24)
            )
            /*Image(
                painter = painterResource(R.drawable.app_logo),
                contentDescription = "Crop Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f),
                contentScale = ContentScale.Crop
            )*/
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = cropName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF2E7D32)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = cropDescription,
                    fontSize = 12.sp,
                    color = Color.DarkGray,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    InfoWithIcon(icon = Icons.Default.Favorite, label = "₹$pricePerKg/kg")
                    InfoWithIcon(icon = Icons.Default.Info, label = "$quantity kg")
                }

                Spacer(modifier = Modifier.height(8.dp))

                InfoWithIcon(
                    icon = Icons.Default.LocationOn,
                    label = location,
                    modifier = Modifier.align(Alignment.End)
                )

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun InfoWithIcon(modifier: Modifier = Modifier, icon: ImageVector, label: String) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(8.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            fontSize = 10.sp,
            color = Color.Gray
        )
    }
}

@Preview
@Composable
fun PreviewCropCard() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        CropCard(modifier = Modifier.weight(1f))
        Spacer(Modifier.width(6.dp))
        CropCard(modifier = Modifier.weight(1f))
    }
}
