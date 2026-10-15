package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Phone
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
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import com.example.model.CustomerAddress
import com.example.model.LanguageHelper
import com.example.ui.theme.FastGreenPrimary
import com.example.ui.theme.FastOrangeAccent
import com.example.viewmodel.MarketplaceViewModel
import com.example.viewmodel.ScreenDestination

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    viewModel: MarketplaceViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateTo(ScreenDestination.CART)
    }

    val language by viewModel.currentLanguage.collectAsState()
    val isBn = language.isBengali()

    val currentAddress by viewModel.checkoutAddress.collectAsState()
    val subtotal by viewModel.cartSubtotal.collectAsState()
    val appliedCoupon by viewModel.appliedCoupon.collectAsState()
    val cartItems by viewModel.cart.collectAsState()

    var recipientName by remember { mutableStateOf(currentAddress.recipientName) }
    var phone by remember { mutableStateOf(currentAddress.phone) }
    var division by remember { mutableStateOf(currentAddress.division) }
    var district by remember { mutableStateOf(currentAddress.district) }
    var thana by remember { mutableStateOf(currentAddress.thana) }
    var streetAddress by remember { mutableStateOf(currentAddress.streetAddress) }
    var deliveryNotes by remember { mutableStateOf(currentAddress.deliveryNotes) }

    val divisionsList = listOf(
        "Dhaka", "Chittagong", "Rajshahi", "Khulna",
        "Barishal", "Sylhet", "Rangpur", "Mymensingh"
    )
    var divisionExpanded by remember { mutableStateOf(false) }

    val isInsideDhaka = division.equals("Dhaka", ignoreCase = true) || district.contains("Dhaka", ignoreCase = true)
    val deliveryFee = if (isInsideDhaka) 60.0 else 120.0
    val discount = if (appliedCoupon in listOf("FAST50", "EID2026", "WELCOME")) 50.0 else 0.0
    val totalAmount = maxOf(0.0, (subtotal + deliveryFee) - discount)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isBn) "অর্ডার চেকআউট" else "Checkout & Shipping",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateTo(ScreenDestination.CART) },
                        modifier = Modifier.testTag("checkout_back_button")
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
                .testTag("checkout_screen"),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Bangladesh Delivery Address Card
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = FastGreenPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isBn) "ডেলিভারি ঠিকানা (বাংলাদেশ)" else "Delivery Address (Bangladesh)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Full Name
                    OutlinedTextField(
                        value = recipientName,
                        onValueChange = { recipientName = it },
                        label = { Text(if (isBn) "প্রাপকের পুরো নাম" else "Recipient Full Name") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("checkout_name_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Phone Number
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text(if (isBn) "মোবাইল নম্বর (০১XXXXXXXXX)" else "Phone Number") },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Phone, contentDescription = null, tint = Color.Gray)
                        },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("checkout_phone_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Division Dropdown
                    ExposedDropdownMenuBox(
                        expanded = divisionExpanded,
                        onExpandedChange = { divisionExpanded = !divisionExpanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = division,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(if (isBn) "বিভাগ (Division)" else "Division") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = divisionExpanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                                .testTag("checkout_division_dropdown")
                        )
                        ExposedDropdownMenu(
                            expanded = divisionExpanded,
                            onDismissRequest = { divisionExpanded = false }
                        ) {
                            divisionsList.forEach { div ->
                                DropdownMenuItem(
                                    text = { Text(div) },
                                    onClick = {
                                        division = div
                                        divisionExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // District & Thana
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = district,
                            onValueChange = { district = it },
                            label = { Text(if (isBn) "জেলা (District)" else "District") },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("checkout_district_input")
                        )

                        OutlinedTextField(
                            value = thana,
                            onValueChange = { thana = it },
                            label = { Text(if (isBn) "থানা / উপজেলা" else "Thana / Upazila") },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("checkout_thana_input")
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Street Address
                    OutlinedTextField(
                        value = streetAddress,
                        onValueChange = { streetAddress = it },
                        label = { Text(if (isBn) "বাড়ি, রোড, এলাকা বা ল্যান্ডমার্ক" else "Street Address & Landmark") },
                        maxLines = 2,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("checkout_street_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Delivery Note
                    OutlinedTextField(
                        value = deliveryNotes,
                        onValueChange = { deliveryNotes = it },
                        label = { Text(if (isBn) "ডেলিভারি নির্দেশিকা (ঐচ্ছিক)" else "Delivery Notes (Optional)") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("checkout_notes_input")
                    )
                }
            }

            // Payment Method Selection
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Payments, contentDescription = null, tint = FastOrangeAccent, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isBn) "পেমেন্ট পদ্ধতি" else "Payment Method",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // COD - Active Default
                    Surface(
                        color = Color(0xFFE8F5E9),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.5.dp, FastGreenPrimary, RoundedCornerShape(10.dp))
                            .padding(2.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = true,
                                onClick = null,
                                colors = RadioButtonDefaults.colors(selectedColor = FastGreenPrimary)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(
                                    text = if (isBn) "ক্যাশ অন ডেলিভারি (Cash on Delivery)" else "Cash on Delivery (COD)",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = FastGreenPrimary
                                )
                                Text(
                                    text = if (isBn) "পণ্য হাতে পেয়ে রাইডারকে টাকা পরিশোধ করুন (নিরাপদ)" else "Pay upon parcel inspection at your doorstep (Safe & Verified)",
                                    fontSize = 11.sp,
                                    color = Color.DarkGray
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Online payment placeholder info
                    Surface(
                        color = Color(0xFFFAFAFA),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(10.dp))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = false,
                                onClick = null,
                                enabled = false
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(
                                    text = if (isBn) "ডিজিটাল পেমেন্ট (বিকাশ / নগদ / কার্ড)" else "bKash / Nagad / Debit Card (Online)",
                                    fontSize = 13.sp,
                                    color = Color.Gray,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = if (isBn) "পরবর্তী আপডেটে সুরক্ষিত বাংলাদেশি পেমেন্ট গেটওয়ে যুক্ত হবে" else "Secure PG integration ready for production rollout",
                                    fontSize = 10.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }
            }

            // Summary & Confirm Card
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = if (isBn) "চূড়ান্ত অর্ডার বিবরণ" else "Final Order Breakdown",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = if (isBn) "উপমোট (${cartItems.size}টি আইটেম)" else "Subtotal (${cartItems.size} items)", fontSize = 13.sp)
                        Text(text = LanguageHelper.formatBdt(subtotal, language), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (isInsideDhaka) {
                                if (isBn) "ডেলিভারি ফি (ঢাকা সিটি)" else "Delivery Fee (Dhaka City)"
                            } else {
                                if (isBn) "ডেলিভারি ফি (ঢাকার বাইরে)" else "Delivery Fee (Outside Dhaka)"
                            },
                            fontSize = 13.sp
                        )
                        Text(text = LanguageHelper.formatBdt(deliveryFee, language), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }

                    if (discount > 0) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = if (isBn) "কুপন ছাড় (FAST50)" else "Coupon Discount", fontSize = 13.sp, color = Color(0xFF2E7D32))
                            Text(text = "- ${LanguageHelper.formatBdt(discount, language)}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Divider(color = Color(0xFFEEEEEE))
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isBn) "সর্বমোট (ক্যাশ অন ডেলিভারি)" else "Total (COD)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Text(
                            text = LanguageHelper.formatBdt(totalAmount, language),
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp,
                            color = FastGreenPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Final Submit Order Button
                    Button(
                        onClick = {
                            viewModel.checkoutAddress.value = CustomerAddress(
                                recipientName = recipientName.ifEmpty { "Customer" },
                                phone = phone.ifEmpty { "01700-000000" },
                                division = division,
                                district = district.ifEmpty { "Dhaka" },
                                thana = thana.ifEmpty { "Central" },
                                streetAddress = streetAddress.ifEmpty { "Address on file" },
                                deliveryNotes = deliveryNotes
                            )
                            viewModel.submitCheckoutOrder()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = FastGreenPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("checkout_confirm_order_button")
                    ) {
                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isBn) "অর্ডার কনফার্ম করুন" else "Confirm & Place Order",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
