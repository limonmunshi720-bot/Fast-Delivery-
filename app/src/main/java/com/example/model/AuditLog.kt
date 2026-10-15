package com.example.model

data class AuditLog(
    val id: String,
    val action: String,
    val details: String,
    val performedBy: String,
    val timestamp: String,
    val category: String = "MARKETPLACE" // "SECURITY", "VENDOR", "ORDER", "SETTINGS"
)
