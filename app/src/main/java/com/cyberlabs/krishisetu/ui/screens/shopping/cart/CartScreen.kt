package com.cyberlabs.krishisetu.ui.screens.shopping.cart

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.cyberlabs.krishisetu.R
import com.cyberlabs.krishisetu.shopping.cart.CartViewModel
import com.cyberlabs.krishisetu.util.navigation.BuyerBottomBar
import com.cyberlabs.krishisetu.util.navigation.TopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    navController: NavController,
    vm: CartViewModel
) {
    val cartItems by vm.cartItems.collectAsState()
    val totalPrice by vm.totalPrice.collectAsState()
    val urls by vm.imageUrls.collectAsState()
    val isLoading by vm.isLoading
    val buyerId by vm.buyerId.collectAsState()

    // State for Payment Selection
    var selectedPaymentMode by remember { mutableStateOf("COD") } // "COD" or "ONLINE"

    Scaffold(
        topBar = { TopBar("Your Cart", navController) },
        bottomBar = { BuyerBottomBar(navController, 2) }
    ) { innerPadding ->
        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            }

            cartItems.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    Text(
                        "Your cart is empty",
                        Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    // Info Banner
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            tint = Color.Gray,
                            contentDescription = null
                        )
                        Text(
                            text = "Please confirm bargained prices with the farmer before placing the order.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }

                    // Cart Items List
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        items(cartItems.size) { index ->
                            val item = cartItems[index]
                            val imageUrl = urls[item.crop.id]
                            if (imageUrl == null) {
                                vm.loadImage(item.crop)
                            }
                            CartItemCard(
                                title = item.crop.title,
                                description = item.crop.description,
                                quantity = item.quantity,
                                price = item.priceAtAdd.toInt(),
                                onClick = { navController.navigate("cropShop/${item.crop.id}") },
                                imageUrl = imageUrl,
                                onIncrease = { vm.increaseQuantity(item) },
                                onDecrease = { vm.decreaseQuantity(item) },
                                onDelete = { vm.removeItemFromCart(item) },
                                location = item.crop.location
                            )
                        }
                    }

                    // Payment Mode Selection Section
                    PaymentSelectionCard(
                        selectedMode = selectedPaymentMode,
                        onModeSelected = { selectedPaymentMode = it }
                    )

                    // Checkout Button Section
                    TotalPriceCard(totalPrice) {
                        // Pass the payment mode to Checkout Screen
                        // Ensure your NavHost route accepts this argument: "checkout/{buyerId}/{paymentMode}"
                        navController.navigate("checkout/${buyerId}/$selectedPaymentMode")
                    }
                }
            }
        }
    }
}

@Composable
fun PaymentSelectionCard(
    selectedMode: String,
    onModeSelected: (String) -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Payment Method",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            // COD Option
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onModeSelected("COD") }
            ) {
                RadioButton(
                    selected = selectedMode == "COD",
                    onClick = { onModeSelected("COD") }
                )
                Text(text = "Cash on Delivery", style = MaterialTheme.typography.bodyMedium)
            }

            // Online Option
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onModeSelected("ONLINE") }
            ) {
                RadioButton(
                    selected = selectedMode == "ONLINE",
                    onClick = { onModeSelected("ONLINE") }
                )
                Text(
                    text = "Online Payment (Razorpay)",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

// ... (CartItemCard and TotalPriceCard remain mostly the same, standard imports apply)

@Composable
fun CartItemCard(
    title: String,
    description: String,
    quantity: Int,
    price: Int,
    imageUrl: String?,
    location: String,
    onClick: () -> Unit,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RectangleShape,
        colors = CardDefaults.cardColors(containerColor = Color.White, contentColor = Color.Black),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Spacer(Modifier.height(12.dp))
        Row(modifier = Modifier.padding(horizontal = 12.dp)) {
            Column {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = title,
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(id = R.drawable.app_logo),
                    error = painterResource(id = R.drawable.app_logo)
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onDecrease) {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, "Decrease")
                    }
                    Text(text = quantity.toString(), style = MaterialTheme.typography.bodyMedium)
                    IconButton(onClick = onIncrease) {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, "Increase")
                    }
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier
                .fillMaxWidth()
                .weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    IconButton(onClick = onDelete, modifier = Modifier.size(18.dp)) {
                        Icon(
                            Icons.Default.Delete,
                            tint = Color.DarkGray,
                            contentDescription = "Delete"
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Outlined.LocationOn,
                        "Location",
                        tint = Color.Gray,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = location,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = Color.Gray
                    )
                }
                Spacer(Modifier.height(28.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "₹${price * quantity}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF388E3C)
                    )
                }
            }
        }
    }
}

@Composable
private fun TotalPriceCard(totalPrice: Int, onCheckout: () -> Unit = {}) {
    ElevatedCard(
        shape = RectangleShape,
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Total Price: ",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "₹$totalPrice",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF388E3C)
                )
            }
            ElevatedButton(
                onClick = onCheckout,
                colors = ButtonDefaults.elevatedButtonColors()
                    .copy(containerColor = Color(0xFFF9631B), contentColor = Color.White)
            ) {
                Text(
                    text = "Checkout",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
