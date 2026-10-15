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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.LanguageHelper
import com.example.ui.theme.FastGreenPrimary
import com.example.ui.theme.FastOrangeAccent
import com.example.viewmodel.MarketplaceViewModel
import com.example.viewmodel.ScreenDestination

@Composable
fun CartScreen(
    viewModel: MarketplaceViewModel,
    modifier: Modifier = Modifier
) {
    val language by viewModel.currentLanguage.collectAsState()
    val cartItems by viewModel.cart.collectAsState()
    val subtotal by viewModel.cartSubtotal.collectAsState()
    val appliedCoupon by viewModel.appliedCoupon.collectAsState()
    val address by viewModel.checkoutAddress.collectAsState()
    val isBn = language.isBengali()

    var inputCoupon by remember { mutableStateOf(appliedCoupon) }

    val deliveryFee = if (address.isInsideDhaka) 60.0 else 120.0
    val discount = if (appliedCoupon in listOf("FAST50", "EID2026", "WELCOME")) 50.0 else 0.0
    val total = maxOf(0.0, (subtotal + deliveryFee) - discount)

    if (cartItems.isEmpty()) {
        // Empty State
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(24.dp)
                .testTag("cart_empty_state"),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .background(Color(0xFFE8F5E9), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = "Empty Cart",
                        tint = FastGreenPrimary,
                        modifier = Modifier.size(46.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = if (isBn) "আপনার শপিং কার্ট খালি রয়েছে" else "Your Shopping Cart is Empty",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = if (isBn) "পণ্য যোগ করতে হোমপেজে যান অথবা আমাদের এআই অ্যাসিস্ট্যান্টের সাহায্য নিন" else "Explore fresh marketplace items or ask our AI Order Assistant",
                    fontSize = 13.sp,
                    color = Color.Gray,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = { viewModel.navigateTo(ScreenDestination.HOME) },
                    colors = ButtonDefaults.buttonColors(containerColor = FastGreenPrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("cart_explore_button")
                ) {
                    Text(
                        text = if (isBn) "কেনাকাটা শুরু করুন" else "Start Shopping",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        return
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp)
            .testTag("cart_screen"),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isBn) "শপিং কার্ট (${cartItems.size}টি পণ্য)" else "Shopping Cart (${cartItems.size} items)",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (isBn) "সব মুছুন" else "Clear All",
                    fontSize = 12.sp,
                    color = Color.Red,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { viewModel.clearCart() }
                )
            }
        }

        // Cart Item Cards
        items(cartItems, key = { it.product.id }) { item ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("cart_item_${item.product.id}")
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Image
                    AsyncImage(
                        model = item.product.imageUrl,
                        contentDescription = item.product.localizedName(language),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(70.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFEEEEEE))
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.product.localizedName(language),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                        Text(
                            text = "${item.product.vendorName}",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = LanguageHelper.formatBdt(item.product.price, language),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = FastGreenPrimary
                        )
                    }

                    // Quantity Controls
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(Color(0xFFF1F5F2), RoundedCornerShape(8.dp))
                            .padding(2.dp)
                    ) {
                        IconButton(
                            onClick = { viewModel.updateCartQuantity(item.product.id, item.quantity - 1) },
                            modifier = Modifier.size(28.dp)
                        ) {
                            if (item.quantity == 1) {
                                Icon(imageVector = Icons.Default.DeleteOutline, contentDescription = "Delete", tint = Color.Red, modifier = Modifier.size(16.dp))
                            } else {
                                Icon(imageVector = Icons.Default.Remove, contentDescription = "Decrease", modifier = Modifier.size(16.dp))
                            }
                        }

                        Text(
                            text = if (isBn) LanguageHelper.toBengaliNumerals(item.quantity) else item.quantity.toString(),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )

                        IconButton(
                            onClick = { viewModel.updateCartQuantity(item.product.id, item.quantity + 1) },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "Increase", modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }

        // Coupon Section
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.LocalOffer, contentDescription = null, tint = FastOrangeAccent, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isBn) "প্রমো কুপন কোড (FAST50)" else "Promo Coupon Code",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = inputCoupon,
                            onValueChange = { inputCoupon = it },
                            placeholder = { Text("FAST50", fontSize = 12.sp) },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .testTag("coupon_input")
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = { viewModel.applyCoupon(inputCoupon) },
                            colors = ButtonDefaults.buttonColors(containerColor = FastGreenPrimary),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .height(50.dp)
                                .testTag("apply_coupon_button")
                        ) {
                            Text(text = if (isBn) "প্রয়োগ" else "Apply")
                        }
                    }

                    if (discount > 0) {
                        Text(
                            text = if (isBn) "✓ ৫০ ৳ মূল্যছাড় সক্রিয় হয়েছে!" else "✓ ৳50 Discount Applied!",
                            fontSize = 11.sp,
                            color = Color(0xFF2E7D32),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }

        // Price Summary Card
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = if (isBn) "মূল্য বিবরণী" else "Order Summary",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Subtotal
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = if (isBn) "পণ্য উপমোট" else "Subtotal", fontSize = 13.sp, color = Color.Gray)
                        Text(text = LanguageHelper.formatBdt(subtotal, language), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Delivery Fee
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (isBn) {
                                if (address.isInsideDhaka) "ডেলিভারি চার্জ (ঢাকা সিটি)" else "ডেলিভারি চার্জ (ঢাকার বাইরে)"
                            } else {
                                if (address.isInsideDhaka) "Delivery Fee (Inside Dhaka)" else "Delivery Fee (Outside Dhaka)"
                            },
                            fontSize = 13.sp,
                            color = Color.Gray
                        )
                        Text(text = LanguageHelper.formatBdt(deliveryFee, language), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }

                    if (discount > 0) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = if (isBn) "কুপন ছাড়" else "Promo Discount", fontSize = 13.sp, color = Color(0xFF2E7D32))
                            Text(text = "- ${LanguageHelper.formatBdt(discount, language)}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Divider(color = Color(0xFFEEEEEE))
                    Spacer(modifier = Modifier.height(10.dp))

                    // Grand Total
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (isBn) "সর্বমোট প্রদেয়" else "Total Payable",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (isBn) "(ক্যাশ অন ডেলিভারি)" else "(Cash on Delivery)",
                                fontSize = 10.sp,
                                color = Color.Gray
                            )
                        }
                        Text(
                            text = LanguageHelper.formatBdt(total, language),
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Black,
                            color = FastGreenPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Checkout Button
                    Button(
                        onClick = { viewModel.navigateTo(ScreenDestination.CHECKOUT) },
                        colors = ButtonDefaults.buttonColors(containerColor = FastOrangeAccent),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("cart_proceed_checkout_button")
                    ) {
                        Text(
                            text = if (isBn) "চেকআউটে এগিয়ে যান" else "Proceed to Checkout",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
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
