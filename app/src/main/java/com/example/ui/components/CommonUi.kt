package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
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
import com.example.model.CustomerUser
import com.example.model.Employee
import com.example.model.OrderStatus
import com.example.model.UserRole
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BhojonTopBar(
    currentRole: UserRole,
    customerUser: CustomerUser? = null,
    employeeUser: Employee? = null,
    isAdminAuthenticated: Boolean = false,
    cartItemCount: Int,
    onRoleChangeClick: () -> Unit,
    onCartClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            Column {
                BhojonBrandLogo(
                    emblemSize = 34.dp,
                    isDarkTheme = false,
                    showTagline = false
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp),
                    modifier = Modifier.padding(start = 44.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Location",
                        tint = FoodTerracotta,
                        modifier = Modifier.size(11.dp)
                    )
                    Text(
                        text = BhojonBrand.LOCATION,
                        style = MaterialTheme.typography.labelSmall,
                        color = MutedText,
                        fontSize = 10.sp
                    )
                }
            }
        },
        actions = {
            // Role & User Identity Switcher Button
            Surface(
                onClick = onRoleChangeClick,
                shape = RoundedCornerShape(20.dp),
                color = when (currentRole) {
                    UserRole.CUSTOMER -> SageGreenLight
                    UserRole.EMPLOYEE -> MustardGoldLight
                    UserRole.ADMIN -> TerracottaLight
                },
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    when (currentRole) {
                        UserRole.CUSTOMER -> ForestGreen.copy(alpha = 0.4f)
                        UserRole.EMPLOYEE -> MustardGold.copy(alpha = 0.5f)
                        UserRole.ADMIN -> Terracotta.copy(alpha = 0.5f)
                    }
                ),
                modifier = Modifier
                    .padding(end = 8.dp)
                    .testTag("role_switcher_button")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = when (currentRole) {
                            UserRole.CUSTOMER -> if (customerUser != null) Icons.Default.AccountCircle else Icons.Outlined.RestaurantMenu
                            UserRole.EMPLOYEE -> Icons.Outlined.Badge
                            UserRole.ADMIN -> Icons.Outlined.AdminPanelSettings
                        },
                        contentDescription = "Current Role",
                        modifier = Modifier.size(14.dp),
                        tint = when (currentRole) {
                            UserRole.CUSTOMER -> ForestGreen
                            UserRole.EMPLOYEE -> CharcoalText
                            UserRole.ADMIN -> Terracotta
                        }
                    )
                    Text(
                        text = when (currentRole) {
                            UserRole.CUSTOMER -> customerUser?.fullName?.split(" ")?.firstOrNull() ?: "Customer"
                            UserRole.EMPLOYEE -> employeeUser?.fullName?.split(" ")?.firstOrNull() ?: "Staff"
                            UserRole.ADMIN -> "Administrator"
                        },
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = CharcoalText,
                        maxLines = 1
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Switch",
                        modifier = Modifier.size(16.dp),
                        tint = CharcoalText
                    )
                }
            }

            // Cart Button (visible on customer screens)
            if (currentRole == UserRole.CUSTOMER) {
                IconButton(
                    onClick = onCartClick,
                    modifier = Modifier.testTag("cart_button")
                ) {
                    BadgedBox(
                        badge = {
                            if (cartItemCount > 0) {
                                Badge(
                                    containerColor = Terracotta,
                                    contentColor = Color.White
                                ) {
                                    Text(
                                        text = cartItemCount.toString(),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ShoppingBag,
                            contentDescription = "Shopping Basket",
                            tint = ForestGreen
                        )
                    }
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = WarmCreamBg.copy(alpha = 0.95f)
        ),
        modifier = modifier
    )
}

@Composable
fun RoleSwitcherDialog(
    currentRole: UserRole,
    customerUser: CustomerUser? = null,
    employeeUser: Employee? = null,
    isAdminAuthenticated: Boolean = false,
    onRoleSelected: (UserRole) -> Unit,
    onSignOut: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = "Switch Workspace & Account",
                    style = MaterialTheme.typography.titleLarge,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "আপনার প্রবেশপথ বেছে নিন (Authentication Required)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MutedText
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                UserRole.values().forEach { role ->
                    val isSelected = role == currentRole
                    Surface(
                        onClick = {
                            onRoleSelected(role)
                            onDismiss()
                        },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) SageGreenLight else WarmCardBg,
                        border = androidx.compose.foundation.BorderStroke(
                            if (isSelected) 1.5.dp else 1.dp,
                            if (isSelected) ForestGreen else WarmBorder
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("role_select_${role.name.lowercase()}")
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when (role) {
                                            UserRole.CUSTOMER -> ForestGreen.copy(alpha = 0.15f)
                                            UserRole.EMPLOYEE -> MustardGold.copy(alpha = 0.2f)
                                            UserRole.ADMIN -> Terracotta.copy(alpha = 0.15f)
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = when (role) {
                                        UserRole.CUSTOMER -> Icons.Default.Restaurant
                                        UserRole.EMPLOYEE -> Icons.Default.Engineering
                                        UserRole.ADMIN -> Icons.Default.Dashboard
                                    },
                                    contentDescription = null,
                                    tint = when (role) {
                                        UserRole.CUSTOMER -> ForestGreen
                                        UserRole.EMPLOYEE -> CharcoalText
                                        UserRole.ADMIN -> Terracotta
                                    },
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = role.label,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = CharcoalText
                                    )
                                    if (isSelected) {
                                        Surface(
                                            color = ForestGreen,
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                text = "ACTIVE",
                                                color = Color.White,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                            )
                                        }
                                    }
                                }

                                Text(
                                    text = when (role) {
                                        UserRole.CUSTOMER -> if (customerUser != null) {
                                            "👤 ${customerUser.fullName} (${customerUser.mobile})"
                                        } else {
                                            "Sign In or Sign Up with Mobile & Password"
                                        }
                                        UserRole.EMPLOYEE -> if (employeeUser != null) {
                                            "👨‍🍳 ${employeeUser.fullName} (${employeeUser.role})"
                                        } else {
                                            "Staff Mobile & Password. (Admin registers staff)"
                                        }
                                        UserRole.ADMIN -> if (isAdminAuthenticated) {
                                            "🛡️ Administrator authorized"
                                        } else {
                                            "User ID: Administrator / Pass: Admin1234"
                                        }
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (role == UserRole.ADMIN) Terracotta else ForestGreen,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                // Sign Out button
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    onClick = {
                        onSignOut()
                        onDismiss()
                    },
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFFFEBEE),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF9A9A)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("sign_out_button")
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = "Sign Out",
                            tint = Color(0xFFC62828),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Sign Out / Switch All Accounts",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFC62828)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = ForestGreen)
            }
        },
        containerColor = WarmCardBg
    )
}

@Composable
fun OrderStatusBadge(status: OrderStatus, modifier: Modifier = Modifier) {
    val (bgColor, textColor) = when (status) {
        OrderStatus.PENDING -> WarningOrangeLight to WarningOrange
        OrderStatus.CONFIRMED -> InfoBlueLight to InfoBlue
        OrderStatus.PREPARING -> MustardGoldLight to CharcoalText
        OrderStatus.OUT_FOR_DELIVERY -> TerracottaLight to Terracotta
        OrderStatus.DELIVERED -> SuccessGreenLight to SuccessGreen
        OrderStatus.CANCELLED -> Color(0xFFFFEBEE) to Color(0xFFC62828)
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, textColor.copy(alpha = 0.3f)),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(textColor)
            )
            Text(
                text = status.displayName,
                style = MaterialTheme.typography.labelSmall,
                color = textColor,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun QuantityStepper(
    quantity: Int,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, ForestGreen.copy(alpha = 0.4f)),
        color = SageGreenLight,
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
        ) {
            IconButton(
                onClick = onDecrement,
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Remove,
                    contentDescription = "Decrease quantity",
                    tint = ForestGreen,
                    modifier = Modifier.size(16.dp)
                )
            }
            Text(
                text = quantity.toString(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = ForestGreen,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            IconButton(
                onClick = onIncrement,
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Increase quantity",
                    tint = ForestGreen,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
