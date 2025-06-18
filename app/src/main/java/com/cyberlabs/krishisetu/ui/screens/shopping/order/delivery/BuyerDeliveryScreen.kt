package com.cyberlabs.krishisetu.ui.screens.shopping.order.delivery

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.amplifyframework.datastore.generated.model.Delivery
import com.amplifyframework.datastore.generated.model.DeliveryStatus
import com.cyberlabs.krishisetu.shopping.order.delivery.DeliveriesViewModel
import com.cyberlabs.krishisetu.util.navigation.BuyerBottomBar
import com.cyberlabs.krishisetu.util.navigation.TopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuyerDeliveryScreen(
    navController: NavController,
    viewModel: DeliveriesViewModel = hiltViewModel()
) {
    val deliveries by viewModel.deliveries.collectAsState()

    Scaffold(
        topBar = {
            TopBar("Your Deliveries", navController)
        },
        bottomBar = {
            BuyerBottomBar(navController, 3)
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(12.dp)
            ) {
                Button(
                    onClick = { navController.navigate("buyer_orders") },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFB8C00)
                    ),
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(bottom = 12.dp)
                ) {
                    Text("Pending Orders", color = Color.White)
                }

                if (deliveries.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No deliveries yet.", style = MaterialTheme.typography.bodyMedium)
                    }
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(deliveries.size) { index ->
                            val delivery = deliveries[index]
                            DeliveryCard(delivery = delivery)
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun DeliveryCard(
    delivery: Delivery,
    isFarmer: Boolean = false,
    onSelectedStatus: (DeliveryStatus) -> Unit = {}
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedStatus by remember { mutableStateOf(delivery.deliveryStatus) }

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = Color(0xFFF9F9F9)
        ),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Header: Crop title & Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = delivery.purchase.crop.title,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = Color(0xFF2E7D32)
                )
                StatusBadge(deliveryStatus = delivery.deliveryStatus)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Price
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = null,
                    tint = Color(0xFF388E3C),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "₹${delivery.purchase.totalAmount}",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Delivery Details
            DeliveryDetailRow(label = "Quantity", value = (delivery.deliveryQuantity ?: "N/A").toString())
            DeliveryDetailRow(label = "Address", value = delivery.deliveryAddress)
            DeliveryDetailRow(label = "Pincode", value = delivery.deliveryPincode ?:"N/A")
            DeliveryDetailRow(label = "Phone", value = delivery.deliveryPhone ?: "N/A")

            Spacer(modifier = Modifier.height(12.dp))

            // Agent Info
            Divider(thickness = 1.dp, color = Color(0xFFE0E0E0))

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Delivery Agent",
                style = MaterialTheme.typography.labelLarge.copy(
                    color = Color(0xFF4CAF50),
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(modifier = Modifier.height(6.dp))

            val agent = delivery.agent
            if (agent == null || agent.id == "DUMMY" || agent.id == delivery.farmer.id) {
                DeliveryDetailRow(label = "Name", value = delivery.personalAgentName ?: "N/A")
                DeliveryDetailRow(label = "Email", value = delivery.personalAgentEmail ?: "N/A")
                DeliveryDetailRow(label = "Phone", value = delivery.personalAgentPhone ?: "N/A")
            } else {
                agent.let {
                    DeliveryDetailRow(label = "Name", value = it.name)
                    DeliveryDetailRow(label = "Email", value = it.email)
                    DeliveryDetailRow(label = "Phone", value = it.phone)
                }
            }

            if (isFarmer) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Update Delivery Status",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Gray
                    )
                )
                Spacer(modifier = Modifier.height(6.dp))

                Box {
                    OutlinedButton(
                        onClick = { expanded = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = selectedStatus.name.replace("_", " ").capitalize())
                        Spacer(Modifier.weight(1f))
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DeliveryStatus.values().forEach { status ->
                            DropdownMenuItem(
                                text = { Text(status.name.replace("_", " ").capitalize()) },
                                onClick = {
                                    expanded = false
                                    selectedStatus = status
                                    onSelectedStatus(status)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DeliveryDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            textAlign = TextAlign.End
        )
    }
}


@Composable
fun StatusBadge(deliveryStatus: DeliveryStatus) {
    val (color, text) = when (deliveryStatus) {
        DeliveryStatus.PENDING -> Color(0xFFFFA726) to "Pending"
        DeliveryStatus.PREPARED -> Color(0xFF29B6F6) to "Prepared"
        DeliveryStatus.SHIPPED -> Color(0xFF42A5F5) to "Shipped"
        DeliveryStatus.OUT_FOR_DELIVERY -> Color(0xFF7E57C2) to "Out for Delivery"
        DeliveryStatus.DELIVERED -> Color(0xFF66BB6A) to "Delivered"
        DeliveryStatus.CANCELLED -> Color(0xFFE53935) to "Cancelled"
    }

    Box(
        modifier = Modifier
            .background(color.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = color,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}
