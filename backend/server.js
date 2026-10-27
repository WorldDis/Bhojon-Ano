/**
 * Bhojon.food — Free REST API Backend Server
 * 
 * Tech: Node.js + Express (100% Free, Zero Database Setup Needed)
 * Can be hosted free on Render.com, Railway, Koyeb, Glitch, Vercel, or local machine.
 */

const express = require('express');
const cors = require('cors');
const fs = require('fs');
const path = require('path');

const app = express();
const PORT = process.env.PORT || 5000;
const DB_FILE = path.join(__dirname, 'database.json');
const SEED_FILE = path.join(__dirname, 'seedData.json');

app.use(cors());
app.use(express.json());

// Initialize in-memory database with persistent JSON fallback
let db = {};

function initDb() {
  if (fs.existsSync(DB_FILE)) {
    try {
      db = JSON.parse(fs.readFileSync(DB_FILE, 'utf-8'));
      console.log('Loaded database from database.json');
      return;
    } catch (e) {
      console.warn('Failed reading database.json, reloading seedData.json');
    }
  }

  if (fs.existsSync(SEED_FILE)) {
    db = JSON.parse(fs.readFileSync(SEED_FILE, 'utf-8'));
    saveDb();
    console.log('Initialized database from seedData.json');
  } else {
    db = {
      admin: { userId: "Administrator", password: "Admin1234" },
      customers: [],
      employees: [],
      menu: [],
      orders: [],
      subscriptions: [],
      attendance: [],
      leaveRequests: []
    };
  }
}

function saveDb() {
  try {
    fs.writeFileSync(DB_FILE, JSON.stringify(db, null, 2), 'utf-8');
  } catch (err) {
    console.error('Failed to save to database.json:', err.message);
  }
}

initDb();

// -------------------------------------------------------------
// Health Check & Documentation
// -------------------------------------------------------------
app.get('/', (req, res) => {
  res.json({
    brand: "Bhojon.food (ভোজন.ফুড)",
    tagline: "Gharer Swad, Sabar Jonno",
    status: "Online",
    version: "1.0.0",
    endpoints: {
      health: "/api/health",
      auth: {
        customerLogin: "POST /api/auth/customer/login",
        customerRegister: "POST /api/auth/customer/register",
        employeeLogin: "POST /api/auth/employee/login",
        adminLogin: "POST /api/auth/admin/login (Administrator / Admin1234)"
      },
      menu: "GET /api/menu",
      orders: "GET, POST /api/orders",
      subscriptions: "GET, POST /api/subscriptions",
      employees: "GET, POST /api/employees",
      attendance: "GET /api/attendance",
      leaveRequests: "GET, POST /api/leave-requests"
    }
  });
});

app.get('/api/health', (req, res) => {
  res.json({ status: 'OK', timestamp: new Date().toISOString() });
});

// -------------------------------------------------------------
// 1. AUTHENTICATION & ACCESS CONTROL
// -------------------------------------------------------------

// Customer Sign In
app.post('/api/auth/customer/login', (req, res) => {
  const { mobile, password } = req.body;
  if (!mobile || !password) {
    return res.status(400).json({ error: "Mobile number and password are required." });
  }

  const cleanMobile = mobile.trim();
  const customer = db.customers.find(
    c => c.mobile.replace(/\s+/g, '') === cleanMobile.replace(/\s+/g, '') && c.password === password.trim()
  );

  if (!customer) {
    return res.status(401).json({ error: "Invalid mobile number or password." });
  }

  const { password: _, ...customerSafe } = customer;
  return res.json({ success: true, user: customerSafe });
});

// Customer Sign Up
app.post('/api/auth/customer/register', (req, res) => {
  const { fullName, mobile, password, deliveryAddress } = req.body;
  if (!fullName || !mobile || !password) {
    return res.status(400).json({ error: "Full Name, Mobile Number, and Password are required." });
  }

  const cleanMobile = mobile.trim();
  const existing = db.customers.find(
    c => c.mobile.replace(/\s+/g, '') === cleanMobile.replace(/\s+/g, '')
  );

  if (existing) {
    return res.status(409).json({ error: "An account with this mobile number already exists." });
  }

  const newId = db.customers.length > 0 ? Math.max(...db.customers.map(c => c.id)) + 1 : 1;
  const newCustomer = {
    id: newId,
    fullName: fullName.trim(),
    mobile: cleanMobile,
    password: password.trim(),
    deliveryAddress: (deliveryAddress || "").trim()
  };

  db.customers.push(newCustomer);
  saveDb();

  const { password: _, ...customerSafe } = newCustomer;
  return res.status(201).json({ success: true, user: customerSafe });
});

// Employee Login (Kitchen & Delivery Staff)
app.post('/api/auth/employee/login', (req, res) => {
  const { identifier, password } = req.body; // mobile or code
  if (!identifier || !password) {
    return res.status(400).json({ error: "Registered mobile number or code and password are required." });
  }

  const cleanId = identifier.trim().replace(/\s+/g, '');
  const employee = db.employees.find(
    e => (e.mobile.replace(/\s+/g, '') === cleanId || e.employeeCode.toUpperCase() === cleanId.toUpperCase()) &&
         e.loginPassword === password.trim()
  );

  if (!employee) {
    return res.status(401).json({
      error: "Employee not found or invalid password. Registration is managed only by Admin Desk."
    });
  }

  const { loginPassword: _, ...empSafe } = employee;
  return res.json({ success: true, employee: empSafe });
});

// Admin Login (Single Administrator: Administrator / Admin1234)
app.post('/api/auth/admin/login', (req, res) => {
  const { userId, password } = req.body;
  if (!userId || !password) {
    return res.status(400).json({ error: "Administrator User ID and Password are required." });
  }

  if (userId.trim() === "Administrator" && password.trim() === "Admin1234") {
    return res.json({
      success: true,
      role: "ADMIN",
      message: "Administrator authorized successfully"
    });
  }

  return res.status(401).json({
    error: "Invalid Admin credentials. Fixed single user: User ID 'Administrator', Password 'Admin1234'."
  });
});

// -------------------------------------------------------------
// 2. MENU APIS
// -------------------------------------------------------------
app.get('/api/menu', (req, res) => {
  const { category, isAvailable } = req.query;
  let items = db.menu;

  if (category) {
    items = items.filter(m => m.category.toLowerCase() === category.toLowerCase());
  }
  if (isAvailable !== undefined) {
    items = items.filter(m => m.isAvailable === (isAvailable === 'true'));
  }

  res.json(items);
});

app.post('/api/menu', (req, res) => {
  const { name, bengaliName, description, price, category, mealSlot, isVeg, badge, servingTime } = req.body;
  if (!name || !price) {
    return res.status(400).json({ error: "Item name and price are required." });
  }

  const newId = db.menu.length > 0 ? Math.max(...db.menu.map(m => m.id)) + 1 : 1;
  const newItem = {
    id: newId,
    name: name.trim(),
    bengaliName: (bengaliName || "").trim(),
    description: (description || "").trim(),
    price: Number(price),
    category: category || "Lunch Thali",
    mealSlot: mealSlot || "Lunch Thali (1:30 PM)",
    isVeg: Boolean(isVeg),
    badge: (badge || "Fresh").trim(),
    servingTime: (servingTime || "All Day").trim(),
    isAvailable: true
  };

  db.menu.push(newItem);
  saveDb();
  res.status(201).json(newItem);
});

app.patch('/api/menu/:id/availability', (req, res) => {
  const item = db.menu.find(m => m.id === parseInt(req.params.id));
  if (!item) return res.status(404).json({ error: "Menu item not found" });

  item.isAvailable = !item.isAvailable;
  saveDb();
  res.json(item);
});

app.delete('/api/menu/:id', (req, res) => {
  const initialLen = db.menu.length;
  db.menu = db.menu.filter(m => m.id !== parseInt(req.params.id));
  if (db.menu.length === initialLen) return res.status(404).json({ error: "Menu item not found" });

  saveDb();
  res.json({ success: true, message: "Menu item deleted" });
});

// -------------------------------------------------------------
// 3. ORDERS APIS
// -------------------------------------------------------------
app.get('/api/orders', (req, res) => {
  res.json(db.orders);
});

app.get('/api/orders/:code', (req, res) => {
  const order = db.orders.find(o => o.orderCode === req.params.code);
  if (!order) return res.status(404).json({ error: "Order not found" });
  res.json(order);
});

app.post('/api/orders', (req, res) => {
  const { customerName, mobile, address, mealSlot, paymentMethod, items, notes } = req.body;
  if (!items || !items.length) {
    return res.status(400).json({ error: "Cart items cannot be empty." });
  }

  const randomHex = Math.random().toString(16).substring(2, 10).toUpperCase();
  const dateStr = new Date().toISOString().slice(0, 10).replace(/-/g, '');
  const orderCode = `BJ-${dateStr}-${randomHex}`;

  const totalAmount = items.reduce((sum, it) => sum + (it.price * it.quantity), 0);
  const nowTime = new Date().toLocaleTimeString('en-US', { hour: '2-digit', minute: '2-digit' });

  const newOrder = {
    orderCode,
    customerName: customerName || "Guest Customer",
    mobile: mobile || "+91 98315 22001",
    address: address || "Kolkata, WB",
    mealSlot: mealSlot || "Lunch Thali (1:30 PM)",
    paymentMethod: paymentMethod || "Pay on Delivery (Cash / UPI)",
    totalAmount,
    status: "Pending",
    notes: notes || "",
    createdAt: new Date().toISOString(),
    items,
    history: [
      { status: "Pending", description: "Order placed by customer.", time: nowTime }
    ]
  };

  db.orders.unshift(newOrder);
  saveDb();
  res.status(201).json(newOrder);
});

app.patch('/api/orders/:code/status', (req, res) => {
  const { status, description } = req.body;
  const order = db.orders.find(o => o.orderCode === req.params.code);
  if (!order) return res.status(404).json({ error: "Order not found" });

  const nowTime = new Date().toLocaleTimeString('en-US', { hour: '2-digit', minute: '2-digit' });
  order.status = status;
  order.history.push({
    status,
    description: description || `Status updated to ${status}.`,
    time: nowTime
  });

  saveDb();
  res.json(order);
});

// -------------------------------------------------------------
// 4. WORKFORCE (EMPLOYEES) — Restricted to Admin
// -------------------------------------------------------------
app.get('/api/employees', (req, res) => {
  const safeEmployees = db.employees.map(({ loginPassword, ...rest }) => ({
    ...rest,
    hasPassword: Boolean(loginPassword)
  }));
  res.json(safeEmployees);
});

// Admin-only: Register new employee with initial login password
app.post('/api/employees', (req, res) => {
  const { fullName, role, mobile, salary, shift, loginPassword } = req.body;
  if (!fullName || !mobile) {
    return res.status(400).json({ error: "Full Name and Mobile are required." });
  }

  const newId = db.employees.length > 0 ? Math.max(...db.employees.map(e => e.id)) + 1 : 1;
  const code = `EMP-${String(newId).padStart(3, '0')}`;

  const newEmployee = {
    id: newId,
    employeeCode: code,
    fullName: fullName.trim(),
    role: role || "Kitchen Helper",
    mobile: mobile.trim(),
    loginPassword: (loginPassword || "pass123").trim(),
    salary: Number(salary) || 15000,
    status: "Active",
    shift: shift || "Morning (7:00 AM - 3:00 PM)"
  };

  db.employees.push(newEmployee);
  saveDb();

  const { loginPassword: _, ...empSafe } = newEmployee;
  res.status(201).json(empSafe);
});

// -------------------------------------------------------------
// 5. ATTENDANCE & SHIFTS
// -------------------------------------------------------------
app.get('/api/attendance', (req, res) => {
  res.json(db.attendance);
});

app.post('/api/attendance/check-in', (req, res) => {
  const { employeeId, notes } = req.body;
  const now = new Date();
  const dateStr = now.toISOString().slice(0, 10);
  const timeStr = now.toLocaleTimeString('en-US', { hour: '2-digit', minute: '2-digit' });

  let record = db.attendance.find(a => a.employeeId === Number(employeeId) && a.date === dateStr);
  if (record) {
    return res.status(400).json({ error: "Already checked in today.", record });
  }

  const newId = db.attendance.length > 0 ? Math.max(...db.attendance.map(a => a.id)) + 1 : 1;
  record = {
    id: newId,
    employeeId: Number(employeeId),
    date: dateStr,
    checkInTime: timeStr,
    checkOutTime: null,
    status: "Present",
    notes: notes || "Shift started"
  };

  db.attendance.push(record);
  saveDb();
  res.status(201).json(record);
});

app.post('/api/attendance/check-out', (req, res) => {
  const { employeeId } = req.body;
  const dateStr = new Date().toISOString().slice(0, 10);
  const timeStr = new Date().toLocaleTimeString('en-US', { hour: '2-digit', minute: '2-digit' });

  const record = db.attendance.find(a => a.employeeId === Number(employeeId) && a.date === dateStr);
  if (!record) {
    return res.status(404).json({ error: "No check-in record found for today." });
  }

  record.checkOutTime = timeStr;
  saveDb();
  res.json(record);
});

// -------------------------------------------------------------
// 6. LEAVE REQUESTS
// -------------------------------------------------------------
app.get('/api/leave-requests', (req, res) => {
  res.json(db.leaveRequests);
});

app.post('/api/leave-requests', (req, res) => {
  const { employeeId, employeeName, type, fromDate, toDate, reason } = req.body;
  const newId = db.leaveRequests.length > 0 ? Math.max(...db.leaveRequests.map(l => l.id)) + 1 : 1;

  const newLeave = {
    id: newId,
    employeeId: Number(employeeId),
    employeeName: employeeName || "Staff Member",
    type: type || "Casual Leave",
    fromDate,
    toDate,
    reason: reason || "",
    status: "Pending",
    submittedOn: new Date().toISOString().slice(0, 10)
  };

  db.leaveRequests.unshift(newLeave);
  saveDb();
  res.status(201).json(newLeave);
});

app.patch('/api/leave-requests/:id/status', (req, res) => {
  const { status } = req.body; // "Approved" or "Rejected"
  const leave = db.leaveRequests.find(l => l.id === parseInt(req.params.id));
  if (!leave) return res.status(404).json({ error: "Leave request not found" });

  leave.status = status;
  saveDb();
  res.json(leave);
});

// -------------------------------------------------------------
// 7. SUBSCRIPTIONS
// -------------------------------------------------------------
app.get('/api/subscriptions', (req, res) => {
  res.json(db.subscriptions);
});

app.post('/api/subscriptions', (req, res) => {
  const { planName, customerName, mobile, address, slot, totalDays, totalAmount } = req.body;
  const newId = db.subscriptions.length > 0 ? Math.max(...db.subscriptions.map(s => s.id)) + 1 : 1;

  const newSub = {
    id: newId,
    planName: planName || "Bhojon Meal Subscription",
    customerName: customerName || "Customer",
    mobile: mobile || "+91 98315 22001",
    address: address || "Kolkata",
    slot: slot || "Lunch Thali (1:30 PM)",
    daysRemaining: totalDays || 7,
    totalDays: totalDays || 7,
    totalAmount: totalAmount || 950,
    status: "Active"
  };

  db.subscriptions.unshift(newSub);
  saveDb();
  res.status(201).json(newSub);
});

// Start listening
app.listen(PORT, '0.0.0.0', () => {
  console.log(`\n======================================================`);
  console.log(`🍽️  BHOJON.FOOD REST API BACKEND SERVER ONLINE`);
  console.log(`📡  Listening on port ${PORT} (http://localhost:${PORT})`);
  console.log(`🔐  Admin credentials: User ID 'Administrator' | Pass 'Admin1234'`);
  console.log(`🆓  100% Free - No Cloud Database Costs`);
  console.log(`======================================================\n`);
});
