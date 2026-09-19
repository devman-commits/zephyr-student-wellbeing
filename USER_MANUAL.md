# 📘 Zephyr Student Wellbeing — Complete User Manual

Welcome to the **Zephyr Student Wellbeing & Productivity Platform** user manual. This document guides students, campus administrators, and hackathon evaluators through the features and capabilities of the Zephyr Android application and backend service.

---

## 📑 Table of Contents
1. [Overview & System Requirements](#1-overview--system-requirements)
2. [Installation & Setup](#2-installation--setup)
3. [Authentication & Roles](#3-authentication--roles)
4. [Student User Guide](#4-student-user-guide)
   - [Daily Wellbeing & Circular Progress Gauge](#daily-wellbeing--circular-progress-gauge)
   - [Smart Planner & Task-Driven Streaks](#smart-planner--task-driven-streaks)
   - [Pomodoro Timer & 1-Coin-Per-3-Min Rule](#pomodoro-timer--1-coin-per-3-min-rule)
   - [Collaborative Study Rooms & Host-Controlled Timers](#collaborative-study-rooms--host-controlled-timers)
   - [Client-Side AES-256 Encrypted Chat](#client-side-aes-256-encrypted-chat)
   - [Campus Hub & Mock Facility QR Booking](#campus-hub--mock-facility-qr-booking)
   - [Rewards Store & Profile Badges](#rewards-store--profile-badges)
   - [Dedicated Student Profile Management](#dedicated-student-profile-management)
5. [Administrator User Guide](#5-administrator-user-guide)
   - [Exclusive Admin Portal Access](#exclusive-admin-portal-access)
   - [Inspecting the Users Database](#inspecting-the-users-database)
   - [Provisioning New Student Accounts](#provisioning-new-student-accounts)
   - [Official Campus Pod Facilities Management](#official-campus-pod-facilities-management)
   - [Broadcasting Campus Alert Banners](#broadcasting-campus-alert-banners)
   - [Configuring Backend Server URL](#configuring-backend-server-url)
6. [Backend API Reference](#6-backend-api-reference)
7. [Troubleshooting & FAQ](#7-troubleshooting--faq)

---

## 1. Overview & System Requirements

Zephyr is built natively using:
- **Mobile Client**: Kotlin 1.9+, Jetpack Compose, Material 3, Android SDK 26+ (Android 8.0 Oreo through Android 14+).
- **Backend Service**: Python 3.10+, FastAPI, SQLite, Uvicorn, WebSockets.
- **Crypto Engine**: AES-256 with PBKDF2/SHA-256 key derivation.

---

## 2. Installation & Setup

### Installing the APK on Android
Connect your Android device with USB debugging enabled, or start an Android emulator:
```bash
adb install -r zephyr-student-wellbeing.apk
```

### Running the FastAPI Backend
In the project root directory:
```bash
cd backend
source venv/bin/activate
python server.py
# Server listens on port 8080 (http://0.0.0.0:8080)
```

---

## 3. Authentication & Roles

### Student Accounts
- Any student can register on the login screen using the **Create Account** tab with any chosen username (min 3 characters) and password (min 4 characters).
- Students receive **100 welcome coins** and an initial **0-day streak**.
- Student accounts have **zero access** to administrator controls or dashboards.

### Administrator Accounts
- **Username**: `admin`
- **Password**: `admin` or PIN `1234`
- Admin credentials grant access **only** to the **Admin & Moderator Dashboard** and administrative tools.

---

## 4. Student User Guide

### Daily Wellbeing & Circular Progress Gauge
- The top half of the Home screen features a flat-bottom 180° circular semicircle progress gauge animated with smooth physics.
- Reflects real-time productivity based on completed tasks, study minutes, and focus sessions.

### Smart Planner & Task-Driven Streaks
- **Task Creation**: Tap the floating `+` button in the **Planner** tab to add tasks with categories (Logic, Visual, Focus) and deadlines.
- **Task-Driven Streak Rule**:
  > **Important**: Daily streaks begin at **0 Days**. The streak officially starts (`🔥 1d`) **only when a student checks off and completes a task in the Smart Planner**.
- **Task Removal**: Tap the trash icon next to any task to permanently remove it.

### Pomodoro Timer & 1-Coin-Per-3-Min Rule
- Located at the top of the **Planner** tab.
- **Duration Pill Selector**: Tap `15m`, `25m`, `30m`, `45m`, or `60m`.
- **Dynamic Reward Calculation**:
  - `Coins Earned = max(1, durationMinutes / 3)`
  - 15 minutes = **5 Coins**
  - 25 minutes = **8 Coins**
  - 30 minutes = **10 Coins**
  - 45 minutes = **15 Coins**
  - 60 minutes = **20 Coins**

### Collaborative Study Rooms & Host-Controlled Timers
- Navigate to the **Rooms** tab.
- **Create Room**: Tap **"Create"** in the top right. Enter a room name, 6-character room passkey, and choose your timer length.
- **Host Control**:
  - When you create a room, you are designated the **Host** with `👑 HOST CONTROLS` (Start / Pause / Reset).
  - Joining peers see a synchronized countdown: `⏱️ Timer synchronized with host (@host_username)`.

### Client-Side AES-256 Encrypted Chat
- In study rooms, all messages are encrypted on your device using AES-256 before transmission.
- **Ciphertext Inspector**: Flip the toggle switch in the room header to view the raw wire ciphertext.

### Campus Hub & Mock Facility QR Booking
- Access academic links (Library, Student Wellbeing, Career Hub).
- Book campus facilities (e.g. *Library Silent Pod 4B*) to generate an interactive digital reservation pass with a QR code seed.

### Rewards Store & Profile Badges
- Visit the **Shop** tab to spend earned Campus Coins:
  - **Zen Scholar** (50 Coins) — 📚
  - **Time Master** (100 Coins) — ⏳
  - **Night Owl** (150 Coins) — 🦉
- Equipping a badge updates your avatar across Home, Profile, and Chat.

### Dedicated Student Profile Management
- Tap your avatar on the Home screen or TopAppBar to open your full **Profile**:
  - View equipped badge artwork in high resolution.
  - Edit and save your display name.
  - Inspect personal metrics (Coins, Streak, Study Minutes, Completed Pomodoros).
  - Switch between unlocked badges.

---

## 5. Administrator User Guide

### Exclusive Admin Portal Access
- Administrators log in with `admin` / PIN `1234`.
- The interface opens directly to the **Admin & Moderator Dashboard**.
- Admin accounts automatically replenish to **10,000 Sandbox Coins** for limitless testing.

### Inspecting the Users Database
- In the Admin Dashboard, the **Users Database** displays all registered student and administrator accounts.
- Shows live metrics for each user: username, display name, role, coins balance, daily streak, total focus minutes, and completed Pomodoros.

### Provisioning New Student Accounts
- Administrators can provision accounts directly via the **Provision New Student User** card.
- Set custom usernames, passwords, initial coin allowances, and administrator privileges.

### Official Campus Pod Facilities Management
- Official campus study pods are managed **exclusively by Administrators**.
- Admins can add new campus pod facilities (Name and Building/Floor) or delete existing pods.

### Broadcasting Campus Alert Banners
- Post broadcast announcements that appear on all student home dashboards in real time.

### Configuring Backend Server URL
- Customize the server URL (default `http://10.0.2.2:8080` for emulator or host IP for physical phones).

---

## 6. Backend API Reference

| Endpoint | Method | Description |
|---|:---:|---|
| `/api/health` | GET | Health status check |
| `/api/auth/register` | POST | Student account registration |
| `/api/auth/login` | POST | Student and admin authentication |
| `/api/rooms` | GET | List available study rooms |
| `/api/rooms/create` | POST | Create peer study room |
| `/api/admin/users` | GET / POST | List and provision users |
| `/api/admin/pods` | GET / POST | List and create official pods |
| `/api/admin/pods/{id}` | DELETE | Remove official pod |
| `/api/announcements` | GET / POST | Read or broadcast campus alerts |
| `/ws/study-room/{id}` | WebSocket | Real-time encrypted room chat |

---

## 7. Troubleshooting & FAQ

**Q: Why is my streak showing 0 days?**  
A: Daily streaks start only when you complete your first study task in the Smart Planner. Check off a task to ignite your streak!

**Q: Can students access the Admin Dashboard?**  
A: No. Administrator tools and database access are strictly isolated to authenticated administrator credentials.

**Q: How do study room passkeys work?**  
A: The 6-character passkey is used directly by the cryptographic engine to derive the AES-256 key. Only participants who entered the correct passkey can decrypt messages.
