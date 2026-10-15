package com.example.model

data class MetaMessengerConfig(
    val pageId: String = "109845720192841",
    val pageName: String = "Fast Delivery Official (দ্রুত ডেলিভারি)",
    val pageAccessToken: String = "EAAGm0PX4ZC...[Configured in Server Secrets]",
    val appSecret: String = "98df71a39...[Configured in Server Secrets]",
    val webhookVerifyToken: String = "fast_delivery_meta_secure_token_2026",
    val webhookUrl: String = "https://asia-northeast1.run.app/api/webhooks/messenger",
    val isVerified: Boolean = true,
    val messagingWindowPolicyAccepted: Boolean = true,
    val lastPingStatus: String = "200 OK (Verified)"
)

data class WhatsAppBusinessConfig(
    val wabaId: String = "39201948572019",
    val phoneNumberId: String = "10592837482910",
    val displayPhoneNumber: String = "+880 1700-000000",
    val accessToken: String = "EAANpZBZB9y...[Configured in Server Secrets]",
    val webhookUrl: String = "https://asia-northeast1.run.app/api/webhooks/whatsapp",
    val webhookVerifyToken: String = "fast_delivery_wa_token_2026",
    val isVerified: Boolean = true,
    val templateNamespace: String = "fast_delivery_alerts",
    val lastPingStatus: String = "200 OK (Active)"
)

data class AiSystemConfig(
    val model: String = "gemini-3.5-flash",
    val temperature: Float = 0.3f,
    val maxOutputTokens: Int = 1000,
    val requireHumanReviewForLargeOrders: Boolean = true,
    val thresholdGmvForReview: Double = 10000.0,
    val messengerAiEnabled: Boolean = true,
    val whatsappAiEnabled: Boolean = true
)
