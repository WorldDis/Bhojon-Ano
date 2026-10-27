# BHOJON.FOOD (ভোজন.ফুড) — Full Stack Bengali Food App & Backend

> **"Gharer Swad, Sabar Jonno"** *(Authentic Bengali home-style food delivered conveniently)*

A complete, production-ready solution featuring:
1. **Android App**: Native Android application written in Kotlin and Jetpack Compose (Material Design 3).
2. **Backend Server**: 100% free Node.js / Express REST API (`/backend`) with pre-seeded Bengali dishes, zero database setup fees, and role-based access control.

---

## 🌟 Key Features

### 1. Customer Table (গ্রাহক টেবিল)
- **Daily Rotating Menu**: Morning Tiffin (Luchi, Radhaballabhi), Lunch Thalis (Fish, Mutton, Niramish, Hilsa/Ilish, Chingri Malai), and Dinner Boxes.
- **Role-Based Authentication**:
  - Sign In with Mobile & Password
  - Sign Up with Full Name, Mobile, Delivery Address, and Password
  - Address auto-population in Cart Checkout.
- **Subscriptions**: Weekly & Monthly Bengali meal subscriptions with meal slot delivery management.
- **Live Order Tracking**: Visual 5-step order timeline (Pending → Confirmed → Cooking → Out for Delivery → Delivered).

### 2. Kitchen Team Desk (কর্মচারী / Employee)
- **Staff Shift Attendance**: One-tap daily Check-In and Check-Out with shift timestamps.
- **Order Queue**: View kitchen orders in real-time, transition cooking stages.
- **Assigned Duties & Leave Applications**: Casual/Sick leave management with status tracking.
- **Protected Access**: Log in using registered mobile/code and password. Public registration is locked.

### 3. Restaurant Desk (অ্যাডমিন / Administrator)
- **Single Master Admin**:
  - **User ID**: `Administrator`
  - **Password**: `Admin1234`
- **Employee Onboarding**: Admin registers kitchen & delivery team members and sets their initial login passwords directly.
- **Real-time KPIs**: Revenue, orders, pending kitchen items, active workforce.
- **Menu & Dish Editor**: Add new dishes, adjust pricing, toggle availability, manage daily inventory.

---

## 📁 Project Structure

```
├── app/                  # Android Kotlin & Jetpack Compose Source Code
│   ├── src/main/java/com/example/
│   │   ├── data/         # BhojonRepository (StateFlows, reactive caching, seed data)
│   │   ├── model/        # Domain data models (CustomerUser, Employee, Order, MenuItem)
│   │   └── ui/
│   │       ├── components/  # AuthDialogs, CommonUi, BhojonTopBar, Brand Logo
│   │       ├── screens/     # CustomerMenu, EmployeeScreen, AdminDesk, Cart, Tracking
│   │       └── theme/       # Rustic Bengali Palette (Forest Green, Cream, Terracotta)
│   └── build.gradle.kts
│
├── backend/              # 100% Free Node.js REST API Backend
│   ├── server.js         # Express server with auth, menu, order, and employee routes
│   ├── seedData.json     # Pre-seeded authentic Bengali food items & initial accounts
│   ├── package.json
│   └── README.md
│
├── gradle/               # Gradle wrapper & version catalogs
├── build.gradle.kts      # Root build configuration
└── settings.gradle.kts
```

---

## 🚀 How to Run the Project

### 📱 Android Application
1. Open this repository folder in **Android Studio** (Ladybug or newer recommended).
2. Allow Gradle to sync.
3. Run on an Android Device or Emulator (API 26+).

### 🖥️ Free Backend Server
```bash
cd backend
npm install
npm start
```
The server will start on `http://localhost:5000`.

To deploy online for free (Render.com, Railway, Koyeb, Vercel), follow the guide in `backend/README.md`.

---

## 🔐 Pre-Seeded Test Credentials

| Role | Identifier / User ID | Password | Access Level |
|---|---|---|---|
| **Administrator** | `Administrator` | `Admin1234` | Full Restaurant Desk, Hiring & Financials |
| **Head Chef** | `+91 98301 22345` / `EMP-001` | `pass123` | Kitchen Queue, Duties & Shift Log |
| **Customer** | `+91 98315 22001` | `pass123` | Menu Ordering, Tracking & Subscriptions |

---

## 📤 Uploading to GitHub

### Method A: Direct Export from AI Studio (Recommended)
1. In the AI Studio top navigation bar, click on **Settings** or the **Export** menu.
2. Select **"Push to GitHub"** to link your GitHub account and export automatically to a new repository.
3. Alternatively, click **"Export as ZIP"** to download the archive directly to your computer.

### Method B: Using Git CLI
```bash
git init
git add .
git commit -m "Initial commit: Bhojon.food Android App and Free Backend"
git branch -M main
git remote add origin https://github.com/YOUR_USERNAME/YOUR_REPOSITORY.git
git push -u origin main
```
