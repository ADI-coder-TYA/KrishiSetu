package com.cyberlabs.krishisetu

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.amplifyframework.core.Amplify
import com.cyberlabs.krishisetu.authentication.AuthViewModel
import com.cyberlabs.krishisetu.shopping.cart.CartViewModel
import com.cyberlabs.krishisetu.shopping.order.CheckoutViewModel
import com.cyberlabs.krishisetu.ui.theme.KrishiSetuTheme
import com.cyberlabs.krishisetu.util.navigation.AppNavHost
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow

@AndroidEntryPoint
class MainActivity : ComponentActivity(), PaymentResultListener {

    private val authViewModel by viewModels<AuthViewModel>()
    // 1. Inject CheckoutViewModel (must be same instance as in CheckoutScreen)
    private val checkoutViewModel by viewModels<CheckoutViewModel>()
    // 2. Inject CartViewModel to clear items on success
    private val cartViewModel by viewModels<CartViewModel>()

    private val userLogged = MutableStateFlow<Boolean?>(null)

    // 3. Navigation Signal: False = Stay, True = Go to Home
    private val navigateToHome = MutableStateFlow(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        checkUserSession()
        Checkout.preload(applicationContext)

        setContent {
            val isReady by MyApp.amplifyReady.collectAsState(initial = false)
            val isLoggedIn by userLogged.collectAsState()
            val shouldNavigateHome by navigateToHome.collectAsState()

            val navController = rememberNavController()

            // 4. Listen for the Navigation Signal
            LaunchedEffect(shouldNavigateHome) {
                if (shouldNavigateHome) {
                    // Navigate to Home and clear backstack so user can't go back to Checkout
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                    }
                    // Reset the signal
                    navigateToHome.value = false
                }
            }

            KrishiSetuTheme {
                if (!isReady || isLoggedIn == null) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    val startDestination = if (isLoggedIn == true) "home" else "signUp"
                    AppNavHost(
                        vm = authViewModel,
                        nav = navController,
                        startDestination = startDestination
                    )
                }
            }
        }
    }

    private fun checkUserSession() {
        Amplify.Auth.getCurrentUser(
            { user ->
                Log.i("MainActivity", "Logged in as ${user.username}")
                userLogged.value = true
            },
            { error ->
                Log.e("MainActivity", "No logged-in user: $error")
                userLogged.value = false
            }
        )
    }

    // --------------------------------------------------------
    // RAZORPAY CALLBACKS
    // --------------------------------------------------------

    override fun onPaymentSuccess(razorpayPaymentID: String?) {
        Log.i("MainActivity", "Razorpay Success: $razorpayPaymentID")
        Toast.makeText(this, "Payment Successful. Finalizing Order...", Toast.LENGTH_SHORT).show()

        if (razorpayPaymentID != null) {
            checkoutViewModel.confirmOnlineOrder(
                razorpayPaymentID = razorpayPaymentID,
                onSuccess = {
                    // This runs on a background thread usually, so we update state
                    // which Compose observes on the UI thread.

                    // A. Clear the Cart
                    // Ensure your CartViewModel has a function clearCart() exposed
                    cartViewModel.clearCart()

                    runOnUiThread {
                        Toast.makeText(this, "Order Placed Successfully!", Toast.LENGTH_LONG).show()
                        // B. Trigger Navigation
                        navigateToHome.value = true
                    }
                },
                onFail = {
                    runOnUiThread {
                        Toast.makeText(this, "Order Creation Failed. Contact Support.", Toast.LENGTH_LONG).show()
                    }
                }
            )
        }
    }

    override fun onPaymentError(code: Int, response: String?) {
        Log.e("MainActivity", "Razorpay Error $code: $response")
        try {
            Toast.makeText(this, "Payment Failed: $response", Toast.LENGTH_LONG).show()
            // Reset loading state in VM so spinner stops
            checkoutViewModel.isPlacingOrder = false
        } catch (e: Exception) {
            Log.e("MainActivity", "Error showing toast", e)
        }
    }
}
