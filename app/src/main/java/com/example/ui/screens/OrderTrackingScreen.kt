package com.example.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.model.OrderStatus
import com.example.ui.theme.FastGreenPrimary
import com.example.ui.theme.FastOrangeAccent
import com.example.viewmodel.MarketplaceViewModel
import com.example.viewmodel.ScreenDestination

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderTrackingScreen(
    viewModel: MarketplaceViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateTo(ScreenDestination.ORDERS)
    }

    val order = viewModel.selectedOrder.collectAsState().value ?: return
    val language by viewModel.currentLanguage.collectAsState()
    val isBn = language.isBengali()

    var showCancelDialog by remember { mutableStateOf(false) }
    var cancelReason by remember { mutableStateOf("") }

    var showReturnDialog by remember { mutableStateOf(false) }
    var returnReason by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isBn) "অর্ডার ট্র্যাকিং ও বিস্তারিত" else "Order Tracking & Details",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateTo(ScreenDestination.ORDERS) },
                        modifier = Modifier.testTag("order_tracking_back_button")
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(14.dp)
                .testTag("order_tracking_screen"),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Tracking Header Card
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Order #${order.id}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${order.courierName}",
                                fontSize = 12.sp,
                                color = FastGreenPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Surface(
                            color = order.status.color.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = order.status.localized(language),
                                color = order.status.color,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Tracking Code Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF1F5F2), RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (isBn) "কুরিয়ার ট্র্যাকিং কোড:" else "Courier Tracking ID:",
                                fontSize = 10.sp,
                                color = Color.Gray
                            )
                            Text(
                                text = order.trackingCode,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = FastGreenPrimary
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy",
                            tint = Color.Gray,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Interactive Step Timeline
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isBn) "ডেলিভারি অগ্রগতি টাইমলাইন" else "Delivery Progress Timeline",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    val steps = listOf(
                        OrderStatus.PENDING,
                        OrderStatus.CONFIRMED,
                        OrderStatus.PROCESSING,
                        OrderStatus.SHIPPED,
                        OrderStatus.DELIVERED
                    )

                    val currentIdx = steps.indexOf(order.status).let { if (it < 0) 0 else it }

                    steps.forEachIndexed { index, step ->
                        val isDone = index <= currentIdx
                        val isCurrent = index == currentIdx

                        Row(
                            verticalAlignment = Alignment.Top,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Step circle and line
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.width(32.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .background(
                                            when {
                                                isCurrent -> FastOrangeAccent
                                                isDone -> FastGreenPrimary
                                                else -> Color(0xFFE0E0E0)
                                            },
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isDone) {
                                        Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                    }
                                }

                                if (index < steps.lastIndex) {
                                    Box(
                                        modifier = Modifier
                                            .width(2.dp)
                                            .height(36.dp)
                                            .background(if (index < currentIdx) FastGreenPrimary else Color(0xFFE0E0E0))
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column(modifier = Modifier.padding(bottom = if (index < steps.lastIndex) 16.dp else 0.dp)) {
                                Text(
                                    text = step.localized(language),
                                    fontSize = 13.sp,
                                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.SemiBold,
                                    color = if (isCurrent) FastOrangeAccent else if (isDone) FastGreenPrimary else Color.Gray
                                )
                                Text(
                                    text = when (step) {
                                        OrderStatus.PENDING -> if (isBn) "অর্ডার সার্ভারে সফলভাবে সংরক্ষিত" else "Order registered on server"
                                        OrderStatus.CONFIRMED -> if (isBn) "বিক্রেতা কর্তৃক আইটেম নিশ্চিত" else "Items confirmed by seller"
                                        OrderStatus.PROCESSING -> if (isBn) "হাব-এ প্যাকেজিং ও প্যাকিং চলছে" else "Packaging at fulfillment hub"
                                        OrderStatus.SHIPPED -> if (isBn) "স্টেডফাস্ট/পাঠাও রাইডারের কাছে হস্তান্তর" else "Handed to courier delivery rider"
                                        OrderStatus.DELIVERED -> if (isBn) "ক্যাশ অন ডেলিভারি সফল" else "Delivered & COD collected"
                                        else -> ""
                                    },
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }
            }

            // Customer Delivery Address
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = FastGreenPrimary, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isBn) "ডেলিভারি ঠিকানা" else "Shipping Address",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "${order.address.recipientName} (${order.address.phone})",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${order.address.streetAddress}, ${order.address.thana}, ${order.address.district}, ${order.address.division}",
                        fontSize = 12.sp,
                        color = Color.DarkGray
                    )
                    if (order.address.deliveryNotes.isNotEmpty()) {
                        Text(
                            text = if (isBn) "নোট: ${order.address.deliveryNotes}" else "Note: ${order.address.deliveryNotes}",
                            fontSize = 11.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }

            // Ordered Products List
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = if (isBn) "অর্ডারকৃত পণ্যসমূহ" else "Ordered Products",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    order.items.forEach { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.localizedName(language),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "${item.vendorName} • পরিমাণ: ${if (isBn) LanguageHelper.toBengaliNumerals(item.quantity) else item.quantity}",
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )
                            }
                            Text(
                                text = LanguageHelper.formatBdt(item.total, language),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = FastGreenPrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Divider(color = Color(0xFFEEEEEE))
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = if (isBn) "পণ্য উপমোট" else "Subtotal", fontSize = 12.sp, color = Color.Gray)
                        Text(text = LanguageHelper.formatBdt(order.subtotal, language), fontSize = 12.sp)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = if (isBn) "ডেলিভারি ফি" else "Delivery Fee", fontSize = 12.sp, color = Color.Gray)
                        Text(text = LanguageHelper.formatBdt(order.deliveryFee, language), fontSize = 12.sp)
                    }

                    if (order.discount > 0) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = if (isBn) "কুপন ছাড়" else "Discount", fontSize = 12.sp, color = Color(0xFF2E7D32))
                            Text(text = "- ${LanguageHelper.formatBdt(order.discount, language)}", fontSize = 12.sp, color = Color(0xFF2E7D32))
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = if (isBn) "সর্বমোট (ক্যাশ অন ডেলিভারি)" else "Total (COD)", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text(text = LanguageHelper.formatBdt(order.totalAmount, language), fontSize = 16.sp, fontWeight = FontWeight.Black, color = FastGreenPrimary)
                    }
                }
            }

            // Action Buttons (Cancel / Return)
            if (order.status == OrderStatus.PENDING) {
                OutlinedButton(
                    onClick = { showCancelDialog = true },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("cancel_order_button")
                ) {
                    Icon(imageVector = Icons.Default.Cancel, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = if (isBn) "অর্ডার বাতিল করুন" else "Cancel Order")
                }
            }

            if (order.status == OrderStatus.DELIVERED) {
                Button(
                    onClick = { showReturnDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = FastOrangeAccent),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("request_return_button")
                ) {
                    Icon(imageVector = Icons.Default.Replay, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = if (isBn) "৭ দিনের রিটার্ন আবেদন করুন" else "Request 7-Day Return")
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }

    // Cancel Dialog
    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title = { Text(if (isBn) "অর্ডার বাতিলের কারণ" else "Reason for Cancellation") },
            text = {
                OutlinedTextField(
                    value = cancelReason,
                    onValueChange = { cancelReason = it },
                    placeholder = { Text(if (isBn) "ভুল পণ্য নির্বাচন বা ঠিকানা পরিবর্তন..." else "Wrong item or address...") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.cancelOrder(order.id, cancelReason.ifEmpty { "Customer request" })
                        showCancelDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text(if (isBn) "বাতিল নিশ্চিত" else "Confirm Cancel")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCancelDialog = false }) {
                    Text(if (isBn) "ফিরে যান" else "Back")
                }
            }
        )
    }

    // Return Dialog
    if (showReturnDialog) {
        AlertDialog(
            onDismissRequest = { showReturnDialog = false },
            title = { Text(if (isBn) "পণ্য ফেরত বা পরিবর্তনের কারণ" else "Reason for Return Request") },
            text = {
                OutlinedTextField(
                    value = returnReason,
                    onValueChange = { returnReason = it },
                    placeholder = { Text(if (isBn) "ত্রুটিযুক্ত পণ্য বা সাইজ সমস্যা..." else "Damaged package or wrong size...") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.requestOrderReturn(order.id, returnReason.ifEmpty { "Defect reported" })
                        showReturnDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FastOrangeAccent)
                ) {
                    Text(if (isBn) "আবেদন পাঠান" else "Submit Request")
                }
            },
            dismissButton = {
                TextButton(onClick = { showReturnDialog = false }) {
                    Text(if (isBn) "বাতিল" else "Dismiss")
                }
            }
        )
    }
}
