# Bhojon.food — Free Backend Server (REST API)

> **"Gharer Swad, Sabar Jonno"**  
> 100% Free, zero-setup Node.js & Express REST API for Bhojon.food.

---

## 🌟 Features

- **Authentication & Access Control**:
  - **Customer Sign In & Sign Up**: Mobile number and password based accounts with address auto-fill.
  - **Employee Login**: Staff mobile/code & password. Public registration is locked.
  - **Admin Login**: Single dedicated master administrator (`Administrator` / `Admin1234`).
  - **Admin Staff Registration**: Only Admin Desk can onboard staff and set their initial login passwords.
- **Menu Management**: Daily rotating Bengali dishes (Morning Tiffin, Lunch Thali, Dinner Box) with real-time availability toggle.
- **Live Order Tracking**: End-to-end status updates (`Pending` → `Confirmed` → `Preparing` → `Out for Delivery` → `Delivered`) with audit history timestamps.
- **Workforce Management**: Staff check-in / check-out timestamps, shift logs, and leave application approvals.
- **Zero Cost**: Built-in JSON file database fallback (`database.json`), so you don't need any paid MongoDB, PostgreSQL, or Firebase instance.

---

## 🚀 Quick Start (Local Run)

### 1. Requirements
- Node.js (version 18 or newer)
- npm

### 2. Install Dependencies
```bash
cd backend
npm install
```

### 3. Start the Server
```bash
npm start
```
The server will start at `http://localhost:5000`.

---

## ☁️ 100% Free Cloud Deployment Options

You can host this backend for **₹0 / $0** on any of these free platforms:

### Option A: Render.com (Recommended Free Tier)
1. Push this project to your GitHub repository.
2. Sign in to [Render.com](https://render.com).
3. Click **New +** $\rightarrow$ **Web Service**.
4. Connect your GitHub repo.
5. Set:
   - **Root Directory**: `backend`
   - **Build Command**: `npm install`
   - **Start Command**: `npm start`
6. Click **Create Web Service**. Your free live URL will be:  
   `https://bhojon-food-backend.onrender.com`

### Option B: Railway.app / Koyeb / Vercel
- Simply point to the `/backend` folder with `npm start`.

---

## 📋 API Endpoints Reference

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/health` | Health check |
| `POST` | `/api/auth/customer/register` | Register customer (`fullName`, `mobile`, `password`, `deliveryAddress`) |
| `POST` | `/api/auth/customer/login` | Login customer (`mobile`, `password`) |
| `POST` | `/api/auth/employee/login` | Login staff (`identifier`, `password`) |
| `POST` | `/api/auth/admin/login` | Admin login (`Administrator` / `Admin1234`) |
| `GET` | `/api/menu` | List Bengali menu items |
| `POST` | `/api/menu` | Add dish (Admin) |
| `PATCH` | `/api/menu/:id/availability` | Toggle dish on/off |
| `GET` | `/api/orders` | List customer orders |
| `POST` | `/api/orders` | Place new order |
| `PATCH` | `/api/orders/:code/status` | Update order tracking status |
| `GET` | `/api/employees` | List kitchen & delivery workforce |
| `POST` | `/api/employees` | Admin registers new employee with password |
| `POST` | `/api/attendance/check-in` | Staff shift check-in |
| `POST` | `/api/attendance/check-out` | Staff shift check-out |
| `GET` | `/api/leave-requests` | List leave applications |
| `POST` | `/api/leave-requests` | Submit leave request |
| `PATCH` | `/api/leave-requests/:id/status` | Approve or reject leave |
