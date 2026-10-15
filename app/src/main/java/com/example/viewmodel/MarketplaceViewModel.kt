package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ChatMessage
import com.example.data.GeminiClient
import com.example.data.MarketplaceRepository
import com.example.model.AppUser
import com.example.model.AuditLog
import com.example.model.CartItem
import com.example.model.CustomerAddress
import com.example.model.Language
import com.example.model.MetaMessengerConfig
import com.example.model.Order
import com.example.model.OrderStatus
import com.example.model.Product
import com.example.model.ProductCategory
import com.example.model.UserRole
import com.example.model.Vendor
import com.example.model.WhatsAppBusinessConfig
import com.example.model.AiSystemConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

enum class ScreenDestination {
    HOME,
    CATEGORIES,
    PRODUCT_DETAIL,
    CART,
    CHECKOUT,
    ORDERS,
    ORDER_TRACKING,
    AI_ASSISTANT,
    VENDOR_PORTAL,
    ADMIN_PANEL,
    PLAY_STORE_GUIDE,
    PROFILE
}

class MarketplaceViewModel(
    private val repository: MarketplaceRepository = MarketplaceRepository(),
    private val geminiClient: GeminiClient = GeminiClient()
) : ViewModel() {

    private val timeFormat = SimpleDateFormat("hh:mm a", Locale.ENGLISH)

    // Navigation state
    private val _currentScreen = MutableStateFlow(ScreenDestination.HOME)
    val currentScreen: StateFlow<ScreenDestination> = _currentScreen.asStateFlow()

    private val _selectedProduct = MutableStateFlow<Product?>(null)
    val selectedProduct: StateFlow<Product?> = _selectedProduct.asStateFlow()

    private val _selectedOrder = MutableStateFlow<Order?>(null)
    val selectedOrder: StateFlow<Order?> = _selectedOrder.asStateFlow()

    // Filters
    val searchQuery = MutableStateFlow("")
    val selectedCategory = MutableStateFlow(ProductCategory.ALL)

    // Repository state flows
    val products = repository.products
    val pendingProducts = repository.pendingProducts
    val cart = repository.cart
    val orders = repository.orders
    val vendors = repository.vendors
    val auditLogs = repository.auditLogs
    val currentUser = repository.currentUser
    val currentLanguage = repository.currentLanguage
    val messengerConfig = repository.messengerConfig
    val whatsAppConfig = repository.whatsAppConfig
    val aiConfig = repository.aiConfig

    // Filtered Products for Catalog & Search
    val filteredProducts: StateFlow<List<Product>> = combine(
        products,
        searchQuery,
        selectedCategory
    ) { prodList, query, cat ->
        prodList.filter { product ->
            val matchesCategory = (cat == ProductCategory.ALL || product.category == cat)
            val matchesQuery = query.isBlank() ||
                    product.nameEn.contains(query, ignoreCase = true) ||
                    product.nameBn.contains(query, ignoreCase = true) ||
                    product.vendorName.contains(query, ignoreCase = true) ||
                    product.category.enName.contains(query, ignoreCase = true)
            matchesCategory && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Cart calculations
    val cartSubtotal: StateFlow<Double> = cart.combine(products) { items, _ ->
        items.sumOf { it.totalPrice }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val cartCount: StateFlow<Int> = cart.combine(products) { items, _ ->
        items.sumOf { it.quantity }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Customer Address for Checkout
    val checkoutAddress = MutableStateFlow(
        CustomerAddress(
            recipientName = "Limon Munshi",
            phone = "01711-234567",
            division = "Dhaka",
            district = "Dhaka City",
            thana = "Dhanmondi",
            streetAddress = "House 12, Road 4/A, Dhanmondi",
            deliveryNotes = "Call before arrival"
        )
    )
    val couponCode = MutableStateFlow("FAST50")
    val appliedCoupon = MutableStateFlow("FAST50")

    // AI Assistant State
    private val _aiMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                id = "AI-INIT",
                sender = "assistant",
                text = "আসসালামু আলাইকুম! আমি ফাস্ট ডেলিভারির এআই শপিং অ্যাসিস্ট্যান্ট 'ফাস্টবট'।\n\nআপনি যেকোনো পণ্য, ডেলিভারি চার্জ বা অফার সম্পর্কে জানতে পারেন, অথবা সরাসরি ক্যাশ অন ডেলিভারিতে অর্ডার করতে পারেন।\n\nআজকে আপনাকে কী সাহায্য করতে পারি?",
                timestamp = timeFormat.format(Date())
            )
        )
    )
    val aiMessages: StateFlow<List<ChatMessage>> = _aiMessages.asStateFlow()

    private val _isAiLoading = MutableStateFlow(false)
    val isAiLoading: StateFlow<Boolean> = _isAiLoading.asStateFlow()

    // Navigation Helpers
    fun navigateTo(destination: ScreenDestination) {
        _currentScreen.value = destination
    }

    fun openProductDetail(product: Product) {
        _selectedProduct.value = product
        _currentScreen.value = ScreenDestination.PRODUCT_DETAIL
    }

    fun openOrderTracking(order: Order) {
        _selectedOrder.value = order
        _currentScreen.value = ScreenDestination.ORDER_TRACKING
    }

    // Language Toggle
    fun toggleLanguage() {
        val next = if (currentLanguage.value == Language.BN) Language.EN else Language.BN
        repository.setLanguage(next)
    }

    fun setLanguage(lang: Language) {
        repository.setLanguage(lang)
    }

    // Role Switch
    fun switchRole(role: UserRole) {
        repository.switchUserRole(role)
    }

    // Cart Actions
    fun addToCart(product: Product, quantity: Int = 1) {
        repository.addToCart(product, quantity)
    }

    fun updateCartQuantity(productId: String, quantity: Int) {
        repository.updateCartQuantity(productId, quantity)
    }

    fun clearCart() {
        repository.clearCart()
    }

    fun applyCoupon(code: String) {
        appliedCoupon.value = code.trim().uppercase()
    }

    // Checkout & Order Placement
    fun submitCheckoutOrder(): Order {
        val currentCart = cart.value
        val addr = checkoutAddress.value
        val order = repository.placeOrder(
            items = currentCart,
            address = addr,
            couponCode = appliedCoupon.value,
            isAiAssisted = false
        )
        _selectedOrder.value = order
        _currentScreen.value = ScreenDestination.ORDER_TRACKING
        return order
    }

    // Order Actions
    fun updateOrderStatus(orderId: String, newStatus: OrderStatus, note: String = "") {
        repository.updateOrderStatus(orderId, newStatus, note)
        // Refresh selected order if currently viewing
        val updated = repository.orders.value.find { it.id == orderId }
        if (updated != null && _selectedOrder.value?.id == orderId) {
            _selectedOrder.value = updated
        }
    }

    fun requestOrderReturn(orderId: String, reason: String) {
        updateOrderStatus(orderId, OrderStatus.RETURN_REQUESTED, "Customer requested return: $reason")
    }

    fun cancelOrder(orderId: String, reason: String) {
        updateOrderStatus(orderId, OrderStatus.CANCELLED, "Customer cancelled: $reason")
    }

    // Vendor Actions
    fun registerVendor(
        shopName: String,
        ownerName: String,
        phone: String,
        division: String,
        district: String,
        tradeLicense: String,
        pickupAddress: String,
        category: ProductCategory
    ) {
        val vendor = Vendor(
            id = "VND-${(200..999).random()}",
            shopName = shopName,
            ownerName = ownerName,
            phone = phone,
            division = division,
            district = district,
            tradeLicenseOrNid = tradeLicense,
            pickupAddress = pickupAddress,
            category = category,
            status = com.example.model.VendorStatus.APPROVED,
            isVerified = true,
            rating = 5.0f,
            totalSales = 0.0,
            activeProductsCount = 0
        )
        repository.registerVendor(vendor)
        repository.switchUserRole(UserRole.SELLER)
    }

    fun submitSellerProduct(
        nameEn: String,
        nameBn: String,
        category: ProductCategory,
        price: Double,
        originalPrice: Double,
        stockCount: Int,
        unit: String,
        descriptionEn: String,
        descriptionBn: String
    ) {
        val vendor = vendors.value.firstOrNull { it.id == currentUser.value.vendorId }
            ?: vendors.value.first()

        val product = Product(
            id = "PRD-TEMP",
            nameEn = nameEn,
            nameBn = nameBn,
            category = category,
            price = price,
            originalPrice = if (originalPrice > 0) originalPrice else price,
            stockCount = stockCount,
            unit = unit,
            rating = 5.0f,
            reviewCount = 0,
            isApproved = false,
            vendorId = vendor.id,
            vendorName = vendor.shopName,
            vendorDistrict = vendor.district,
            vendorVerified = vendor.isVerified,
            descriptionEn = descriptionEn,
            descriptionBn = descriptionBn,
            badge = "New Arrival"
        )
        repository.submitProductForApproval(product)
    }

    // Admin Actions
    fun approveProduct(productId: String) {
        repository.approveProduct(productId)
    }

    fun rejectProduct(productId: String) {
        repository.rejectProduct(productId)
    }

    fun updateMetaMessengerConfig(config: MetaMessengerConfig) {
        repository.updateMessengerConfig(config)
    }

    fun updateWhatsAppConfig(config: WhatsAppBusinessConfig) {
        repository.updateWhatsAppConfig(config)
    }

    fun updateAiConfig(config: AiSystemConfig) {
        repository.updateAiConfig(config)
    }

    // AI Assistant Actions
    fun sendAiMessage(userText: String) {
        if (userText.isBlank()) return

        val userMsg = ChatMessage(
            id = UUID.randomUUID().toString(),
            sender = "user",
            text = userText,
            timestamp = timeFormat.format(Date())
        )
        _aiMessages.value = _aiMessages.value + userMsg
        _isAiLoading.value = true

        viewModelScope.launch {
            try {
                val responseText = geminiClient.sendQuery(
                    userMessage = userText,
                    catalog = repository.products.value,
                    conversationHistory = _aiMessages.value,
                    language = repository.currentLanguage.value
                )

                // Check for machine order tag e.g. [PROPOSE_ORDER:id=PRD-01,qty=1]
                var cleanText = responseText
                var suggestedProd: Product? = null
                var suggestedQty = 1

                val tagPattern = Regex("\\[PROPOSE_ORDER:id=([A-Za-z0-9_-]+)(?:,qty=(\\d+))?\\]")
                val match = tagPattern.find(responseText)
                if (match != null) {
                    val prodId = match.groupValues[1]
                    val qty = match.groupValues.getOrNull(2)?.toIntOrNull() ?: 1
                    suggestedProd = repository.products.value.find { it.id == prodId }
                    suggestedQty = qty
                    cleanText = responseText.replace(tagPattern, "").trim()
                }

                val botMsg = ChatMessage(
                    id = UUID.randomUUID().toString(),
                    sender = "assistant",
                    text = cleanText,
                    timestamp = timeFormat.format(Date()),
                    suggestedOrderProduct = suggestedProd,
                    suggestedQuantity = suggestedQty,
                    orderDivision = checkoutAddress.value.division,
                    orderPhone = checkoutAddress.value.phone
                )
                _aiMessages.value = _aiMessages.value + botMsg
            } catch (e: Exception) {
                val errorMsg = ChatMessage(
                    id = UUID.randomUUID().toString(),
                    sender = "assistant",
                    text = if (repository.currentLanguage.value.isBengali()) {
                        "দুঃখিত, সংযোগে সাময়িক সমস্যা হয়েছে। আপনি পণ্য তালিকা থেকে সরাসরি অর্ডার করতে পারেন।"
                    } else {
                        "Sorry, there was a temporary network issue. You can order directly from the catalog."
                    },
                    timestamp = timeFormat.format(Date())
                )
                _aiMessages.value = _aiMessages.value + errorMsg
            } finally {
                _isAiLoading.value = false
            }
        }
    }

    /**
     * Executes 1-tap confirmation card proposed by the AI Assistant.
     */
    fun confirmAiOrder(product: Product, quantity: Int, address: CustomerAddress): Order {
        val order = repository.placeOrder(
            items = listOf(CartItem(product = product, quantity = quantity)),
            address = address,
            couponCode = appliedCoupon.value,
            isAiAssisted = true
        )

        val confirmMsg = ChatMessage(
            id = UUID.randomUUID().toString(),
            sender = "assistant",
            text = if (repository.currentLanguage.value.isBengali()) {
                "ধন্যবাদ! আপনার অর্ডারটি নিশ্চিত করা হয়েছে (অর্ডার আইডি: #${order.id})। মোট প্রদেয়: ৳${order.totalAmount.toInt()} (ক্যাশ অন ডেলিভারি)। দ্রুত ডেলিভারি রাইডার আপনার সাথে যোগাযোগ করবেন।"
            } else {
                "Thank you! Your order has been placed successfully (Order ID: #${order.id}). Total to pay: ৳${order.totalAmount.toInt()} (Cash on Delivery). Our rider will contact you soon."
            },
            timestamp = timeFormat.format(Date())
        )
        _aiMessages.value = _aiMessages.value + confirmMsg

        _selectedOrder.value = order
        return order
    }
}
