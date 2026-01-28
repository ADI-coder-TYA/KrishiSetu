package com.cyberlabs.krishisetu.util.navigation

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.activity.ComponentActivity
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
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
import com.cyberlabs.krishisetu.profile.ProfileViewModel
import com.cyberlabs.krishisetu.shopping.cart.CartViewModel
import com.cyberlabs.krishisetu.shopping.cropListing.cropSearch.SearchViewModel
import com.cyberlabs.krishisetu.shopping.cropListing.cropShop.CropShopViewModel
import com.cyberlabs.krishisetu.shopping.order.CheckoutViewModel
import com.cyberlabs.krishisetu.shopping.order.FarmerOrdersViewModel
import com.cyberlabs.krishisetu.shopping.order.OrdersViewModel
import com.cyberlabs.krishisetu.shopping.order.delivery.DeliveriesViewModel
import com.cyberlabs.krishisetu.ui.screens.authentication.ConfirmScreen
import com.cyberlabs.krishisetu.ui.screens.authentication.SignInScreen
import com.cyberlabs.krishisetu.ui.screens.authentication.SignUpScreen
import com.cyberlabs.krishisetu.ui.screens.chat.ChatListScreen
import com.cyberlabs.krishisetu.ui.screens.chat.ChatScreen
import com.cyberlabs.krishisetu.ui.screens.crops.CropUploadScreen
import com.cyberlabs.krishisetu.ui.screens.crops.CropsScreen
import com.cyberlabs.krishisetu.ui.screens.home.HomeScreen
import com.cyberlabs.krishisetu.ui.screens.profile.ProfileScreen
import com.cyberlabs.krishisetu.ui.screens.shopping.cart.CartScreen
import com.cyberlabs.krishisetu.ui.screens.shopping.cropListing.CropSearchListScreen
import com.cyberlabs.krishisetu.ui.screens.shopping.cropListing.CropShopScreen
import com.cyberlabs.krishisetu.ui.screens.shopping.order.BuyerOrderScreen
import com.cyberlabs.krishisetu.ui.screens.shopping.order.CheckoutScreen
import com.cyberlabs.krishisetu.ui.screens.shopping.order.FarmerOrderScreen
import com.cyberlabs.krishisetu.ui.screens.shopping.order.delivery.BuyerDeliveryScreen
import com.cyberlabs.krishisetu.ui.screens.shopping.order.delivery.FarmerDeliveryScreen

@Composable
fun AppNavHost(vm: AuthViewModel, nav: NavHostController, startDestination: String = "signUp") {
    NavHost(
        navController = nav,
        startDestination = startDestination,
        enterTransition = {
            EnterTransition.None
        },
        exitTransition = {
            ExitTransition.None
        }
    ) {
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
        composable("profile") {
            val profileViewModel: ProfileViewModel = hiltViewModel()
            val authViewModel: AuthViewModel = hiltViewModel()
            ProfileScreen(profileViewModel, authViewModel, nav)
        }
        composable(
            route = "chat/{role}",
            arguments = listOf(
                navArgument("role") { type = NavType.StringType }
            )
        ) {
            val viewModel: ChatListViewModel = hiltViewModel()
            ChatListScreen(nav, viewModel)
        }
        composable("cart") {
            val viewModel: CartViewModel = hiltViewModel()
            CartScreen(nav, viewModel)
        }
        composable(
            route = "checkout/{buyerId}/{paymentMode}",
            arguments = listOf(
                navArgument("buyerId") { type = NavType.StringType },
                navArgument("paymentMode") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val activity = LocalContext.current.findActivity() as ComponentActivity
            val viewModel: CartViewModel = hiltViewModel(activity)
            val buyerId = backStackEntry.arguments?.getString("buyerId")
            val checkoutViewModel: CheckoutViewModel = hiltViewModel(activity)
            val paymentMode = backStackEntry.arguments?.getString("paymentMode")
            CheckoutScreen(
                nav,
                buyerId = buyerId ?: "",
                cartViewModel = viewModel,
                checkoutViewModel = checkoutViewModel,
                paymentMode = paymentMode ?: "COD"
            )
        }
        composable("buyer_delivery") {
            val deliveriesViewModel: DeliveriesViewModel = hiltViewModel()
            BuyerDeliveryScreen(nav, deliveriesViewModel)
        }
        composable("buyer_orders") {
            val ordersViewModel: OrdersViewModel = hiltViewModel()
            BuyerOrderScreen(nav, ordersViewModel)
        }
        composable(
            route = "farmer_delivery/{isFarmer}",
            arguments = listOf(
                navArgument("isFarmer") { type = NavType.BoolType }
            )
        ) {
            val deliveriesViewModel: DeliveriesViewModel = hiltViewModel()
            FarmerDeliveryScreen(nav, deliveriesViewModel)
        }
        composable("farmer_orders") {
            val farmerOrdersViewModel: FarmerOrdersViewModel = hiltViewModel()
            FarmerOrderScreen(nav, farmerOrdersViewModel)
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

fun Context.findActivity(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    return null
}
