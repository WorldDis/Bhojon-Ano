package com.example.data

import com.example.R
import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.*

object BhojonRepository {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
    private val codeDateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())

    fun todayDateString(): String = dateFormat.format(Date())
    fun currentTimeString(): String = timeFormat.format(Date())

    // --- State Flows ---
    private val _currentUserRole = MutableStateFlow(UserRole.CUSTOMER)
    val currentUserRole: StateFlow<UserRole> = _currentUserRole.asStateFlow()

    private val _menuItems = MutableStateFlow<List<MenuItem>>(emptyList())
    val menuItems: StateFlow<List<MenuItem>> = _menuItems.asStateFlow()

    private val _cart = MutableStateFlow<List<CartItem>>(emptyList())
    val cart: StateFlow<List<CartItem>> = _cart.asStateFlow()

    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()

    private val _subscriptionPlans = MutableStateFlow<List<SubscriptionPlan>>(emptyList())
    val subscriptionPlans: StateFlow<List<SubscriptionPlan>> = _subscriptionPlans.asStateFlow()

    private val _customerSubscriptions = MutableStateFlow<List<CustomerSubscription>>(emptyList())
    val customerSubscriptions: StateFlow<List<CustomerSubscription>> = _customerSubscriptions.asStateFlow()

    private val _employees = MutableStateFlow<List<Employee>>(emptyList())
    val employees: StateFlow<List<Employee>> = _employees.asStateFlow()

    private val _attendance = MutableStateFlow<List<AttendanceRecord>>(emptyList())
    val attendance: StateFlow<List<AttendanceRecord>> = _attendance.asStateFlow()

    private val _leaveRequests = MutableStateFlow<List<LeaveRequest>>(emptyList())
    val leaveRequests: StateFlow<List<LeaveRequest>> = _leaveRequests.asStateFlow()

    private val _duties = MutableStateFlow<List<EmployeeDuty>>(emptyList())
    val duties: StateFlow<List<EmployeeDuty>> = _duties.asStateFlow()

    // --- Auth & User State Flows ---
    private val _customerUsers = MutableStateFlow<List<CustomerUser>>(emptyList())
    val customerUsers: StateFlow<List<CustomerUser>> = _customerUsers.asStateFlow()

    private val _currentCustomerUser = MutableStateFlow<CustomerUser?>(null)
    val currentCustomerUser: StateFlow<CustomerUser?> = _currentCustomerUser.asStateFlow()

    private val _currentEmployeeUser = MutableStateFlow<Employee?>(null)
    val currentEmployeeUser: StateFlow<Employee?> = _currentEmployeeUser.asStateFlow()

    private val _isAdminAuthenticated = MutableStateFlow<Boolean>(false)
    val isAdminAuthenticated: StateFlow<Boolean> = _isAdminAuthenticated.asStateFlow()

    private var nextOrderId = 100
    private var nextMenuId = 20
    private var nextSubId = 10
    private var nextLeaveId = 5
    private var nextAttendanceId = 20
    private var nextCustomerId = 10

    init {
        seedInitialData()
    }

    fun setUserRole(role: UserRole) {
        _currentUserRole.value = role
    }

    // --- Authentication operations ---
    fun registerCustomer(
        fullName: String,
        mobile: String,
        password: String,
        address: String
    ): Pair<Boolean, String> {
        val cleanMobile = mobile.trim()
        if (cleanMobile.length < 8) {
            return Pair(false, "Please enter a valid mobile number (e.g. +91 98315 22001)")
        }
        if (password.length < 4) {
            return Pair(false, "Password must be at least 4 characters")
        }
        val cleanDigits = cleanMobile.filter { it.isDigit() }
        val exists = _customerUsers.value.any { it.mobile.filter { c -> c.isDigit() } == cleanDigits }
        if (exists) {
            return Pair(false, "A customer account already exists with this mobile number. Please Sign In.")
        }
        val newUser = CustomerUser(
            id = nextCustomerId++,
            fullName = fullName.trim().ifEmpty { "Customer" },
            mobile = cleanMobile,
            password = password,
            deliveryAddress = address.trim()
        )
        _customerUsers.value = _customerUsers.value + newUser
        _currentCustomerUser.value = newUser
        _currentUserRole.value = UserRole.CUSTOMER
        return Pair(true, "Account created! Welcome to Bhojon.food, ${newUser.fullName}.")
    }

    fun loginCustomer(mobile: String, password: String): Pair<CustomerUser?, String> {
        val cleanDigits = mobile.filter { it.isDigit() }
        val user = _customerUsers.value.find {
            val userDigits = it.mobile.filter { c -> c.isDigit() }
            userDigits.isNotEmpty() && (userDigits == cleanDigits || userDigits.endsWith(cleanDigits) || cleanDigits.endsWith(userDigits))
        }
        if (user == null) {
            return Pair(null, "No registered customer found with this mobile number. Please Sign Up.")
        }
        if (user.password != password) {
            return Pair(null, "Incorrect password. Please try again.")
        }
        _currentCustomerUser.value = user
        _currentUserRole.value = UserRole.CUSTOMER
        return Pair(user, "Welcome back, ${user.fullName}!")
    }

    fun loginEmployee(mobileOrCode: String, password: String): Pair<Employee?, String> {
        val cleanInput = mobileOrCode.trim()
        val cleanDigits = cleanInput.filter { it.isDigit() }
        val emp = _employees.value.find {
            it.employeeCode.equals(cleanInput, ignoreCase = true) ||
            (cleanDigits.length >= 5 && it.mobile.filter { c -> c.isDigit() }.endsWith(cleanDigits))
        }
        if (emp == null) {
            return Pair(null, "Staff record not found. Note: Staff registration is strictly handled by Administrator.")
        }
        if (emp.loginPassword != password) {
            return Pair(null, "Incorrect staff password. Please check with Restaurant Manager.")
        }
        _currentEmployeeUser.value = emp
        _currentUserRole.value = UserRole.EMPLOYEE
        return Pair(emp, "Welcome Chef / Staff ${emp.fullName}!")
    }

    fun loginAdmin(userId: String, password: String): Pair<Boolean, String> {
        val cleanUser = userId.trim()
        if (cleanUser == "Administrator" && password == "Admin1234") {
            _isAdminAuthenticated.value = true
            _currentUserRole.value = UserRole.ADMIN
            return Pair(true, "Administrator access authorized.")
        } else {
            return Pair(false, "Invalid credentials. User ID must be 'Administrator' and Password 'Admin1234'.")
        }
    }

    fun logoutCurrentRole() {
        when (_currentUserRole.value) {
            UserRole.CUSTOMER -> {
                _currentCustomerUser.value = null
            }
            UserRole.EMPLOYEE -> {
                _currentEmployeeUser.value = null
            }
            UserRole.ADMIN -> {
                _isAdminAuthenticated.value = false
            }
        }
    }

    fun logoutAll() {
        _currentCustomerUser.value = null
        _currentEmployeeUser.value = null
        _isAdminAuthenticated.value = false
        _currentUserRole.value = UserRole.CUSTOMER
    }

    // --- Cart operations ---
    fun addToCart(item: MenuItem) {
        val current = _cart.value.toMutableList()
        val index = current.indexOfFirst { it.menuItem.id == item.id }
        if (index >= 0) {
            val existing = current[index]
            current[index] = existing.copy(quantity = existing.quantity + 1)
        } else {
            current.add(CartItem(item, 1))
        }
        _cart.value = current
    }

    fun decrementCart(item: MenuItem) {
        val current = _cart.value.toMutableList()
        val index = current.indexOfFirst { it.menuItem.id == item.id }
        if (index >= 0) {
            val existing = current[index]
            if (existing.quantity > 1) {
                current[index] = existing.copy(quantity = existing.quantity - 1)
            } else {
                current.removeAt(index)
            }
            _cart.value = current
        }
    }

    fun clearCart() {
        _cart.value = emptyList()
    }

    fun getCartTotal(): Double {
        return _cart.value.sumOf { it.menuItem.price * it.quantity }
    }

    fun getCartItemCount(): Int {
        return _cart.value.sumOf { it.quantity }
    }

    // --- Order operations ---
    fun placeOrder(
        customerName: String,
        mobile: String,
        address: String,
        instructions: String,
        mealSlot: String,
        paymentMethod: String
    ): Order? {
        val cartItems = _cart.value
        if (cartItems.isEmpty()) return null

        val orderItems = cartItems.map {
            OrderItem(
                menuItemId = it.menuItem.id,
                itemName = it.menuItem.name,
                quantity = it.quantity,
                unitPrice = it.menuItem.price,
                lineTotal = it.menuItem.price * it.quantity
            )
        }
        val total = orderItems.sumOf { it.lineTotal }
        val datePart = codeDateFormat.format(Date())
        val hexPart = UUID.randomUUID().toString().substring(0, 10).uppercase(Locale.ROOT)
        val code = "BJ-$datePart-$hexPart"

        val initialHistory = listOf(
            OrderStatusEvent(
                status = OrderStatus.PENDING.displayName,
                note = "Order placed by customer via Bhojon App.",
                timestamp = currentTimeString()
            )
        )

        val newOrder = Order(
            id = nextOrderId++,
            orderCode = code,
            customerName = customerName,
            mobile = mobile,
            address = address,
            instructions = instructions,
            deliveryDate = todayDateString(),
            mealSlot = mealSlot,
            paymentMethod = paymentMethod,
            status = OrderStatus.PENDING,
            total = total,
            items = orderItems,
            history = initialHistory
        )

        _orders.value = listOf(newOrder) + _orders.value
        clearCart()
        return newOrder
    }

    fun updateOrderStatus(orderCode: String, newStatus: OrderStatus, note: String = "") {
        val current = _orders.value.toMutableList()
        val index = current.indexOfFirst { it.orderCode.equals(orderCode, ignoreCase = true) }
        if (index >= 0) {
            val order = current[index]
            val eventNote = if (note.isNotBlank()) note else when (newStatus) {
                OrderStatus.CONFIRMED -> "Kitchen has accepted and scheduled this meal."
                OrderStatus.PREPARING -> "Head Chef is actively cooking fresh in the kitchen."
                OrderStatus.OUT_FOR_DELIVERY -> "Delivery partner has picked up your warm thali."
                OrderStatus.DELIVERED -> "Delivered safely with Bengali care. Enjoy your meal!"
                OrderStatus.CANCELLED -> "Order has been cancelled."
                OrderStatus.PENDING -> "Order marked pending."
            }
            val newEvent = OrderStatusEvent(
                status = newStatus.displayName,
                note = eventNote,
                timestamp = currentTimeString()
            )
            current[index] = order.copy(
                status = newStatus,
                history = order.history + newEvent
            )
            _orders.value = current
        }
    }

    fun findOrderByCode(code: String): Order? {
        val cleaned = code.trim()
        return _orders.value.find { it.orderCode.equals(cleaned, ignoreCase = true) }
    }

    // --- Menu CRUD ---
    fun addMenuItem(item: MenuItem) {
        val newItem = item.copy(id = nextMenuId++)
        _menuItems.value = _menuItems.value + newItem
    }

    fun updateMenuItem(updated: MenuItem) {
        _menuItems.value = _menuItems.value.map {
            if (it.id == updated.id) updated else it
        }
    }

    fun toggleItemAvailability(id: Int) {
        _menuItems.value = _menuItems.value.map {
            if (it.id == id) it.copy(available = !it.available) else it
        }
    }

    fun deleteMenuItem(id: Int) {
        _menuItems.value = _menuItems.value.filter { it.id != id }
    }

    // --- Subscriptions ---
    fun addCustomerSubscription(
        planId: Int,
        customerName: String,
        mobile: String,
        address: String
    ): CustomerSubscription? {
        val plan = _subscriptionPlans.value.find { it.id == planId } ?: return null
        val calendar = Calendar.getInstance()
        val start = dateFormat.format(calendar.time)
        val days = if (plan.duration.contains("Weekly", ignoreCase = true)) 7 else 30
        calendar.add(Calendar.DAY_OF_YEAR, days)
        val end = dateFormat.format(calendar.time)

        val newSub = CustomerSubscription(
            id = nextSubId++,
            planId = plan.id,
            planName = plan.name,
            customerName = customerName,
            mobile = mobile,
            deliveryAddress = address,
            startDate = start,
            endDate = end,
            totalPrice = plan.price,
            status = "Active",
            paymentStatus = "Paid",
            mealsDelivered = 0,
            totalMeals = plan.mealCount
        )
        _customerSubscriptions.value = listOf(newSub) + _customerSubscriptions.value
        return newSub
    }

    fun updateSubscriptionStatus(id: Int, newStatus: String) {
        _customerSubscriptions.value = _customerSubscriptions.value.map {
            if (it.id == id) it.copy(status = newStatus) else it
        }
    }

    // --- Attendance & Workforce ---
    fun checkInEmployee(employeeId: Int, note: String?) {
        val emp = _employees.value.find { it.id == employeeId } ?: return
        val today = todayDateString()
        val timeNow = currentTimeString()
        val currentRecords = _attendance.value.toMutableList()
        val existingIndex = currentRecords.indexOfFirst { it.employeeId == employeeId && it.date == today }

        if (existingIndex >= 0) {
            val record = currentRecords[existingIndex]
            currentRecords[existingIndex] = record.copy(checkInTime = timeNow, status = "Present", note = note)
        } else {
            currentRecords.add(
                0,
                AttendanceRecord(
                    id = nextAttendanceId++,
                    employeeId = employeeId,
                    employeeName = emp.fullName,
                    date = today,
                    checkInTime = timeNow,
                    checkOutTime = null,
                    status = "Present",
                    note = note,
                    hoursWorked = 0.0
                )
            )
        }
        _attendance.value = currentRecords

        // Update employee status
        _employees.value = _employees.value.map {
            if (it.id == employeeId) it.copy(status = "On Duty") else it
        }
    }

    fun checkOutEmployee(employeeId: Int, note: String?) {
        val today = todayDateString()
        val timeNow = currentTimeString()
        val currentRecords = _attendance.value.toMutableList()
        val existingIndex = currentRecords.indexOfFirst { it.employeeId == employeeId && it.date == today }

        if (existingIndex >= 0) {
            val record = currentRecords[existingIndex]
            currentRecords[existingIndex] = record.copy(
                checkOutTime = timeNow,
                status = "Completed",
                note = if (note != null) "${record.note ?: ""}; $note" else record.note,
                hoursWorked = 8.0
            )
            _attendance.value = currentRecords
        }

        _employees.value = _employees.value.map {
            if (it.id == employeeId) it.copy(status = "Active") else it
        }
    }

    fun submitLeaveRequest(
        employeeId: Int,
        leaveType: String,
        fromDate: String,
        toDate: String,
        reason: String
    ) {
        val emp = _employees.value.find { it.id == employeeId } ?: return
        val newLeave = LeaveRequest(
            id = nextLeaveId++,
            employeeId = employeeId,
            employeeName = emp.fullName,
            leaveType = leaveType,
            fromDate = fromDate,
            toDate = toDate,
            reason = reason,
            status = "Pending"
        )
        _leaveRequests.value = listOf(newLeave) + _leaveRequests.value
    }

    fun reviewLeaveRequest(leaveId: Int, approved: Boolean, reviewerNote: String) {
        _leaveRequests.value = _leaveRequests.value.map {
            if (it.id == leaveId) {
                it.copy(
                    status = if (approved) "Approved" else "Rejected",
                    reviewedBy = "Biplab Sen (Manager)"
                )
            } else it
        }
    }

    fun completeDuty(dutyId: Int) {
        _duties.value = _duties.value.map {
            if (it.id == dutyId) it.copy(status = "Completed") else it
        }
    }

    fun addEmployee(emp: Employee) {
        _employees.value = _employees.value + emp.copy(id = (_employees.value.maxOfOrNull { it.id } ?: 0) + 1)
    }

    // --- Seeder ---
    private fun seedInitialData() {
        _menuItems.value = listOf(
            MenuItem(
                id = 1,
                name = "Luchi & Cholar Dal",
                bengaliName = "লুচি ও ছোলার ডাল",
                description = "4 crisp golden puffed luchis with traditional sonamug cholar dal infused with fried coconut bits and garam masala.",
                category = "Morning Tiffin",
                price = 25.0,
                servingTime = "9:00 AM",
                available = true,
                dietary = "Pure Veg",
                highlight = "Daily Morning Classic",
                imageRes = R.drawable.img_luchi_tiffin
            ),
            MenuItem(
                id = 2,
                name = "Aloo Paratha with Curd & Pickle",
                bengaliName = "আলু পরোটা ও আচার",
                description = "2 fluffy tawa roasted parathas stuffed with homestyle spiced potato, served with sweet lime pickle and fresh creamy dahi.",
                category = "Morning Tiffin",
                price = 25.0,
                servingTime = "9:00 AM",
                available = true,
                dietary = "Pure Veg",
                highlight = "Filling Breakfast",
                imageRes = R.drawable.img_luchi_tiffin
            ),
            MenuItem(
                id = 3,
                name = "Koraishutir Kochuri & Alur Dom",
                bengaliName = "কড়াইশুঁটির কচুরি ও আলুর দম",
                description = "3 handcrafted winter green peas kochuris served with sweet-tangy hing infused Bengali Kashmiri alur dom.",
                category = "Morning Tiffin",
                price = 35.0,
                servingTime = "9:00 AM",
                available = true,
                dietary = "Pure Veg",
                highlight = "Chef's Special",
                imageRes = R.drawable.img_luchi_tiffin
            ),
            MenuItem(
                id = 4,
                name = "Regular Bengali Thali",
                bengaliName = "সাধারণ বাংলা থালি",
                description = "Steamed fine rice, golden sonamug dal, seasonal mixed vegetable curry (ghonto/shukto), crunchy aloo bhaja, tomato khejur chutney and fresh salad.",
                category = "Lunch Thali",
                price = 40.0,
                servingTime = "1:30 PM",
                available = true,
                dietary = "Pure Veg",
                highlight = "Bhojon Best Seller",
                imageRes = R.drawable.img_bengali_thali
            ),
            MenuItem(
                id = 5,
                name = "Special Royal Thali",
                bengaliName = "স্পেশাল রাজকীয় থালি",
                description = "Fragrant rice, desi ghee, thick sonamug dal, traditional aloo posto, fried papad, sweet tomato chutney and earthen cup mishti doi.",
                category = "Lunch Thali",
                price = 50.0,
                servingTime = "1:30 PM",
                available = true,
                dietary = "Pure Veg",
                highlight = "Royal Feast",
                imageRes = R.drawable.img_bengali_thali
            ),
            MenuItem(
                id = 6,
                name = "Egg Thali (Dim Kosha Thali)",
                bengaliName = "ডিম কষা থালি",
                description = "Steamed rice, sonamug dal, 2 farm fresh eggs cooked in thick caramelized onion-ginger gravy, fried potatoes and fresh green salad.",
                category = "Lunch Thali",
                price = 70.0,
                servingTime = "1:30 PM",
                available = true,
                dietary = "Egg / Non-Veg",
                highlight = "High Protein",
                imageRes = R.drawable.img_macher_jhol
            ),
            MenuItem(
                id = 7,
                name = "Katla Macher Jhol Thali",
                bengaliName = "কাতলা মাছের ঝোল থালি",
                description = "Fresh Ganges Katla fish steak cooked homestyle with potatoes and cauliflowers in light cumin gravy, served with hot rice and dal.",
                category = "Lunch Thali",
                price = 110.0,
                servingTime = "1:30 PM",
                available = true,
                dietary = "Egg / Non-Veg",
                highlight = "Fish Lover's Choice",
                imageRes = R.drawable.img_macher_jhol
            ),
            MenuItem(
                id = 8,
                name = "Soft Roti & Spiced Egg Tadka",
                bengaliName = "রুটি ও ডিম তড়কা",
                description = "4 hand-tossed soft phulka rotis paired with authentic Kolkata dhaba-style scrambled egg dal tadka and fresh crunchy onion rings.",
                category = "Dinner",
                price = 40.0,
                servingTime = "9:00 PM",
                available = true,
                dietary = "Egg / Non-Veg",
                highlight = "Dhaba Favorite",
                imageRes = R.drawable.img_roti_tadka
            ),
            MenuItem(
                id = 9,
                name = "Roti, Dal & Alur Dom",
                bengaliName = "রুটি, ডাল ও আলুর দম",
                description = "4 piping hot whole wheat rotis served with homestyle Bengali alur dom and yellow moong dal with cumin tempering.",
                category = "Dinner",
                price = 40.0,
                servingTime = "9:00 PM",
                available = true,
                dietary = "Pure Veg",
                highlight = "Homestyle Comfort",
                imageRes = R.drawable.img_roti_tadka
            ),
            MenuItem(
                id = 10,
                name = "Comfort Rice & Shobji Dinner",
                bengaliName = "ভাত ও সবজি ডিনার",
                description = "Light comforting night meal with steamed rice, digestive patla moong dal, and seasonal mixed vegetable curry with fresh coriander.",
                category = "Dinner",
                price = 45.0,
                servingTime = "9:00 PM",
                available = true,
                dietary = "Pure Veg",
                highlight = "Light & Healthy",
                imageRes = R.drawable.img_bengali_thali
            )
        )

        _subscriptionPlans.value = listOf(
            SubscriptionPlan(
                id = 1,
                name = "Weekly Tiffin & Lunch Plan",
                bengaliName = "সাপ্তাহিক টিফিন ও দুপুরের খাবার",
                duration = "Weekly (7 Days)",
                includedMeals = "7 Morning Tiffins (Luchi/Paratha) + 7 Regular Lunch Thalis",
                mealCount = 14,
                price = 450.0,
                active = true
            ),
            SubscriptionPlan(
                id = 2,
                name = "Monthly Full Bhojon Thali",
                bengaliName = "মাসিক পূর্ণ ভোজন থালি",
                duration = "Monthly (30 Days)",
                includedMeals = "30 Daily Chef Lunch Thalis with weekend special dessert & curd",
                mealCount = 30,
                price = 1400.0,
                active = true
            ),
            SubscriptionPlan(
                id = 3,
                name = "Monthly Evening Dinner Plan",
                bengaliName = "মাসিক সন্ধ্যা ডিনার প্ল্যান",
                duration = "Monthly (30 Days)",
                includedMeals = "30 Hand-made Phulka Roti Meals with Egg Tadka or Alur Dom",
                mealCount = 30,
                price = 1150.0,
                active = true
            )
        )

        _customerSubscriptions.value = listOf(
            CustomerSubscription(
                id = 1,
                planId = 2,
                planName = "Monthly Full Bhojon Thali",
                customerName = "Priya Banerjee",
                mobile = "+91 98310 98765",
                deliveryAddress = "Flat 4B, Sukanta Pally, Jadavpur, Kolkata",
                startDate = "2026-10-01",
                endDate = "2026-10-31",
                totalPrice = 1400.0,
                status = "Active",
                paymentStatus = "Paid",
                mealsDelivered = 14,
                totalMeals = 30
            ),
            CustomerSubscription(
                id = 2,
                planId = 1,
                planName = "Weekly Tiffin & Lunch Plan",
                customerName = "Anirban Bhattacharya",
                mobile = "+91 98322 11223",
                deliveryAddress = "Sector V, Tech Park Gate 2, Salt Lake",
                startDate = "2026-10-10",
                endDate = "2026-10-17",
                totalPrice = 450.0,
                status = "Active",
                paymentStatus = "Paid",
                mealsDelivered = 4,
                totalMeals = 14
            )
        )

        _employees.value = listOf(
            Employee(
                id = 1,
                employeeCode = "EMP-001",
                fullName = "Subhashish Ghosh",
                bengaliName = "সুভাশীষ ঘোষ",
                role = "Head Chef",
                mobile = "+91 98301 22345",
                status = "On Duty",
                joiningDate = "2023-01-15",
                salary = 24000.0,
                pfApplicable = true,
                emergencyContact = "Brother: +91 98301 99999"
            ),
            Employee(
                id = 2,
                employeeCode = "EMP-002",
                fullName = "Tapan Mondal",
                bengaliName = "তপন মণ্ডল",
                role = "Delivery Partner",
                mobile = "+91 98302 33456",
                status = "On Duty",
                joiningDate = "2023-04-10",
                salary = 16500.0,
                pfApplicable = true,
                emergencyContact = "Wife: +91 98302 88888"
            ),
            Employee(
                id = 3,
                employeeCode = "EMP-003",
                fullName = "Anima Roy",
                bengaliName = "অনিমা রায়",
                role = "Kitchen Assistant",
                mobile = "+91 98303 44567",
                status = "Active",
                joiningDate = "2023-06-01",
                salary = 14500.0,
                pfApplicable = true,
                emergencyContact = "Father: +91 98303 77777"
            ),
            Employee(
                id = 4,
                employeeCode = "EMP-004",
                fullName = "Biplab Sen",
                bengaliName = "বিপ্লব সেন",
                role = "Restaurant Manager",
                mobile = "+91 98304 55678",
                status = "Active",
                joiningDate = "2022-11-01",
                salary = 28000.0,
                pfApplicable = true,
                emergencyContact = "Mother: +91 98304 66666"
            )
        )

        val today = todayDateString()
        _attendance.value = listOf(
            AttendanceRecord(
                id = 1,
                employeeId = 1,
                employeeName = "Subhashish Ghosh",
                date = today,
                checkInTime = "06:30 AM",
                checkOutTime = null,
                status = "Present",
                note = "Morning kitchen fire lit and luchi prep started on time.",
                hoursWorked = 5.5
            ),
            AttendanceRecord(
                id = 2,
                employeeId = 2,
                employeeName = "Tapan Mondal",
                date = today,
                checkInTime = "08:15 AM",
                checkOutTime = null,
                status = "Present",
                note = "Morning breakfast delivery rounds completed.",
                hoursWorked = 3.8
            ),
            AttendanceRecord(
                id = 3,
                employeeId = 3,
                employeeName = "Anima Roy",
                date = "2026-10-13",
                checkInTime = "07:00 AM",
                checkOutTime = "03:30 PM",
                status = "Completed",
                note = "Full shift completed.",
                hoursWorked = 8.5
            )
        )

        _duties.value = listOf(
            EmployeeDuty(
                id = 1,
                employeeId = 1,
                title = "Prepare 40 Lunch Thalis",
                description = "Cook sonamug dal, aloo posto and steamed fine rice ready by 12:45 PM.",
                dueDate = today,
                status = "In Progress"
            ),
            EmployeeDuty(
                id = 2,
                employeeId = 2,
                title = "Deliver Sector V Office Tiffins",
                description = "12 thalis packed in thermal bags for delivery to Tech Park Gate 2.",
                dueDate = today,
                status = "In Progress"
            ),
            EmployeeDuty(
                id = 3,
                employeeId = 3,
                title = "Inspect Fresh Market Greens",
                description = "Check mustard greens, cauliflowers and fresh coriander arrival.",
                dueDate = today,
                status = "Completed"
            )
        )

        _leaveRequests.value = listOf(
            LeaveRequest(
                id = 1,
                employeeId = 3,
                employeeName = "Anima Roy",
                leaveType = "Casual Leave",
                fromDate = "2026-10-18",
                toDate = "2026-10-19",
                reason = "Family religious festival in village home.",
                status = "Pending"
            )
        )

        // Initial realistic orders
        _orders.value = listOf(
            Order(
                id = 101,
                orderCode = "BJ-20261014-9FA2E078B1",
                customerName = "Debashis Mukherjee",
                mobile = "+91 98315 22001",
                address = "12/A Raja Subodh Mallick Road, Garia, Kolkata",
                instructions = "Please include extra sonamug dal and green chilies.",
                deliveryDate = today,
                mealSlot = "Lunch Thali",
                paymentMethod = "Pay on Delivery",
                status = OrderStatus.PREPARING,
                total = 110.0,
                items = listOf(
                    OrderItem(5, "Special Royal Thali", 1, 50.0, 50.0),
                    OrderItem(4, "Regular Bengali Thali", 1, 40.0, 40.0),
                    OrderItem(1, "Luchi & Cholar Dal", 1, 25.0, 25.0)
                ),
                history = listOf(
                    OrderStatusEvent("Pending", "Order received via Bhojon App.", "10:15 AM"),
                    OrderStatusEvent("Confirmed", "Kitchen accepted lunch order.", "10:20 AM"),
                    OrderStatusEvent("Preparing", "Head Chef Subhashish is cooking royal thali.", "11:00 AM")
                )
            ),
            Order(
                id = 102,
                orderCode = "BJ-20261014-4CA819D205",
                customerName = "Poulomi Sengupta",
                mobile = "+91 98309 88123",
                address = "Flat 2C, Green Valley Apts, New Town, Action Area 1",
                instructions = "Ring bell twice, deliver to door.",
                deliveryDate = today,
                mealSlot = "Lunch Thali",
                paymentMethod = "Pay on Delivery",
                status = OrderStatus.OUT_FOR_DELIVERY,
                total = 140.0,
                items = listOf(
                    OrderItem(6, "Egg Thali (Dim Kosha Thali)", 2, 70.0, 140.0)
                ),
                history = listOf(
                    OrderStatusEvent("Pending", "Order placed.", "09:45 AM"),
                    OrderStatusEvent("Confirmed", "Kitchen confirmed order.", "09:50 AM"),
                    OrderStatusEvent("Preparing", "Chef completed cooking and packing.", "10:30 AM"),
                    OrderStatusEvent("Out for Delivery", "Tapan Mondal is on route.", "11:15 AM")
                )
            ),
            Order(
                id = 103,
                orderCode = "BJ-20261014-88A12BC5DF",
                customerName = "Arjun Das",
                mobile = "+91 98741 55667",
                address = "33 Ballygunge Circular Road, Kolkata",
                instructions = "",
                deliveryDate = today,
                mealSlot = "Morning Tiffin",
                paymentMethod = "Pay on Delivery",
                status = OrderStatus.DELIVERED,
                total = 75.0,
                items = listOf(
                    OrderItem(1, "Luchi & Cholar Dal", 3, 25.0, 75.0)
                ),
                history = listOf(
                    OrderStatusEvent("Pending", "Order placed.", "08:00 AM"),
                    OrderStatusEvent("Confirmed", "Accepted by Bhojon Kitchen.", "08:05 AM"),
                    OrderStatusEvent("Preparing", "Fried fresh hot luchis.", "08:20 AM"),
                    OrderStatusEvent("Out for Delivery", "Dispatched to Ballygunge.", "08:45 AM"),
                    OrderStatusEvent("Delivered", "Delivered warm to customer.", "09:15 AM")
                )
            )
        )

        // Seed registered customers
        val initialCustomers = listOf(
            CustomerUser(
                id = 1,
                fullName = "Debashis Mukherjee",
                mobile = "+91 98315 22001",
                password = "pass123",
                deliveryAddress = "12/A Raja Subodh Mallick Road, Garia, Kolkata"
            ),
            CustomerUser(
                id = 2,
                fullName = "Priya Banerjee",
                mobile = "+91 98310 98765",
                password = "pass123",
                deliveryAddress = "Flat 4B, Sukanta Pally, Jadavpur, Kolkata"
            ),
            CustomerUser(
                id = 3,
                fullName = "Arjun Das",
                mobile = "+91 98741 55667",
                password = "pass123",
                deliveryAddress = "33 Ballygunge Circular Road, Kolkata"
            ),
            CustomerUser(
                id = 4,
                fullName = "Poulomi Sengupta",
                mobile = "+91 98309 88123",
                password = "pass123",
                deliveryAddress = "Flat 2C, Green Valley Apts, New Town, Action Area 1"
            )
        )
        _customerUsers.value = initialCustomers
        _currentCustomerUser.value = initialCustomers.first()
        _currentEmployeeUser.value = _employees.value.firstOrNull()
    }
}
