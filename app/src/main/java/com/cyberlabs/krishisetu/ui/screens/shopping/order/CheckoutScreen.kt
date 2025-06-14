package com.cyberlabs.krishisetu.ui.screens.shopping.order

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.amplifyframework.datastore.generated.model.CartItem
import com.cyberlabs.krishisetu.R
import com.cyberlabs.krishisetu.shopping.cart.CartViewModel
import com.cyberlabs.krishisetu.shopping.order.CheckoutViewModel
import com.cyberlabs.krishisetu.util.users.userNameFlow

@Composable
fun CheckoutScreen(
    navController: NavController,
    buyerId: String, // Replace with actual user name source
    cartViewModel: CartViewModel,
    checkoutViewModel: CheckoutViewModel
) {
    val context = LocalContext.current
    val cartItems by cartViewModel.cartItems.collectAsState()
    val imageUrls by cartViewModel.imageUrls.collectAsState()
    val bargainInputs = remember { mutableStateMapOf<String, String>() }
    Log.d("CheckoutScreen", "cartItems: $cartItems")
    Log.d("CheckoutScreen", "imageUrls: $imageUrls")
    val userName by userNameFlow(buyerId).collectAsState(initial = null)

    // Address state
    var deliveryAddress by remember { mutableStateOf("") }
    var deliveryPincode by remember { mutableStateOf("") }
    var deliveryPhone by remember { mutableStateOf("") }

    // Bargained prices mapped to item IDs
    val bargainedPrices = remember { mutableStateMapOf<String, Int>() }

    // Compute totals using either bargained or actual price
    val totalPrice = cartItems.sumOf {
        val finalPrice = (bargainedPrices[it.crop.id]) ?: (it.crop.price.toInt().times(it.quantity))
        Log.d("CheckoutScreen", "it.quantity: ${it.quantity}")
        Log.d("CheckoutScreen", "crop: ${it.crop.title}")
        Log.d("CheckoutScreen", "finalPrice: $finalPrice")
        finalPrice
    }
    val totalQuantity = cartItems.sumOf { it.quantity }

    //Expansion states
    var isExpandedAddress by remember { mutableStateOf(true) }
    var isExpandedOrder by remember { mutableStateOf(false) }
    var isExpandedAmount by remember { mutableStateOf(true) }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
        ) {
            AddressCard(
                isExpanded = isExpandedAddress,
                onExpandedClick = {
                    isExpandedAddress = !isExpandedAddress
                    isExpandedOrder = false
                    isExpandedAmount = false
                },
                name = userName ?: "",
                onAddressChange = { address, pincode, phone ->
                    deliveryAddress = address
                    deliveryPincode = pincode
                    deliveryPhone = phone
                },
                initialDeliveryAddress = deliveryAddress,
                initialDeliveryPincode = deliveryPincode,
                initialDeliveryPhone = deliveryPhone
            )

            OrderSummaryCard(
                isExpanded = isExpandedOrder,
                onExpandedClick = {
                    isExpandedOrder = !isExpandedOrder
                    isExpandedAddress = false
                    isExpandedAmount = false
                },
                cartItems = cartItems,
                imageUrls = imageUrls,
                modifier = Modifier.padding(top = 4.dp),
                cartViewModel = cartViewModel,
                bargainInputs = bargainInputs
            ) { cropId, bargainPrice ->
                bargainedPrices[cropId] = bargainPrice
                bargainInputs[cropId] = bargainPrice.toString()
            }

            AmountCard(
                isExpanded = isExpandedAmount,
                onExpandedClick = {
                    isExpandedAmount = !isExpandedAmount
                    isExpandedAddress = false
                    isExpandedOrder = false
                },
                totalPrice = totalPrice,
                totalQuantity = totalQuantity
            )

            Spacer(Modifier.height(12.dp))

        }
        OrderButton(
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 12.dp)
        ) {
            if (deliveryAddress.isBlank() || deliveryPincode.isBlank() || deliveryPhone.isBlank()) {
                Toast.makeText(context, "Please complete your delivery address", Toast.LENGTH_SHORT)
                    .show()
                return@OrderButton
            }

            // Pair cart items with their final bargained price
            val finalOrder = cartItems.map {
                val price = bargainedPrices[it.crop.id] ?: (it.priceAtAdd * it.quantity).toInt()
                it to price
            }

            checkoutViewModel.placeOrder(
                finalOrder,
                deliveryAddress,
                deliveryPincode,
                deliveryPhone
            )
        }
    }
}

@Composable
fun OrderButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    ElevatedButton(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth(),
        onClick = onClick,
        colors = ButtonDefaults.elevatedButtonColors().copy(
            containerColor = Color(0xFFF9631B),
            contentColor = Color.White
        )
    ) {
        Text(
            text = "Place Order",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun AddressCard(
    isExpanded: Boolean = true,
    onExpandedClick: () -> Unit,
    name: String,
    modifier: Modifier = Modifier,
    initialDeliveryAddress: String,
    initialDeliveryPincode: String,
    initialDeliveryPhone: String,
    onAddressChange: (
        addressLine: String,
        pincode: String,
        phone: String
    ) -> Unit = { _, _, _ -> }
) {
    var address by rememberSaveable(initialDeliveryAddress) { mutableStateOf(initialDeliveryAddress) }
    var pincode by remember(initialDeliveryPincode) { mutableStateOf(initialDeliveryPincode) }
    var phone by rememberSaveable(initialDeliveryPhone) { mutableStateOf(initialDeliveryPhone) }

    var isPincodeError by remember { mutableStateOf(false) }
    var isPhoneError by remember { mutableStateOf(false) }

    ElevatedCard(
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {

            // Header Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onExpandedClick() }
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Delivery Address",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF424242)
                    )
                    Text(
                        text = name,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFF616161)
                    )
                }
                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (isExpanded) "Collapse" else "Expand"
                )
            }

            // Expandable Content
            AnimatedVisibility(visible = isExpanded) {
                Column {
                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = address,
                        onValueChange = {
                            address = it
                            onAddressChange(address, pincode, phone)
                        },
                        label = { Text("Address") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )

                    Spacer(Modifier.height(8.dp))

                    OutlinedTextField(
                        value = phone,
                        onValueChange = {
                            isPhoneError =
                                it.isNotEmpty() && !it.matches(Regex("^(\\+91)?[6-9][0-9]{9}$"))
                            phone = it
                            onAddressChange(address, pincode, phone)
                        },
                        label = { Text("Phone Number") },
                        isError = isPhoneError,
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.fillMaxWidth(0.75f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    if (isPhoneError) {
                        Text(
                            text = "Enter a valid phone number",
                            color = Color.Red,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 4.dp, top = 2.dp)
                        )
                    }

                    Spacer(Modifier.height(8.dp))

                    OutlinedTextField(
                        value = pincode,
                        onValueChange = {
                            isPincodeError =
                                it.isNotEmpty() && !it.matches(Regex("^[1-9][0-9]{5}$"))
                            pincode = it
                            onAddressChange(address, pincode, phone)
                        },
                        label = { Text("Pincode") },
                        isError = isPincodeError,
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(0.5f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    if (isPincodeError) {
                        Text(
                            text = "Enter a valid 6-digit pincode",
                            color = Color.Red,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 4.dp, top = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OrderSummaryCard(
    isExpanded: Boolean = false,
    onExpandedClick: () -> Unit,
    modifier: Modifier = Modifier,
    cartViewModel: CartViewModel,
    cartItems: List<CartItem>,
    imageUrls: Map<String, String?>,
    bargainInputs: Map<String, String>,
    onBargainPriceChange: (String, Int) -> Unit,
) {
    ElevatedCard(
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onExpandedClick() }
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Order Summary",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF424242)
                    )
                }
                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (isExpanded) "Collapse" else "Expand"
                )
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    cartItems.forEach { cartItem ->
                        val imageUrl = imageUrls[cartItem.crop.id]
                        if (imageUrl == null) {
                            cartViewModel.loadImage(cartItem.crop)
                        }
                        OrderSummaryItem(
                            title = cartItem.crop.title,
                            quantity = cartItem.quantity,
                            price = cartItem.priceAtAdd.toInt(),
                            imageUrl = imageUrl,
                            farmer = cartItem.crop.farmer.name,
                            initialInput = bargainInputs[cartItem.crop.id] ?: ""
                        ) { bargainPrice ->
                            onBargainPriceChange(cartItem.crop.id, bargainPrice)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OrderSummaryItem(
    title: String,
    quantity: Int,
    price: Int,
    imageUrl: String?,
    farmer: String,
    initialInput: String,
    onBargainPriceChange: (Int) -> Unit,
) {
    var bargainInput by rememberSaveable(initialInput) { mutableStateOf(initialInput) }
    var bargainError by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(modifier = Modifier.padding(12.dp)) {

            AsyncImage(
                model = imageUrl,
                contentDescription = title,
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(10.dp)),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.app_logo),
                error = painterResource(id = R.drawable.app_logo),
                onError = {
                    Log.e(
                        "OrderSummaryItem",
                        "Error loading image: ${it.result.throwable.cause?.message}",
                        it.result.throwable
                    )
                }
            )

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = Color(0xFF333333)
                )

                Spacer(Modifier.height(6.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Qty: ",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                    Box(
                        modifier = Modifier
                            .border(1.dp, Color.LightGray, RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "$quantity",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                Spacer(Modifier.height(6.dp))

                Text(
                    text = "Farmer: $farmer",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF757575)
                )

                Spacer(Modifier.height(10.dp))

                Text(
                    text = "Price: ₹${price * quantity}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF388E3C)
                )

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = bargainInput,
                    onValueChange = {
                        bargainInput = it
                        val parsed = it.toIntOrNull()
                        if (parsed != null && parsed > 0 && parsed <= price * quantity) {
                            bargainError = false
                            onBargainPriceChange(parsed)
                        } else {
                            bargainError = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Enter Bargained Price") },
                    placeholder = { Text("e.g. ₹${(price * quantity).toInt()}") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = bargainError,
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp)
                )

                if (bargainError) {
                    Text(
                        text = "Enter a valid price (less than or equal to actual)",
                        color = Color.Red,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 4.dp, top = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun AmountCard(
    isExpanded: Boolean = true,
    onExpandedClick: () -> Unit,
    modifier: Modifier = Modifier,
    totalPrice: Int,
    totalQuantity: Int
) {
    ElevatedCard(
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onExpandedClick() }
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Amount Details",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF424242)
                    )
                }
                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (isExpanded) "Collapse" else "Expand"
                )
            }

            AnimatedVisibility(visible = isExpanded) {
                Box(
                    modifier = Modifier
                        .padding(vertical = 16.dp, horizontal = 8.dp)
                        .fillMaxWidth()
                        .border(2.dp, Color(0xFFEBEBEB))
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        ) {
                            Text(
                                text = "Total Price: ",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "₹$totalPrice",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium,
                                color = Color(0xFF388E3C)
                            )
                        }
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        ) {
                            Text(
                                text = "Total Quantity: ",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "$totalQuantity",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium,
                                color = Color(0xFF734A19)
                            )
                        }
                    }
                }
            }
        }
    }
}
