package com.cyberlabs.krishisetu.ui.screens.crops

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.cyberlabs.krishisetu.R
import com.cyberlabs.krishisetu.crops.CropViewModel
import com.cyberlabs.krishisetu.util.navigation.FarmerBottomBar
import com.cyberlabs.krishisetu.util.navigation.TopBar

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
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("cropUpload") },
                containerColor = Color(0xFF2C6F30),
                contentColor = Color.White,
                shape = CircleShape,
                elevation = FloatingActionButtonDefaults.elevation(8.dp),
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Upload Crop")
            }
        },
        topBar = {
            TopBar("Your Crops", navController)
        },
        bottomBar = {
            FarmerBottomBar(navController, 2)
        },
        containerColor = Color(0xFFF9F9F9)
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            when {
                isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 48.dp)
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFF2C6F30),
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }

                errorMsg != null -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp)
                    ) {
                        Text(
                            text = "⚠️ $errorMsg",
                            color = Color.Red,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }

                crops.isEmpty() -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp)
                    ) {
                        Text(
                            text = "🌱 You haven’t uploaded any crops yet.\nTap + to get started!",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            fontStyle = FontStyle.Italic,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }

                else -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(crops.size) { index ->
                            val crop = crops[index]
                            CropCard(
                                modifier = Modifier
                                    .clickable {
                                        navController.navigate("cropShop/${crop.cropId}")
                                    },
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
        colors = CardDefaults.elevatedCardColors().copy(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.elevatedCardElevation(6.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        Column(modifier = Modifier.background(Color.White)) {
            AsyncImage(
                model = imageUrl,
                contentDescription = "Crop Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
                placeholder = painterResource(R.drawable.app_logo),
                error = painterResource(R.drawable.baseline_crop_24)
            )

            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = cropName,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                    color = Color(0xFF2E7D32),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = cropDescription,
                    fontSize = 13.sp,
                    color = Color(0xFF444444),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    InfoWithIcon(
                        icon = Icons.Default.ShoppingCart,
                        label = "₹$pricePerKg/kg",
                        tint = Color(0xFF388E3C),
                        fontSize = 12.sp
                    )
                    InfoWithIcon(
                        icon = Icons.Default.Info,
                        label = "$quantity kg",
                        tint = Color(0xFF1976D2),
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                InfoWithIcon(
                    icon = Icons.Default.LocationOn,
                    label = location,
                    tint = Color(0xFF6A1B9A),
                    fontSize = 11.sp,
                    maxLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun InfoWithIcon(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    tint: Color = Color.Gray,
    fontSize: TextUnit = 11.sp,
    maxLines: Int = 1
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier
                .size(16.dp)
                .padding(end = 4.dp)
        )
        Text(
            text = label,
            maxLines = maxLines,
            overflow = TextOverflow.Ellipsis,
            fontSize = fontSize,
            color = tint,
            lineHeight = fontSize
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
