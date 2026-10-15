package com.example.data

import com.example.model.AppUser
import com.example.model.AuditLog
import com.example.model.CartItem
import com.example.model.CustomerAddress
import com.example.model.Language
import com.example.model.MetaMessengerConfig
import com.example.model.Order
import com.example.model.OrderProductSnapshot
import com.example.model.OrderStatus
import com.example.model.OrderTimelineEvent
import com.example.model.Product
import com.example.model.ProductCategory
import com.example.model.UserRole
import com.example.model.Vendor
import com.example.model.VendorStatus
import com.example.model.WhatsAppBusinessConfig
import com.example.model.AiSystemConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class MarketplaceRepository {

    private val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.ENGLISH)

    private val initialVendors = listOf(
        Vendor(
            id = "VND-101",
            shopName = "Dhaka Agro & Daily Bazar",
            ownerName = "Rafiqul Islam",
            phone = "+880 1711-234567",
            division = "Dhaka",
            district = "Dhaka City (Kawran Bazar)",
            tradeLicenseOrNid = "TRAD/DSCC/029182/2023",
            pickupAddress = "Plot 14, Kawran Bazar Market, Dhaka",
            category = ProductCategory.GROCERIES,
            status = VendorStatus.APPROVED,
            isVerified = true,
            rating = 4.9f,
            totalSales = 245000.0,
            activeProductsCount = 18
        ),
        Vendor(
            id = "VND-102",
            shopName = "Bengal Tech & Gadget Zone",
            ownerName = "Tanvir Ahmed",
            phone = "+880 1819-876543",
            division = "Dhaka",
            district = "Dhaka (Motijheel)",
            tradeLicenseOrNid = "TRAD/DNCC/110293/2024",
            pickupAddress = "Level 4, Multiplan Center, Elephant Road, Dhaka",
            category = ProductCategory.ELECTRONICS,
            status = VendorStatus.APPROVED,
            isVerified = true,
            rating = 4.7f,
            totalSales = 530000.0,
            activeProductsCount = 24
        ),
        Vendor(
            id = "VND-103",
            shopName = "Shonar Bangla Weavers",
            ownerName = "Nasreen Sultana",
            phone = "+880 1912-334455",
            division = "Dhaka",
            district = "Tangail",
            tradeLicenseOrNid = "TRAD/TNG/448201/2022",
            pickupAddress = "Bajitpur Handloom Cluster, Tangail",
            category = ProductCategory.FASHION,
            status = VendorStatus.APPROVED,
            isVerified = true,
            rating = 4.9f,
            totalSales = 320000.0,
            activeProductsCount = 15
        ),
        Vendor(
            id = "VND-104",
            shopName = "Sundarban Pure Forest Honey",
            ownerName = "Abdul Halim",
            phone = "+880 1715-998877",
            division = "Khulna",
            district = "Satkhira",
            tradeLicenseOrNid = "TRAD/SAT/882194/2024",
            pickupAddress = "Munshiganj, Shyamnagar, Satkhira",
            category = ProductCategory.SNACKS_SWEETS,
            status = VendorStatus.APPROVED,
            isVerified = true,
            rating = 5.0f,
            totalSales = 180000.0,
            activeProductsCount = 6
        )
    )

    private val initialProducts = listOf(
        Product(
            id = "PRD-01",
            nameEn = "Premium Miniket Rice (৫ কেজি)",
            nameBn = "প্রিমিয়াম মিনিকেট চাল (৫ কেজি বস্তা)",
            category = ProductCategory.GROCERIES,
            price = 370.0,
            originalPrice = 410.0,
            stockCount = 145,
            unit = "5 kg",
            rating = 4.9f,
            reviewCount = 210,
            isApproved = true,
            vendorId = "VND-101",
            vendorName = "Dhaka Agro & Daily Bazar",
            vendorDistrict = "Dhaka",
            vendorVerified = true,
            descriptionEn = "100% sortex clean, naturally polished premium Miniket rice from Dinajpur. Fresh harvest, non-sticky and delicious for daily family meals.",
            descriptionBn = "দিনাজপুরের সেরা মানের ১০০% সরটেক্স ক্লিন প্রিমিয়াম মিনিকেট চাল। রান্নার পর ভাত ঝরঝরে ও সুস্বাদু থাকে।",
            imageUrl = "https://images.unsplash.com/photo-1586201375761-83865001e31c?w=500",
            badge = "Flash Deal"
        ),
        Product(
            id = "PRD-02",
            nameEn = "Pure Cold-Pressed Mustard Oil 1L",
            nameBn = "ঘানির খাঁটি সরিষার তেল ১ লিটার",
            category = ProductCategory.GROCERIES,
            price = 260.0,
            originalPrice = 295.0,
            stockCount = 80,
            unit = "1 Litre",
            rating = 4.8f,
            reviewCount = 142,
            isApproved = true,
            vendorId = "VND-101",
            vendorName = "Dhaka Agro & Daily Bazar",
            vendorDistrict = "Dhaka",
            vendorVerified = true,
            descriptionEn = "Authentic Ghani-broken mustard oil extracted from pure local mustard seeds. Pungent aroma and rich taste for traditional Bengali bhorta and curries.",
            descriptionBn = "স্থানীয় বাছাইকৃত সরিষা থেকে কাঠের ঘানিতে ভাঙানো ১০০% খাঁটি ঝাঁঝালো সরিষার তেল। ভর্তা ও রান্নায় অতুলনীয় স্বাদ।",
            imageUrl = "https://images.unsplash.com/photo-1474979266404-7eaacbcd87c5?w=500",
            badge = "Best Seller"
        ),
        Product(
            id = "PRD-03",
            nameEn = "Wireless ANC Bluetooth Earbuds Pro",
            nameBn = "ওয়্যারলেস অ্যাক্টিভ নয়েজ ক্যানসেলিং ইয়ারবাডস",
            category = ProductCategory.ELECTRONICS,
            price = 1850.0,
            originalPrice = 2490.0,
            stockCount = 38,
            unit = "1 Piece",
            rating = 4.7f,
            reviewCount = 98,
            isApproved = true,
            vendorId = "VND-102",
            vendorName = "Bengal Tech & Gadget Zone",
            vendorDistrict = "Dhaka",
            vendorVerified = true,
            descriptionEn = "True Wireless Stereo earbuds with 35dB Active Noise Cancellation, Quad Mic ENC for crystal-clear calls in Dhaka traffic, and 30-hour battery life with fast Type-C charging.",
            descriptionBn = "৩৫ ডেসিবেল অ্যাক্টিভ নয়েজ ক্যানসেলেশন ও ৪টি মাইক্রোফোন সমৃদ্ধ ট্রু ওয়্যারলেস ইয়ারবাডস। ঢাকা ট্রাফিকেও নিখুঁত কল ও ৩০ ঘণ্টার ব্যাটারি ব্যাকআপ।",
            imageUrl = "https://images.unsplash.com/photo-1590658268037-6bf12165a8df?w=500",
            badge = "Hot Deal"
        ),
        Product(
            id = "PRD-04",
            nameEn = "Fast 20000mAh 22.5W Power Bank",
            nameBn = "ফাস্ট চার্জিং পাওয়ার ব্যাংক ২০০০০ mAh",
            category = ProductCategory.ELECTRONICS,
            price = 1490.0,
            originalPrice = 1890.0,
            stockCount = 55,
            unit = "1 Piece",
            rating = 4.8f,
            reviewCount = 135,
            isApproved = true,
            vendorId = "VND-102",
            vendorName = "Bengal Tech & Gadget Zone",
            vendorDistrict = "Dhaka",
            vendorVerified = true,
            descriptionEn = "High-density polymer 20000mAh power bank supporting Quick Charge 3.0 & PD 22.5W. LED digital percentage display and airline approved.",
            descriptionBn = "২২.৫ ওয়াট পিডি ফাস্ট চার্জিং সমৃদ্ধ শক্তিশালী ২০০০০ mAh পাওয়ার ব্যাংক। ডিজিটাল ডিসপ্লে ও সব ফোনের সাথে সামঞ্জস্যপূর্ণ।",
            imageUrl = "https://images.unsplash.com/photo-1609592424361-94943fcfbd80?w=500"
        ),
        Product(
            id = "PRD-05",
            nameEn = "Traditional Tangail Cotton Panjabi",
            nameBn = "টাঙ্গাইল হস্তচালিত সুতি পাঞ্জাবি",
            category = ProductCategory.FASHION,
            price = 1350.0,
            originalPrice = 1650.0,
            stockCount = 25,
            unit = "1 Piece",
            rating = 4.9f,
            reviewCount = 67,
            isApproved = true,
            vendorId = "VND-103",
            vendorName = "Shonar Bangla Weavers",
            vendorDistrict = "Tangail",
            vendorVerified = true,
            descriptionEn = "Handwoven 100% pure combed cotton Panjabi crafted by master artisans in Tangail. Breathable, comfortable for Bengali humid weather, with minimal chest embroidery.",
            descriptionBn = "টাঙ্গাইলের ঐতিহ্যবাহী কারিগরদের বোনা শতভাগ সুতি আরামদায়ক পাঞ্জাবি। জুমার নামাজ, ঈদ ও উৎসবের উপযোগী।",
            imageUrl = "https://images.unsplash.com/photo-1583743814966-8936f5b7be1a?w=500",
            badge = "Artisan"
        ),
        Product(
            id = "PRD-06",
            nameEn = "Raw Wild Sundarban Honey 500g",
            nameBn = "সুন্দরবনের খাঁটি বুনো মধু ৫০০ গ্রাম",
            category = ProductCategory.SNACKS_SWEETS,
            price = 680.0,
            originalPrice = 750.0,
            stockCount = 42,
            unit = "500 gram",
            rating = 5.0f,
            reviewCount = 176,
            isApproved = true,
            vendorId = "VND-104",
            vendorName = "Sundarban Pure Forest Honey",
            vendorDistrict = "Satkhira",
            vendorVerified = true,
            descriptionEn = "Raw, unpasteurized forest honey gathered directly by local Mouals from the Sundarbans mangrove jungle. Rich in natural enzymes and antioxidants.",
            descriptionBn = "সুন্দরবনের গভীর জঙ্গল থেকে সরাসরি মৌয়ালদের সংগৃহীত প্রাকৃতিক বুনো মধু। কোনো কৃত্রিম মিষ্টি বা প্রিজারভেটিভ নেই।",
            imageUrl = "https://images.unsplash.com/photo-1587049352846-4a222e784d38?w=500",
            badge = "Organic"
        ),
        Product(
            id = "PRD-07",
            nameEn = "Home First Aid & Emergency Health Kit",
            nameBn = "পারিবারিক প্রাথমিক চিকিৎসা ও ফার্স্ট এইড কিট",
            category = ProductCategory.HEALTHCARE,
            price = 890.0,
            originalPrice = 1100.0,
            stockCount = 60,
            unit = "1 Kit",
            rating = 4.8f,
            reviewCount = 52,
            isApproved = true,
            vendorId = "VND-101",
            vendorName = "Dhaka Agro & Daily Bazar",
            vendorDistrict = "Dhaka",
            vendorVerified = true,
            descriptionEn = "Complete 32-piece household medical emergency kit containing digital thermometer, antiseptic solution, bandage rolls, micropore tape, burn gel, and scissors.",
            descriptionBn = "ঘরে বা ভ্রমণের জন্য ৩২টি প্রয়োজনীয় সামগ্রীযুক্ত পূর্ণাঙ্গ প্রাথমিক চিকিৎসা বক্স। ডিজিটাল থার্মোমিটার ও জরুরি ঔষধ রাখার সুবিধা।",
            imageUrl = "https://images.unsplash.com/photo-1603398938378-e54eab446dde?w=500"
        ),
        Product(
            id = "PRD-08",
            nameEn = "Dhakai Jamdani Luxury Saree",
            nameBn = "আভিজাত্যের ঢাকাই জামদানি শাড়ি",
            category = ProductCategory.FASHION,
            price = 3850.0,
            originalPrice = 4500.0,
            stockCount = 12,
            unit = "1 Piece",
            rating = 4.9f,
            reviewCount = 44,
            isApproved = true,
            vendorId = "VND-103",
            vendorName = "Shonar Bangla Weavers",
            vendorDistrict = "Tangail",
            vendorVerified = true,
            descriptionEn = "Hand-loomed 84-count fine cotton Dhaka Jamdani with intricate floral motifs. UNESCO heritage craft perfection for Bengali weddings and festivities.",
            descriptionBn = "ডেমরা ও রূপগঞ্জের তাঁতিদের হাতে বোনা ৮৪ কাউন্ট সুতার খাঁটি ঢাকাই জামদানি। চোখ জুড়ানো নকশা ও আভিজাত্য।",
            imageUrl = "https://images.unsplash.com/photo-1610030469983-98e550d6193c?w=500",
            badge = "Heritage"
        )
    )

    // A sample pending product submitted by a seller awaiting Admin Approval
    private val initialPendingProducts = listOf(
        Product(
            id = "PRD-PENDING-01",
            nameEn = "Organic Kalijira Aromatic Polao Rice 2kg",
            nameBn = "সুগন্ধি কালিজিরা পোলাও চাল ২ কেজি",
            category = ProductCategory.GROCERIES,
            price = 280.0,
            originalPrice = 320.0,
            stockCount = 50,
            unit = "2 kg",
            rating = 0.0f,
            reviewCount = 0,
            isApproved = false,
            vendorId = "VND-101",
            vendorName = "Dhaka Agro & Daily Bazar",
            vendorDistrict = "Dhaka",
            vendorVerified = true,
            descriptionEn = "Small grain fragrant Kalijira rice for Biryani and Polao.",
            descriptionBn = "পোলাও, পায়েস ও বিরিয়ানির জন্য সুগন্ধি ছোট দানার কালিজিরা চাল।",
            imageUrl = "https://images.unsplash.com/photo-1586201375761-83865001e31c?w=500"
        )
    )

    private val initialOrders = listOf(
        Order(
            id = "FD-92811",
            items = listOf(
                OrderProductSnapshot("PRD-01", "Premium Miniket Rice (৫ কেজি)", "প্রিমিয়াম মিনিকেট চাল (৫ কেজি বস্তা)", 370.0, 2, "Dhaka Agro & Daily Bazar"),
                OrderProductSnapshot("PRD-02", "Pure Cold-Pressed Mustard Oil 1L", "ঘানির খাঁটি সরিষার তেল ১ লিটার", 260.0, 1, "Dhaka Agro & Daily Bazar")
            ),
            subtotal = 1000.0,
            deliveryFee = 60.0,
            discount = 50.0,
            totalAmount = 1010.0,
            status = OrderStatus.SHIPPED,
            address = CustomerAddress(
                recipientName = "Fahim Shahriar",
                phone = "01712-345678",
                division = "Dhaka",
                district = "Dhaka City",
                thana = "Dhanmondi",
                streetAddress = "House 24, Road 7, Dhanmondi, Dhaka",
                deliveryNotes = "Call upon arrival at the gate"
            ),
            paymentMethod = "Cash on Delivery (ক্যাশ অন ডেলিভারি)",
            courierName = "Steadfast Courier (স্টেডফাস্ট)",
            trackingCode = "STDF-BD-8829104",
            createdAt = "14 Oct 2026, 09:30 AM",
            timeline = listOf(
                OrderTimelineEvent(OrderStatus.PENDING, "14 Oct, 09:30 AM", "Order received via App", "অ্যাপের মাধ্যমে অর্ডার গৃহীত হয়েছে"),
                OrderTimelineEvent(OrderStatus.CONFIRMED, "14 Oct, 10:15 AM", "Verified by seller", "বিক্রেতা কর্তৃক নিশ্চিত করা হয়েছে"),
                OrderTimelineEvent(OrderStatus.PROCESSING, "14 Oct, 11:00 AM", "Packed at Kawran Bazar Hub", "কাওরান বাজার হাবে প্যাকিং সম্পন্ন"),
                OrderTimelineEvent(OrderStatus.SHIPPED, "14 Oct, 01:20 PM", "Handed over to delivery rider", "ডেলিভারি রাইডারের নিকট হস্তান্তর করা হয়েছে")
            )
        ),
        Order(
            id = "FD-92795",
            items = listOf(
                OrderProductSnapshot("PRD-03", "Wireless ANC Bluetooth Earbuds Pro", "ওয়্যারলেস অ্যাক্টিভ নয়েজ ক্যানসেলিং ইয়ারবাডস", 1850.0, 1, "Bengal Tech & Gadget Zone")
            ),
            subtotal = 1850.0,
            deliveryFee = 120.0,
            discount = 0.0,
            totalAmount = 1970.0,
            status = OrderStatus.CONFIRMED,
            address = CustomerAddress(
                recipientName = "Nusrat Jahan",
                phone = "01823-998877",
                division = "Chittagong",
                district = "Chittagong City",
                thana = "Panchlaish",
                streetAddress = "Flat 5B, Green Valley Apartment, Nasirabad, Chittagong",
                deliveryNotes = "Deliver between 2 PM to 6 PM"
            ),
            paymentMethod = "Cash on Delivery (ক্যাশ অন ডেলিভারি)",
            courierName = "Pathao Courier (পাঠাও)",
            trackingCode = "PTH-CTG-449102",
            createdAt = "14 Oct 2026, 11:45 AM",
            timeline = listOf(
                OrderTimelineEvent(OrderStatus.PENDING, "14 Oct, 11:45 AM", "Order received", "অর্ডার গৃহীত হয়েছে"),
                OrderTimelineEvent(OrderStatus.CONFIRMED, "14 Oct, 12:10 PM", "Confirmed for dispatch", "পাঠানোর জন্য নিশ্চিত করা হয়েছে")
            )
        )
    )

    private val initialAuditLogs = listOf(
        AuditLog("LOG-001", "SYSTEM_INIT", "Fast Delivery Multi-Vendor Platform initialized with Bangladesh BDT settings", "System Admin", "14 Oct 2026, 08:00 AM", "SECURITY"),
        AuditLog("LOG-002", "VENDOR_VERIFY", "Approved trade license for Dhaka Agro & Daily Bazar (VND-101)", "Super Admin", "14 Oct 2026, 08:30 AM", "VENDOR"),
        AuditLog("LOG-003", "AI_CONFIG", "Configured Gemini 3.5 Flash system prompt for Bangladesh catalog grounding", "Super Admin", "14 Oct 2026, 09:00 AM", "SETTINGS"),
        AuditLog("LOG-004", "ORDER_DISPATCH", "Order #FD-92811 dispatched via Steadfast Courier", "Dispatcher Admin", "14 Oct 2026, 01:20 PM", "ORDER")
    )

    // Observable StateFlows
    private val _products = MutableStateFlow(initialProducts)
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    private val _pendingProducts = MutableStateFlow(initialPendingProducts)
    val pendingProducts: StateFlow<List<Product>> = _pendingProducts.asStateFlow()

    private val _cart = MutableStateFlow<List<CartItem>>(emptyList())
    val cart: StateFlow<List<CartItem>> = _cart.asStateFlow()

    private val _orders = MutableStateFlow(initialOrders)
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()

    private val _vendors = MutableStateFlow(initialVendors)
    val vendors: StateFlow<List<Vendor>> = _vendors.asStateFlow()

    private val _auditLogs = MutableStateFlow(initialAuditLogs)
    val auditLogs: StateFlow<List<AuditLog>> = _auditLogs.asStateFlow()

    private val _currentUser = MutableStateFlow(
        AppUser(
            id = "USR-7721",
            name = "Limon Munshi",
            email = "limonmunshi720@gmail.com",
            phone = "01711-000000",
            role = UserRole.CUSTOMER
        )
    )
    val currentUser: StateFlow<AppUser> = _currentUser.asStateFlow()

    private val _currentLanguage = MutableStateFlow(Language.BN) // Bengali by default
    val currentLanguage: StateFlow<Language> = _currentLanguage.asStateFlow()

    private val _messengerConfig = MutableStateFlow(MetaMessengerConfig())
    val messengerConfig: StateFlow<MetaMessengerConfig> = _messengerConfig.asStateFlow()

    private val _whatsAppConfig = MutableStateFlow(WhatsAppBusinessConfig())
    val whatsAppConfig: StateFlow<WhatsAppBusinessConfig> = _whatsAppConfig.asStateFlow()

    private val _aiConfig = MutableStateFlow(AiSystemConfig())
    val aiConfig: StateFlow<AiSystemConfig> = _aiConfig.asStateFlow()

    fun setLanguage(language: Language) {
        _currentLanguage.value = language
    }

    fun switchUserRole(role: UserRole) {
        val user = _currentUser.value
        _currentUser.value = user.copy(
            role = role,
            vendorId = if (role == UserRole.SELLER) "VND-101" else null
        )
        addAuditLog("ROLE_SWITCH", "User switched active session role to ${role.name}", user.name, "SECURITY")
    }

    fun addToCart(product: Product, quantity: Int = 1) {
        val currentList = _cart.value.toMutableList()
        val index = currentList.indexOfFirst { it.product.id == product.id }
        if (index >= 0) {
            val existing = currentList[index]
            currentList[index] = existing.copy(quantity = existing.quantity + quantity)
        } else {
            currentList.add(CartItem(product = product, quantity = quantity))
        }
        _cart.value = currentList
    }

    fun updateCartQuantity(productId: String, newQuantity: Int) {
        val currentList = _cart.value.toMutableList()
        if (newQuantity <= 0) {
            currentList.removeAll { it.product.id == productId }
        } else {
            val index = currentList.indexOfFirst { it.product.id == productId }
            if (index >= 0) {
                currentList[index] = currentList[index].copy(quantity = newQuantity)
            }
        }
        _cart.value = currentList
    }

    fun clearCart() {
        _cart.value = emptyList()
    }

    /**
     * Server-side secure order placement.
     * Calculates prices, delivery fee based on division (Dhaka = 60, Outside = 120),
     * applies verified discount, updates stock, and writes audit record.
     */
    fun placeOrder(
        items: List<CartItem>,
        address: CustomerAddress,
        couponCode: String = "",
        isAiAssisted: Boolean = false
    ): Order {
        val now = dateFormat.format(Date())
        val orderSnapshots = items.map { cartItem ->
            OrderProductSnapshot(
                productId = cartItem.product.id,
                nameEn = cartItem.product.nameEn,
                nameBn = cartItem.product.nameBn,
                unitPrice = cartItem.product.price,
                quantity = cartItem.quantity,
                vendorName = cartItem.product.vendorName
            )
        }

        val subtotal = items.sumOf { it.totalPrice }
        val deliveryFee = if (address.isInsideDhaka) 60.0 else 120.0
        val discount = if (couponCode.trim().uppercase() in listOf("FAST50", "EID2026", "WELCOME")) 50.0 else 0.0
        val totalAmount = maxOf(0.0, (subtotal + deliveryFee) - discount)

        val orderId = "FD-${(10000..99999).random()}"
        val trackingCode = "FAST-${(100000..999999).random()}"

        val newOrder = Order(
            id = orderId,
            items = orderSnapshots,
            subtotal = subtotal,
            deliveryFee = deliveryFee,
            discount = discount,
            totalAmount = totalAmount,
            status = OrderStatus.PENDING,
            address = address,
            paymentMethod = "Cash on Delivery (ক্যাশ অন ডেলিভারি)",
            courierName = if (address.isInsideDhaka) "Fast Delivery Express / পাঠাও" else "Steadfast Courier",
            trackingCode = trackingCode,
            createdAt = now,
            timeline = listOf(
                OrderTimelineEvent(
                    status = OrderStatus.PENDING,
                    timestamp = now,
                    description = if (isAiAssisted) "Order placed via AI Assistant" else "Order placed on Fast Delivery App",
                    descriptionBn = if (isAiAssisted) "এআই অ্যাসিস্ট্যান্টের মাধ্যমে অর্ডার তৈরি হয়েছে" else "ফাস্ট ডেলিভারি অ্যাপে অর্ডার তৈরি হয়েছে"
                )
            ),
            isAiAssisted = isAiAssisted
        )

        // Deduct stock safely
        val currentProducts = _products.value.toMutableList()
        for (item in items) {
            val pIdx = currentProducts.indexOfFirst { it.id == item.product.id }
            if (pIdx >= 0) {
                val p = currentProducts[pIdx]
                val updatedStock = maxOf(0, p.stockCount - item.quantity)
                currentProducts[pIdx] = p.copy(stockCount = updatedStock)
            }
        }
        _products.value = currentProducts

        // Append order to top
        val currentOrders = _orders.value.toMutableList()
        currentOrders.add(0, newOrder)
        _orders.value = currentOrders

        // Clear cart
        clearCart()

        addAuditLog(
            action = "ORDER_CREATE",
            details = "Order $orderId created for ৳ $totalAmount by ${address.recipientName} (${address.district}). AI Assisted: $isAiAssisted",
            performedBy = address.recipientName,
            category = "ORDER"
        )

        return newOrder
    }

    fun updateOrderStatus(orderId: String, newStatus: OrderStatus, note: String = "") {
        val currentOrders = _orders.value.toMutableList()
        val index = currentOrders.indexOfFirst { it.id == orderId }
        if (index >= 0) {
            val order = currentOrders[index]
            val now = dateFormat.format(Date())
            val event = OrderTimelineEvent(
                status = newStatus,
                timestamp = now,
                description = note.ifEmpty { "Status updated to ${newStatus.enLabel}" },
                descriptionBn = note.ifEmpty { "স্ট্যাটাস পরিবর্তন: ${newStatus.bnLabel}" }
            )
            val updatedTimeline = order.timeline + event
            currentOrders[index] = order.copy(
                status = newStatus,
                timeline = updatedTimeline
            )
            _orders.value = currentOrders

            addAuditLog(
                action = "ORDER_STATUS_UPDATE",
                details = "Order $orderId moved to ${newStatus.name}. Note: $note",
                performedBy = _currentUser.value.name,
                category = "ORDER"
            )
        }
    }

    /**
     * Seller submits a product. It is stored as pending approval.
     */
    fun submitProductForApproval(product: Product) {
        val pending = _pendingProducts.value.toMutableList()
        val unapprovedProduct = product.copy(
            id = "PRD-SUB-${(1000..9999).random()}",
            isApproved = false
        )
        pending.add(0, unapprovedProduct)
        _pendingProducts.value = pending

        addAuditLog(
            action = "PRODUCT_SUBMIT",
            details = "Seller ${product.vendorName} submitted product '${product.nameEn}' for approval",
            performedBy = product.vendorName,
            category = "VENDOR"
        )
    }

    /**
     * Admin approves a product. It moves from pending to public catalog.
     */
    fun approveProduct(productId: String) {
        val pending = _pendingProducts.value.toMutableList()
        val p = pending.find { it.id == productId } ?: return
        pending.removeAll { it.id == productId }
        _pendingProducts.value = pending

        val publicProducts = _products.value.toMutableList()
        publicProducts.add(0, p.copy(isApproved = true))
        _products.value = publicProducts

        addAuditLog(
            action = "PRODUCT_APPROVE",
            details = "Admin approved product '${p.nameEn}' (#$productId)",
            performedBy = _currentUser.value.name,
            category = "MARKETPLACE"
        )
    }

    /**
     * Admin rejects a pending product.
     */
    fun rejectProduct(productId: String, reason: String = "Policy violation or incomplete details") {
        val pending = _pendingProducts.value.toMutableList()
        val p = pending.find { it.id == productId } ?: return
        pending.removeAll { it.id == productId }
        _pendingProducts.value = pending

        addAuditLog(
            action = "PRODUCT_REJECT",
            details = "Admin rejected product '${p.nameEn}' (#$productId). Reason: $reason",
            performedBy = _currentUser.value.name,
            category = "MARKETPLACE"
        )
    }

    fun registerVendor(vendor: Vendor) {
        val current = _vendors.value.toMutableList()
        current.add(0, vendor)
        _vendors.value = current
        addAuditLog(
            action = "VENDOR_REGISTER",
            details = "New vendor registered: ${vendor.shopName} (${vendor.district})",
            performedBy = vendor.ownerName,
            category = "VENDOR"
        )
    }

    fun addAuditLog(action: String, details: String, performedBy: String, category: String = "MARKETPLACE") {
        val now = dateFormat.format(Date())
        val log = AuditLog(
            id = "LOG-${UUID.randomUUID().toString().take(6).uppercase()}",
            action = action,
            details = details,
            performedBy = performedBy,
            timestamp = now,
            category = category
        )
        val logs = _auditLogs.value.toMutableList()
        logs.add(0, log)
        _auditLogs.value = logs
    }

    fun updateMessengerConfig(config: MetaMessengerConfig) {
        _messengerConfig.value = config
        addAuditLog("CONFIG_MESSENGER", "Updated Meta Messenger Webhook/Token settings", _currentUser.value.name, "SETTINGS")
    }

    fun updateWhatsAppConfig(config: WhatsAppBusinessConfig) {
        _whatsAppConfig.value = config
        addAuditLog("CONFIG_WHATSAPP", "Updated WhatsApp Business Cloud API settings", _currentUser.value.name, "SETTINGS")
    }

    fun updateAiConfig(config: AiSystemConfig) {
        _aiConfig.value = config
        addAuditLog("CONFIG_AI", "Updated Gemini AI settings: model=${config.model}, temp=${config.temperature}", _currentUser.value.name, "SETTINGS")
    }
}
