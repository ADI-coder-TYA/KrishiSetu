package com.cyberlabs.krishisetu.ui.screens.shopping.order

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.amplifyframework.datastore.generated.model.Order
import com.cyberlabs.krishisetu.shopping.order.OrdersViewModel
import com.cyberlabs.krishisetu.util.navigation.TopBar
import java.time.OffsetDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuyerOrderScreen(
    navController: NavController,
    ordersViewModel: OrdersViewModel = hiltViewModel()
) {
    val ordersState by ordersViewModel.orders.collectAsState(initial = emptyList())

    var confirmationBoxState by remember { mutableStateOf(false) }
    var confirmationBoxOrder by remember { mutableStateOf<Order?>(null) }

    Scaffold(
        topBar = {
            TopBar("My Orders", navController, true)
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                LazyColumn(
                    contentPadding = padding,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(Color(0xFFF8F9FA))
                        .padding(16.dp)
                ) {
                    items(ordersState.size) { index ->
                        val order = ordersState[index]
                        OrderCard(
                            order = order,
                            onVisitCrop = {
                                navController.navigate("cropShop/${it.crop.id}")
                            },
                            onCancel = {
                                confirmationBoxOrder = it
                                confirmationBoxState = true
                            }
                        )
                    }
                }
                if (confirmationBoxState) {
                    ConfirmFinalDecisionDialog(
                        onConfirm = {
                            confirmationBoxState = false
                            // Run cancellation flow
                            ordersViewModel.cancelOrder(confirmationBoxOrder!!)
                            confirmationBoxOrder = null
                        },
                        onDismiss = {
                            confirmationBoxState = false
                            confirmationBoxOrder = null
                        }
                    )
                }
            }
        }
    )
}

@Composable
private fun OrderCard(
    order: Order,
    onVisitCrop: (Order) -> Unit,
    onCancel: (Order) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .shadow(6.dp, RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {

            // 1. Header: Crop Name and Status
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = order.crop.title,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                StatusBadge(status = order.orderStatus.name)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 2. Payment Mode Display (NEW ADDITION)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Payment Mode: ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
                Text(
                    // Fallback to "COD" if field is missing/null (legacy orders)
                    text = order.paymentMode ?: "COD",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (order.paymentMode == "ONLINE") Color(0xFF1976D2) else Color(0xFF795548)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = Color.LightGray.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(12.dp))

            // 3. Price Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Bargained Price", fontSize = 14.sp, color = Color.Gray)
                    Text(
                        "₹${order.bargainedPrice}",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                }
                Column {
                    Text("Original Price", fontSize = 14.sp, color = Color.Gray)
                    Text("₹${order.realPrice}/kg", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 4. Delivery Info
            Text("Delivery Details", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(6.dp))

            DeliveryInfoRow(icon = Icons.Default.Place, text = order.deliveryAddress)
            DeliveryInfoRow(icon = Icons.Default.LocationOn, text = "Pincode: ${order.deliveryPincode}")
            DeliveryInfoRow(icon = Icons.Default.Phone, text = order.deliveryPhone)
            DeliveryInfoRow(icon = Icons.Default.Info, text = "Quantity: ${order.quantity}")

            // 5. Action Buttons (Pending vs Others)
            if (order.orderStatus.name == "PENDING") {
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    ElevatedButton(
                        modifier = Modifier.weight(1f),
                        onClick = { onVisitCrop(order) },
                        colors = ButtonDefaults.elevatedButtonColors().copy(
                            containerColor = Color(0xFFFFB300),
                            contentColor = Color.White
                        )
                    ) {
                        Text(text = "Visit Crop")
                    }
                    VerticalDivider(
                        thickness = 1.dp,
                        modifier = Modifier.padding(horizontal = 4.dp).height(24.dp)
                    )
                    ElevatedButton(
                        modifier = Modifier.weight(1f),
                        onClick = { onCancel(order) },
                        colors = ButtonDefaults.elevatedButtonColors().copy(
                            containerColor = Color(0xFFCB3226),
                            contentColor = Color.White
                        )
                    ) {
                        Text("Cancel Order")
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    ElevatedButton(
                        modifier = Modifier.fillMaxWidth(0.6f),
                        onClick = { onVisitCrop(order) },
                        colors = ButtonDefaults.elevatedButtonColors().copy(
                            containerColor = Color(0xFFFFB300),
                            contentColor = Color.White
                        )
                    ) {
                        Text(text = "Visit Crop")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 6. Order Time Footer
            Text(
                text = "Ordered at: ${
                    try {
                        OffsetDateTime.parse(order.createdAt.format()).toLocalDateTime().toString().replace("T", " ")
                    } catch (e: Exception) {
                        "Unknown Date"
                    }
                }",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )
        }
    }
}

@Composable
private fun ConfirmFinalDecisionDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f)),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            tonalElevation = 4.dp,
            shadowElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .wrapContentHeight()
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Are you sure?",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "You CANNOT change this decision later.\nPlease confirm you want to CANCEL this order.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Close")
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(
                        onClick = onConfirm,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Confirm")
                    }
                }
            }
        }
    }
}
