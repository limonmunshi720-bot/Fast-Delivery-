package com.example.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.StatusCancelled
import com.example.ui.theme.StatusConfirmed
import com.example.ui.theme.StatusDelivered
import com.example.ui.theme.StatusPending
import com.example.ui.theme.StatusProcessing
import com.example.ui.theme.StatusShipped

enum class OrderStatus(
    val key: String,
    val enLabel: String,
    val bnLabel: String,
    val color: Color
) {
    PENDING("pending", "Pending Verification", "অপেক্ষমাণ", StatusPending),
    CONFIRMED("confirmed", "Order Confirmed", "নিশ্চিতকৃত", StatusConfirmed),
    PROCESSING("processing", "Packaging & Processing", "প্রক্রিয়াধীন", StatusProcessing),
    SHIPPED("shipped", "Shipped with Courier", "পাঠানো হয়েছে", StatusShipped),
    DELIVERED("delivered", "Delivered Successfully", "ডেলিভারি সম্পন্ন", StatusDelivered),
    CANCELLED("cancelled", "Cancelled", "বাতিল", StatusCancelled),
    RETURN_REQUESTED("return_requested", "Return Requested", "ফেরত আবেদন", Color(0xFFC2185B));

    fun localized(lang: Language): String = if (lang.isBengali()) bnLabel else enLabel
}

data class CustomerAddress(
    val recipientName: String,
    val phone: String,
    val division: String, // e.g. "Dhaka", "Chittagong"
    val district: String, // e.g. "Dhaka City", "Gazipur", "Cumilla"
    val thana: String,    // e.g. "Dhanmondi", "Mirpur", "Kotwali"
    val streetAddress: String,
    val deliveryNotes: String = ""
) {
    val isInsideDhaka: Boolean
        get() = division.equals("Dhaka", ignoreCase = true) || district.contains("Dhaka", ignoreCase = true)
}

data class OrderProductSnapshot(
    val productId: String,
    val nameEn: String,
    val nameBn: String,
    val unitPrice: Double,
    val quantity: Int,
    val vendorName: String
) {
    val total: Double get() = unitPrice * quantity
    fun localizedName(lang: Language): String = if (lang.isBengali()) nameBn else nameEn
}

data class OrderTimelineEvent(
    val status: OrderStatus,
    val timestamp: String,
    val description: String,
    val descriptionBn: String
)

data class Order(
    val id: String,
    val items: List<OrderProductSnapshot>,
    val subtotal: Double,
    val deliveryFee: Double,
    val discount: Double = 0.0,
    val totalAmount: Double,
    val status: OrderStatus,
    val address: CustomerAddress,
    val paymentMethod: String = "Cash on Delivery (ক্যাশ অন ডেলিভারি)",
    val courierName: String = "Steadfast Courier / পাঠাও",
    val trackingCode: String,
    val createdAt: String,
    val timeline: List<OrderTimelineEvent> = emptyList(),
    val isAiAssisted: Boolean = false
)
