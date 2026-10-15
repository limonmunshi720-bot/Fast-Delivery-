package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Language
import com.example.model.UserRole
import com.example.ui.theme.FastGreenPrimary
import com.example.ui.theme.FastOrangeAccent
import com.example.viewmodel.MarketplaceViewModel
import com.example.viewmodel.ScreenDestination

@Composable
fun ProfileScreen(
    viewModel: MarketplaceViewModel,
    modifier: Modifier = Modifier
) {
    val language by viewModel.currentLanguage.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val address by viewModel.checkoutAddress.collectAsState()
    val isBn = language.isBengali()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(14.dp)
            .testTag("profile_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // User Profile Header Card
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .background(FastGreenPrimary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(30.dp))
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = currentUser.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = currentUser.email,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        color = when (currentUser.role) {
                            UserRole.ADMIN -> FastOrangeAccent
                            UserRole.SELLER -> FastGreenPrimary
                            else -> Color(0xFF1976D2)
                        },
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = currentUser.role.localized(language),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }

        // Active Role Switcher Card
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = if (isBn) "সক্রিয় ভূমিকা পরিবর্তন করুন (সুইচ মোড)" else "Switch Active Account Mode",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    UserRole.values().forEach { role ->
                        val isSelected = currentUser.role == role
                        Surface(
                            color = if (isSelected) FastGreenPrimary else Color(0xFFF1F5F2),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { viewModel.switchRole(role) }
                                .testTag("role_switch_${role.name}")
                        ) {
                            Text(
                                text = role.localized(language),
                                color = if (isSelected) Color.White else Color.DarkGray,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                modifier = Modifier.padding(vertical = 10.dp)
                            )
                        }
                    }
                }
            }
        }

        // Language Switcher Section
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Language, contentDescription = null, tint = FastGreenPrimary)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(text = if (isBn) "অ্যাপের ভাষা (Language)" else "App Language", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(text = if (isBn) "বাংলা (বাংলাদেশ)" else "English (Global)", fontSize = 11.sp, color = Color.Gray)
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Surface(
                        color = if (language == Language.BN) FastGreenPrimary else Color(0xFFF1F5F2),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.clickable { viewModel.setLanguage(Language.BN) }
                    ) {
                        Text(
                            text = "বাংলা",
                            color = if (language == Language.BN) Color.White else Color.DarkGray,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }

                    Surface(
                        color = if (language == Language.EN) FastGreenPrimary else Color(0xFFF1F5F2),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.clickable { viewModel.setLanguage(Language.EN) }
                    ) {
                        Text(
                            text = "EN",
                            color = if (language == Language.EN) Color.White else Color.DarkGray,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // Menu Navigation Links
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                ProfileMenuItem(
                    icon = Icons.Default.Storefront,
                    title = if (isBn) "মাল্টি-ভেন্ডর সেলার প্যানেল" else "Multi-Vendor Seller Portal",
                    subtitle = if (isBn) "পণ্য জমা দিন, বিক্রি ও অর্ডার দেখুন" else "Submit products, manage inventory & orders",
                    onClick = { viewModel.navigateTo(ScreenDestination.VENDOR_PORTAL) },
                    testTag = "menu_vendor_portal"
                )

                Divider(color = Color(0xFFF0F0F0))

                ProfileMenuItem(
                    icon = Icons.Default.AdminPanelSettings,
                    title = if (isBn) "প্রাইভেট অ্যাডমিন পোর্টাল (/admin)" else "Private Admin Panel (/admin)",
                    subtitle = if (isBn) "পণ্য অনুমোদন, অডিট লগ ও ইন্টিগ্রেশন" else "Product approvals, audit logs, Meta & WhatsApp APIs",
                    onClick = { viewModel.navigateTo(ScreenDestination.ADMIN_PANEL) },
                    testTag = "menu_admin_panel"
                )

                Divider(color = Color(0xFFF0F0F0))

                ProfileMenuItem(
                    icon = Icons.Default.Android,
                    title = if (isBn) "গুগল প্লে স্টোর ও ক্যাপাসিটর প্যাকেজিং গাইড" else "Google Play & Capacitor Packaging Guide",
                    subtitle = if (isBn) "AAB বান্ডেল তৈরি ও প্লে কনসোল নির্দেশিকা" else "Build instructions, keystore & release checklist",
                    onClick = { viewModel.navigateTo(ScreenDestination.PLAY_STORE_GUIDE) },
                    testTag = "menu_play_guide"
                )

                Divider(color = Color(0xFFF0F0F0))

                ProfileMenuItem(
                    icon = Icons.Default.LocationOn,
                    title = if (isBn) "সংরক্ষিত ডেলিভারি ঠিকানা" else "Saved Delivery Address",
                    subtitle = "${address.streetAddress}, ${address.district}",
                    onClick = { viewModel.navigateTo(ScreenDestination.CHECKOUT) },
                    testTag = "menu_address"
                )

                Divider(color = Color(0xFFF0F0F0))

                ProfileMenuItem(
                    icon = Icons.Default.Policy,
                    title = if (isBn) "ক্যাশ অন ডেলিভারি ও ৭ দিনের রিটার্ন পলিসি" else "Cash on Delivery & 7-Day Return Policy",
                    subtitle = if (isBn) "সারাদেশে নিরাপদ ও নির্ভরযোগ্য কেনাকাটা" else "100% verified nationwide policies",
                    onClick = { viewModel.navigateTo(ScreenDestination.AI_ASSISTANT) },
                    testTag = "menu_policy"
                )
            }
        }

        Spacer(modifier = Modifier.height(60.dp))
    }
}

@Composable
fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    testTag: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(14.dp)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(FastGreenPrimary.copy(alpha = 0.12f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = FastGreenPrimary, modifier = Modifier.size(20.dp))
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            Text(text = subtitle, color = Color.Gray, fontSize = 11.sp, maxLines = 1)
        }

        Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(18.dp))
    }
}
