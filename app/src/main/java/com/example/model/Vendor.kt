package com.example.model

enum class VendorStatus {
    APPROVED,
    PENDING_REVIEW,
    SUSPENDED
}

data class Vendor(
    val id: String,
    val shopName: String,
    val ownerName: String,
    val phone: String,
    val division: String,
    val district: String,
    val tradeLicenseOrNid: String,
    val pickupAddress: String,
    val category: ProductCategory,
    val status: VendorStatus = VendorStatus.APPROVED,
    val isVerified: Boolean = true,
    val rating: Float = 4.8f,
    val totalSales: Double = 0.0,
    val activeProductsCount: Int = 0
)
