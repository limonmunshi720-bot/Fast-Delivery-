package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.AppBottomNav
import com.example.ui.components.AppTopBar
import com.example.ui.screens.AdminPanelScreen
import com.example.ui.screens.AiAssistantScreen
import com.example.ui.screens.CartScreen
import com.example.ui.screens.CategoriesScreen
import com.example.ui.screens.CheckoutScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.OrderTrackingScreen
import com.example.ui.screens.OrdersScreen
import com.example.ui.screens.PlayStoreGuideScreen
import com.example.ui.screens.ProductDetailScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.VendorPortalScreen
import com.example.ui.theme.FastDeliveryTheme
import com.example.viewmodel.MarketplaceViewModel
import com.example.viewmodel.ScreenDestination

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FastDeliveryTheme {
                FastDeliveryApp()
            }
        }
    }
}

@Composable
fun FastDeliveryApp(
    viewModel: MarketplaceViewModel = viewModel()
) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val currentLanguage by viewModel.currentLanguage.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val cartCount by viewModel.cartCount.collectAsState()

    // Handle system back navigation to return to Home
    if (currentScreen != ScreenDestination.HOME) {
        BackHandler {
            when (currentScreen) {
                ScreenDestination.PRODUCT_DETAIL -> viewModel.navigateTo(ScreenDestination.HOME)
                ScreenDestination.CHECKOUT -> viewModel.navigateTo(ScreenDestination.CART)
                ScreenDestination.ORDER_TRACKING -> viewModel.navigateTo(ScreenDestination.ORDERS)
                ScreenDestination.PLAY_STORE_GUIDE -> viewModel.navigateTo(ScreenDestination.PROFILE)
                else -> viewModel.navigateTo(ScreenDestination.HOME)
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            // Hide global top bar on screens that provide their own top bar
            val hasCustomTopBar = currentScreen in listOf(
                ScreenDestination.PRODUCT_DETAIL,
                ScreenDestination.CHECKOUT,
                ScreenDestination.ORDER_TRACKING,
                ScreenDestination.ADMIN_PANEL,
                ScreenDestination.PLAY_STORE_GUIDE
            )
            if (!hasCustomTopBar) {
                AppTopBar(
                    currentLanguage = currentLanguage,
                    currentRole = currentUser.role,
                    cartItemCount = cartCount,
                    onLanguageToggle = { viewModel.toggleLanguage() },
                    onCartClick = { viewModel.navigateTo(ScreenDestination.CART) },
                    onAdminClick = { viewModel.navigateTo(ScreenDestination.ADMIN_PANEL) },
                    onVendorClick = { viewModel.navigateTo(ScreenDestination.VENDOR_PORTAL) }
                )
            }
        },
        bottomBar = {
            // Hide bottom navigation on full-screen flows
            val showBottomNav = currentScreen !in listOf(
                ScreenDestination.CHECKOUT,
                ScreenDestination.PRODUCT_DETAIL,
                ScreenDestination.ORDER_TRACKING,
                ScreenDestination.ADMIN_PANEL,
                ScreenDestination.PLAY_STORE_GUIDE
            )
            if (showBottomNav) {
                AppBottomNav(
                    currentDestination = currentScreen,
                    currentLanguage = currentLanguage,
                    cartItemCount = cartCount,
                    onNavigate = { dest -> viewModel.navigateTo(dest) }
                )
            }
        }
    ) { innerPadding ->
        val contentModifier = Modifier.padding(innerPadding)

        when (currentScreen) {
            ScreenDestination.HOME -> HomeScreen(viewModel, contentModifier)
            ScreenDestination.CATEGORIES -> CategoriesScreen(viewModel, contentModifier)
            ScreenDestination.PRODUCT_DETAIL -> ProductDetailScreen(viewModel)
            ScreenDestination.CART -> CartScreen(viewModel, contentModifier)
            ScreenDestination.CHECKOUT -> CheckoutScreen(viewModel)
            ScreenDestination.ORDERS -> OrdersScreen(viewModel, contentModifier)
            ScreenDestination.ORDER_TRACKING -> OrderTrackingScreen(viewModel)
            ScreenDestination.AI_ASSISTANT -> AiAssistantScreen(viewModel, contentModifier)
            ScreenDestination.VENDOR_PORTAL -> VendorPortalScreen(viewModel, contentModifier)
            ScreenDestination.ADMIN_PANEL -> AdminPanelScreen(viewModel)
            ScreenDestination.PLAY_STORE_GUIDE -> PlayStoreGuideScreen(viewModel)
            ScreenDestination.PROFILE -> ProfileScreen(viewModel, contentModifier)
        }
    }
}
