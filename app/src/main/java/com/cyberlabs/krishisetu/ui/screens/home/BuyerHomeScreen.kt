package com.cyberlabs.krishisetu.ui.screens.home

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.cyberlabs.krishisetu.R
import com.cyberlabs.krishisetu.authentication.AuthViewModel
import com.cyberlabs.krishisetu.shopping.cropListing.cropSearch.SearchViewModel
import com.cyberlabs.krishisetu.util.navigation.BuyerBottomBar
import com.cyberlabs.krishisetu.util.navigation.TopBar

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun BuyerHomeScreen(
    vm: SearchViewModel = hiltViewModel(),
    navController: NavController = rememberNavController(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    Scaffold(
        containerColor = Color.White,
        topBar = { TopBar("कृषिसेतु", navController) },
        bottomBar = { BuyerBottomBar(navController) }
    ) { innerPadding ->

        val query by vm.query.collectAsState()
        var active by remember { mutableStateOf(false) }

        Column(modifier = Modifier.padding(innerPadding)) {

            // Search Bar
            SearchBar(
                colors = SearchBarDefaults.colors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(16.dp)
                    .border(
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(2.dp, Color(0xFF939393))
                    ),
                windowInsets = WindowInsets(0),
                inputField = {
                    SearchBarDefaults.InputField(
                        query = query,
                        onQueryChange = { vm.updateQuery(it) },
                        onSearch = {
                            active = false
                            navController.navigate("search/$query")
                            vm.updateRecentSearches(query)
                        },
                        expanded = active,
                        onExpandedChange = { active = it },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = "Search")
                        },
                        placeholder = { Text("Search for crops, items...", color = Color(0xFF7F7F7F)) },
                        trailingIcon = {
                            if (active) {
                                IconButton(onClick = {
                                    if (query.isEmpty()) active = false
                                    else vm.updateQuery("")
                                }) {
                                    Icon(Icons.Default.Close, contentDescription = "Close")
                                }
                            }
                        }
                    )
                },
                expanded = active,
                onExpandedChange = { active = it }
            ) {
                vm.recentSearches.forEach {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                vm.updateQuery(it)
                                active = false
                                navController.navigate("search/$it")
                            }
                            .padding(16.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.outline_history_24),
                            contentDescription = null,
                            tint = Color(0xFF7F7F7F),
                            modifier = Modifier.padding(end = 12.dp)
                        )
                        Text(it, color = Color(0xFF7F7F7F))
                    }
                }
            }

            // Section: Categories (with image-based buttons)
            Text(
                "Shop by Category",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            ) {
                val categories = listOf<Pair<String, Int>>(
                    "Mango" to R.drawable.mango,
                    "Potato" to R.drawable.potato,
                    "Tomato" to R.drawable.tomato,
                    "Wheat" to R.drawable.wheat,
                    "Litchi" to R.drawable.litchi,
                    "Onion" to R.drawable.onion
                )

                items(categories.size) { index ->
                    val (title, resId) = categories[index]
                    CategoryCard(title, resId) {
                        vm.updateQuery(title)
                        navController.navigate("search/$title")
                        vm.updateRecentSearches(title)
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryCard(
    title: String,
    @DrawableRes imageRes: Int,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(12.dp)
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFDFDFD))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                modifier = Modifier
                    .size(80.dp),
                shape = CircleShape,
                color = Color(0xFFF0F0F0),
                shadowElevation = 4.dp
            ) {
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF333333),
                    letterSpacing = 0.15.sp
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}


