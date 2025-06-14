package com.cyberlabs.krishisetu.ui.screens.shopping.cropListing

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.amplifyframework.datastore.generated.model.UserRole
import com.cyberlabs.krishisetu.R
import com.cyberlabs.krishisetu.authentication.AuthViewModel
import com.cyberlabs.krishisetu.shopping.cart.CartViewModel
import com.cyberlabs.krishisetu.shopping.cropListing.cropShop.CropShopViewModel
import com.cyberlabs.krishisetu.util.users.userRoleFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CropShopScreen(
    vm: CropShopViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel(),
    cartViewModel: CartViewModel = hiltViewModel(),
    navController: NavController
) {
    var selectedIndex by remember { mutableIntStateOf(0) }
    val tabItems = listOf(
        "About the Crop", "Farm Details", "Farmer Info", "Variable Weight"
    )

    val userRole by userRoleFlow(authViewModel).collectAsState(initial = null)

    Scaffold(
        topBar = {
            TopAppBar(
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
                },
                title = {},
            )
        }
    ) { innerPadding ->
        when {
            vm.isLoading.value -> {
                Box(modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            }

            vm.errorMsg.value != null -> {
                Text(
                    text = vm.errorMsg.value!!,
                    modifier = Modifier.padding(innerPadding),
                    color = Color.Red
                )
            }

            else -> {
                vm.cropData.value?.let { cropData ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        LazyColumn(
                            modifier = Modifier.weight(1f)
                        ) {
                            item {
                                Text(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    text = cropData.title,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                )
                            }
                            item {
                                AsyncImage(
                                    model = cropData.imageUrl,
                                    contentDescription = "Crop Image",
                                    modifier = Modifier
                                        .padding(horizontal = 16.dp, vertical = 8.dp)
                                        .fillMaxWidth()
                                        .height(350.dp)
                                        .clip(RoundedCornerShape(20.dp)),
                                    contentScale = ContentScale.Crop,
                                    placeholder = painterResource(R.drawable.app_logo), // Optional fallback
                                    error = painterResource(R.drawable.baseline_crop_24)
                                )
                                Spacer(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(4.dp)
                                        .background(Color(0xFFEBEBEB))
                                )
                            }
                            item {
                                Column(
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(
                                            horizontal = 16.dp,
                                            vertical = 8.dp
                                        )
                                    ) {
                                        Text(
                                            color = Color.Gray,
                                            fontWeight = FontWeight.SemiBold,
                                            text = "Quantity Available: ",
                                            fontSize = 18.sp
                                        )
                                        Text(
                                            text = "${cropData.quantityAvailable} kg",
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 18.sp,
                                        )
                                    }
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                                        verticalAlignment = Alignment.Bottom,
                                        modifier = Modifier.padding(
                                            horizontal = 16.dp,
                                            vertical = 8.dp
                                        )
                                    ) {
                                        Text(
                                            text = "₹${cropData.price.toInt()}/kg",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 28.sp
                                        )
                                        Text(
                                            text = "(negotiable)",
                                            fontSize = 14.sp
                                        )
                                    }
                                    Spacer(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(4.dp)
                                            .background(Color(0XFFEBEBEB))
                                    )
                                }
                            }
                            item {
                                ScrollableTabRow(
                                    edgePadding = 16.dp,
                                    selectedTabIndex = selectedIndex,
                                    indicator = { tabPositions ->
                                        TabRowDefaults.SecondaryIndicator(
                                            color = Color(0xFF2E7D32),
                                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedIndex])
                                        )
                                    }
                                ) {
                                    tabItems.forEachIndexed { index, title ->
                                        Tab(
                                            selected = index == selectedIndex,
                                            onClick = { selectedIndex = index },
                                            selectedContentColor = Color(0xFF2E7D32),
                                            unselectedContentColor = Color.Black,

                                            ) {
                                            Text(
                                                fontWeight = FontWeight.SemiBold,
                                                text = title,
                                                modifier = Modifier.padding(16.dp),
                                                fontSize = 18.sp
                                            )
                                        }
                                    }
                                }
                            }
                            item {
                                when (selectedIndex) {
                                    0 -> {
                                        Text(
                                            modifier = Modifier.padding(16.dp),
                                            text = cropData.description,
                                            color = Color(0xFF7F7F7F)
                                        )
                                    }

                                    1 -> {
                                        Text(
                                            modifier = Modifier.padding(16.dp),
                                            text = "Farm Location: ${cropData.location}",
                                            color = Color(0xFF7F7F7F)
                                        )
                                    }

                                    2 -> {
                                        Column(
                                            verticalArrangement = Arrangement.spacedBy(4.dp),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp)
                                        ) {
                                            Text(
                                                text = "Farmer Name: ${cropData.farmer.name}",
                                                color = Color(0xFF7F7F7F)
                                            )
                                            Text(
                                                text = "Email Address: ${cropData.farmer.email}",
                                                color = Color(0xFF7F7F7F)
                                            )
                                            Text(
                                                text = "Phone Number: ${cropData.farmer.phone}",
                                                color = Color(0xFF7F7F7F)
                                            )
                                        }
                                    }

                                    else -> {
                                        Text(
                                            modifier = Modifier.padding(16.dp),
                                            text = "Please note that the item(s) in this product may vary slightly in size and weight. Hence, the actual weight of the product delivered can have a small variance.",
                                            color = Color(0xFF7F7F7F)
                                        )
                                    }
                                }
                            }
                        }
                        if (userRole == UserRole.BUYER) {
                            Row(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Button(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f),
                                    onClick = {
                                        cartViewModel.buyerId.value?.let {
                                            cartViewModel.addItemToCart(it, cropData.cropId, 1, cropData.price)
                                        }
                                    }
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                    ) {
                                        Icon(
                                            painter = painterResource(R.drawable.outline_shopping_cart_24),
                                            contentDescription = "Cart"
                                        )
                                        Spacer(
                                            Modifier.width(8.dp)
                                        )
                                        Text(
                                            text = "Add to Cart",
                                            color = Color.Black,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp
                                        )
                                    }
                                }
                                Button(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f),
                                    onClick = {
                                        val farmerID = cropData.farmer.id
                                        vm.currUserId.value?.let {
                                            navController.navigate("chatList/${it}/$farmerID")
                                            Log.d("ChatViewModel", "Current user ID: $it")
                                            Log.d("ChatViewModel", "Chat partner ID: $farmerID")
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors().copy(
                                        containerColor = Color(0xFFFCD613)
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                    ) {
                                        Icon(
                                            painter = painterResource(R.drawable.rounded_chat_bubble_24),
                                            contentDescription = "Chat"
                                        )
                                        Spacer(
                                            Modifier.width(8.dp)
                                        )
                                        Text(
                                            text = "Talk to Farmer",
                                            color = Color.Black,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp
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
