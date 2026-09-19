import sqlite3
import hashlib
import json
import os
from typing import Dict, List, Optional
from fastapi import FastAPI, WebSocket, WebSocketDisconnect, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel

DB_PATH = os.path.join(os.path.dirname(__file__), "zephyr.db")

app = FastAPI(title="Zephyr Student Wellbeing API", version="1.0.0")

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

def init_db():
    conn = sqlite3.connect(DB_PATH)
    cur = conn.cursor()
    cur.execute("""
    CREATE TABLE IF NOT EXISTS users (
        id TEXT PRIMARY KEY,
        username TEXT UNIQUE NOT NULL,
        password_hash TEXT NOT NULL,
        display_name TEXT NOT NULL,
        coins INTEGER DEFAULT 100,
        streak_days INTEGER DEFAULT 1,
        is_admin INTEGER DEFAULT 0,
        equipped_badge TEXT,
        unlocked_badges TEXT DEFAULT '[]'
    )
    """)
    cur.execute("""
    CREATE TABLE IF NOT EXISTS announcements (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        message TEXT NOT NULL,
        timestamp DATETIME DEFAULT CURRENT_TIMESTAMP
    )
    """)
    cur.execute("""
    CREATE TABLE IF NOT EXISTS bookings (
        id TEXT PRIMARY KEY,
        facility_name TEXT NOT NULL,
        location TEXT NOT NULL,
        time_slot TEXT NOT NULL,
        date_str TEXT NOT NULL,
        username TEXT NOT NULL,
        status TEXT DEFAULT 'CONFIRMED'
    )
    """)
    cur.execute("""
    CREATE TABLE IF NOT EXISTS study_rooms (
        id TEXT PRIMARY KEY,
        name TEXT NOT NULL,
        passkey TEXT NOT NULL,
        creator_username TEXT NOT NULL,
        duration_minutes INTEGER DEFAULT 25,
        is_official INTEGER DEFAULT 0
    )
    """)
    cur.execute("""
    CREATE TABLE IF NOT EXISTS official_pods (
        id TEXT PRIMARY KEY,
        name TEXT NOT NULL,
        building TEXT NOT NULL
    )
    """)
    
    # Ensure default admin user exists
    admin_hash = hashlib.sha256("admin123".encode()).hexdigest()
    cur.execute("SELECT id FROM users WHERE username = 'admin'")
    if not cur.fetchone():
        cur.execute("""
        INSERT INTO users (id, username, password_hash, display_name, coins, streak_days, is_admin, equipped_badge, unlocked_badges)
        VALUES ('user_admin', 'admin', ?, 'Zephyr Administrator', 10000, 14, 1, 'badge_time_master', '["badge_time_master","badge_zen_scholar","badge_night_owl"]')
        """, (admin_hash,))
        
    # Default announcement
    cur.execute("SELECT id FROM announcements")
    if not cur.fetchone():
        cur.execute("INSERT INTO announcements (message) VALUES ('📢 Zephyr Finals Week: Library Silent Pods are open 24/7 with free brain snacks!')")

    # Seed default official pods
    cur.execute("SELECT id FROM official_pods")
    if not cur.fetchone():
        cur.execute("INSERT INTO official_pods (id, name, building) VALUES ('pod_1', 'Library Silent Pod 4B', 'Science Library Floor 3')")
        cur.execute("INSERT INTO official_pods (id, name, building) VALUES ('pod_2', 'Discussion Room 2A', 'Student Union Hub')")

    # Seed default rooms
    cur.execute("SELECT id FROM study_rooms")
    if not cur.fetchone():
        cur.execute("INSERT INTO study_rooms (id, name, passkey, creator_username, duration_minutes, is_official) VALUES ('room_1', 'Library Silent Pod 4B', 'STU123', 'admin', 20, 1)")
        cur.execute("INSERT INTO study_rooms (id, name, passkey, creator_username, duration_minutes, is_official) VALUES ('room_2', 'Late Night Hackers Lounge', 'CODE88', 'alex', 25, 0)")

    conn.commit()
    conn.close()

init_db()

class RegisterRequest(BaseModel):
    username: str
    password: str
    display_name: Optional[str] = None

class LoginRequest(BaseModel):
    username: str
    password: Optional[str] = None
    admin_pin: Optional[str] = None

class AnnouncementRequest(BaseModel):
    message: str

class BadgeActionRequest(BaseModel):
    username: str
    badge_id: str
    action: str  # 'buy' or 'equip'

class CreateRoomRequest(BaseModel):
    name: str
    passkey: str
    creator_username: str = "student"
    duration_minutes: int = 25

class AdminUserRequest(BaseModel):
    username: str
    password: str
    initial_coins: int = 100
    is_admin: bool = False

class OfficialPodRequest(BaseModel):
    name: str
    building: str

# In-Memory WebSocket Room Manager
class ConnectionManager:
    def __init__(self):
        self.active_rooms: Dict[str, List[WebSocket]] = {}

    async def connect(self, room_id: str, websocket: WebSocket):
        await websocket.accept()
        if room_id not in self.active_rooms:
            self.active_rooms[room_id] = []
        self.active_rooms[room_id].append(websocket)

    def disconnect(self, room_id: str, websocket: WebSocket):
        if room_id in self.active_rooms and websocket in self.active_rooms[room_id]:
            self.active_rooms[room_id].remove(websocket)

    async def broadcast_room(self, room_id: str, message: dict):
        if room_id in self.active_rooms:
            dead_connections = []
            for connection in self.active_rooms[room_id]:
                try:
                    await connection.send_text(json.dumps(message))
                except Exception:
                    dead_connections.append(connection)
            for dead in dead_connections:
                self.disconnect(room_id, dead)

manager = ConnectionManager()

PORT = int(os.environ.get("PORT", 8080))

@app.get("/api/health")
def health():
    return {"status": "ok", "service": "Zephyr Wellbeing Server", "port": PORT}

@app.post("/api/auth/register")
def register(req: RegisterRequest):
    u = req.username.strip().lower()
    if not u or not req.password:
        raise HTTPException(status_code=400, detail="Username and password required")
    
    conn = sqlite3.connect(DB_PATH)
    cur = conn.cursor()
    cur.execute("SELECT id FROM users WHERE username = ?", (u,))
    if cur.fetchone():
        conn.close()
        raise HTTPException(status_code=400, detail="Username already taken")
    
    user_id = f"user_{hashlib.md5(u.encode()).hexdigest()[:8]}"
    pwd_hash = hashlib.sha256(req.password.encode()).hexdigest()
    disp_name = req.display_name or req.username.capitalize()
    is_adm = 1 if u == "admin" else 0
    init_coins = 10000 if is_adm else 100
    
    cur.execute("""
    INSERT INTO users (id, username, password_hash, display_name, coins, streak_days, is_admin, equipped_badge, unlocked_badges)
    VALUES (?, ?, ?, ?, ?, 1, ?, NULL, '[]')
    """, (user_id, u, pwd_hash, disp_name, init_coins, is_adm))
    conn.commit()
    conn.close()
    
    return {
        "success": True,
        "user": {
            "id": user_id,
            "username": u,
            "displayName": disp_name,
            "coins": init_coins,
            "streakDays": 1,
            "isAdmin": bool(is_adm),
            "equippedBadge": None,
            "unlockedBadges": []
        }
    }

@app.post("/api/auth/login")
def login(req: LoginRequest):
    u = req.username.strip().lower()
    
    # Admin PIN bypass
    if req.admin_pin == "1234" or (u == "admin" and req.password in ["1234", "admin123", "admin"]):
        conn = sqlite3.connect(DB_PATH)
        cur = conn.cursor()
        # Ensure admin balance refreshed to 10k every login
        cur.execute("UPDATE users SET coins = 10000 WHERE username = 'admin'")
        conn.commit()
        cur.execute("SELECT id, username, display_name, coins, streak_days, is_admin, equipped_badge, unlocked_badges FROM users WHERE username = 'admin'")
        row = cur.fetchone()
        conn.close()
        return {
            "success": True,
            "user": {
                "id": row[0],
                "username": row[1],
                "displayName": row[2],
                "coins": 10000,
                "streakDays": row[4],
                "isAdmin": True,
                "equippedBadge": row[6],
                "unlockedBadges": json.loads(row[7] or "[]")
            }
        }
        
    pwd_hash = hashlib.sha256((req.password or "").encode()).hexdigest()
    conn = sqlite3.connect(DB_PATH)
    cur = conn.cursor()
    cur.execute("SELECT id, username, password_hash, display_name, coins, streak_days, is_admin, equipped_badge, unlocked_badges FROM users WHERE username = ?", (u,))
    row = cur.fetchone()
    if not row or row[2] != pwd_hash:
        conn.close()
        raise HTTPException(status_code=401, detail="Invalid username or password")
    
    coins = 10000 if row[6] else row[4]
    if row[6]:
        cur.execute("UPDATE users SET coins = 10000 WHERE id = ?", (row[0],))
        conn.commit()
        
    user_data = {
        "id": row[0],
        "username": row[1],
        "displayName": row[3],
        "coins": coins,
        "streakDays": row[5],
        "isAdmin": bool(row[6]),
        "equippedBadge": row[7],
        "unlockedBadges": json.loads(row[8] or "[]")
    }
    conn.close()
    return {"success": True, "user": user_data}

@app.get("/api/announcements")
def get_announcement():
    conn = sqlite3.connect(DB_PATH)
    cur = conn.cursor()
    cur.execute("SELECT message FROM announcements ORDER BY id DESC LIMIT 1")
    row = cur.fetchone()
    conn.close()
    return {"message": row[0] if row else "Welcome to Zephyr Student Wellbeing!"}

@app.post("/api/announcements")
def set_announcement(req: AnnouncementRequest):
    conn = sqlite3.connect(DB_PATH)
    cur = conn.cursor()
    cur.execute("INSERT INTO announcements (message) VALUES (?)", (req.message.strip(),))
    conn.commit()
    conn.close()
    return {"success": True, "message": req.message.strip()}

@app.post("/api/user/badge")
def handle_badge(req: BadgeActionRequest):
    prices = {"badge_zen_scholar": 50, "badge_time_master": 100, "badge_night_owl": 150}
    conn = sqlite3.connect(DB_PATH)
    cur = conn.cursor()
    cur.execute("SELECT id, coins, equipped_badge, unlocked_badges, is_admin FROM users WHERE username = ?", (req.username.strip().lower(),))
    row = cur.fetchone()
    if not row:
        conn.close()
        raise HTTPException(status_code=404, detail="User not found")
        
    user_id, coins, equipped, unlocked_json, is_admin = row
    unlocked: list = json.loads(unlocked_json or "[]")
    
    if req.action == "buy":
        cost = prices.get(req.badge_id, 50)
        if coins < cost and not is_admin:
            conn.close()
            raise HTTPException(status_code=400, detail="Not enough coins")
        if req.badge_id not in unlocked:
            unlocked.append(req.badge_id)
        new_coins = coins if is_admin else coins - cost
        equipped = req.badge_id
        cur.execute("UPDATE users SET coins = ?, equipped_badge = ?, unlocked_badges = ? WHERE id = ?", (new_coins, equipped, json.dumps(unlocked), user_id))
        conn.commit()
        conn.close()
        return {"success": True, "coins": new_coins, "equippedBadge": equipped, "unlockedBadges": unlocked}
        
    elif req.action == "equip":
        if req.badge_id in unlocked or is_admin:
            equipped = req.badge_id
            cur.execute("UPDATE users SET equipped_badge = ? WHERE id = ?", (equipped, user_id))
            conn.commit()
            conn.close()
            return {"success": True, "equippedBadge": equipped}
        else:
            conn.close()
            raise HTTPException(status_code=400, detail="Badge not unlocked yet")

    conn.close()
    raise HTTPException(status_code=400, detail="Invalid action")

# Study Rooms API
@app.get("/api/rooms")
def list_rooms():
    conn = sqlite3.connect(DB_PATH)
    cur = conn.cursor()
    cur.execute("SELECT id, name, passkey, creator_username, duration_minutes, is_official FROM study_rooms")
    rows = cur.fetchall()
    conn.close()
    return {
        "rooms": [
            {
                "id": r[0],
                "name": r[1],
                "passkey": r[2],
                "creatorUsername": r[3],
                "durationMinutes": r[4],
                "isOfficialPod": bool(r[5])
            }
            for r in rows
        ]
    }

@app.post("/api/rooms/create")
def create_room(req: CreateRoomRequest):
    conn = sqlite3.connect(DB_PATH)
    cur = conn.cursor()
    room_id = f"room_{hashlib.md5(f'{req.name}_{req.passkey}_{os.urandom(4)}'.encode()).hexdigest()[:8]}"
    cur.execute(
        "INSERT INTO study_rooms (id, name, passkey, creator_username, duration_minutes, is_official) VALUES (?, ?, ?, ?, ?, 0)",
        (room_id, req.name.strip(), req.passkey.strip().upper(), req.creator_username.strip(), req.duration_minutes)
    )
    conn.commit()
    conn.close()
    return {
        "success": True,
        "room": {
            "id": room_id,
            "name": req.name.strip(),
            "passkey": req.passkey.strip().upper(),
            "creatorUsername": req.creator_username.strip(),
            "durationMinutes": req.duration_minutes,
            "isOfficialPod": False
        }
    }

# Admin User Management API
@app.get("/api/admin/users")
def list_users():
    conn = sqlite3.connect(DB_PATH)
    cur = conn.cursor()
    cur.execute("SELECT id, username, display_name, coins, streak_days, is_admin FROM users")
    rows = cur.fetchall()
    conn.close()
    return {
        "users": [
            {
                "id": r[0],
                "username": r[1],
                "displayName": r[2],
                "coins": r[3],
                "streakDays": r[4],
                "isAdmin": bool(r[5])
            }
            for r in rows
        ]
    }

@app.post("/api/admin/users")
def admin_provision_user(req: AdminUserRequest):
    u = req.username.strip().lower()
    if len(u) < 3 or len(req.password) < 4:
        raise HTTPException(status_code=400, detail="Username min 3 chars, password min 4 chars")
    
    conn = sqlite3.connect(DB_PATH)
    cur = conn.cursor()
    cur.execute("SELECT id FROM users WHERE username = ?", (u,))
    if cur.fetchone():
        conn.close()
        raise HTTPException(status_code=400, detail="Username already exists")
    
    user_id = f"user_{hashlib.md5(u.encode()).hexdigest()[:8]}"
    pwd_hash = hashlib.sha256(req.password.encode()).hexdigest()
    disp_name = req.username.strip().capitalize()
    is_adm = 1 if req.is_admin else 0
    coins = 10000 if is_adm else req.initial_coins
    
    cur.execute("""
    INSERT INTO users (id, username, password_hash, display_name, coins, streak_days, is_admin, equipped_badge, unlocked_badges)
    VALUES (?, ?, ?, ?, ?, 1, ?, 'badge_zen_scholar', '["badge_zen_scholar"]')
    """, (user_id, u, pwd_hash, disp_name, coins, is_adm))
    conn.commit()
    conn.close()
    
    return {"success": True, "message": f"User {u} provisioned successfully"}

# Admin Official Pod Management API
@app.get("/api/admin/pods")
def list_official_pods():
    conn = sqlite3.connect(DB_PATH)
    cur = conn.cursor()
    cur.execute("SELECT id, name, building FROM official_pods")
    rows = cur.fetchall()
    conn.close()
    return {"pods": [{"id": r[0], "name": r[1], "building": r[2]} for r in rows]}

@app.post("/api/admin/pods")
def create_official_pod(req: OfficialPodRequest):
    conn = sqlite3.connect(DB_PATH)
    cur = conn.cursor()
    pod_id = f"pod_{hashlib.md5(f'{req.name}_{req.building}'.encode()).hexdigest()[:8]}"
    cur.execute("INSERT INTO official_pods (id, name, building) VALUES (?, ?, ?)", (pod_id, req.name.strip(), req.building.strip()))
    # Also add to study rooms as official pod
    cur.execute(
        "INSERT INTO study_rooms (id, name, passkey, creator_username, duration_minutes, is_official) VALUES (?, ?, 'CAMPUS', 'admin', 25, 1)",
        (pod_id, req.name.strip())
    )
    conn.commit()
    conn.close()
    return {"success": True, "pod": {"id": pod_id, "name": req.name.strip(), "building": req.building.strip()}}

@app.delete("/api/admin/pods/{pod_id}")
def delete_official_pod(pod_id: str):
    conn = sqlite3.connect(DB_PATH)
    cur = conn.cursor()
    cur.execute("DELETE FROM official_pods WHERE id = ?", (pod_id,))
    cur.execute("DELETE FROM study_rooms WHERE id = ?", (pod_id,))
    conn.commit()
    conn.close()
    return {"success": True, "message": f"Pod {pod_id} deleted"}

@app.websocket("/ws/study-room/{room_id}")
async def websocket_study_room(websocket: WebSocket, room_id: str):
    await manager.connect(room_id, websocket)
    try:
        while True:
            data = await websocket.receive_text()
            message_obj = json.loads(data)
            await manager.broadcast_room(room_id, message_obj)
    except WebSocketDisconnect:
        manager.disconnect(room_id, websocket)
    except Exception:
        manager.disconnect(room_id, websocket)

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=PORT)
