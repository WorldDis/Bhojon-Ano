package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.model.OrderStatus
import com.example.ui.components.OrderStatusBadge
import com.example.ui.theme.*

@Composable
fun EmployeeScreen(
    modifier: Modifier = Modifier
) {
    val employees by BhojonRepository.employees.collectAsState()
    val attendance by BhojonRepository.attendance.collectAsState()
    val duties by BhojonRepository.duties.collectAsState()
    val orders by BhojonRepository.orders.collectAsState()
    val leaveRequests by BhojonRepository.leaveRequests.collectAsState()

    val currentEmployeeUser by BhojonRepository.currentEmployeeUser.collectAsState()
    val currentEmployee = currentEmployeeUser ?: employees.firstOrNull() ?: return
    val today = BhojonRepository.todayDateString()
    val todayRecord = attendance.find { it.employeeId == currentEmployee.id && it.date == today }
    val isCheckedIn = todayRecord?.checkInTime != null && todayRecord.checkOutTime == null

    var shiftNoteInput by remember { mutableStateOf("") }
    var notificationMessage by remember { mutableStateOf<String?>(null) }
    var showLeaveDialog by remember { mutableStateOf(false) }

    var leaveType by remember { mutableStateOf("Casual Leave") }
    var fromDate by remember { mutableStateOf("2026-10-20") }
    var toDate by remember { mutableStateOf("2026-10-21") }
    var leaveReason by remember { mutableStateOf("") }

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
            // Employee Profile Welcome
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = WarmCardBg,
                    border = androidx.compose.foundation.BorderStroke(1.dp, WarmBorder)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(ForestGreen),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = currentEmployee.fullName.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString("").ifEmpty { "EM" },
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = currentEmployee.fullName,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = CharcoalText
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = if (isCheckedIn) SuccessGreenLight else SageGreenLight,
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = if (isCheckedIn) "ON DUTY" else "OFF DUTY",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (isCheckedIn) SuccessGreen else MutedText,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "${currentEmployee.role} · ${currentEmployee.employeeCode}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Terracotta
                            )
                            Text(
                                text = "Joining Date: ${currentEmployee.joiningDate} · Salary: ₹${currentEmployee.salary.toInt()}/mo",
                                style = MaterialTheme.typography.labelSmall,
                                color = MutedText
                            )
                        }
                    }
                }
            }

            if (notificationMessage != null) {
                item {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = SuccessGreenLight,
                        border = androidx.compose.foundation.BorderStroke(1.dp, SuccessGreen.copy(alpha = 0.5f))
                    ) {
                        Text(
                            text = notificationMessage!!,
                            style = MaterialTheme.typography.bodySmall,
                            color = SuccessGreen,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }

            // Daily Attendance Card
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = WarmCardBg,
                    border = androidx.compose.foundation.BorderStroke(1.dp, WarmBorder)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "TODAY'S SHIFT ATTENDANCE",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MutedText
                            )
                            Text(
                                text = today,
                                style = MaterialTheme.typography.labelSmall,
                                color = ForestGreen,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Check-In Time", style = MaterialTheme.typography.labelSmall, color = MutedText)
                                Text(
                                    text = todayRecord?.checkInTime ?: "Not checked in",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (todayRecord?.checkInTime != null) ForestGreen else MutedText
                                )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Check-Out Time", style = MaterialTheme.typography.labelSmall, color = MutedText)
                                Text(
                                    text = todayRecord?.checkOutTime ?: if (isCheckedIn) "Active Shift" else "—",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = CharcoalText
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        OutlinedTextField(
                            value = shiftNoteInput,
                            onValueChange = { shiftNoteInput = it },
                            placeholder = { Text("Shift note (e.g. morning luchi prep done)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = {
                                    BhojonRepository.checkInEmployee(currentEmployee.id, shiftNoteInput.ifBlank { null })
                                    notificationMessage = "Checked into shift at ${BhojonRepository.currentTimeString()}"
                                    shiftNoteInput = ""
                                },
                                enabled = !isCheckedIn,
                                colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("employee_checkin_button")
                            ) {
                                Icon(imageVector = Icons.AutoMirrored.Filled.Login, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Check In")
                            }

                            Button(
                                onClick = {
                                    BhojonRepository.checkOutEmployee(currentEmployee.id, shiftNoteInput.ifBlank { null })
                                    notificationMessage = "Checked out of shift at ${BhojonRepository.currentTimeString()}"
                                    shiftNoteInput = ""
                                },
                                enabled = isCheckedIn,
                                colors = ButtonDefaults.buttonColors(containerColor = Terracotta),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("employee_checkout_button")
                            ) {
                                Icon(imageVector = Icons.AutoMirrored.Filled.Logout, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Check Out")
                            }
                        }
                    }
                }
            }

            // Assigned Duties
            item {
                Text(
                    text = "ASSIGNED KITCHEN DUTIES · দায়িত্ব",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MutedText
                )
            }

            items(duties) { duty ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = WarmCardBg,
                    border = androidx.compose.foundation.BorderStroke(1.dp, WarmBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Checkbox(
                            checked = duty.status == "Completed",
                            onCheckedChange = { BhojonRepository.completeDuty(duty.id) },
                            colors = CheckboxDefaults.colors(checkedColor = ForestGreen)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = duty.title,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = CharcoalText
                            )
                            Text(
                                text = duty.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MutedText
                            )
                        }
                        Surface(
                            color = if (duty.status == "Completed") SuccessGreenLight else MustardGoldLight,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = duty.status,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (duty.status == "Completed") SuccessGreen else CharcoalText,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            // Active Kitchen Orders Ready for Prep
            item {
                Text(
                    text = "ACTIVE KITCHEN ORDERS TO FULFILL",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MutedText,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            val pendingOrPrepOrders = orders.filter { it.status != OrderStatus.DELIVERED && it.status != OrderStatus.CANCELLED }
            if (pendingOrPrepOrders.isEmpty()) {
                item {
                    Text(
                        text = "All kitchen orders completed for the current slot! Great job.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MutedText
                    )
                }
            } else {
                items(pendingOrPrepOrders) { order ->
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
                                Text(
                                    text = order.orderCode,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Terracotta
                                )
                                OrderStatusBadge(status = order.status)
                            }
                            Text(
                                text = "Slot: ${order.mealSlot} · For: ${order.customerName}",
                                style = MaterialTheme.typography.bodySmall,
                                color = CharcoalText
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            order.items.forEach { line ->
                                Text(
                                    text = "• ${line.quantity}x ${line.itemName}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MutedText
                                )
                            }
                            if (order.instructions.isNotBlank()) {
                                Text(
                                    text = "Chef note: \"${order.instructions}\"",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Terracotta
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                when (order.status) {
                                    OrderStatus.PENDING -> {
                                        Button(
                                            onClick = { BhojonRepository.updateOrderStatus(order.orderCode, OrderStatus.CONFIRMED) },
                                            colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                                            shape = RoundedCornerShape(6.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text("Accept & Confirm Order")
                                        }
                                    }
                                    OrderStatus.CONFIRMED -> {
                                        Button(
                                            onClick = { BhojonRepository.updateOrderStatus(order.orderCode, OrderStatus.PREPARING) },
                                            colors = ButtonDefaults.buttonColors(containerColor = MustardGold),
                                            shape = RoundedCornerShape(6.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text("Start Cooking in Kitchen")
                                        }
                                    }
                                    OrderStatus.PREPARING -> {
                                        Button(
                                            onClick = { BhojonRepository.updateOrderStatus(order.orderCode, OrderStatus.OUT_FOR_DELIVERY) },
                                            colors = ButtonDefaults.buttonColors(containerColor = Terracotta),
                                            shape = RoundedCornerShape(6.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text("Ready · Dispatch for Delivery")
                                        }
                                    }
                                    OrderStatus.OUT_FOR_DELIVERY -> {
                                        Button(
                                            onClick = { BhojonRepository.updateOrderStatus(order.orderCode, OrderStatus.DELIVERED) },
                                            colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                                            shape = RoundedCornerShape(6.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text("Mark Handed Over / Delivered")
                                        }
                                    }
                                    else -> {}
                                }
                            }
                        }
                    }
                }
            }

            // Leave Requests section
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "LEAVE REQUESTS · ছুটির আবেদন",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MutedText
                    )
                    TextButton(onClick = { showLeaveDialog = true }) {
                        Text("+ Apply for Leave", color = ForestGreen, fontWeight = FontWeight.Bold)
                    }
                }
            }

            items(leaveRequests) { req ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = WarmCardBg,
                    border = androidx.compose.foundation.BorderStroke(1.dp, WarmBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${req.leaveType} (${req.fromDate} to ${req.toDate})",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Reason: ${req.reason}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MutedText
                            )
                        }
                        Surface(
                            color = when (req.status) {
                                "Approved" -> SuccessGreenLight
                                "Rejected" -> Color(0xFFFFEBEE)
                                else -> WarningOrangeLight
                            },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = req.status,
                                style = MaterialTheme.typography.labelSmall,
                                color = when (req.status) {
                                    "Approved" -> SuccessGreen
                                    "Rejected" -> Color(0xFFC62828)
                                    else -> WarningOrange
                                },
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        // Leave application dialog
        if (showLeaveDialog) {
            AlertDialog(
                onDismissRequest = { showLeaveDialog = false },
                title = { Text("Apply for Leave", fontFamily = FontFamily.Serif) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = leaveType,
                            onValueChange = { leaveType = it },
                            label = { Text("Leave Type (Casual, Sick, Emergency)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = fromDate,
                            onValueChange = { fromDate = it },
                            label = { Text("From Date (YYYY-MM-DD)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = toDate,
                            onValueChange = { toDate = it },
                            label = { Text("To Date (YYYY-MM-DD)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = leaveReason,
                            onValueChange = { leaveReason = it },
                            label = { Text("Reason for leave") },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 3
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (leaveReason.isNotBlank()) {
                                BhojonRepository.submitLeaveRequest(
                                    employeeId = currentEmployee.id,
                                    leaveType = leaveType,
                                    fromDate = fromDate,
                                    toDate = toDate,
                                    reason = leaveReason
                                )
                                notificationMessage = "Leave application submitted to Manager Biplab Sen."
                                showLeaveDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ForestGreen)
                    ) {
                        Text("Submit Request")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showLeaveDialog = false }) {
                        Text("Cancel")
                    }
                },
                containerColor = WarmCardBg
            )
        }
    }
}
