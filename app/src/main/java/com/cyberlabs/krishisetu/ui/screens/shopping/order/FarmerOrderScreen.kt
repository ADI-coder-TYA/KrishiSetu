package com.cyberlabs.krishisetu.ui.screens.shopping.order

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.amplifyframework.datastore.generated.model.Order
import com.cyberlabs.krishisetu.shopping.order.FarmerOrdersViewModel
import com.cyberlabs.krishisetu.states.PersonalDeliveryAgentDetails
import com.cyberlabs.krishisetu.util.navigation.TopBar
import java.time.OffsetDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmerOrderScreen(
    navController: NavController,
    ordersViewModel: FarmerOrdersViewModel = hiltViewModel(),
) {
    val ordersState by ordersViewModel.orders.collectAsState()
    var dialogBoxState by remember { mutableStateOf(false) }
    var dialogBoxOrder by remember { mutableStateOf<Order?>(null) }

    var confirmationBoxState by remember { mutableStateOf(false) }
    var confirmationBoxOrder by remember { mutableStateOf<Order?>(null) }
    var isRejecting by remember { mutableStateOf<Boolean?>(null) }

    var deliveryAgentDialogBoxState by remember { mutableStateOf(false) }
    var deliveryAgentDialogBoxOrder by remember { mutableStateOf<Order?>(null) }

    val context = LocalContext.current

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
                            onChangeStatus = {
                                dialogBoxState = true
                                dialogBoxOrder = it
                            }
                        )
                    }
                }

                // --- Dialogs ---

                if (dialogBoxState) {
                    AcceptOrRejectOrderDialog(
                        order = dialogBoxOrder!!,
                        onAccept = {
                            dialogBoxState = false
                            confirmationBoxOrder = dialogBoxOrder
                            isRejecting = false
                            confirmationBoxState = true
                            dialogBoxOrder = null
                        },
                        onReject = {
                            dialogBoxState = false
                            confirmationBoxOrder = dialogBoxOrder
                            isRejecting = true
                            confirmationBoxState = true
                            dialogBoxOrder = null
                        },
                        onClose = {
                            dialogBoxState = false
                            dialogBoxOrder = null
                        }
                    )
                }
                if (confirmationBoxState) {
                    ConfirmFinalDecisionDialog(
                        actionType = if (isRejecting!!) "reject" else "accept",
                        onConfirm = {
                            confirmationBoxState = false
                            if (isRejecting!!) ordersViewModel.rejectOrder(confirmationBoxOrder!!)
                            else {
                                if (confirmationBoxOrder!!.crop.quantityAvailable - confirmationBoxOrder!!.quantity >= 0) {
                                    deliveryAgentDialogBoxOrder = confirmationBoxOrder!!
                                    deliveryAgentDialogBoxState = true
                                } else {
                                    Toast.makeText(context, "Not enough quantity available", Toast.LENGTH_LONG).show()
                                }
                            }
                            confirmationBoxOrder = null
                            isRejecting = null
                        },
                        onDismiss = {
                            confirmationBoxState = false
                            confirmationBoxOrder = null
                            isRejecting = null
                        }
                    )
                }
                if (deliveryAgentDialogBoxState) {
                    ChooseDeliveryAgentDialog(
                        onConfirm = { isPersonal, personalDetails, krishiEmail ->
                            deliveryAgentDialogBoxState = false
                            ordersViewModel.acceptOrder(
                                deliveryAgentDialogBoxOrder!!,
                                krishiEmail,
                                isPersonal,
                                personalDetails,
                            )
                        },
                        onDismiss = {
                            deliveryAgentDialogBoxState = false
                            deliveryAgentDialogBoxOrder = null
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
    onChangeStatus: (Order) -> Unit
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

            // 2. Payment Mode Display (NEW)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Payment Mode: ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
                Text(
                    text = order.paymentMode ?: "COD", // Default to COD if field is null
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
                    Text("₹${order.realPrice}", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
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
                Spacer(Modifier.height(16.dp))

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
                        onClick = { onChangeStatus(order) },
                        colors = ButtonDefaults.elevatedButtonColors().copy(
                            containerColor = Color(0xFF0861AF),
                            contentColor = Color.White
                        )
                    ) {
                        Text("Accept or Reject")
                    }
                }

            } else {
                Spacer(modifier = Modifier.height(16.dp))
                Row (
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

            // 6. Order Time
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

// Helper composable for cleaner code
@Composable
fun DeliveryInfoRow(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 2.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF4CAF50),
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = text, fontSize = 14.sp, color = Color.DarkGray)
    }
}

@Composable
fun StatusBadge(status: String) {
    val (backgroundColor, label) = when (status.uppercase()) {
        "PENDING" -> Color(0xFFFF9800) to "Pending"
        "ACCEPTED" -> Color(0xFF4CAF50) to "Accepted"
        "REJECTED" -> Color(0xFFF44336) to "Rejected"
        "CANCELLED" -> Color(0xFFD32F2F) to "Cancelled"
        else -> Color.Gray to status
    }

    Box(
        modifier = Modifier
            .background(backgroundColor, RoundedCornerShape(50))
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun AcceptOrRejectOrderDialog(
    order: Order,
    onAccept: (Order) -> Unit,
    onReject: (Order) -> Unit,
    onClose: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            tonalElevation = 4.dp,
            shadowElevation = 6.dp,
            modifier = Modifier.fillMaxWidth(0.9f).wrapContentHeight()
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.TopEnd) {
                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.Close, "Close dialog", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                Text("Confirm Order", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.SemiBold))
                Spacer(modifier = Modifier.height(12.dp))
                Divider()
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    "Would you like to accept this order?\nYou can assign a delivery agent after accepting.",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(28.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    Button(
                        onClick = { onReject(order) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF57C00)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) { Text("Reject") }
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(
                        onClick = { onAccept(order) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) { Text("Accept") }
                }
            }
        }
    }
}

@Composable
private fun ConfirmFinalDecisionDialog(
    actionType: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            tonalElevation = 4.dp,
            shadowElevation = 8.dp,
            modifier = Modifier.fillMaxWidth(0.85f).wrapContentHeight()
        ) {
            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Are you sure?", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold))
                Spacer(modifier = Modifier.height(12.dp))
                Text("You CANNOT change this decision later.\nPlease confirm you want to ${actionType.uppercase()} this order.", style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(24.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9E9E9E)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) { Text("Cancel") }
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(
                        onClick = onConfirm,
                        colors = ButtonDefaults.buttonColors(containerColor = if (actionType == "accept") Color(0xFF1976D2) else Color(0xFFD32F2F)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) { Text("Confirm") }
                }
            }
        }
    }
}

@Composable
fun ChooseDeliveryAgentDialog(
    onConfirm: (isPersonal: Boolean, personalDetails: PersonalDeliveryAgentDetails?, krishiEmail: String?) -> Unit,
    onDismiss: () -> Unit
) {
    var isPersonalAgent by remember { mutableStateOf(false) }
    var personalName by remember { mutableStateOf("") }
    var personalEmail by remember { mutableStateOf("") }
    var personalPhone by remember { mutableStateOf("") }
    var krishiEmail by remember { mutableStateOf("") }

    Box(
        modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.6f)).padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(modifier = Modifier.fillMaxWidth(0.9f).wrapContentHeight(), shape = RoundedCornerShape(20.dp)) {
            Column(modifier = Modifier.padding(24.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Choose Delivery Agent", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold), color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    DeliveryAgentChoice("KrishiSetu", !isPersonalAgent) { isPersonalAgent = false }
                    DeliveryAgentChoice("Personal", isPersonalAgent) { isPersonalAgent = true }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (isPersonalAgent) {
                        OutlinedTextField(value = personalName, onValueChange = { personalName = it }, label = { Text("Agent Name") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = personalPhone, onValueChange = { personalPhone = it }, label = { Text("Phone Number") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone), singleLine = true, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = personalEmail, onValueChange = { personalEmail = it }, label = { Text("Email") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email), singleLine = true, modifier = Modifier.fillMaxWidth())
                    } else {
                        OutlinedTextField(value = krishiEmail, onValueChange = { krishiEmail = it }, label = { Text("KrishiSetu Agent Email") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email), singleLine = true, modifier = Modifier.fillMaxWidth())
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    TextButton(onClick = onDismiss, modifier = Modifier.weight(1f)) { Text("Cancel", color = MaterialTheme.colorScheme.error) }
                    Button(
                        onClick = {
                            if (isPersonalAgent) onConfirm(true, PersonalDeliveryAgentDetails(personalName.trim(), personalPhone.trim(), personalEmail.trim()), null)
                            else onConfirm(false, null, krishiEmail.trim())
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF43A047))
                    ) { Text("Continue") }
                }
            }
        }
    }
}

@Composable
fun DeliveryAgentChoice(title: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        color = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant,
        border = BorderStroke(1.dp, if (selected) MaterialTheme.colorScheme.primary else Color.LightGray),
        modifier = Modifier.width(140.dp).height(50.dp).clip(RoundedCornerShape(10.dp))
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(title, style = MaterialTheme.typography.bodyMedium, color = if (selected) MaterialTheme.colorScheme.primary else Color.DarkGray)
        }
    }
}
