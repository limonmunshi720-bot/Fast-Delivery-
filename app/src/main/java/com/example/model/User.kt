package com.example.model

enum class UserRole(val enLabel: String, val bnLabel: String) {
    CUSTOMER("Customer", "ক্রেতা"),
    SELLER("Vendor / Seller", "বিক্রেতা"),
    ADMIN("Super Admin", "সুপার অ্যাডমিন");

    fun localized(lang: Language): String = if (lang.isBengali()) bnLabel else enLabel
}

data class AppUser(
    val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val role: UserRole,
    val vendorId: String? = null,
    val isAuthenticated: Boolean = true
)
