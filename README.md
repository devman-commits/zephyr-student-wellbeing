# 🎓 Zephyr Student Wellbeing & Productivity Platform

> **Smart Student Wellbeing & Productivity Platform with Integrated Planning, Collaboration & Mock Services**  
> Built for the Zephyr App Development Hackathon using **Kotlin & Jetpack Compose (Material 3)** with a local **Python FastAPI Backend**.

---

## 🌟 Overview & Problem Solved

Students often struggle to balance academics, wellness, and campus collaboration. Existing tools are fragmented, clunky, and lack privacy.

**Zephyr** is an open, private, and engaging student ecosystem:
1. **Material 3 / Material You Design**: True 180° circular semicircle progress gauge, weekday streak bar, calming pastel palettes, and system-synced Dark/Light modes with WCAG-compliant high contrast.
2. **Encrypted Study Rooms & Host-Controlled Timers**: Peer study rooms protected by **client-side AES-256 encryption** derived from 6-character room passkeys. The room creator (host) controls the synchronized Pomodoro focus timer (**15m, 25m, 30m, 45m, 60m**), while joining peers view real-time countdowns.
3. **Smart Planner & Task-Driven Streaks**: Focus routines and task checklists. **Study streaks start only when a student marks their first task as completed.**
4. **Custom Pomodoro Rewards (1 Coin / 3 Mins)**: Dynamic focus rewards awarding **1 coin per 3 minutes** of deep study (`max(1, duration / 3)`).
5. **Dedicated Profile & Identity Screen**: Displays custom profile badge art, editable display name, verified role pills, and personal wellbeing metrics.
6. **Strict Role Security & Admin Portal**:
   - **Student Accounts**: Pure student experience without admin controls or distractions.
   - **Admin Account**: Dedicated administrator portal with access to the **Users Database** (live records of all accounts, coins, streaks, study minutes, and roles), **Official Campus Pods Manager** (library facility management), broadcast alert banners, and 10,000 auto-refreshing sandbox coins.
7. **Local FastAPI & SQLite Backend**: Real-time study room messaging via WebSockets, SQLite database for user accounts, announcements, and campus bookings.

---

## 🏗️ Architecture & Security Model

```
                    ┌────────────────────────┐
                    │    Student Devices     │
                    └───────────┬────────────┘
                                │
                  AES-256 Wire  │ WebSocket / REST
                   Ciphertext   │ (Port 8080)
                                ▼
                    ┌────────────────────────┐
                    │ FastAPI Backend Server │
                    │    SQLite Database     │
                    └───────────┬────────────┘
                                │
                                ▼
                    ┌────────────────────────┐
                    │ Admin Dashboard Portal │
                    │ - Users Database View  │
                    │ - Official Pod Manager │
                    │ - Broadcast Alerts     │
                    └────────────────────────┘
```

### 🔐 Zero-Knowledge AES-256 Encryption
- Plaintext messages are encrypted directly on the client using AES-256 with a SHA-256 key derived from the 6-character room passkey.
- The server only relays ciphertext; neither the server nor eavesdroppers can inspect chat content.
- Presenters can toggle the **Ciphertext Inspector** in the chat room to display raw wire strings in real time.

---

## 📱 User Roles & Access Control

| Feature | Student User | Campus Administrator |
|---|:---:|:---:|
| **Daily Home & Wellbeing Gauge** | ✅ | ❌ (Dedicated Admin View) |
| **Smart Planner & Task List** | ✅ | ❌ |
| **Task Completion Starts Streak** | ✅ | ❌ |
| **Create Custom Study Rooms & Host Timers** | ✅ | ✅ |
| **Encrypted Peer Chat** | ✅ | ✅ |
| **Profile Screen & Badge Switcher** | ✅ | ❌ |
| **Admin & Moderator Dashboard** | ❌ (Access Removed) | ✅ (Exclusive) |
| **Users Database Access** | ❌ | ✅ |
| **Official Campus Pods Management** | ❌ (View Only) | ✅ (Create & Delete) |
| **10,000 Auto-Refreshing Coins** | ❌ (Earned via Focus) | ✅ (Sandbox Testing) |

---

## 🚀 Quick Start & Installation

### 1. Install Pre-built Android APK
The verified APK is packaged and ready to test:
```bash
adb install -r zephyr-student-wellbeing.apk
```

### 2. Start the Local FastAPI Backend Server
```bash
cd backend
source venv/bin/activate  # or python3 -m venv venv
pip install -r requirements.txt
python server.py
# Server starts on http://0.0.0.0:8080
```

### 3. Build from Source
```bash
./gradlew assembleDebug
# Generated APK: app/build/outputs/apk/debug/app-debug.apk
```

---

## 🧑‍💻 Credentials & Testing Walkthrough

- **Student Login**:
  - Sign in or create any new student account (e.g. `alex` / `pass1234`).
  - Starts with 100 Campus Coins and **0d Streak**.
  - Complete a task in **Planner** to start your streak (`🔥 1d`).
- **Administrator Login**:
  - Username: `admin`
  - Password: `admin` or PIN `1234`
  - Accesses the exclusive **Admin Dashboard** with the full **Users Database**, **Campus Pod Manager**, and **10,000 sandbox coins**.
