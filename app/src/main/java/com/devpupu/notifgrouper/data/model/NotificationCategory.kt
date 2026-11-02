package com.devpupu.notifgrouper.data.model

import androidx.compose.ui.graphics.Color

enum class NotificationCategory(
    val id: String,
    val displayName: String,
    val color: Color,
    val priorityRank: Int
) {
    ALL("all", "All Apps", Color(0xFF6366F1), 99),
    COMMUNICATION("communication", "Chats & Messages", Color(0xFF3B82F6), 1),
    WORK("work", "Work & Mail", Color(0xFF8B5CF6), 2),
    FINANCE("finance", "Finance & Banking", Color(0xFF10B981), 3),
    DELIVERY("delivery", "Orders & Delivery", Color(0xFFF59E0B), 4),
    SOCIAL("social", "Social Media", Color(0xFFEC4899), 5),
    ENTERTAINMENT("entertainment", "Media & Video", Color(0xFFEF4444), 6),
    SYSTEM("system", "System & Device", Color(0xFF6B7280), 7);

    companion object {
        fun matchCategory(packageName: String): NotificationCategory {
            val pkg = packageName.lowercase()
            return when {
                pkg.contains("whatsapp") || pkg.contains("telegram") || pkg.contains("signal") ||
                        pkg.contains("messaging") || pkg.contains("mms") || pkg.contains("sms") ||
                        pkg.contains("viber") || pkg.contains("wechat") || pkg.contains("line") -> COMMUNICATION

                pkg.contains("gmail") || pkg.contains("android.gm") || pkg.contains(".gm") ||
                        pkg.contains("email") || pkg.contains("slack") ||
                        pkg.contains("teams") || pkg.contains("outlook") || pkg.contains("calendar") ||
                        pkg.contains("drive") || pkg.contains("notion") || pkg.contains("github") ||
                        pkg.contains("jira") || pkg.contains("trello") || pkg.contains("asana") -> WORK

                pkg.contains("instagram") || pkg.contains("twitter") || pkg.contains("facebook") ||
                        pkg.contains("tiktok") || pkg.contains("reddit") || pkg.contains("snapchat") ||
                        pkg.contains("threads") || pkg.contains("linkedin") || pkg.contains("discord") -> SOCIAL

                pkg.contains("uber") || pkg.contains("lyft") || pkg.contains("doordash") ||
                        pkg.contains("grubhub") || pkg.contains("postmates") || pkg.contains("instacart") ||
                        pkg.contains("amazon") || pkg.contains("ebay") || pkg.contains("deliveroo") ||
                        pkg.contains("swiggy") || pkg.contains("zomato") -> DELIVERY

                pkg.contains("bank") || pkg.contains("paypal") || pkg.contains("venmo") ||
                        pkg.contains("cashapp") || pkg.contains("revolut") || pkg.contains("wallet") ||
                        pkg.contains("chase") || pkg.contains("wells") || pkg.contains("citi") -> FINANCE

                pkg.contains("youtube") || pkg.contains("netflix") || pkg.contains("spotify") ||
                        pkg.contains("twitch") || pkg.contains("hulu") || pkg.contains("disney") ||
                        pkg.contains("primevideo") || pkg.contains("soundcloud") -> ENTERTAINMENT

                else -> SYSTEM
            }
        }
    }
}