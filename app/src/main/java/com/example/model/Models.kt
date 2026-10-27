package com.example.model

enum class UserRole(val label: String, val bengaliLabel: String) {
    CUSTOMER("Customer Table", "গ্রাহক টেবিল"),
    EMPLOYEE("Kitchen Team", "রান্নাঘর টিম"),
    ADMIN("Restaurant Desk", "ম্যানেজার ডেস্ক")
}

enum class OrderStatus(val displayName: String) {
    PENDING("Pending"),
    CONFIRMED("Confirmed"),
    PREPARING("Preparing"),
    OUT_FOR_DELIVERY("Out for Delivery"),
    DELIVERED("Delivered"),
    CANCELLED("Cancelled")
}

data class MenuItem(
    val id: Int,
    val name: String,
    val bengaliName: String,
    val description: String,
    val category: String, // "Morning Tiffin", "Lunch Thali", "Dinner"
    val price: Double,
    val servingTime: String,
    val available: Boolean = true,
    val dietary: String = "Pure Veg", // "Pure Veg", "Egg / Non-Veg"
    val highlight: String = "",
    val imageRes: Int? = null
)

data class CartItem(
    val menuItem: MenuItem,
    val quantity: Int
)

data class OrderItem(
    val menuItemId: Int,
    val itemName: String,
    val quantity: Int,
    val unitPrice: Double,
    val lineTotal: Double
)

data class OrderStatusEvent(
    val status: String,
    val note: String,
    val timestamp: String
)

data class Order(
    val id: Int,
    val orderCode: String,
    val customerName: String,
    val mobile: String,
    val address: String,
    val instructions: String = "",
    val deliveryDate: String,
    val mealSlot: String,
    val paymentMethod: String = "Pay on Delivery",
    val status: OrderStatus = OrderStatus.PENDING,
    val total: Double,
    val items: List<OrderItem>,
    val history: List<OrderStatusEvent> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
)

data class SubscriptionPlan(
    val id: Int,
    val name: String,
    val bengaliName: String,
    val duration: String,
    val includedMeals: String,
    val mealCount: Int,
    val price: Double,
    val active: Boolean = true
)

data class CustomerSubscription(
    val id: Int,
    val planId: Int,
    val planName: String,
    val customerName: String,
    val mobile: String,
    val deliveryAddress: String,
    val startDate: String,
    val endDate: String,
    val totalPrice: Double,
    val status: String = "Active", // "Active", "Paused", "Completed"
    val paymentStatus: String = "Paid",
    val mealsDelivered: Int = 0,
    val totalMeals: Int
)

data class CustomerUser(
    val id: Int,
    val fullName: String,
    val mobile: String,
    val password: String,
    val deliveryAddress: String = ""
)

data class Employee(
    val id: Int,
    val employeeCode: String,
    val fullName: String,
    val bengaliName: String,
    val role: String,
    val mobile: String,
    val status: String = "Active", // "Active", "On Duty", "On Leave"
    val joiningDate: String,
    val salary: Double,
    val pfApplicable: Boolean = true,
    val emergencyContact: String = "",
    val loginPassword: String = "pass123"
)

data class AttendanceRecord(
    val id: Int,
    val employeeId: Int,
    val employeeName: String,
    val date: String,
    val checkInTime: String?,
    val checkOutTime: String?,
    val status: String = "Present", // "Present", "Completed", "Half Day"
    val note: String? = null,
    val hoursWorked: Double = 0.0
)

data class LeaveRequest(
    val id: Int,
    val employeeId: Int,
    val employeeName: String,
    val leaveType: String, // "Casual Leave", "Sick Leave", "Emergency"
    val fromDate: String,
    val toDate: String,
    val reason: String,
    val status: String = "Pending", // "Pending", "Approved", "Rejected"
    val reviewedBy: String? = null
)

data class EmployeeDuty(
    val id: Int,
    val employeeId: Int,
    val title: String,
    val description: String,
    val dueDate: String,
    val status: String = "Pending" // "Pending", "In Progress", "Completed"
)
