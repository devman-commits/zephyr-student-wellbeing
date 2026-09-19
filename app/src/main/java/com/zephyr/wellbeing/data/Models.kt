package com.zephyr.wellbeing.data

data class User(
    val id: String,
    val username: String,
    val displayName: String,
    val isAdmin: Boolean = false,
    val coins: Int = 0,
    val streakDays: Int = 0,
    val totalStudyMinutes: Int = 0,
    val completedPomodoros: Int = 0,
    val passwordHash: String = "",
    val equippedBadge: String? = null,
    val unlockedThemes: List<String> = listOf("lavender"),
    val unlockedBadges: List<String> = listOf("badge_zen_scholar")
)

data class ProfileBadge(
    val id: String,
    val name: String,
    val description: String,
    val cost: Int,
    val drawableResName: String,
    val emoji: String
)

data class TaskItem(
    val id: String,
    val title: String,
    val category: String = "General",
    val deadline: String = "Today",
    val isCompleted: Boolean = false
)

data class StudyRoom(
    val id: String,
    val name: String,
    val passkey: String,
    val participantsCount: Int = 1,
    val pomodoroSecondsRemaining: Int = 1500,
    val creatorUsername: String = "admin",
    val isOfficialPod: Boolean = false,
    val timerDurationMinutes: Int = 25,
    val isTimerRunning: Boolean = false
)

data class OfficialPod(
    val id: String,
    val name: String,
    val building: String
)

data class ChatMessage(
    val id: String,
    val sender: String,
    val ciphertext: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class CampusResource(
    val id: String,
    val title: String,
    val description: String,
    val url: String,
    val category: String
)

data class BookingPass(
    val id: String,
    val facilityName: String,
    val building: String,
    val timeSlot: String,
    val date: String,
    val studentUsername: String,
    val status: String = "Reserved",
    val qrCodeSeed: String
)

data class ShopItem(
    val id: String,
    val title: String,
    val description: String,
    val price: Int,
    val previewHex: String,
    val type: String // "theme" or "badge"
)
