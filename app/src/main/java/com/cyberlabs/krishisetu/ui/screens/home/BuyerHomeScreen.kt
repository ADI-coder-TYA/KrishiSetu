package com.cyberlabs.krishisetu.ui.screens.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.cyberlabs.krishisetu.R
import com.cyberlabs.krishisetu.shopping.cropListing.cropSearch.SearchViewModel
import com.cyberlabs.krishisetu.util.navigation.BuyerBottomBar

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun BuyerHomeScreen(
    vm: SearchViewModel = hiltViewModel(),
    navController: NavController = rememberNavController()
) {
    Scaffold(
        containerColor = Color.White,
        topBar = {
            ElevatedCard(
                colors = CardDefaults.elevatedCardColors(
                    containerColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.elevatedCardElevation(
                    defaultElevation = 4.dp
                ),
                shape = RectangleShape
            ) {
                CenterAlignedTopAppBar(
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.White
                    ),
                    title = {
                        Row(
                            modifier = Modifier.fillMaxHeight(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.app_logo),
                                contentDescription = "Logo",
                                modifier = Modifier.clip(CircleShape)
                            )
                            Text(
                                text = "कृषिसेतु",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = {
                                //TODO: Navigate to profile
                            }
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_person_24),
                                contentDescription = "Profile"
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                //TODO: Navigate to settings
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings"
                            )
                        }
                    }
                )
            }
        },
        bottomBar = {
            BuyerBottomBar(navController)
        }
    ) { innerPadding ->

        val query by vm.query.collectAsState()
        var active by remember { mutableStateOf(false) }

        Column(
            Modifier.padding(innerPadding)
        ) {
            SearchBar(
                colors = SearchBarDefaults.colors(
                    containerColor = Color.White
                ),
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
                vm.recentSearches.forEach {
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
        }
    }
}
