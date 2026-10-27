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
import com.example.model.UserRole
import com.example.ui.components.OrderStatusBadge
import com.example.ui.theme.*

@Composable
fun OrderTrackingScreen(
    orderCode: String,
    onBackToMenu: () -> Unit,
    modifier: Modifier = Modifier
) {
    val orders by BhojonRepository.orders.collectAsState()
    val currentRole by BhojonRepository.currentUserRole.collectAsState()
    val order = orders.find { it.orderCode.equals(orderCode.trim(), ignoreCase = true) }

    val steps = listOf(
        Triple("Order Received", OrderStatus.PENDING, Icons.Outlined.CheckCircle),
        Triple("Kitchen Confirmed", OrderStatus.CONFIRMED, Icons.Outlined.ThumbUp),
        Triple("Being Prepared", OrderStatus.PREPARING, Icons.Outlined.SoupKitchen),
        Triple("On Its Way", OrderStatus.OUT_FOR_DELIVERY, Icons.Outlined.DeliveryDining),
        Triple("Delivered", OrderStatus.DELIVERED, Icons.Outlined.Celebration)
    )

    val currentStepIndex = when (order?.status) {
        OrderStatus.PENDING -> 0
        OrderStatus.CONFIRMED -> 1
        OrderStatus.PREPARING -> 2
        OrderStatus.OUT_FOR_DELIVERY -> 3
        OrderStatus.DELIVERED -> 4
        OrderStatus.CANCELLED -> -1
        null -> 0
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(WarmCreamBg)
    ) {
        if (order == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.SearchOff,
                    contentDescription = null,
                    tint = Terracotta,
                    modifier = Modifier.size(56.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Order Not Found",
                    style = MaterialTheme.typography.headlineMedium,
                    fontFamily = FontFamily.Serif,
                    color = CharcoalText
                )
                Text(
                    text = "We couldn't locate an order with code: $orderCode",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MutedText,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                Button(
                    onClick = onBackToMenu,
                    colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Return to Menu")
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                // Header card
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
                                TextButton(
                                    onClick = onBackToMenu,
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Back",
                                        tint = ForestGreen,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Back to Menu", color = ForestGreen, fontWeight = FontWeight.Bold)
                                }
                                OrderStatusBadge(status = order.status)
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = when (order.status) {
                                    OrderStatus.DELIVERED -> "Good food has arrived."
                                    OrderStatus.CANCELLED -> "This order was cancelled."
                                    else -> "We're on it, ${order.customerName.split(" ").first()}."
                                },
                                style = MaterialTheme.typography.headlineMedium,
                                fontFamily = FontFamily.Serif,
                                color = CharcoalText
                            )

                            Text(
                                text = "Order Code: ${order.orderCode}",
                                style = MaterialTheme.typography.labelMedium,
                                color = Terracotta,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${order.mealSlot} · Delivery: ${order.deliveryDate}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MutedText
                            )
                        }
                    }
                }

                // 5-step visual tracking timeline
                item {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = WarmCardBg,
                        border = androidx.compose.foundation.BorderStroke(1.dp, WarmBorder)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text(
                                text = "LIVE MEAL PROGRESS",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MutedText
                            )
                            Spacer(modifier = Modifier.height(14.dp))

                            if (order.status == OrderStatus.CANCELLED) {
                                Surface(
                                    color = Color(0xFFFFEBEE),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Cancel,
                                            contentDescription = null,
                                            tint = Color(0xFFC62828)
                                        )
                                        Text(
                                            text = "This order was cancelled. Please contact the Bhojon kitchen desk if you need help.",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color(0xFFC62828)
                                        )
                                    }
                                }
                            } else {
                                steps.forEachIndexed { idx, (label, status, icon) ->
                                    val isDone = idx <= currentStepIndex
                                    val isCurrent = idx == currentStepIndex

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    if (isDone) ForestGreen else SageGreenLight
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = icon,
                                                contentDescription = null,
                                                tint = if (isDone) Color.White else MutedText,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(14.dp))

                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = label,
                                                style = MaterialTheme.typography.titleSmall,
                                                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                                                color = if (isDone) CharcoalText else MutedText
                                            )
                                            if (isCurrent) {
                                                Text(
                                                    text = "● Active stage right now",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = ForestGreen,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }

                                        if (isDone) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "Completed",
                                                tint = ForestGreen,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }

                                    if (idx < steps.size - 1) {
                                        Box(
                                            modifier = Modifier
                                                .padding(start = 17.dp)
                                                .width(2.dp)
                                                .height(20.dp)
                                                .background(if (idx < currentStepIndex) ForestGreen else SageGreenLight)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // If role is Employee or Admin, show quick order status changer!
                if (currentRole != UserRole.CUSTOMER && order.status != OrderStatus.DELIVERED && order.status != OrderStatus.CANCELLED) {
                    item {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = TerracottaLight,
                            border = androidx.compose.foundation.BorderStroke(1.dp, Terracotta.copy(alpha = 0.4f))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "STAFF KITCHEN CONTROLS",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = TerracottaDark
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Update this order's status directly as kitchen progresses:",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = CharcoalText
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    val nextStatus = when (order.status) {
                                        OrderStatus.PENDING -> OrderStatus.CONFIRMED
                                        OrderStatus.CONFIRMED -> OrderStatus.PREPARING
                                        OrderStatus.PREPARING -> OrderStatus.OUT_FOR_DELIVERY
                                        OrderStatus.OUT_FOR_DELIVERY -> OrderStatus.DELIVERED
                                        else -> null
                                    }
                                    if (nextStatus != null) {
                                        Button(
                                            onClick = {
                                                BhojonRepository.updateOrderStatus(order.orderCode, nextStatus)
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text("Advance to ${nextStatus.displayName}")
                                        }
                                    }
                                    OutlinedButton(
                                        onClick = {
                                            BhojonRepository.updateOrderStatus(order.orderCode, OrderStatus.CANCELLED)
                                        },
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFC62828)),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("Cancel")
                                    }
                                }
                            }
                        }
                    }
                }

                // Ordered Items
                item {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = WarmCardBg,
                        border = androidx.compose.foundation.BorderStroke(1.dp, WarmBorder)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text(
                                text = "ORDERED DISHES · খাবারের তালিকা",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MutedText
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            order.items.forEach { item ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "${item.quantity}x ${item.itemName}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = CharcoalText
                                    )
                                    Text(
                                        text = "₹${item.lineTotal.toInt()}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = CharcoalText
                                    )
                                }
                            }

                            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = WarmBorder)

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Total Paid (${order.paymentMethod})",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "₹${order.total.toInt()}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = ForestGreen
                                )
                            }
                        }
                    }
                }

                // Delivery info
                item {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = WarmCardBg,
                        border = androidx.compose.foundation.BorderStroke(1.dp, WarmBorder)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text(
                                text = "DESTINATION & INSTRUCTIONS",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MutedText
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = order.customerName,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = order.address,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MutedText
                            )
                            Text(
                                text = "Phone: ${order.mobile}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MutedText
                            )
                            if (order.instructions.isNotBlank()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Notes: \"${order.instructions}\"",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Terracotta
                                )
                            }
                        }
                    }
                }

                // Order Activity History
                if (order.history.isNotEmpty()) {
                    item {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = WarmCardBg,
                            border = androidx.compose.foundation.BorderStroke(1.dp, WarmBorder)
                        ) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Text(
                                    text = "KITCHEN & DELIVERY LOG",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MutedText
                                )
                                Spacer(modifier = Modifier.height(10.dp))

                                order.history.forEach { event ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Text(
                                            text = event.timestamp,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Terracotta,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.width(70.dp)
                                        )
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = event.status,
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = CharcoalText
                                            )
                                            Text(
                                                text = event.note,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MutedText
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
