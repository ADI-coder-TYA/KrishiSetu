package com.cyberlabs.krishisetu.ui.screens.home

import android.content.Intent
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
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.amplifyframework.datastore.generated.model.Delivery
import com.amplifyframework.datastore.generated.model.DeliveryStatus
import com.cyberlabs.krishisetu.R
import com.cyberlabs.krishisetu.shopping.order.delivery.DeliveryAgentHomeViewModel
import com.cyberlabs.krishisetu.util.navigation.DeliveryBottomBar
import com.cyberlabs.krishisetu.util.navigation.TopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeliveryHomeScreen(
    navController: NavController,
    viewModel: DeliveryAgentHomeViewModel = hiltViewModel()
) {
    val deliveries by viewModel.deliveries.collectAsState()
    val sheetState = rememberModalBottomSheetState()
    val context = LocalContext.current

    // Holds: Triple(display name, phone number, userId for chat)
    var contactTarget by remember { mutableStateOf<Triple<String, String, String>?>(null) }

    // Bottom Sheet for contact
    if (contactTarget != null) {
        ModalBottomSheet(
            onDismissRequest = { contactTarget = null },
            sheetState = sheetState,
            dragHandle = null,
            containerColor = Color.White,
            tonalElevation = 8.dp
        ) {
            val (displayName, phone, userId) = contactTarget!!

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Contact $displayName", fontWeight = FontWeight.Bold, fontSize = 20.sp)

                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_DIAL).apply {
                            data = "tel:$phone".toUri()
                        }
                        context.startActivity(intent)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Call, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Call")
                }

                Spacer(Modifier.height(12.dp))

                Button(
                    onClick = {
                        val deliveryAgentId = deliveries.firstOrNull()?.agent?.id
                        if (deliveryAgentId != null) {
                            navController.navigate("chatList/$deliveryAgentId/$userId")
                            contactTarget = null
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2C6F30))
                ) {
                    Icon(
                        painterResource(R.drawable.rounded_chat_bubble_24),
                        contentDescription = null,
                        tint = Color.White
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Chat", color = Color.White)
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopBar("कृषिसेतु", navController)
        },
        bottomBar = {
            DeliveryBottomBar(navController)
        }
    ) { innerPadding ->
        if (deliveries.isEmpty()) {
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Log.i("DeliveryHomeScreen", "$deliveries")
                Text("No deliveries assigned yet", style = MaterialTheme.typography.bodyMedium)
            }
        } else {
            LazyColumn(
                contentPadding = innerPadding,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize()
            ) {
                items(deliveries.size) { index ->
                    val delivery = deliveries[index]
                    DeliveryCard(
                        delivery = delivery,
                        onTalkToFarmer = {
                            contactTarget = Triple(
                                "Farmer",
                                delivery.farmer.phone,
                                delivery.farmer.id
                            )
                        },
                        onTalkToBuyer = {
                            contactTarget = Triple(
                                "Buyer",
                                delivery.buyer.phone,
                                delivery.buyer.id
                            )
                        }
                    )
                }
            }
        }
    }
}


@Composable
fun DeliveryCard(
    delivery: Delivery,
    onTalkToFarmer: () -> Unit,
    onTalkToBuyer: () -> Unit
) {
    ElevatedCard(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Title + Status Badge Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = delivery.purchase.crop.title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1B5E20),
                    modifier = Modifier.weight(1f)
                )
                StatusBadge(delivery.deliveryStatus)
            }

            Spacer(Modifier.height(12.dp))

            // Farmer + Buyer
            InfoRow("Farmer", delivery.farmer.name)
            InfoRow("Buyer", delivery.buyer.name)

            Spacer(Modifier.height(12.dp))

            // Pickup + Delivery
            LocationRow("Pickup Location", delivery.purchase.crop.location)
            Spacer(Modifier.height(8.dp))
            LocationRow("Delivery Address", delivery.deliveryAddress)

            Spacer(Modifier.height(16.dp))

            // Buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedButton(
                    onClick = onTalkToFarmer,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Person, contentDescription = null)
                    Spacer(Modifier.width(6.dp))
                    Text("Farmer")
                }

                OutlinedButton(
                    onClick = onTalkToBuyer,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Person, contentDescription = null)
                    Spacer(Modifier.width(6.dp))
                    Text("Buyer")
                }
            }
        }
    }
}

@Composable
private fun StatusBadge(status: DeliveryStatus) {
    val (color, label) = when (status) {
        DeliveryStatus.PENDING -> Color(0xFFFFA726) to "Pending"
        DeliveryStatus.PREPARED -> Color(0xFF42A5F5) to "Prepared"
        DeliveryStatus.SHIPPED -> Color(0xFF26C6DA) to "Shipped"
        DeliveryStatus.OUT_FOR_DELIVERY -> Color(0xFFAB47BC) to "Out for Delivery"
        DeliveryStatus.DELIVERED -> Color(0xFF66BB6A) to "Delivered"
        DeliveryStatus.CANCELLED -> Color(0xFFEF5350) to "Cancelled"
    }

    Box(
        modifier = Modifier
            .background(color.copy(alpha = 0.2f), shape = RoundedCornerShape(50))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            color = color,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "$label:",
            fontWeight = FontWeight.Medium,
            color = Color.Gray
        )
        Text(
            text = value,
            fontWeight = FontWeight.Normal
        )
    }
}

@Composable
private fun LocationRow(label: String, address: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(label, fontWeight = FontWeight.Medium, color = Color.Gray)
        Text(
            text = address,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}
