package com.cyberlabs.krishisetu.ui.screens.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.amplifyframework.datastore.generated.model.UserRole
import com.cyberlabs.krishisetu.authentication.AuthViewModel
import com.cyberlabs.krishisetu.util.users.userRoleFlow

@Composable
fun HomeScreen(vm: AuthViewModel, navController: NavController) {
    val userRole by userRoleFlow(vm).collectAsState(initial = null)

    when (userRole) {
        UserRole.FARMER -> FarmerHomeScreen(navController)
        UserRole.BUYER -> { BuyerHomeScreen(navController = navController)
        }

        UserRole.DELIVERY_AGENT -> { /* DeliveryAgentHomeScreen(navController) */
        }

        null -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }
}
