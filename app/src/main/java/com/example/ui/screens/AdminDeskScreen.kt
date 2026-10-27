package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.automirrored.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BhojonRepository
import com.example.model.*
import com.example.ui.components.OrderStatusBadge
import com.example.ui.theme.*

@Composable
fun AdminDeskScreen(
    onTrackOrder: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val orders by BhojonRepository.orders.collectAsState()
    val menuItems by BhojonRepository.menuItems.collectAsState()
    val employees by BhojonRepository.employees.collectAsState()
    val attendance by BhojonRepository.attendance.collectAsState()
    val subscriptions by BhojonRepository.customerSubscriptions.collectAsState()
    val leaveRequests by BhojonRepository.leaveRequests.collectAsState()

    var activeTab by remember { mutableStateOf("Orders") }
    val adminTabs = listOf("Orders", "Menu", "Team & Attendance", "Subscriptions", "Reports")

    // KPI Calculations
    val todayRevenue = remember(orders) {
        orders.filter { it.status != OrderStatus.CANCELLED }.sumOf { it.total }
    }
    val pendingCount = remember(orders) {
        orders.count { it.status == OrderStatus.PENDING || it.status == OrderStatus.CONFIRMED }
    }
    val onDutyStaffCount = remember(employees) {
        employees.count { it.status == "On Duty" }
    }

    // Orders tab states
    var orderSearch by remember { mutableStateOf("") }
    var orderStatusFilter by remember { mutableStateOf("All") }

    // Menu add/edit dialog states
    var showAddMenuDialog by remember { mutableStateOf(false) }
    var newDishName by remember { mutableStateOf("") }
    var newDishBengali by remember { mutableStateOf("") }
    var newDishCategory by remember { mutableStateOf("Lunch Thali") }
    var newDishPrice by remember { mutableStateOf("45") }
    var newDishServingTime by remember { mutableStateOf("1:30 PM") }
    var newDishDesc by remember { mutableStateOf("") }
    var newDishDietary by remember { mutableStateOf("Pure Veg") }

    // Add Employee dialog states
    var showAddEmployeeDialog by remember { mutableStateOf(false) }
    var newEmpName by remember { mutableStateOf("") }
    var newEmpRole by remember { mutableStateOf("Kitchen Helper") }
    var newEmpMobile by remember { mutableStateOf("+91 ") }
    var newEmpSalary by remember { mutableStateOf("15000") }
    var newEmpPassword by remember { mutableStateOf("pass123") }

    var adminNotice by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(WarmCreamBg)
    ) {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // Admin Desk Brand Header (matching laptop mockup from reference)
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = DeepForestGreen,
                    border = androidx.compose.foundation.BorderStroke(1.dp, MustardGold.copy(alpha = 0.4f)),
                    shadowElevation = 4.dp
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            BhojonBrandLogo(
                                isDarkTheme = true,
                                emblemSize = 42.dp,
                                showTagline = true
                            )
                            Surface(
                                color = Color.Black.copy(alpha = 0.35f),
                                shape = RoundedCornerShape(20.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, MustardGold.copy(alpha = 0.4f))
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(7.dp)
                                            .clip(CircleShape)
                                            .background(SuccessGreen)
                                    )
                                    Text(
                                        text = "Admin • Super Admin",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color(0xFFFFF8E8),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // KPIs Grid (matching reference: Today's Revenue ₹8,450 +12%, Total Orders 42 +8%, Pending Kitchen 7)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    KpiCard(
                        title = "Today's Revenue",
                        value = "₹ 8,450",
                        growth = "↑ 12%",
                        icon = Icons.Outlined.CurrencyRupee,
                        tint = ForestGreen,
                        modifier = Modifier.weight(1f)
                    )
                    KpiCard(
                        title = "Total Orders",
                        value = "42",
                        growth = "↑ 8%",
                        icon = Icons.AutoMirrored.Outlined.ReceiptLong,
                        tint = Terracotta,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    KpiCard(
                        title = "Pending Kitchen",
                        value = "7",
                        growth = null,
                        icon = Icons.Outlined.SoupKitchen,
                        tint = WarningOrange,
                        modifier = Modifier.weight(1f)
                    )
                    KpiCard(
                        title = "Staff on Duty",
                        value = "$onDutyStaffCount / ${employees.size}",
                        growth = null,
                        icon = Icons.Outlined.Groups,
                        tint = SuccessGreen,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            if (adminNotice != null) {
                item {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = SuccessGreenLight
                    ) {
                        Text(
                            text = adminNotice!!,
                            color = SuccessGreen,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }

            // Sub-tabs navigation
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(adminTabs) { tab ->
                        val isSelected = tab == activeTab
                        Surface(
                            onClick = { activeTab = tab },
                            shape = RoundedCornerShape(20.dp),
                            color = if (isSelected) ForestGreen else WarmCardBg,
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) ForestGreen else WarmBorder)
                        ) {
                            Text(
                                text = tab,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else CharcoalText,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }

            // Tab 1: Orders Management
            if (activeTab == "Orders") {
                item {
                    Column {
                        OutlinedTextField(
                            value = orderSearch,
                            onValueChange = { orderSearch = it },
                            placeholder = { Text("Search by code, customer or phone...") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(listOf("All", "Pending", "Confirmed", "Preparing", "Out for Delivery", "Delivered", "Cancelled")) { st ->
                                val isSel = orderStatusFilter == st
                                FilterChip(
                                    selected = isSel,
                                    onClick = { orderStatusFilter = st },
                                    label = { Text(st, fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = ForestGreen,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }
                    }
                }

                val filteredOrders = orders.filter { o ->
                    val matchesQuery = o.orderCode.contains(orderSearch, ignoreCase = true) ||
                            o.customerName.contains(orderSearch, ignoreCase = true) ||
                            o.mobile.contains(orderSearch, ignoreCase = true)
                    val matchesStatus = orderStatusFilter == "All" || o.status.displayName == orderStatusFilter
                    matchesQuery && matchesStatus
                }

                if (filteredOrders.isEmpty()) {
                    item {
                        Text(
                            text = "No orders found matching the criteria.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MutedText,
                            modifier = Modifier.padding(vertical = 20.dp)
                        )
                    }
                } else {
                    items(filteredOrders) { o ->
                        AdminOrderCard(
                            order = o,
                            onTrack = { onTrackOrder(o.orderCode) },
                            onAdvance = { next ->
                                BhojonRepository.updateOrderStatus(o.orderCode, next)
                                adminNotice = "Order ${o.orderCode} updated to ${next.displayName}"
                            }
                        )
                    }
                }
            }

            // Tab 2: Menu Management
            if (activeTab == "Menu") {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "DISH CATALOG (${menuItems.size} ITEMS)",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MutedText
                        )
                        Button(
                            onClick = { showAddMenuDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add New Dish")
                        }
                    }
                }

                items(menuItems) { dish ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = WarmCardBg,
                        border = androidx.compose.foundation.BorderStroke(1.dp, WarmBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = dish.name,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = CharcoalText
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "₹${dish.price.toInt()}",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = ForestGreen
                                    )
                                }
                                Text(
                                    text = "${dish.category} · ${dish.servingTime} · ${dish.dietary}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Terracotta
                                )
                                Text(
                                    text = dish.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MutedText,
                                    maxLines = 1
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Switch(
                                    checked = dish.available,
                                    onCheckedChange = { BhojonRepository.toggleItemAvailability(dish.id) },
                                    colors = SwitchDefaults.colors(checkedThumbColor = ForestGreen)
                                )
                                Text(
                                    text = if (dish.available) "Available" else "Disabled",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (dish.available) SuccessGreen else MaterialTheme.colorScheme.error
                                )
                            }
                            IconButton(onClick = { BhojonRepository.deleteMenuItem(dish.id) }) {
                                Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = Color(0xFFC62828))
                            }
                        }
                    }
                }
            }

            // Tab 3: Team & Attendance & Leaves
            if (activeTab == "Team & Attendance") {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "RESTAURANT STAFF (${employees.size})",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MutedText
                        )
                        Button(
                            onClick = { showAddEmployeeDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add Staff")
                        }
                    }
                }

                items(employees) { emp ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = WarmCardBg,
                        border = androidx.compose.foundation.BorderStroke(1.dp, WarmBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(ForestGreen.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = emp.fullName.take(2).uppercase(),
                                    fontWeight = FontWeight.Bold,
                                    color = ForestGreen
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = emp.fullName,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${emp.role} · ${emp.employeeCode}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Terracotta
                                )
                                Text(
                                    text = "Mobile: ${emp.mobile} · Pass: ${emp.loginPassword}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = ForestGreen,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "Salary: ₹${emp.salary.toInt()}/mo · Joined ${emp.joiningDate}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MutedText
                                )
                            }
                            Surface(
                                color = if (emp.status == "On Duty") SuccessGreenLight else SageGreenLight,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = emp.status,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (emp.status == "On Duty") SuccessGreen else CharcoalText,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }

                // Leave approvals section
                item {
                    Text(
                        text = "PENDING LEAVE APPROVALS",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MutedText,
                        modifier = Modifier.padding(top = 10.dp)
                    )
                }

                val pendingLeaves = leaveRequests.filter { it.status == "Pending" }
                if (pendingLeaves.isEmpty()) {
                    item {
                        Text(
                            text = "No pending leave requests.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MutedText
                        )
                    }
                } else {
                    items(pendingLeaves) { req ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = WarmCardBg,
                            border = androidx.compose.foundation.BorderStroke(1.dp, WarmBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "${req.employeeName} — ${req.leaveType}",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Dates: ${req.fromDate} to ${req.toDate} · Reason: ${req.reason}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MutedText
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(
                                        onClick = {
                                            BhojonRepository.reviewLeaveRequest(req.id, true, "Approved by Manager")
                                            adminNotice = "Leave request for ${req.employeeName} Approved."
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                                        shape = RoundedCornerShape(6.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Approve")
                                    }
                                    OutlinedButton(
                                        onClick = {
                                            BhojonRepository.reviewLeaveRequest(req.id, false, "Staff needed on shift")
                                            adminNotice = "Leave request Rejected."
                                        },
                                        shape = RoundedCornerShape(6.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Reject")
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Tab 4: Subscriptions Management
            if (activeTab == "Subscriptions") {
                item {
                    Text(
                        text = "ACTIVE MEAL SUBSCRIPTIONS (${subscriptions.size})",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MutedText
                    )
                }

                items(subscriptions) { sub ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = WarmCardBg,
                        border = androidx.compose.foundation.BorderStroke(1.dp, WarmBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = sub.planName,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = ForestGreen
                                )
                                Surface(
                                    color = if (sub.status == "Active") SuccessGreenLight else WarningOrangeLight,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = sub.status,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (sub.status == "Active") SuccessGreen else WarningOrange,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Customer: ${sub.customerName} (${sub.mobile})",
                                style = MaterialTheme.typography.bodySmall,
                                color = CharcoalText
                            )
                            Text(
                                text = "Address: ${sub.deliveryAddress}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MutedText
                            )
                            Text(
                                text = "Period: ${sub.startDate} to ${sub.endDate} · ₹${sub.totalPrice.toInt()}",
                                style = MaterialTheme.typography.labelSmall,
                                color = Terracotta
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedButton(
                                    onClick = {
                                        val newSt = if (sub.status == "Active") "Paused" else "Active"
                                        BhojonRepository.updateSubscriptionStatus(sub.id, newSt)
                                    },
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(if (sub.status == "Active") "Pause Subscription" else "Resume Subscription")
                                }
                            }
                        }
                    }
                }
            }

            // Tab 5: Reports & Summary
            if (activeTab == "Reports") {
                item {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = WarmCardBg,
                        border = androidx.compose.foundation.BorderStroke(1.dp, WarmBorder)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text(
                                text = "SALES & PERFORMANCE SUMMARY",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MutedText
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Date Range: ${BhojonRepository.todayDateString().slice(0..6)}-01 to ${BhojonRepository.todayDateString()}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Terracotta,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Total Food Revenue Recorded", color = MutedText)
                                Text("₹${todayRevenue.toInt()}", fontWeight = FontWeight.Bold, color = ForestGreen)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Total Customer Orders", color = MutedText)
                                Text("${orders.size}", fontWeight = FontWeight.Bold)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Average Order Value (AOV)", color = MutedText)
                                val aov = if (orders.isNotEmpty()) (todayRevenue / orders.size).toInt() else 0
                                Text("₹$aov", fontWeight = FontWeight.Bold)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Monthly Active Meal Plans", color = MutedText)
                                Text("${subscriptions.size}", fontWeight = FontWeight.Bold)
                            }

                            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = WarmBorder)

                            Text(
                                text = "Orders by Meal Slot:",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            val lunchCount = orders.count { it.mealSlot.contains("Lunch") }
                            val tiffinCount = orders.count { it.mealSlot.contains("Tiffin") }
                            val dinnerCount = orders.count { it.mealSlot.contains("Dinner") }
                            Text("• Lunch Thalis: $lunchCount orders (${if (orders.isNotEmpty()) lunchCount * 100 / orders.size else 0}%)", style = MaterialTheme.typography.bodySmall)
                            Text("• Morning Tiffins: $tiffinCount orders (${if (orders.isNotEmpty()) tiffinCount * 100 / orders.size else 0}%)", style = MaterialTheme.typography.bodySmall)
                            Text("• Dinner: $dinnerCount orders (${if (orders.isNotEmpty()) dinnerCount * 100 / orders.size else 0}%)", style = MaterialTheme.typography.bodySmall)

                            Spacer(modifier = Modifier.height(14.dp))
                            Button(
                                onClick = {
                                    adminNotice = "Report exported to spreadsheet CSV & saved to manager records."
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Export Report as CSV / Spreadsheet")
                            }
                        }
                    }
                }
            }
        }

        // Add Dish Dialog
        if (showAddMenuDialog) {
            AlertDialog(
                onDismissRequest = { showAddMenuDialog = false },
                title = { Text("Add Dish to Bhojon Menu", fontFamily = FontFamily.Serif) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = newDishName,
                            onValueChange = { newDishName = it },
                            label = { Text("Dish Name (e.g. Kosha Mangsho)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = newDishBengali,
                            onValueChange = { newDishBengali = it },
                            label = { Text("Bengali Script Name (কষা মাংস)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = newDishPrice,
                            onValueChange = { newDishPrice = it },
                            label = { Text("Price in ₹") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = newDishCategory,
                            onValueChange = { newDishCategory = it },
                            label = { Text("Category (Morning Tiffin, Lunch Thali, Dinner)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = newDishServingTime,
                            onValueChange = { newDishServingTime = it },
                            label = { Text("Serving Time (e.g. 1:30 PM)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = newDishDesc,
                            onValueChange = { newDishDesc = it },
                            label = { Text("Ingredients / Description") },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 2
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val priceVal = newDishPrice.toDoubleOrNull() ?: 40.0
                            if (newDishName.isNotBlank()) {
                                BhojonRepository.addMenuItem(
                                    MenuItem(
                                        id = 0,
                                        name = newDishName.trim(),
                                        bengaliName = newDishBengali.trim(),
                                        description = newDishDesc.trim(),
                                        category = newDishCategory.trim(),
                                        price = priceVal,
                                        servingTime = newDishServingTime.trim(),
                                        available = true,
                                        dietary = newDishDietary
                                    )
                                )
                                adminNotice = "Dish $newDishName added to active menu!"
                                showAddMenuDialog = false
                                newDishName = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ForestGreen)
                    ) {
                        Text("Save Dish")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddMenuDialog = false }) {
                        Text("Cancel")
                    }
                },
                containerColor = WarmCardBg
            )
        }

        // Add Employee Dialog
        if (showAddEmployeeDialog) {
            AlertDialog(
                onDismissRequest = { showAddEmployeeDialog = false },
                title = { Text("Add Restaurant Staff", fontFamily = FontFamily.Serif) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = newEmpName,
                            onValueChange = { newEmpName = it },
                            label = { Text("Full Name (নাম)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = newEmpRole,
                            onValueChange = { newEmpRole = it },
                            label = { Text("Job Title (Chef, Delivery, Kitchen Helper)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = newEmpMobile,
                            onValueChange = { newEmpMobile = it },
                            label = { Text("Registered Mobile (+91...)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = newEmpPassword,
                            onValueChange = { newEmpPassword = it },
                            label = { Text("Staff Login Password (পাসওয়ার্ড)") },
                            modifier = Modifier.fillMaxWidth(),
                            supportingText = {
                                Text("Employee uses this mobile and password to access Kitchen Team.")
                            }
                        )
                        OutlinedTextField(
                            value = newEmpSalary,
                            onValueChange = { newEmpSalary = it },
                            label = { Text("Monthly Salary (₹)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val salaryVal = newEmpSalary.toDoubleOrNull() ?: 15000.0
                            if (newEmpName.isNotBlank()) {
                                val code = "EMP-00${employees.size + 1}"
                                val pass = newEmpPassword.trim().ifEmpty { "pass123" }
                                BhojonRepository.addEmployee(
                                    Employee(
                                        id = 0,
                                        employeeCode = code,
                                        fullName = newEmpName.trim(),
                                        bengaliName = "",
                                        role = newEmpRole.trim(),
                                        mobile = newEmpMobile.trim(),
                                        status = "Active",
                                        joiningDate = BhojonRepository.todayDateString(),
                                        salary = salaryVal,
                                        pfApplicable = true,
                                        loginPassword = pass
                                    )
                                )
                                adminNotice = "Added $newEmpName ($code) to staff! Login mobile: ${newEmpMobile.trim()}, pass: $pass"
                                showAddEmployeeDialog = false
                                newEmpName = ""
                                newEmpMobile = "+91 "
                                newEmpPassword = "pass123"
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ForestGreen)
                    ) {
                        Text("Register Staff")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddEmployeeDialog = false }) {
                        Text("Cancel")
                    }
                },
                containerColor = WarmCardBg
            )
        }
    }
}

@Composable
private fun KpiCard(
    title: String,
    value: String,
    growth: String? = null,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = WarmCardBg,
        border = androidx.compose.foundation.BorderStroke(1.dp, WarmBorder),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(tint.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = tint,
                    modifier = Modifier.size(20.dp)
                )
            }
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall,
                    color = MutedText
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = value,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = CharcoalText
                    )
                    if (growth != null) {
                        Surface(
                            color = SuccessGreenLight,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = growth,
                                style = MaterialTheme.typography.labelSmall,
                                color = SuccessGreen,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminOrderCard(
    order: Order,
    onTrack: () -> Unit,
    onAdvance: (OrderStatus) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = WarmCardBg,
        border = androidx.compose.foundation.BorderStroke(1.dp, WarmBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = order.orderCode,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Terracotta
                    )
                    Text(
                        text = "${order.mealSlot} · ${order.deliveryDate}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MutedText
                    )
                }
                OrderStatusBadge(status = order.status)
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Customer: ${order.customerName} (${order.mobile})",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "Address: ${order.address}",
                style = MaterialTheme.typography.bodySmall,
                color = MutedText
            )

            Spacer(modifier = Modifier.height(6.dp))
            order.items.forEach { line ->
                Text(
                    text = "• ${line.quantity}x ${line.itemName} (₹${line.lineTotal.toInt()})",
                    style = MaterialTheme.typography.bodySmall,
                    color = CharcoalText
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total: ₹${order.total.toInt()} (${order.paymentMethod})",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = ForestGreen
                )
                TextButton(onClick = onTrack) {
                    Text("View Full Live Details", color = ForestGreen, style = MaterialTheme.typography.labelSmall)
                }
            }

            // Quick Status Advance buttons
            val nextStatus = when (order.status) {
                OrderStatus.PENDING -> OrderStatus.CONFIRMED
                OrderStatus.CONFIRMED -> OrderStatus.PREPARING
                OrderStatus.PREPARING -> OrderStatus.OUT_FOR_DELIVERY
                OrderStatus.OUT_FOR_DELIVERY -> OrderStatus.DELIVERED
                else -> null
            }

            if (nextStatus != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = { onAdvance(nextStatus) },
                        colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Advance to ${nextStatus.displayName}")
                    }
                    OutlinedButton(
                        onClick = { onAdvance(OrderStatus.CANCELLED) },
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("Cancel", color = Color(0xFFC62828))
                    }
                }
            }
        }
    }
}
