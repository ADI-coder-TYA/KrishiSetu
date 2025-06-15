package com.cyberlabs.krishisetu.ui.screens.shopping.cropListing

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.cyberlabs.krishisetu.R
import com.cyberlabs.krishisetu.shopping.cropListing.cropSearch.SearchViewModel
import com.cyberlabs.krishisetu.util.navigation.TopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CropSearchListScreen(
    navController: NavController,
    vm: SearchViewModel = hiltViewModel()
) {
    val searchResults = vm.searchResults.collectAsState()
    val query by vm.query.collectAsState()
    val recentSearches = vm.recentSearches
    var active by remember { mutableStateOf(false) }
    val isSearching by vm.isSearching.collectAsState()

    var listState = rememberLazyGridState()
    // Detect when scrolled to the end
    val shouldLoadMore = remember {
        derivedStateOf {
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()
            val totalItems = listState.layoutInfo.totalItemsCount
            lastVisibleItem != null && lastVisibleItem.index >= totalItems - 1
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore.value && !isSearching) {
            vm.fetchNextPage()
        }
    }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopBar("Search Results", navController, true)
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier.padding(innerPadding)
        ) {
            SearchBar(
                colors = SearchBarDefaults.colors(
                    containerColor = Color.White
                ),
                windowInsets = WindowInsets(0),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(16.dp)
                    .border(
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(2.dp, Color(0xFF939393))
                    ),
                inputField = {
                    SearchBarDefaults.InputField(
                        modifier = Modifier.padding(bottom = 8.dp),
                        query = query,
                        onQueryChange = {
                            vm.updateQuery(it)
                        },
                        onSearch = {
                            active = false
                            navController.navigate("search/$query")
                            vm.updateRecentSearches(query)
                        },
                        expanded = active,
                        onExpandedChange = {
                            active = it
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search"
                            )
                        },
                        placeholder = {
                            Text(
                                text = "Search",
                                color = Color(0xFF7F7F7F)
                            )
                        },
                        trailingIcon = {
                            if (active) {
                                IconButton(
                                    onClick = {
                                        if (query.isEmpty()) active = false
                                        else vm.updateQuery("")
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Close"
                                    )
                                }
                            }
                        }
                    )
                },
                expanded = active,
                onExpandedChange = {
                    active = it
                }
            ) {
                //Display recent searches
                recentSearches.forEach {
                    Row(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Icon(
                            modifier = Modifier.padding(end = 12.dp),
                            painter = painterResource(R.drawable.outline_history_24),
                            contentDescription = null,
                            tint = Color(0xFF7F7F7F)
                        )
                        Text(
                            text = it,
                            color = Color(0xFF7F7F7F)
                        )
                    }
                }
            }

            LazyVerticalGrid(
                modifier = Modifier.padding(vertical = 16.dp),
                state = listState,
                columns = GridCells.Fixed(2)
            ) {
                searchResults.value.forEach { crop ->
                    item {
                        SearchCropCard(
                            modifier = Modifier
                                .clickable {
                                    navController.navigate("cropShop/${crop.id}")
                                },
                            title = crop.title,
                            price = crop.price,
                            imageUrl = crop.imageUrl
                        )
                    }
                }
                if (isSearching) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth()) {
                            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchCropCard(
    modifier: Modifier = Modifier,
    imageUrl: String? = null,
    title: String = "Mango Sindhura",
    price: Int = 50
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        ) {
            AsyncImage(
                model = imageUrl,
                contentDescription = "Crop Image",
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.app_logo),
                error = painterResource(R.drawable.app_logo),
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .background(Color(0xFFF3F3F3))
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    color = Color(0xFF2E7D32),
                    fontWeight = FontWeight.SemiBold
                ),
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "₹$price/kg",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF424242)
                ),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

