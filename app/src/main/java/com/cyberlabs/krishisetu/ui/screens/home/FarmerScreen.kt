package com.cyberlabs.krishisetu.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Button
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
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.cyberlabs.krishisetu.R
import com.cyberlabs.krishisetu.ai.AiChatMessage
import com.cyberlabs.krishisetu.ai.GeminiChatViewModel
import com.cyberlabs.krishisetu.authentication.AuthViewModel
import com.cyberlabs.krishisetu.util.navigation.FarmerBottomBar
import com.cyberlabs.krishisetu.util.navigation.TopBar
import kotlinx.coroutines.launch

@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmerHomeScreen(
    navController: NavController = rememberNavController(),
    geminiChatViewModel: GeminiChatViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {

    val sheetState =
        rememberModalBottomSheetState(skipPartiallyExpanded = true) // Sheet state for bottom sheet
    val scope = rememberCoroutineScope() // Coroutine scope for launching suspend functions
    var showBottomSheet by remember { mutableStateOf(false) } // State to control bottom sheet visibility

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
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(R.drawable.google_gemini_icon),
                    contentDescription = "Google Gemini"
                )
            }
        },
        containerColor = Color.White,
        topBar = {
            TopBar("कृषिसेतु", navController)
        },
        bottomBar = {
            FarmerBottomBar(navController)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
        ) {
            Button(
                onClick = {
                    authViewModel.signOut()
                    navController.navigate("signIn")
                }
            ) {
                Text(
                    text = "Sign Out",
                    color = Color.Black
                )
            }
        }
        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false }, // Hide sheet when dismissed
                sheetState = sheetState,
                // Adjust window insets to prevent keyboard from obscuring content
                contentWindowInsets = {
                    WindowInsets.systemBars.only(WindowInsetsSides.Bottom)
                }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(500.dp) // Fixed height for the chat window
                        .padding(horizontal = 16.dp, vertical = 8.dp) // Adjusted padding
                ) {
                    Text(
                        text = "KrishiSetu AI Assistant",
                        fontSize = 18.sp,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    // Pass the ViewModel to the ChatScreen
                    GeminiChatScreen(geminiChatViewModel = geminiChatViewModel)
                }
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
