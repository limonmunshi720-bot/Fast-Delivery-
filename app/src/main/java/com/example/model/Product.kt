package com.example.model

enum class ProductCategory(val enName: String, val bnName: String, val iconName: String) {
    ALL("All Items", "সব পণ্য", "category"),
    GROCERIES("Daily Bazar & Groceries", "নিত্যদিনের বাজার ও মুদি", "shopping_basket"),
    ELECTRONICS("Electronics & Gadgets", "ইলেকট্রনিক্স ও গ্যাজেট", "devices"),
    FASHION("Fashion & Clothing", "পোশাক ও ফ্যাশন", "checkroom"),
    SNACKS_SWEETS("Bengali Snacks & Sweets", "দেশি খাবার ও মিষ্টান্ন", "restaurant"),
    HEALTHCARE("Health & Wellness", "স্বাস্থ্য ও পরিচর্যা", "health_and_safety"),
    HOME_LIVING("Home & Kitchen", "ঘর সাজসজ্জা ও রান্নাঘর", "cottage");

    fun localizedName(lang: Language): String = if (lang.isBengali()) bnName else enName
}

data class Product(
    val id: String,
    val nameEn: String,
    val nameBn: String,
    val category: ProductCategory,
    val price: Double,
    val originalPrice: Double,
    val stockCount: Int,
    val unit: String,
    val rating: Float,
    val reviewCount: Int,
    val isApproved: Boolean = true,
    val vendorId: String,
    val vendorName: String,
    val vendorDistrict: String,
    val vendorVerified: Boolean = true,
    val descriptionEn: String,
    val descriptionBn: String,
    val imageUrl: String = "",
    val badge: String = "" // "Flash Deal", "Best Seller", "Organic", etc.
) {
    val discountPercent: Int
        get() = if (originalPrice > price) {
            (((originalPrice - price) / originalPrice) * 100).toInt()
        } else 0

    val isInStock: Boolean
        get() = stockCount > 0

    fun localizedName(lang: Language): String = if (lang.isBengali()) nameBn else nameEn
    fun localizedDescription(lang: Language): String = if (lang.isBengali()) descriptionBn else descriptionEn
}
