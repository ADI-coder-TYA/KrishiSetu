package com.cyberlabs.krishisetu.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.amplifyframework.datastore.generated.model.Order
import com.cyberlabs.krishisetu.R
import com.cyberlabs.krishisetu.ai.AiChatMessage
import com.cyberlabs.krishisetu.ai.GeminiChatViewModel
import com.cyberlabs.krishisetu.crops.CropData
import com.cyberlabs.krishisetu.crops.CropViewModel
import com.cyberlabs.krishisetu.shopping.order.FarmerOrdersViewModel
import com.cyberlabs.krishisetu.util.navigation.FarmerBottomBar
import com.cyberlabs.krishisetu.util.navigation.TopBar
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmerHomeScreen(
    navController: NavController,
    geminiChatViewModel: GeminiChatViewModel = hiltViewModel(),
    cropsViewModel: CropViewModel = hiltViewModel(),
    ordersViewModel: FarmerOrdersViewModel = hiltViewModel(),
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }

    val crops by cropsViewModel.crops
    val orders by ordersViewModel.orders.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    showBottomSheet = true
                    scope.launch { sheetState.show() }
                },
                containerColor = Color.White,
                modifier = Modifier.padding(16.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.google_gemini_icon),
                    contentDescription = "Google Gemini",
                    modifier = Modifier.size(24.dp)
                )
            }
        },
        containerColor = Color.White,
        topBar = { TopBar("कृषिसेतु", navController) },
        bottomBar = { FarmerBottomBar(navController) }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Section(
                title = "🌾 Your Crops",
                onViewMore = { navController.navigate("crops") },
                items = crops.sortedByDescending { it.createdAt }.take(5),
                cardContent = { crop ->
                    CropCard(crop)
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Section(
                title = "📦 Recent Orders",
                onViewMore = { navController.navigate("farmer_orders") },
                items = orders.sortedByDescending { it.createdAt }.take(5),
                cardContent = { order ->
                    OrderCard(order)
                }
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = " 🚚 Delivery Updates",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF424242),
                modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
            )

            CheckDeliveriesButton(
                onClick = { navController.navigate("farmer_delivery/true") }
            )

            Spacer(modifier = Modifier.height(16.dp))
        }


        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState,
                contentWindowInsets = { WindowInsets.systemBars.only(WindowInsetsSides.Bottom) }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(500.dp)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "KrishiSetu AI Assistant",
                        fontSize = 18.sp,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        textAlign = TextAlign.Center
                    )
                    GeminiChatScreen(geminiChatViewModel = geminiChatViewModel)
                }
            }
        }
    }
}

@Composable
fun <T> Section(
    title: String,
    onViewMore: () -> Unit,
    items: List<T>,
    cardContent: @Composable (T) -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            TextButton(onClick = onViewMore) {
                Text("View All")
            }
        }
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(items.size) { index ->
                val item = items[index]
                cardContent(item)
            }
        }
    }
}

@Composable
fun CropCard(crop: CropData) {
    Card(
        colors = CardDefaults.cardColors().copy(
            containerColor = Color.White
        ),
        modifier = Modifier
            .width(160.dp)
            .height(220.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column {
            AsyncImage(
                model = crop.imageUrl,
                contentDescription = "Crop Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f),
                placeholder = painterResource(R.drawable.app_logo),
                error = painterResource(R.drawable.app_logo),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.height(8.dp))
            Column(modifier = Modifier.padding(horizontal = 8.dp)) {
                Text(
                    text = crop.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Text(
                    text = "₹${crop.price}/kg",
                    fontSize = 12.sp,
                    color = Color(0xFF388E3C)
                )
                val formattedDate = crop.createdAt?.let {
                    val localDateTime =
                        LocalDateTime.ofInstant(it.toDate().toInstant(), ZoneOffset.UTC)
                    localDateTime.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
                }
                Text(
                    text = formattedDate.toString(),
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun OrderCard(order: Order) {
    val formattedDate = order.createdAt?.let {
        val localDateTime =
            LocalDateTime.ofInstant(it.toDate().toInstant(), ZoneOffset.UTC)
        localDateTime.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
    } ?: "Unknown date"

    Card(
        modifier = Modifier
            .width(200.dp)
            .height(140.dp)
            .padding(4.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = order.crop.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF2E7D32),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "₹${order.bargainedPrice}/kg",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF6A1B9A)
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Spacer(Modifier.height(4.dp))
                StatusChip(
                    status = order.orderStatus.toString().lowercase(),
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = formattedDate,
                    fontSize = 11.sp,
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}

@Composable
fun GeminiChatScreen(geminiChatViewModel: GeminiChatViewModel) {
    // Observe messages from the ViewModel's mutableStateListOf
    val messages by remember { mutableStateOf(geminiChatViewModel.messages) }
    val listState = rememberLazyListState() // State to control LazyColumn scrolling
    var inputText by remember { mutableStateOf("") } // State for the input text field

    // Automatically scroll to the last message when new messages arrive
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            state = listState, // Attach the state to enable scrolling control
            modifier = Modifier
                .weight(1f) // Takes available vertical space
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        ) {
            items(messages.size) { index ->
                val message = messages[index]
                MessageBubble(message = message) // Render each message
            }
        }

        // Input field and Send button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                label = { Text("Ask me anything...") },
                modifier = Modifier.weight(1f),
                singleLine = false, // Allow multi-line input
                maxLines = 5,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = Color.Gray
                ),
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    imeAction = androidx.compose.ui.text.input.ImeAction.Send // Show send button on keyboard
                ),
                keyboardActions = androidx.compose.foundation.text.KeyboardActions(
                    onSend = {
                        if (inputText.isNotBlank()) {
                            geminiChatViewModel.sendMessage(inputText)
                            inputText = "" // Clear input field
                        }
                    }
                )
            )
            Spacer(Modifier.width(8.dp))
            IconButton(
                onClick = {
                    geminiChatViewModel.sendMessage(inputText)
                    inputText = "" // Clear input field
                },
                enabled = inputText.isNotBlank() // Enable send button only if text is not blank
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.Send, // Using Material Icons built-in Send icon
                    contentDescription = "Send message",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun MessageBubble(message: AiChatMessage) {
    val isUser = message.sender == AiChatMessage.Sender.USER
    val bubbleColor =
        if (isUser) Color(0xFFDCF8C6) else Color(0xFFF1F0F0) // Light green vs light gray
    val textColor = Color.Black

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start // Align right for user, left for AI
    ) {
        Card(
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = bubbleColor),
            modifier = Modifier.widthIn(max = 280.dp) // Max width for message bubble
        ) {
            Text(
                text = message.text,
                color = textColor,
                fontSize = 16.sp,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

@Composable
fun StatusChip(modifier: Modifier, status: String) {
    val bgColor = when (status.lowercase()) {
        "pending" -> Color(0xFFFFF59D)
        "accepted" -> Color(0xFFC8E6C9)
        "rejected" -> Color(0xFFFFCDD2)
        else -> Color.LightGray
    }
    val textColor = when (status.lowercase()) {
        "pending" -> Color(0xFFFBC02D)
        "accepted" -> Color(0xFF388E3C)
        "rejected" -> Color(0xFFD32F2F)
        else -> Color.DarkGray
    }

    Box(
        modifier = modifier
            .background(color = bgColor, shape = RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = status.capitalize(),
            color = textColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun CheckDeliveriesButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .height(56.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF2E7D32),
            contentColor = Color.White
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
    ) {
        Icon(
            painter = painterResource(R.drawable.filled_delivery_truck_24),
            tint = Color.White,
            contentDescription = "Deliveries",
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Check Your Deliveries",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
