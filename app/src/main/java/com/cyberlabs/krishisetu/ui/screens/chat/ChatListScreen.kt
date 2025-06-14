package com.cyberlabs.krishisetu.ui.screens.chat

import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.cyberlabs.krishisetu.R
import com.cyberlabs.krishisetu.chat.ChatListViewModel
import com.cyberlabs.krishisetu.util.navigation.BuyerBottomBar
import com.cyberlabs.krishisetu.util.navigation.FarmerBottomBar
import com.cyberlabs.krishisetu.util.navigation.TopBar

@Preview
@Composable
fun ChatListItem(
    role: String = "Farmer",
    name: String = "Aditya Jaiswal",
    profilePicUrl: String? = null,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = profilePicUrl,
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .border(1.5.dp, MaterialTheme.colorScheme.primary, CircleShape),
                placeholder = painterResource(id = R.drawable.baseline_account_circle_24),
                error = painterResource(id = R.drawable.baseline_account_circle_24),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
                Text(
                    text = role,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.primary
                    )
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatListScreen(
    navController: NavController,
    chatListViewModel: ChatListViewModel
) {
    val chatList = chatListViewModel.chatList
    val role = chatListViewModel.role

    Scaffold(
        topBar = {
            TopBar("Your Chats", navController)
        },
        bottomBar = {
            if (role == "farmer") {
                FarmerBottomBar(navController, selectedIndex = 1)
            } else if (role == "buyer") {
                BuyerBottomBar(navController, selectedIndex = 1)
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            items(chatList) { partner ->
                //TODO: Replace avatar icon with Profile Picture
                val context = LocalContext.current
                ChatListItem(
                    role = partner.role.name,
                    name = partner.name,
                    profilePicUrl = partner.profilePicUrl,
                    onClick = {
                        //TODO: Navigate to Chat Screen Passing partnerID and currentUserID
                        if (chatListViewModel.currentUserId != null) {
                            navController.navigate("chatList/${chatListViewModel.currentUserId}/${partner.userId}")
                        } else {
                            // Optional: Inform the user
                            Toast.makeText(
                                context,
                                "Please wait, loading user data...",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                )
            }
        }
    }
}
