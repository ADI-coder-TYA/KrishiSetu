package com.cyberlabs.krishisetu.util.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.cyberlabs.krishisetu.authentication.AuthViewModel
import com.cyberlabs.krishisetu.chat.ChatListViewModel
import com.cyberlabs.krishisetu.chat.ChatViewModel
import com.cyberlabs.krishisetu.crops.CropUploadViewModel
import com.cyberlabs.krishisetu.crops.CropViewModel
import com.cyberlabs.krishisetu.shopping.cart.CartViewModel
import com.cyberlabs.krishisetu.shopping.cropListing.cropSearch.SearchViewModel
import com.cyberlabs.krishisetu.shopping.cropListing.cropShop.CropShopViewModel
import com.cyberlabs.krishisetu.ui.screens.authentication.ConfirmScreen
import com.cyberlabs.krishisetu.ui.screens.authentication.SignInScreen
import com.cyberlabs.krishisetu.ui.screens.authentication.SignUpScreen
import com.cyberlabs.krishisetu.ui.screens.chat.ChatListScreen
import com.cyberlabs.krishisetu.ui.screens.chat.ChatScreen
import com.cyberlabs.krishisetu.ui.screens.crops.CropUploadScreen
import com.cyberlabs.krishisetu.ui.screens.crops.CropsScreen
import com.cyberlabs.krishisetu.ui.screens.home.HomeScreen
import com.cyberlabs.krishisetu.ui.screens.shopping.cart.CartScreen
import com.cyberlabs.krishisetu.ui.screens.shopping.cropListing.CropSearchListScreen
import com.cyberlabs.krishisetu.ui.screens.shopping.cropListing.CropShopScreen

@Composable
fun AppNavHost(vm: AuthViewModel, nav: NavHostController) {
    NavHost(nav, startDestination = "signUp") {
        composable("signUp") {
            SignUpScreen(
                vm,
                onSignIn = { nav.navigate("signIn") },
                onNext = { nav.navigate("confirm") }
            )
        }
        composable("confirm") { ConfirmScreen(vm) { nav.navigate("signIn") } }
        composable("signIn") {
            SignInScreen(
                vm,
                onSignUp = { nav.navigate("signUp") },
                onSignedIn = { nav.navigate("home") })
        }
        composable("home") { HomeScreen(vm, nav) }
        composable("chat") {
            val viewModel: ChatListViewModel = hiltViewModel()
            ChatListScreen(nav, viewModel)
        }
        composable("cart") {
            val viewModel: CartViewModel = hiltViewModel()
            CartScreen(nav, viewModel)
        }
        composable(
            route = "chatList/{currentUserId}/{chatPartnerId}",
            arguments = listOf(
                navArgument("currentUserId") { type = NavType.StringType },
                navArgument("chatPartnerId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val viewModel: ChatViewModel = hiltViewModel()
            ChatScreen(viewModel, nav)
        }
        composable(
            route = "cropShop/{cropId}",
            arguments = listOf(
                navArgument("cropId") { type = NavType.StringType }
            )) {
            val viewModel: CropShopViewModel = hiltViewModel()
            val authViewModel: AuthViewModel = hiltViewModel()
            val cartViewModel: CartViewModel = hiltViewModel()
            CropShopScreen(viewModel, authViewModel, cartViewModel, nav)
        }
        composable("crops") {
            val viewModel: CropViewModel = hiltViewModel()
            CropsScreen(nav, viewModel)
        }
        composable("cropUpload") {
            val viewModel: CropUploadViewModel = hiltViewModel()
            CropUploadScreen(viewModel, nav)
        }
        composable(
            route = "search/{query}",
            arguments = listOf(
                navArgument("query") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val viewModel: SearchViewModel = hiltViewModel()
            val query = backStackEntry.arguments?.getString("query")
            query?.let { viewModel.updateQuery(query) }
            viewModel.searchCrops()
            CropSearchListScreen(nav, viewModel)
        }
    }
}
