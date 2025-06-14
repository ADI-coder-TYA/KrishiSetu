package com.cyberlabs.krishisetu.util.navigation

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.cyberlabs.krishisetu.R

@Composable
fun BuyerBottomBar(navController: NavController, selectedIndex: Int = 0) {
    val bottomItemList = listOf(
        Pair("Home", R.drawable.baseline_home_24),
        Pair("Chat", R.drawable.rounded_chat_bubble_24),
        Pair("Cart", R.drawable.outline_shopping_cart_24),
        Pair("Delivery", R.drawable.filled_delivery_truck_24)
    )

    var selectedOption by remember { mutableStateOf(bottomItemList[selectedIndex].first) }

    BottomAppBar (
        containerColor = Color(0xFFF5F5F5)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            for (item in bottomItemList) {
                BottomNavItem(
                    icon = item.second,
                    title = item.first,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                        .weight(1f),
                    selected = selectedOption == item.first
                ) { route ->
                    if (route.lowercase() == "chat") navController.navigate("chat/buyer")
                    else if (route.lowercase() == "delivery") navController.navigate("buyer_delivery")
                    else navController.navigate(route.lowercase())
                    selectedOption = route
                }
            }
        }
    }
}

@Composable
fun FarmerBottomBar(navController: NavController, selectedIndex: Int = 0) {
    val bottomItemList = listOf(
        Pair("Home", R.drawable.baseline_home_24),
        Pair("Chat", R.drawable.rounded_chat_bubble_24),
        Pair("Crops", R.drawable.baseline_crop_24),
        Pair("Delivery", R.drawable.filled_delivery_truck_24)
    )

    var selectedOption by remember { mutableStateOf(bottomItemList[selectedIndex].first) }

    BottomAppBar(
        containerColor = Color(0xFFF5F5F5)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            for (item in bottomItemList) {
                BottomNavItem(
                    icon = item.second,
                    title = item.first,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                        .weight(1f),
                    selected = selectedOption == item.first
                ) { route ->
                    if (route.lowercase() == "chat") navController.navigate("chat/farmer")
                    else navController.navigate(route.lowercase())
                    selectedOption = route
                }
            }
        }
    }
}

@Composable
fun BottomNavItem(
    modifier: Modifier = Modifier,
    @DrawableRes icon: Int,
    title: String,
    selected: Boolean = false,
    onClick: (String) -> Unit,
) {
    IconButton(
        colors = IconButtonDefaults.iconButtonColors().copy(
            containerColor = if (selected) Color.White else Color.Transparent
        ),
        modifier = modifier,
        onClick = {
            onClick(title)
        }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                tint = if (selected) Color(0xFFAA7705) else Color.Black,
                painter = painterResource(id = icon),
                contentDescription = title
            )
            Text(
                color = if (selected) Color(0xFFAA7705) else Color.Black,
                text = title
            )
        }
    }
}
