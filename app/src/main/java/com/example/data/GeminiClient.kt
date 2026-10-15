package com.example.data

import android.util.Log
import com.example.BuildConfig
import com.example.model.Language
import com.example.model.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class ChatMessage(
    val id: String,
    val sender: String, // "user", "assistant"
    val text: String,
    val timestamp: String,
    val suggestedOrderProduct: Product? = null,
    val suggestedQuantity: Int = 1,
    val orderDivision: String = "Dhaka",
    val orderPhone: String = ""
)

class GeminiClient {

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    suspend fun sendQuery(
        userMessage: String,
        catalog: List<Product>,
        conversationHistory: List<ChatMessage>,
        language: Language
    ): String = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }

        // Build grounded catalog prompt
        val catalogContext = buildString {
            appendLine("=== FAST DELIVERY BANGLADESH PRODUCT CATALOG ===")
            for (p in catalog) {
                appendLine("- ID: ${p.id} | Name: ${p.nameEn} / ${p.nameBn} | Price: ৳${p.price.toInt()} (Regular ৳${p.originalPrice.toInt()}) | Stock: ${p.stockCount} ${p.unit} | Category: ${p.category.enName} | Vendor: ${p.vendorName} (${p.vendorDistrict})")
            }
            appendLine("=== DELIVERY POLICIES ===")
            appendLine("1. Inside Dhaka: ৳60 (Delivered within 24-48 hours).")
            appendLine("2. Outside Dhaka (All 64 districts): ৳120 (Delivered within 3-5 days).")
            appendLine("3. Payment Method: 100% Cash on Delivery (ক্যাশ অন ডেলিভারি). Customers inspect goods upon delivery.")
            appendLine("4. Return Policy: 7-day hassle-free return for defective or damaged items.")
            appendLine("5. Active Promo Coupon: 'FAST50' for ৳50 discount on orders over ৳500.")
        }

        val systemInstruction = """
            You are FastBot (দ্রুত সহায়ক), the official AI Order Assistant for 'Fast Delivery', the leading multi-vendor e-commerce platform in Bangladesh.
            
            RULES & CONSTRAINTS:
            - Respond in ${if (language.isBengali()) "natural, polite Bengali (বাংলা)" else "friendly, clear English"}.
            - CRITICAL: Never invent products, stock counts, or prices. Only use products listed in the provided catalog.
            - If a customer asks about prices, availability, or delivery fees, quote the exact figures.
            - If a customer wants to place an order, collect: Product Name/ID, Quantity, Recipient Name, Phone Number (Bangladeshi 11-digit e.g., 017...), and Delivery District (Dhaka or Outside).
            - Explain that payment is Cash on Delivery.
            - If the order information is complete or the user specifies a product to purchase, append this special machine tag at the end of your response so the app can render the interactive 1-tap confirmation card:
              [PROPOSE_ORDER:id=PRODUCT_ID,qty=QUANTITY]
              (Replace PRODUCT_ID with the exact ID from catalog, e.g. PRD-01, and QUANTITY with the integer quantity).
        """.trimIndent()

        // If no real API key or placeholder, run our intelligent grounded local fallback engine
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY" || apiKey.length < 10) {
            return@withContext generateLocalGroundedResponse(userMessage, catalog, language)
        }

        try {
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"

            val contentsArray = JSONArray()

            // System + Catalog preamble
            val systemPart = JSONObject().put("text", "$systemInstruction\n\n$catalogContext")
            val systemContent = JSONObject().put("role", "user").put("parts", JSONArray().put(systemPart))
            contentsArray.put(systemContent)

            val modelAck = JSONObject().put("role", "model").put("parts", JSONArray().put(JSONObject().put("text", "Understood. I am FastBot, ready to assist customers with Fast Delivery's verified catalog in Bangladesh.")))
            contentsArray.put(modelAck)

            // Add previous recent turns
            for (msg in conversationHistory.takeLast(4)) {
                val role = if (msg.sender == "user") "user" else "model"
                val part = JSONObject().put("text", msg.text)
                contentsArray.put(JSONObject().put("role", role).put("parts", JSONArray().put(part)))
            }

            // Current user message
            val currentPart = JSONObject().put("text", userMessage)
            contentsArray.put(JSONObject().put("role", "user").put("parts", JSONArray().put(currentPart)))

            val requestBodyJson = JSONObject()
            requestBodyJson.put("contents", contentsArray)

            val genConfig = JSONObject()
            genConfig.put("temperature", 0.4)
            genConfig.put("maxOutputTokens", 800)
            requestBodyJson.put("generationConfig", genConfig)

            val request = Request.Builder()
                .url(url)
                .post(requestBodyJson.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            val responseString = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                Log.w("GeminiClient", "API error: ${response.code} $responseString")
                return@withContext generateLocalGroundedResponse(userMessage, catalog, language)
            }

            val json = JSONObject(responseString)
            val candidates = json.optJSONArray("candidates")
            val firstCandidate = candidates?.optJSONObject(0)
            val content = firstCandidate?.optJSONObject("content")
            val parts = content?.optJSONArray("parts")
            val text = parts?.optJSONObject(0)?.optString("text")

            if (!text.isNullOrBlank()) {
                text
            } else {
                generateLocalGroundedResponse(userMessage, catalog, language)
            }
        } catch (e: Exception) {
            Log.e("GeminiClient", "Failed Gemini call", e)
            generateLocalGroundedResponse(userMessage, catalog, language)
        }
    }

    /**
     * Local grounded conversational engine guaranteeing instant and reliable answers
     * matching the Fast Delivery catalog even offline or prior to API key insertion.
     */
    private fun generateLocalGroundedResponse(
        query: String,
        catalog: List<Product>,
        language: Language
    ): String {
        val lower = query.lowercase()

        // Match product query
        val matchedProduct = catalog.firstOrNull { p ->
            lower.contains(p.nameEn.lowercase()) ||
            lower.contains(p.nameBn) ||
            (lower.contains("rice") || lower.contains("চাল")) && p.id == "PRD-01" ||
            (lower.contains("oil") || lower.contains("তেল")) && p.id == "PRD-02" ||
            (lower.contains("earbud") || lower.contains("ইয়ারবাড") || lower.contains("হেডফোন")) && p.id == "PRD-03" ||
            (lower.contains("power bank") || lower.contains("পাওয়ার ব্যাংক")) && p.id == "PRD-04" ||
            (lower.contains("panjabi") || lower.contains("পাঞ্জাবি")) && p.id == "PRD-05" ||
            (lower.contains("honey") || lower.contains("মধু")) && p.id == "PRD-06" ||
            (lower.contains("saree") || lower.contains("শাড়ি") || lower.contains("জামদানি")) && p.id == "PRD-08"
        }

        // Delivery fee inquiries
        if (lower.contains("delivery") || lower.contains("ডেলিভারি") || lower.contains("চার্জ") || lower.contains("fee")) {
            return if (language.isBengali()) {
                "ফাস্ট ডেলিভারির ডেলিভারি চার্জ:\n\n• ঢাকা সিটির ভেতরে: মাত্র ৬০ ৳ (২৪ থেকে ৪৮ ঘণ্টার মধ্যে ডেলিভারি)।\n• সমগ্র বাংলাদেশের অন্য যেকোনো জেলায়: ১২০ ৳ (৩ থেকে ৫ কার্যদিবসের মধ্যে)।\n\nআমাদের সমস্ত অর্ডারে রয়েছে ক্যাশ অন ডেলিভারি (COD) সুবিধা! আপনি কি কোনো পণ্য অর্ডার করতে চান?"
            } else {
                "Fast Delivery Shipping Rates:\n\n• Inside Dhaka City: ৳60 (Delivered in 24–48 hours).\n• Outside Dhaka (All 64 districts): ৳120 (Delivered in 3–5 working days).\n\n100% Cash on Delivery available nationwide! What would you like to order today?"
            }
        }

        // Cash on delivery inquiries
        if (lower.contains("cash") || lower.contains("ক্যাশ") || lower.contains("cod") || lower.contains("পেমেন্ট") || lower.contains("payment")) {
            return if (language.isBengali()) {
                "হ্যাঁ! ফাস্ট ডেলিভারিতে আপনি ১০০% ক্যাশ অন ডেলিভারিতে (Cash on Delivery) পণ্য গ্রহণ করতে পারবেন। ডেলিভারি রাইডারের কাছ থেকে পণ্য দেখে ও যাচাই করে মূল্য পরিশোধ করার সম্পূর্ণ সুবিধা রয়েছে।"
            } else {
                "Yes! 100% Cash on Delivery (COD) is supported for all products on Fast Delivery. You pay only after receiving and inspecting the package from our delivery rider."
            }
        }

        // Return / refund policy
        if (lower.contains("return") || lower.contains("ফেরত") || lower.contains("রিটার্ন") || lower.contains("refund")) {
            return if (language.isBengali()) {
                "আমাদের রয়েছে ৭ দিনের সহজ রিটার্ন পলিসি! পণ্য হাতে পাওয়ার পর কোনো ত্রুটি বা সমস্যা থাকলে অ্যাপের 'Orders' সেকশন থেকে সরাসরি 'Return Request' পাঠাতে পারবেন। আমাদের প্রতিনিধি আপনার বাসা থেকে বিনামূল্যে পণ্য সংগ্রহ করবেন।"
            } else {
                "We provide a 7-day hassle-free return policy! If an item is damaged or does not match specifications, initiate a 'Return Request' in the Orders tab and our rider will collect it."
            }
        }

        // Discount / coupon
        if (lower.contains("coupon") || lower.contains("কুপন") || lower.contains("অফার") || lower.contains("discount") || lower.contains("offer")) {
            return if (language.isBengali()) {
                "বিশেষ অফার! চেকআউটে 'FAST50' প্রোমোকোড ব্যবহার করলেই পাচ্ছেন ৫০ ৳ তাৎক্ষণিক ছাড়! এছাড়াও ফ্ল্যাশ ডিলে মিনিকেট চাল ও ইয়ারবাডসে চলছে আকর্ষণীয় মূল্যছাড়।"
            } else {
                "Special Offer! Apply coupon 'FAST50' at checkout for an instant ৳50 discount on your order! Also check our Flash Deals on Groceries & Electronics."
            }
        }

        // Product specific recommendation or order request
        if (matchedProduct != null) {
            val stockStatus = if (matchedProduct.stockCount > 0) "স্টক আছে (${matchedProduct.stockCount} ${matchedProduct.unit})" else "স্টক শেষ"
            return if (language.isBengali()) {
                "পণ্য তথ্য:\n• নাম: ${matchedProduct.nameBn}\n• মূল্য: ৳${matchedProduct.price.toInt()} (রেগুলার ৳${matchedProduct.originalPrice.toInt()})\n• বিক্রেতা: ${matchedProduct.vendorName} (${matchedProduct.vendorDistrict})\n• অবস্থা: $stockStatus\n\nআপনি কি এখনই এই পণ্যটি ক্যাশ অন ডেলিভারিতে অর্ডার করতে চান? নিচে কনফার্ম করুন:\n[PROPOSE_ORDER:id=${matchedProduct.id},qty=1]"
            } else {
                "Product Details:\n• Name: ${matchedProduct.nameEn}\n• Price: ৳${matchedProduct.price.toInt()} (Regular ৳${matchedProduct.originalPrice.toInt()})\n• Vendor: ${matchedProduct.vendorName} (${matchedProduct.vendorDistrict})\n• Stock: In Stock (${matchedProduct.stockCount} ${matchedProduct.unit})\n\nWould you like to order this via Cash on Delivery? Tap confirm below:\n[PROPOSE_ORDER:id=${matchedProduct.id},qty=1]"
            }
        }

        // General greeting or fallback
        return if (language.isBengali()) {
            "আসসালামু আলাইকুম! আমি ফাস্ট ডেলিভারির এআই অ্যাসিস্ট্যান্ট 'ফাস্টবট'।\n\nআমি আপনাকে পণ্য যাচাই, মূল্য জানা, ক্যাশ অন ডেলিভারি এবং দ্রুত অর্ডার কনফার্ম করতে সহায়তা করতে পারি।\n\nআমাদের জনপ্রিয় কয়েকটি পণ্য:\n১. প্রিমিয়াম মিনিকেট চাল (৫ কেজি) - ৳৩৭০\n২. খাঁটি সরিষার তেল (১ লিটার) - ৳২৬০\n৩. ওয়্যারলেস ANC ইয়ারবাডস - ৳১৮৫০\n৪. সুন্দরবনের খাঁটি মধু - ৳৬৮০\n\nআপনি কোন পণ্যটি সম্পর্কে জানতে বা অর্ডার করতে চান?"
        } else {
            "Hello! I am FastBot, your AI Shopping Assistant for Fast Delivery.\n\nI can help you check real-time stock, pricing, Dhaka & nationwide delivery fees, and prepare your Cash on Delivery order in seconds.\n\nPopular items today:\n1. Premium Miniket Rice 5kg - ৳370\n2. Pure Mustard Oil 1L - ৳260\n3. Wireless ANC Earbuds - ৳1,850\n4. Raw Sundarban Honey - ৳680\n\nWhich product would you like to explore or order?"
        }
    }
}
