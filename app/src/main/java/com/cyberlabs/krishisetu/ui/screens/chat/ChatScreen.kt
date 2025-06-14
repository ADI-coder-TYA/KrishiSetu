package com.cyberlabs.krishisetu.ui.screens.chat

import android.content.Intent
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.cyberlabs.krishisetu.R
import com.cyberlabs.krishisetu.chat.ChatViewModel
import com.cyberlabs.krishisetu.util.users.userNameFlow
import com.cyberlabs.krishisetu.util.users.userPhoneFlow
import com.cyberlabs.krishisetu.util.users.userProfilePicUrlFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    chatViewModel: ChatViewModel,
    navController: NavController
) {
    val context = LocalContext.current
    val messages by chatViewModel.messages.collectAsState()
    val currentUserId = chatViewModel.currentUserId
    val chatPartnerId = chatViewModel.chatPartnerId
    val partnerName by userNameFlow(chatPartnerId).collectAsState(initial = null)
    val profilePicUrl by userProfilePicUrlFlow(chatPartnerId).collectAsState(initial = null)
    Log.i("ChatViewModel", "Profile Pic Url in composable: $profilePicUrl")
    val phoneNumber by userPhoneFlow(chatPartnerId).collectAsState(initial = null)


    // For scrolling to the bottom when new messages arrive
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.lastIndex)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.Companion.fillMaxHeight(),
                        verticalAlignment = Alignment.Companion.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AsyncImage(
                            model = profilePicUrl,
                            contentDescription = "Profile Picture",
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape),
                            placeholder = painterResource(R.drawable.baseline_account_circle_24),
                            error = painterResource(R.drawable.baseline_account_circle_24),
                            onError = { error ->
                                Log.e("ChatScreen", "Error loading profile picture: ${error.result.throwable.cause?.message}", error.result.throwable)
                            }
                        )
                        Text(
                            text = partnerName ?: "Loading...",
                            fontSize = 18.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.navigateUp()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            val sanitizedNumber = phoneNumber?.trim()
                            sanitizedNumber?.let {
                                if (Patterns.PHONE.matcher(it).matches()) {
                                    val intent = Intent(Intent.ACTION_DIAL).apply {
                                        data = "tel:$it".toUri()
                                    }
                                    context.startActivity(intent)
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Invalid phone number",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Phone,
                            contentDescription = "Call"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .imePadding()
                .padding(innerPadding)
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                items(messages.size) { index ->
                    val currMessage = messages[index]
                    ChatMessageItem(
                        currMessage.content,
                        currMessage.sender.id == currentUserId,
                        currMessage.isRead
                    )
                }
            }
            ChatTextField(
                messageText = chatViewModel.text,
                onMessageChange = { messageText ->
                    chatViewModel.text = messageText
                }
            ) {
                chatViewModel.sendMessage(chatViewModel.text)
            }
        }
    }
}

@Composable
fun ChatTextField(
    modifier: Modifier = Modifier,
    messageText: String = "",
    onMessageChange: (String) -> Unit = {},
    onSendClick: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(8.dp),
        verticalAlignment = Alignment.Bottom,
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, Color.LightGray),
            color = Color(0xFFD4D4D4),
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp)
        ) {
            TextField(
                textStyle = TextStyle.Default.copy(
                    fontSize = 16.sp
                ),
                value = messageText,
                singleLine = false,
                onValueChange = onMessageChange,
                colors = TextFieldDefaults.colors().copy(
                    focusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                ),
                maxLines = 50,
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Send
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                placeholder = {
                    Text(
                        fontSize = 16.sp,
                        text = "Message...",
                        color = Color.DarkGray
                    )
                },
                keyboardActions = KeyboardActions(
                    onSend = {
                        if (messageText.isNotBlank()) onSendClick()
                    }
                )
            )
        }
        IconButton(
            onClick = {
                if (messageText.isNotBlank()) onSendClick()
            },
            modifier = Modifier
                .size(48.dp)
                .background(Color(0xFF000000), CircleShape)
        ) {
            Icon(
                tint = Color.White,
                imageVector = Icons.AutoMirrored.Filled.Send,
                contentDescription = "Send"
            )
        }
    }
}

@Preview
@Composable
fun ChatTextFieldPreview() {
    ChatTextField()
}

@Composable
fun ChatMessageItem(text: String, isOwnMessage: Boolean, isSeen: Boolean) {
    val backgroundColor = if (isOwnMessage) Color(0xFFDCF8C6) else Color(0xFFFFFFFF)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = if (isOwnMessage) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            shape = RoundedCornerShape(30),
            color = backgroundColor,
            modifier = Modifier
                .widthIn(max = LocalConfiguration.current.screenWidthDp.dp.times(if (!isOwnMessage) 0.75f else 0.8f))
                .padding(2.dp),
            tonalElevation = 2.dp,
            shadowElevation = 2.dp
        ) {
            Box(
                modifier = Modifier.padding(top = 12.dp, end = 8.dp, start = 12.dp, bottom = 2.dp)
            ) {
                Text(
                    text = text,
                    fontSize = 16.sp,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(
                            end = if (isOwnMessage) 32.dp else 0.dp,
                            bottom = 12.dp
                        ) // Leave space for tick icon
                )
                if (isOwnMessage) {
                    Icon(
                        painter = painterResource(R.drawable.double_tick),
                        contentDescription = "Seen",
                        tint = if (isSeen) Color(0xFF34B7F1) else Color.Gray,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(24.dp) // WhatsApp's blue tick is ~16dp
                    )
                }
            }
        }
    }
}


@Preview
@Composable
fun ChatMessageItemPreview() {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        ChatMessageItem("Hello, Kya kar rhe ho?\nSoch rhi thi tumse ganna lelu", true, true)
        ChatMessageItem("Lamba hai ji madam lamba teri jaan kasam le lamba", false, true)
        ChatMessageItem("Aajao fir aaj raat self-delivery krne aajao", true, false)
    }
}
