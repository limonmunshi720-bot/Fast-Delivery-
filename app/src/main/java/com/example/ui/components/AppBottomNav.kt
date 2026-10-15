package com.example.ui.components

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.ListAlt
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.model.Language
import com.example.model.LanguageHelper
import com.example.ui.theme.FastGreenPrimary
import com.example.ui.theme.FastOrangeAccent
import com.example.viewmodel.ScreenDestination

@Composable
fun AppBottomNav(
    currentDestination: ScreenDestination,
    currentLanguage: Language,
    cartItemCount: Int,
    onNavigate: (ScreenDestination) -> Unit
) {
    NavigationBar(
        modifier = Modifier
            .windowInsetsPadding(WindowInsets.navigationBars)
            .testTag("app_bottom_nav"),
        containerColor = Color.White,
        contentColor = FastGreenPrimary
    ) {
        val isBn = currentLanguage.isBengali()

        // Home
        NavigationBarItem(
            selected = currentDestination == ScreenDestination.HOME,
            onClick = { onNavigate(ScreenDestination.HOME) },
            icon = {
                Icon(
                    imageVector = if (currentDestination == ScreenDestination.HOME) Icons.Filled.Home else Icons.Outlined.Home,
                    contentDescription = "Home"
                )
            },
            label = {
                Text(
                    text = if (isBn) "হোম" else "Home",
                    fontSize = 11.sp,
                    fontWeight = if (currentDestination == ScreenDestination.HOME) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = FastGreenPrimary,
                selectedTextColor = FastGreenPrimary,
                indicatorColor = FastGreenPrimary.copy(alpha = 0.15f)
            ),
            modifier = Modifier.testTag("nav_home")
        )

        // Categories
        NavigationBarItem(
            selected = currentDestination == ScreenDestination.CATEGORIES,
            onClick = { onNavigate(ScreenDestination.CATEGORIES) },
            icon = {
                Icon(
                    imageVector = if (currentDestination == ScreenDestination.CATEGORIES) Icons.Filled.Category else Icons.Outlined.Category,
                    contentDescription = "Categories"
                )
            },
            label = {
                Text(
                    text = if (isBn) "ক্যাটাগরি" else "Catalog",
                    fontSize = 11.sp,
                    fontWeight = if (currentDestination == ScreenDestination.CATEGORIES) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = FastGreenPrimary,
                selectedTextColor = FastGreenPrimary,
                indicatorColor = FastGreenPrimary.copy(alpha = 0.15f)
            ),
            modifier = Modifier.testTag("nav_categories")
        )

        // AI Assistant
        NavigationBarItem(
            selected = currentDestination == ScreenDestination.AI_ASSISTANT,
            onClick = { onNavigate(ScreenDestination.AI_ASSISTANT) },
            icon = {
                Icon(
                    imageVector = if (currentDestination == ScreenDestination.AI_ASSISTANT) Icons.Filled.AutoAwesome else Icons.Outlined.AutoAwesome,
                    contentDescription = "AI Assistant",
                    tint = FastOrangeAccent
                )
            },
            label = {
                Text(
                    text = if (isBn) "এআই সহায়ক" else "AI Order",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = FastOrangeAccent
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = FastOrangeAccent,
                selectedTextColor = FastOrangeAccent,
                indicatorColor = FastOrangeAccent.copy(alpha = 0.15f)
            ),
            modifier = Modifier.testTag("nav_ai_assistant")
        )

        // Cart
        NavigationBarItem(
            selected = currentDestination == ScreenDestination.CART,
            onClick = { onNavigate(ScreenDestination.CART) },
            icon = {
                BadgedBox(
                    badge = {
                        if (cartItemCount > 0) {
                            Badge(
                                containerColor = FastOrangeAccent,
                                contentColor = Color.White
                            ) {
                                Text(
                                    text = if (isBn) LanguageHelper.toBengaliNumerals(cartItemCount) else cartItemCount.toString(),
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = if (currentDestination == ScreenDestination.CART) Icons.Filled.ShoppingCart else Icons.Outlined.ShoppingCart,
                        contentDescription = "Cart"
                    )
                }
            },
            label = {
                Text(
                    text = if (isBn) "কার্ট" else "Cart",
                    fontSize = 11.sp,
                    fontWeight = if (currentDestination == ScreenDestination.CART) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = FastGreenPrimary,
                selectedTextColor = FastGreenPrimary,
                indicatorColor = FastGreenPrimary.copy(alpha = 0.15f)
            ),
            modifier = Modifier.testTag("nav_cart")
        )

        // Orders
        NavigationBarItem(
            selected = currentDestination == ScreenDestination.ORDERS || currentDestination == ScreenDestination.ORDER_TRACKING,
            onClick = { onNavigate(ScreenDestination.ORDERS) },
            icon = {
                Icon(
                    imageVector = if (currentDestination == ScreenDestination.ORDERS) Icons.Filled.ListAlt else Icons.Outlined.ListAlt,
                    contentDescription = "Orders"
                )
            },
            label = {
                Text(
                    text = if (isBn) "অর্ডার" else "Orders",
                    fontSize = 11.sp,
                    fontWeight = if (currentDestination == ScreenDestination.ORDERS) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = FastGreenPrimary,
                selectedTextColor = FastGreenPrimary,
                indicatorColor = FastGreenPrimary.copy(alpha = 0.15f)
            ),
            modifier = Modifier.testTag("nav_orders")
        )

        // Profile / Menu
        NavigationBarItem(
            selected = currentDestination == ScreenDestination.PROFILE,
            onClick = { onNavigate(ScreenDestination.PROFILE) },
            icon = {
                Icon(
                    imageVector = if (currentDestination == ScreenDestination.PROFILE) Icons.Filled.Person else Icons.Outlined.Person,
                    contentDescription = "Profile"
                )
            },
            label = {
                Text(
                    text = if (isBn) "প্রোফাইল" else "Account",
                    fontSize = 11.sp,
                    fontWeight = if (currentDestination == ScreenDestination.PROFILE) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = FastGreenPrimary,
                selectedTextColor = FastGreenPrimary,
                indicatorColor = FastGreenPrimary.copy(alpha = 0.15f)
            ),
            modifier = Modifier.testTag("nav_profile")
        )
    }
}
