package com.cyberlabs.krishisetu.ui.screens.shopping.order

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
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
            TopAppBar(title = { Text("My Orders") })
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
                                // Handle order status change here, open alert dialog box
                                dialogBoxState = true
                                dialogBoxOrder = it
                            }
                        )
                    }
                }
                if (dialogBoxState) {
                    AcceptOrRejectOrderDialog(
                        order = dialogBoxOrder!!,
                        onAccept = {
                            dialogBoxState = false
                            // Ask for confirmation before accepting
                            confirmationBoxOrder = dialogBoxOrder
                            isRejecting = false
                            confirmationBoxState = true

                            dialogBoxOrder = null
                        },
                        onReject = {
                            dialogBoxState = false
                            //Ask for confirmation before rejecting
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
                            //Run acceptation or rejection flow
                            if (isRejecting!!) ordersViewModel.rejectOrder(confirmationBoxOrder!!)
                            else {
                                if (confirmationBoxOrder!!.crop.quantityAvailable - confirmationBoxOrder!!.quantity > 0) {
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

            // Crop Name and Status Badge
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = order.crop.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                StatusBadge(status = order.orderStatus.name)
            }

            Spacer(modifier = Modifier.height(12.dp))
            Divider()

            // Price Section
            Spacer(modifier = Modifier.height(12.dp))
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

            // Delivery Info
            Text("Delivery Details", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Place,
                    contentDescription = "Address",
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(order.deliveryAddress, fontSize = 14.sp)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = "Pincode",
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Pincode: ${order.deliveryPincode}", fontSize = 14.sp)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Phone,
                    contentDescription = "Phone",
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(order.deliveryPhone, fontSize = 14.sp)
            }

            if (order.orderStatus.name == "PENDING") {
                Spacer(Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    ElevatedButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        onClick = {
                            onVisitCrop(order)
                        },
                        colors = ButtonDefaults.elevatedButtonColors().copy(
                            containerColor = Color(0xFFFFB300),
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = "Visit Crop"
                        )
                    }
                    VerticalDivider(
                        thickness = 1.dp,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                    ElevatedButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        onClick = {
                            onChangeStatus(order)
                        },
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
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ElevatedButton(
                        modifier = Modifier
                            .fillMaxWidth(0.5f),
                        onClick = {
                            onVisitCrop(order)
                        },
                        colors = ButtonDefaults.elevatedButtonColors().copy(
                            containerColor = Color(0xFFFFB300),
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = "Visit Crop"
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Order Time
            Text(
                text = "Ordered at: ${
                    OffsetDateTime.parse(order.createdAt.format()).toLocalDateTime()
                }",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )
        }
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
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .wrapContentHeight()
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.TopEnd
                ) {
                    IconButton(onClick = onClose) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close dialog",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Confirm Order",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                Divider(
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.surfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Would you like to accept this order?\nYou can assign a delivery agent after accepting.",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        lineHeight = 22.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(28.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = { onReject(order) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFF57C00), // Deep amber
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Reject")
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Button(
                        onClick = { onAccept(order) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1976D2), // Deep blue
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Accept")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun ConfirmFinalDecisionDialog(
    actionType: String, // "accept" or "reject"
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
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "Are you sure?",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "You CANNOT change this decision later.\nPlease confirm you want to ${actionType.uppercase()} this order.",
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
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF9E9E9E), // Grey
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel")
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Button(
                        onClick = onConfirm,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (actionType == "accept") Color(0xFF1976D2) else Color(0xFFD32F2F),
                            contentColor = Color.White
                        ),
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
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f))
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .wrapContentHeight(),
            shape = RoundedCornerShape(20.dp),
            tonalElevation = 6.dp,
            shadowElevation = 12.dp,
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Text(
                    text = "Choose Delivery Agent",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Radio Choices
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    DeliveryAgentChoice(
                        title = "KrishiSetu",
                        selected = !isPersonalAgent,
                        onClick = { isPersonalAgent = false }
                    )
                    DeliveryAgentChoice(
                        title = "Personal",
                        selected = isPersonalAgent,
                        onClick = { isPersonalAgent = true }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Dynamic Fields
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (isPersonalAgent) {
                        OutlinedTextField(
                            value = personalName,
                            onValueChange = { personalName = it },
                            label = { Text("Agent Name") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = personalPhone,
                            onValueChange = { personalPhone = it },
                            label = { Text("Phone Number") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = personalEmail,
                            onValueChange = { personalEmail = it },
                            label = { Text("Email") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        OutlinedTextField(
                            value = krishiEmail,
                            onValueChange = { krishiEmail = it },
                            label = { Text("KrishiSetu Agent Email") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp)
                            .height(48.dp),
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = {
                            if (isPersonalAgent) {
                                val details = PersonalDeliveryAgentDetails(
                                    name = personalName.trim(),
                                    phone = personalPhone.trim(),
                                    email = personalEmail.trim()
                                )
                                onConfirm(true, details, null)
                            } else {
                                onConfirm(false, null, krishiEmail.trim())
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 8.dp)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF43A047),
                            contentColor = Color.White
                        )
                    ) {
                        Text("Continue")
                    }
                }
            }
        }
    }
}

@Composable
fun DeliveryAgentChoice(
    title: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        color = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
        else MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = if (selected) 4.dp else 0.dp,
        border = BorderStroke(
            1.dp,
            if (selected) MaterialTheme.colorScheme.primary else Color.LightGray
        ),
        modifier = Modifier
            .width(140.dp)
            .height(50.dp)
            .clip(RoundedCornerShape(10.dp))
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = if (selected) MaterialTheme.colorScheme.primary else Color.DarkGray
            )
        }
    }
}
