package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddBusiness
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.LanguageHelper
import com.example.model.ProductCategory
import com.example.model.UserRole
import com.example.ui.theme.FastGreenPrimary
import com.example.ui.theme.FastOrangeAccent
import com.example.ui.theme.StatusPending
import com.example.viewmodel.MarketplaceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VendorPortalScreen(
    viewModel: MarketplaceViewModel,
    modifier: Modifier = Modifier
) {
    val language by viewModel.currentLanguage.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val vendors by viewModel.vendors.collectAsState()
    val allProducts by viewModel.products.collectAsState()
    val allPending by viewModel.pendingProducts.collectAsState()
    val allOrders by viewModel.orders.collectAsState()
    val isBn = language.isBengali()

    // Determine current vendor (isolated)
    val currentVendor = vendors.find { it.id == currentUser.vendorId } ?: vendors.first()

    // Isolated products & orders for this seller
    val myApprovedProducts = allProducts.filter { it.vendorId == currentVendor.id }
    val myPendingProducts = allPending.filter { it.vendorId == currentVendor.id }
    val myOrders = allOrders.filter { order ->
        order.items.any { it.vendorName == currentVendor.shopName }
    }

    var selectedTab by remember { mutableIntStateOf(0) }

    // New Product Form State
    var pNameEn by remember { mutableStateOf("") }
    var pNameBn by remember { mutableStateOf("") }
    var pCategory by remember { mutableStateOf(ProductCategory.GROCERIES) }
    var pPrice by remember { mutableStateOf("") }
    var pOriginalPrice by remember { mutableStateOf("") }
    var pStock by remember { mutableStateOf("") }
    var pUnit by remember { mutableStateOf("1 Piece") }
    var pDescEn by remember { mutableStateOf("") }
    var pDescBn by remember { mutableStateOf("") }
    var formSuccessMessage by remember { mutableStateOf("") }
    var categoryDropdownOpen by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF7FAF7))
            .testTag("vendor_portal_screen")
    ) {
        // Vendor Header
        Surface(
            color = FastGreenPrimary,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = currentVendor.shopName,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            if (currentVendor.isVerified) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Verified Vendor",
                                    tint = FastOrangeAccent,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        Text(
                            text = if (isBn) "ভেন্ডর আইডি: ${currentVendor.id} • ${currentVendor.district}" else "Vendor ID: ${currentVendor.id} • ${currentVendor.district}",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }

                    Surface(
                        color = FastOrangeAccent,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = if (isBn) "সেলার প্যানেল" else "Seller Mode",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        // Tabs
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.White,
            contentColor = FastGreenPrimary,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = FastGreenPrimary
                )
            }
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text(if (isBn) "ওভারভিউ" else "Overview", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text(if (isBn) "পণ্য যোগ" else "Add Item", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = { Text(if (isBn) "আমার পণ্য (${myApprovedProducts.size + myPendingProducts.size})" else "My Items", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = selectedTab == 3,
                onClick = { selectedTab = 3 },
                text = { Text(if (isBn) "অর্ডার (${myOrders.size})" else "Orders", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
            )
        }

        // Tab Content
        when (selectedTab) {
            0 -> {
                // Overview & Stats
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // KPI Grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Total Sales
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Icon(imageVector = Icons.Default.MonetizationOn, contentDescription = null, tint = FastGreenPrimary)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = if (isBn) "মোট বিক্রয়" else "Total Earnings", fontSize = 11.sp, color = Color.Gray)
                                Text(
                                    text = LanguageHelper.formatBdt(currentVendor.totalSales, language),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = FastGreenPrimary
                                )
                            }
                        }

                        // Active Products
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Icon(imageVector = Icons.Default.Inventory2, contentDescription = null, tint = FastOrangeAccent)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = if (isBn) "অনুমোদিত পণ্য" else "Active Products", fontSize = 11.sp, color = Color.Gray)
                                Text(
                                    text = "${myApprovedProducts.size}",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Pending Approvals
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Icon(imageVector = Icons.Default.HourglassTop, contentDescription = null, tint = StatusPending)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = if (isBn) "অপেক্ষমাণ পণ্য" else "Pending Review", fontSize = 11.sp, color = Color.Gray)
                                Text(
                                    text = "${myPendingProducts.size}",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = StatusPending
                                )
                            }
                        }

                        // Orders to Fulfill
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Icon(imageVector = Icons.Default.ShoppingBag, contentDescription = null, tint = FastGreenPrimary)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = if (isBn) "মোট অর্ডার" else "Total Orders", fontSize = 11.sp, color = Color.Gray)
                                Text(
                                    text = "${myOrders.size}",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // Multi-Vendor Architecture Explanation Notice
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = if (isBn) "মাল্টি-ভেন্ডর সুরক্ষা ও নীতিমালা" else "Multi-Vendor Security & Isolation",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = FastGreenPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (isBn) "• বিক্রেতার জমাকৃত সকল পণ্য সুপার অ্যাডমিন কর্তৃক অনুমোদিত হওয়ার পরেই কেবল মূল মার্কেটপ্লেসে প্রদর্শিত হয়।\n• একজন বিক্রেতা অন্য কোনো বিক্রেতার পণ্য বা অর্ডারে হস্তক্ষেপ করতে পারেন না।\n• ১০০% ক্যাশ অন ডেলিভারি হিসাবের মাধ্যমে অর্থ নিরাপদে সরাসরি বিক্রেতার অ্যাকাউন্টে জমা হয়।"
                                else "• Products submitted by vendors require Super Admin approval before going live.\n• Full isolation enforced: Vendors can only view and manage their own inventory.\n• Automated Cash on Delivery payouts upon courier delivery reconciliation.",
                                fontSize = 11.sp,
                                color = Color(0xFF1B5E20),
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }

            1 -> {
                // Add Product Form
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = if (isBn) "নতুন পণ্য জমা দিন (অ্যাডমিন অনুমোদনের জন্য)" else "Submit Product for Admin Approval",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )

                    if (formSuccessMessage.isNotEmpty()) {
                        Surface(
                            color = Color(0xFFE8F5E9),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = formSuccessMessage,
                                color = FastGreenPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }

                    OutlinedTextField(
                        value = pNameEn,
                        onValueChange = { pNameEn = it },
                        label = { Text("Product Name (English)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = pNameBn,
                        onValueChange = { pNameBn = it },
                        label = { Text("পণ্যের নাম (বাংলা)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Category
                    ExposedDropdownMenuBox(
                        expanded = categoryDropdownOpen,
                        onExpandedChange = { categoryDropdownOpen = !categoryDropdownOpen },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = pCategory.localizedName(language),
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(if (isBn) "ক্যাটাগরি" else "Category") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryDropdownOpen) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = categoryDropdownOpen,
                            onDismissRequest = { categoryDropdownOpen = false }
                        ) {
                            ProductCategory.values().filter { it != ProductCategory.ALL }.forEach { cat ->
                                DropdownMenuItem(
                                    text = { Text(cat.localizedName(language)) },
                                    onClick = {
                                        pCategory = cat
                                        categoryDropdownOpen = false
                                    }
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = pPrice,
                            onValueChange = { pPrice = it },
                            label = { Text(if (isBn) "বিক্রয় মূল্য (৳)" else "Sale Price (৳)") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = pOriginalPrice,
                            onValueChange = { pOriginalPrice = it },
                            label = { Text(if (isBn) "রেগুলার মূল্য (৳)" else "Regular Price (৳)") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = pStock,
                            onValueChange = { pStock = it },
                            label = { Text(if (isBn) "মজুদ পরিমাণ (Stock)" else "Stock Count") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = pUnit,
                            onValueChange = { pUnit = it },
                            label = { Text(if (isBn) "একক (e.g. 1 kg / 1 pc)" else "Unit") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    OutlinedTextField(
                        value = pDescBn,
                        onValueChange = { pDescBn = it },
                        label = { Text(if (isBn) "পণ্যের বিবরণ (বাংলা)" else "Description (Bengali)") },
                        maxLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = pDescEn,
                        onValueChange = { pDescEn = it },
                        label = { Text(if (isBn) "পণ্যের বিবরণ (English)" else "Description (English)") },
                        maxLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = {
                            val priceVal = pPrice.toDoubleOrNull() ?: 100.0
                            val origVal = pOriginalPrice.toDoubleOrNull() ?: priceVal
                            val stockVal = pStock.toIntOrNull() ?: 20

                            viewModel.submitSellerProduct(
                                nameEn = pNameEn.ifEmpty { "New Vendor Item" },
                                nameBn = pNameBn.ifEmpty { "নতুন ভেন্ডর পণ্য" },
                                category = pCategory,
                                price = priceVal,
                                originalPrice = origVal,
                                stockCount = stockVal,
                                unit = pUnit,
                                descriptionEn = pDescEn.ifEmpty { "Authentic product from ${currentVendor.shopName}" },
                                descriptionBn = pDescBn.ifEmpty { "${currentVendor.shopName} থেকে সংগৃহীত খাঁটি পণ্য।" }
                            )

                            formSuccessMessage = if (isBn) "✓ পণ্যটি অনুমোদনের জন্য জমা দেওয়া হয়েছে! অ্যাডমিন অনুমোদনের পর এটি মার্কেটপ্লেসে প্রদর্শিত হবে।"
                            else "✓ Submitted for approval! It will go live once reviewed by Super Admin."

                            // Clear inputs
                            pNameEn = ""
                            pNameBn = ""
                            pPrice = ""
                            pOriginalPrice = ""
                            pStock = ""
                            pDescEn = ""
                            pDescBn = ""
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = FastGreenPrimary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("vendor_submit_product_button")
                    ) {
                        Text(
                            text = if (isBn) "অনুমোদনের জন্য জমা দিন" else "Submit for Review",
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(40.dp))
                }
            }

            2 -> {
                // My Products List
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        Text(
                            text = if (isBn) "আমার শপের পণ্য ক্যাটালগ" else "My Product Catalog",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }

                    // Pending
                    items(myPendingProducts) { p ->
                        Card(
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF9C4)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = p.localizedName(language), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text(text = "৳ ${p.price.toInt()} • স্টক: ${p.stockCount} ${p.unit}", fontSize = 11.sp, color = Color.DarkGray)
                                }
                                Surface(
                                    color = StatusPending,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = if (isBn) "অপেক্ষমাণ" else "Pending Review",
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Approved
                    items(myApprovedProducts) { p ->
                        Card(
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = p.localizedName(language), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text(text = "৳ ${p.price.toInt()} • স্টক: ${p.stockCount} ${p.unit}", fontSize = 11.sp, color = Color.DarkGray)
                                }
                                Surface(
                                    color = FastGreenPrimary,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = if (isBn) "সক্রিয়" else "Live",
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }

            3 -> {
                // My Orders
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        Text(
                            text = if (isBn) "আমার শপের কাস্টমার অর্ডার" else "Orders to Fulfill",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }

                    if (myOrders.isEmpty()) {
                        item {
                            Text(
                                text = if (isBn) "কোনো নতুন অর্ডার পাওয়া যায়নি" else "No orders to fulfill currently",
                                color = Color.Gray,
                                modifier = Modifier.padding(vertical = 20.dp)
                            )
                        }
                    } else {
                        items(myOrders) { order ->
                            Card(
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(text = "#${order.id}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text(text = order.status.localized(language), color = order.status.color, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = "গ্রাহক: ${order.address.recipientName} (${order.address.district})", fontSize = 11.sp, color = Color.Gray)
                                    Text(text = "মোট: ৳${order.totalAmount.toInt()} (ক্যাশ অন ডেলিভারি)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = FastGreenPrimary)
                                }
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }
}
