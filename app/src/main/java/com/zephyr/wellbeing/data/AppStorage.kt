package com.zephyr.wellbeing.data

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class AppStorage(private val context: Context) {

    private val prefs = context.getSharedPreferences("zephyr_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    var currentUser = mutableStateOf<User?>(null)
    var alertBanner = mutableStateOf<String?>("📢 Zephyr Finals Week: Library Silent Pods are open 24/7 with free brain snacks!")
    
    val tasks = mutableStateListOf<TaskItem>()
    val bookings = mutableStateListOf<BookingPass>()
    val resources = mutableStateListOf<CampusResource>()
    val rooms = mutableStateListOf<StudyRoom>()
    val roomMessages = mutableStateListOf<ChatMessage>()
    var activeRoom = mutableStateOf<StudyRoom?>(null)
    var showCiphertext = mutableStateOf(false)
    val officialPods = mutableStateListOf<OfficialPod>()

    val availableBadges = listOf(
        ProfileBadge("badge_zen_scholar", "Zen Scholar", "Achieve mental clarity and profound focus mastery.", 50, "badge_zen_scholar", "📚"),
        ProfileBadge("badge_time_master", "Time Master", "Harness the power of consistent Pomodoro momentum.", 100, "badge_time_master", "⏳"),
        ProfileBadge("badge_night_owl", "Night Owl", "Triumph in late-night hackathons and quiet study sessions.", 150, "badge_night_owl", "🦉")
    )

    var serverUrl = mutableStateOf(prefs.getString("server_url", "http://10.0.2.2:8080") ?: "http://10.0.2.2:8080")

    init {
        loadInitialData()
    }

    fun updateServerUrl(url: String) {
        val clean = url.trim().removeSuffix("/")
        serverUrl.value = clean
        prefs.edit().putString("server_url", clean).apply()
    }

    private fun loadInitialData() {
        // Load default resources
        resources.addAll(
            listOf(
                CampusResource("1", "Academic SIS & Course Portal", "Class schedules, grades, transcripts", "https://developer.android.com", "Academic"),
                CampusResource("2", "University Library Catalog", "JSTOR, IEEE Xplore, research archives", "https://developer.android.com/jetpack/compose", "Library"),
                CampusResource("3", "Student Wellbeing & Mental Health", "24/7 counselor hotline & mindfulness workshops", "https://developer.android.com/design", "Wellness"),
                CampusResource("4", "Career Development & Internships", "Recruitment fairs, resume reviews", "https://github.com", "Career")
            )
        )

        // Load official pods
        officialPods.addAll(
            listOf(
                OfficialPod("pod_1", "Library Silent Pod 4B", "Science Library Floor 3"),
                OfficialPod("pod_2", "Discussion Room 2A", "Student Union Hub")
            )
        )

        // Load default rooms
        rooms.addAll(
            listOf(
                StudyRoom(
                    id = "room_1",
                    name = "Library Silent Pod 4B",
                    passkey = "STU123",
                    participantsCount = 3,
                    pomodoroSecondsRemaining = 1200,
                    creatorUsername = "admin",
                    isOfficialPod = true,
                    timerDurationMinutes = 20
                ),
                StudyRoom(
                    id = "room_2",
                    name = "Late Night Hackers Lounge",
                    passkey = "CODE88",
                    participantsCount = 5,
                    pomodoroSecondsRemaining = 850,
                    creatorUsername = "alex",
                    isOfficialPod = false,
                    timerDurationMinutes = 25
                )
            )
        )

        // Seed initial message
        val pass = "STU123"
        val cipher = CryptoUtils.encrypt("Welcome to the encrypted study room! Ready to focus?", pass)
        roomMessages.add(ChatMessage("m1", "Alex Chen", cipher))

        // Ensure admin user is registered in the username registry
        val registered = getRegisteredUsersSet()
        if (!registered.contains("admin")) {
            registered.add("admin")
            saveRegisteredUsersSet(registered)
        }

        // Check saved session
        val savedUser = prefs.getString("active_username", prefs.getString("username", null))
        if (savedUser != null) {
            loadUserData(savedUser)
        }
    }

    private fun getRegisteredUsersSet(): MutableSet<String> {
        return (prefs.getStringSet("all_registered_users", null)?.toMutableSet() ?: mutableSetOf("admin"))
    }

    private fun saveRegisteredUsersSet(users: Set<String>) {
        prefs.edit().putStringSet("all_registered_users", users).apply()
    }

    fun getAllRegisteredUsers(): List<String> {
        return getRegisteredUsersSet().toList().sorted()
    }

    fun getAllUserDetails(): List<User> {
        val usernames = getRegisteredUsersSet().toList().sorted()
        return usernames.map { u ->
            val isAdmin = (u == "admin") || prefs.getBoolean("user_${u}_is_admin", false)
            val coins = if (isAdmin) 10000 else prefs.getInt("user_${u}_coins", 100)
            val streak = prefs.getInt("user_${u}_streak", 0)
            val minutes = prefs.getInt("user_${u}_minutes", 0)
            val pomodoros = prefs.getInt("user_${u}_pomodoros", 0)
            val equipped = prefs.getString("user_${u}_equipped_badge", "badge_zen_scholar")
            val defaultName = if (u == "admin") "Zephyr Administrator" else u.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
            val dispName = prefs.getString("user_${u}_display_name", defaultName) ?: defaultName
            User(
                id = "user_$u",
                username = u,
                displayName = dispName,
                isAdmin = isAdmin,
                coins = coins,
                streakDays = streak,
                totalStudyMinutes = minutes,
                completedPomodoros = pomodoros,
                equippedBadge = equipped
            )
        }
    }

    private fun loadUserData(username: String) {
        val u = username.trim().lowercase()
        val isAdmin = (u == "admin") || prefs.getBoolean("user_${u}_is_admin", prefs.getBoolean("is_admin", false))
        val coins = if (isAdmin) 10000 else prefs.getInt("user_${u}_coins", prefs.getInt("coins", 100))
        val streak = prefs.getInt("user_${u}_streak", prefs.getInt("streak", 0))
        val minutes = prefs.getInt("user_${u}_minutes", prefs.getInt("minutes", 0))
        val pomodoros = prefs.getInt("user_${u}_pomodoros", prefs.getInt("pomodoros", 0))
        val equippedBadge = prefs.getString("user_${u}_equipped_badge", prefs.getString("equipped_badge", "badge_zen_scholar"))
        val defaultBadges = if (isAdmin) setOf("badge_zen_scholar", "badge_time_master", "badge_night_owl") else setOf("badge_zen_scholar")
        val unlocked = prefs.getStringSet("user_${u}_unlocked_badges", prefs.getStringSet("unlocked_badges", defaultBadges))?.toList() ?: defaultBadges.toList()
        val defaultName = if (u == "admin") "Zephyr Administrator" else u.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        val displayName = prefs.getString("user_${u}_display_name", prefs.getString("display_name", defaultName)) ?: defaultName

        val user = User(
            id = "user_$u",
            username = u,
            displayName = displayName,
            isAdmin = isAdmin,
            coins = coins,
            streakDays = streak,
            totalStudyMinutes = minutes,
            completedPomodoros = pomodoros,
            equippedBadge = equippedBadge,
            unlockedBadges = unlocked
        )
        currentUser.value = user
        loadUserTasks(u)

        if (isAdmin) {
            replenishAdminCoins()
        }
    }

    private fun loadUserTasks(username: String) {
        tasks.clear()
        val u = username.trim().lowercase()
        val json = prefs.getString("user_${u}_tasks", null)
        if (!json.isNullOrEmpty()) {
            try {
                val type = object : TypeToken<List<TaskItem>>() {}.type
                val loaded: List<TaskItem> = gson.fromJson(json, type) ?: emptyList()
                tasks.addAll(loaded)
                return
            } catch (ignored: Exception) {
            }
        }
        // If admin and no tasks yet, seed default admin tasks
        if (u == "admin") {
            tasks.addAll(
                listOf(
                    TaskItem("1", "Algorithms Problem Set 4", "Logic", "Today, 11:59 PM", false),
                    TaskItem("2", "Review Material 3 Typography Specs", "Visual", "Tomorrow", true),
                    TaskItem("3", "Deep Study Block: Distributed Systems", "Focus", "Today, 4:00 PM", false)
                )
            )
            saveUserTasks(u)
        }
    }

    private fun saveUserTasks(username: String) {
        val u = username.trim().lowercase()
        val json = gson.toJson(tasks.toList())
        prefs.edit().putString("user_${u}_tasks", json).apply()
    }

    fun registerUser(username: String, password: String): Pair<Boolean, String?> {
        val trimmed = username.trim().lowercase()
        if (trimmed.length < 3) return Pair(false, "Username must be at least 3 characters")
        if (password.length < 4) return Pair(false, "Password must be at least 4 characters")

        val storedHash = prefs.getString("user_${trimmed}_pwd", prefs.getString("pwd_$trimmed", null))
        if (storedHash != null) {
            return Pair(false, "Username already registered. Please sign in.")
        }

        val pwdHash = CryptoUtils.hashPassword(password)
        val isAdmin = trimmed == "admin"
        val initialCoins = if (isAdmin) 10000 else 100
        val dispName = username.trim().replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        val badges = if (isAdmin) listOf("badge_zen_scholar", "badge_time_master", "badge_night_owl") else listOf("badge_zen_scholar")

        val user = User(
            id = "user_${System.currentTimeMillis()}",
            username = trimmed,
            displayName = dispName,
            isAdmin = isAdmin,
            coins = initialCoins,
            streakDays = 0,
            totalStudyMinutes = 0,
            completedPomodoros = 0,
            passwordHash = pwdHash,
            equippedBadge = "badge_zen_scholar",
            unlockedBadges = badges
        )

        prefs.edit()
            .putString("active_username", trimmed)
            .putString("username", trimmed)
            .putString("display_name", dispName)
            .putBoolean("is_admin", isAdmin)
            .putInt("coins", initialCoins)
            .putString("user_${trimmed}_pwd", pwdHash)
            .putString("pwd_$trimmed", pwdHash)
            .putString("user_${trimmed}_display_name", dispName)
            .putBoolean("user_${trimmed}_is_admin", isAdmin)
            .putInt("user_${trimmed}_coins", initialCoins)
            .putInt("user_${trimmed}_streak", 0)
            .putInt("user_${trimmed}_minutes", 0)
            .putInt("user_${trimmed}_pomodoros", 0)
            .putString("user_${trimmed}_equipped_badge", "badge_zen_scholar")
            .putStringSet("user_${trimmed}_unlocked_badges", badges.toSet())
            .putString("user_${trimmed}_tasks", "[]")
            .apply()

        val registered = getRegisteredUsersSet()
        registered.add(trimmed)
        saveRegisteredUsersSet(registered)

        tasks.clear()
        currentUser.value = user
        return Pair(true, null)
    }

    fun loginUser(username: String, password: String): Pair<Boolean, String?> {
        val trimmed = username.trim().lowercase()
        if (trimmed.isEmpty() || password.isEmpty()) {
            return Pair(false, "Please enter both username and password")
        }

        // Admin shortcut with PIN 1234 or admin123
        val isAdminMatch = trimmed == "admin" && (password == "1234" || password == "admin123" || password == "admin")
        if (isAdminMatch) {
            val user = User(
                id = "user_admin",
                username = "admin",
                displayName = "Zephyr Administrator",
                isAdmin = true,
                coins = 10000,
                streakDays = 7,
                equippedBadge = prefs.getString("user_admin_equipped_badge", prefs.getString("equipped_badge", "badge_time_master")),
                unlockedBadges = listOf("badge_zen_scholar", "badge_time_master", "badge_night_owl")
            )
            prefs.edit()
                .putString("active_username", "admin")
                .putString("username", "admin")
                .putString("display_name", "Zephyr Administrator")
                .putBoolean("is_admin", true)
                .putInt("coins", 10000)
                .putInt("user_admin_coins", 10000)
                .apply()
            currentUser.value = user
            loadUserTasks("admin")
            replenishAdminCoins()
            return Pair(true, null)
        }

        val storedHash = prefs.getString("user_${trimmed}_pwd", prefs.getString("pwd_$trimmed", null))
        val inputHash = CryptoUtils.hashPassword(password)

        if (storedHash != null) {
            if (storedHash != inputHash) {
                return Pair(false, "Incorrect password")
            }
        } else {
            // First time login auto-registers
            return registerUser(username, password)
        }

        prefs.edit().putString("active_username", trimmed).apply()
        loadUserData(trimmed)
        return Pair(true, null)
    }

    fun authenticateAdmin(pin: String): Boolean {
        if (pin.trim() == "1234") {
            val user = User(
                id = "admin_1",
                username = "admin",
                displayName = "Zephyr Administrator",
                isAdmin = true,
                coins = 10000,
                streakDays = 7,
                equippedBadge = "badge_time_master",
                unlockedBadges = listOf("badge_zen_scholar", "badge_time_master", "badge_night_owl")
            )
            prefs.edit()
                .putString("active_username", "admin")
                .putString("username", "admin")
                .putString("display_name", "Zephyr Administrator")
                .putBoolean("is_admin", true)
                .putInt("coins", 10000)
                .putInt("user_admin_coins", 10000)
                .apply()
            currentUser.value = user
            loadUserTasks("admin")
            replenishAdminCoins()
            return true
        }
        return false
    }

    fun adminCreateUser(username: String, password: String, initialCoins: Int = 100, isAdmin: Boolean = false): Pair<Boolean, String?> {
        val trimmed = username.trim().lowercase()
        if (trimmed.length < 3) return Pair(false, "Username must be at least 3 characters")
        if (password.length < 4) return Pair(false, "Password must be at least 4 characters")

        val existingHash = prefs.getString("user_${trimmed}_pwd", prefs.getString("pwd_$trimmed", null))
        if (existingHash != null) {
            return Pair(false, "Username '$trimmed' is already registered")
        }

        val pwdHash = CryptoUtils.hashPassword(password)
        val dispName = username.trim().replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        val badges = if (isAdmin) listOf("badge_zen_scholar", "badge_time_master", "badge_night_owl") else listOf("badge_zen_scholar")

        prefs.edit()
            .putString("user_${trimmed}_pwd", pwdHash)
            .putString("pwd_$trimmed", pwdHash)
            .putString("user_${trimmed}_display_name", dispName)
            .putBoolean("user_${trimmed}_is_admin", isAdmin)
            .putInt("user_${trimmed}_coins", initialCoins)
            .putInt("user_${trimmed}_streak", 0)
            .putInt("user_${trimmed}_minutes", 0)
            .putInt("user_${trimmed}_pomodoros", 0)
            .putString("user_${trimmed}_equipped_badge", "badge_zen_scholar")
            .putStringSet("user_${trimmed}_unlocked_badges", badges.toSet())
            .putString("user_${trimmed}_tasks", "[]")
            .apply()

        val registered = getRegisteredUsersSet()
        registered.add(trimmed)
        saveRegisteredUsersSet(registered)

        return Pair(true, null)
    }

    fun updateDisplayName(newName: String) {
        val user = currentUser.value ?: return
        val clean = newName.trim()
        if (clean.isEmpty()) return
        val updated = user.copy(displayName = clean)
        currentUser.value = updated
        val u = user.username.lowercase()
        prefs.edit()
            .putString("user_${u}_display_name", clean)
            .putString("display_name", clean)
            .apply()
    }

    fun replenishAdminCoins() {
        val user = currentUser.value ?: return
        if (user.isAdmin) {
            val updated = user.copy(coins = 10000)
            currentUser.value = updated
            val u = user.username.lowercase()
            prefs.edit()
                .putInt("user_${u}_coins", 10000)
                .putInt("coins", 10000)
                .apply()
        }
    }

    fun buyBadge(badgeId: String, cost: Int): Boolean {
        val user = currentUser.value ?: return false
        if (!user.isAdmin && user.coins < cost) return false

        val unlocked = user.unlockedBadges.toMutableList()
        if (!unlocked.contains(badgeId)) {
            unlocked.add(badgeId)
        }
        val newCoins = if (user.isAdmin) user.coins else user.coins - cost
        val updated = user.copy(coins = newCoins, unlockedBadges = unlocked, equippedBadge = badgeId)
        currentUser.value = updated

        val u = user.username.lowercase()
        prefs.edit()
            .putInt("user_${u}_coins", updated.coins)
            .putString("user_${u}_equipped_badge", badgeId)
            .putStringSet("user_${u}_unlocked_badges", unlocked.toSet())
            .putInt("coins", updated.coins)
            .putString("equipped_badge", badgeId)
            .putStringSet("unlocked_badges", unlocked.toSet())
            .apply()
        return true
    }

    fun equipBadge(badgeId: String): Boolean {
        val user = currentUser.value ?: return false
        if (!user.unlockedBadges.contains(badgeId) && !user.isAdmin) return false

        val updated = user.copy(equippedBadge = badgeId)
        currentUser.value = updated
        val u = user.username.lowercase()
        prefs.edit()
            .putString("user_${u}_equipped_badge", badgeId)
            .putString("equipped_badge", badgeId)
            .apply()
        return true
    }

    fun recordPomodoroCompletion(durationMinutes: Int = 25) {
        val user = currentUser.value ?: return
        val coinsEarned = maxOf(1, durationMinutes / 3)
        val updated = user.copy(
            coins = user.coins + coinsEarned,
            completedPomodoros = user.completedPomodoros + 1,
            totalStudyMinutes = user.totalStudyMinutes + durationMinutes
        )
        currentUser.value = updated
        val u = user.username.lowercase()
        prefs.edit()
            .putInt("user_${u}_coins", updated.coins)
            .putInt("user_${u}_pomodoros", updated.completedPomodoros)
            .putInt("user_${u}_minutes", updated.totalStudyMinutes)
            .putInt("coins", updated.coins)
            .putInt("pomodoros", updated.completedPomodoros)
            .putInt("minutes", updated.totalStudyMinutes)
            .apply()
    }

    fun toggleTask(taskId: String) {
        val index = tasks.indexOfFirst { it.id == taskId }
        if (index != -1) {
            val old = tasks[index]
            val willBeCompleted = !old.isCompleted
            tasks[index] = old.copy(isCompleted = willBeCompleted)
            val u = currentUser.value?.username?.lowercase()
            if (u != null) {
                saveUserTasks(u)
                if (willBeCompleted) {
                    val currentStreak = currentUser.value?.streakDays ?: 0
                    if (currentStreak == 0) {
                        val newStreak = 1
                        val updated = currentUser.value?.copy(streakDays = newStreak)
                        currentUser.value = updated
                        prefs.edit()
                            .putInt("user_${u}_streak", newStreak)
                            .putInt("streak", newStreak)
                            .apply()
                    }
                }
            }
        }
    }

    fun removeTask(taskId: String) {
        tasks.removeAll { it.id == taskId }
        currentUser.value?.username?.let { saveUserTasks(it) }
    }

    fun addTask(title: String, category: String, deadline: String) {
        tasks.add(0, TaskItem("task_${System.currentTimeMillis()}", title, category, deadline, false))
        currentUser.value?.username?.let { saveUserTasks(it) }
    }

    fun createStudyRoom(name: String, passkey: String, durationMinutes: Int = 25): StudyRoom {
        val hostUsername = currentUser.value?.username ?: "student"
        val roomId = "room_${System.currentTimeMillis()}"
        val cleanPass = if (passkey.isBlank()) "STU123" else passkey.trim().uppercase()
        val cleanName = if (name.isBlank()) "Peer Focus Pod" else name.trim()
        val newRoom = StudyRoom(
            id = roomId,
            name = cleanName,
            passkey = cleanPass,
            participantsCount = 1,
            pomodoroSecondsRemaining = durationMinutes * 60,
            creatorUsername = hostUsername,
            isOfficialPod = false,
            timerDurationMinutes = durationMinutes,
            isTimerRunning = false
        )
        rooms.add(0, newRoom)
        return newRoom
    }

    fun addOfficialPod(name: String, building: String): OfficialPod {
        val id = "pod_${System.currentTimeMillis()}"
        val pod = OfficialPod(id, name.trim(), building.trim())
        officialPods.add(pod)
        rooms.add(
            StudyRoom(
                id = id,
                name = name.trim(),
                passkey = "CAMPUS",
                participantsCount = 1,
                pomodoroSecondsRemaining = 1500,
                creatorUsername = "admin",
                isOfficialPod = true,
                timerDurationMinutes = 25,
                isTimerRunning = false
            )
        )
        return pod
    }

    fun removeOfficialPod(podId: String) {
        officialPods.removeAll { it.id == podId }
        rooms.removeAll { it.id == podId }
    }

    fun createBooking(facilityName: String, building: String, timeSlot: String, date: String): BookingPass {
        val student = currentUser.value?.displayName ?: "Student"
        val pass = BookingPass(
            id = "pass_${System.currentTimeMillis()}",
            facilityName = facilityName,
            building = building,
            timeSlot = timeSlot,
            date = date,
            studentUsername = student,
            status = "Reserved",
            qrCodeSeed = "ZEPHYR-QR-${System.currentTimeMillis()}"
        )
        bookings.add(0, pass)
        return pass
    }

    fun logout() {
        currentUser.value = null
        tasks.clear()
        prefs.edit()
            .remove("active_username")
            .remove("username")
            .remove("display_name")
            .remove("is_admin")
            .apply()
    }
}
