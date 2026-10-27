package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.BhojonRepository
import com.example.model.UserRole
import com.example.ui.components.*
import com.example.ui.theme.DeepForestGreen
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.WarmCardBg
import com.example.ui.theme.WarmCreamBg

@Composable
fun BhojonApp() {
    val currentRole by BhojonRepository.currentUserRole.collectAsState()
    val currentCustomerUser by BhojonRepository.currentCustomerUser.collectAsState()
    val currentEmployeeUser by BhojonRepository.currentEmployeeUser.collectAsState()
    val isAdminAuthenticated by BhojonRepository.isAdminAuthenticated.collectAsState()

    val cart by BhojonRepository.cart.collectAsState()
    val orders by BhojonRepository.orders.collectAsState()

    var showSplashScreen by remember { mutableStateOf(true) }
    var showRoleSwitcher by remember { mutableStateOf(false) }
    var showCartSheet by remember { mutableStateOf(false) }

    // Role-specific authentication dialog states
    var showCustomerAuthDialog by remember { mutableStateOf(false) }
    var showEmployeeAuthDialog by remember { mutableStateOf(false) }
    var showAdminAuthDialog by remember { mutableStateOf(false) }

    // Customer Navigation state
    var customerTab by remember { mutableStateOf(0) } // 0: Menu, 1: Subscriptions, 2: Track Order
    var activeTrackingOrderCode by remember { mutableStateOf<String?>(null) }

    val cartItemCount = remember(cart) { cart.sumOf { it.quantity } }

    BackHandler(enabled = showSplashScreen.not() || activeTrackingOrderCode != null || customerTab != 0 || currentRole != UserRole.CUSTOMER) {
        if (activeTrackingOrderCode != null) {
            activeTrackingOrderCode = null
        } else if (customerTab != 0) {
            customerTab = 0
        } else if (currentRole != UserRole.CUSTOMER) {
            BhojonRepository.setUserRole(UserRole.CUSTOMER)
        } else if (!showSplashScreen) {
            showSplashScreen = true
        }
    }

    // Splash Screen on launch
    if (showSplashScreen && currentRole == UserRole.CUSTOMER) {
        WelcomeSplashScreen(
            onGetStarted = { showSplashScreen = false }
        )
    } else {
        Scaffold(
            topBar = {
                BhojonTopBar(
                    currentRole = currentRole,
                    customerUser = currentCustomerUser,
                    employeeUser = currentEmployeeUser,
                    isAdminAuthenticated = isAdminAuthenticated,
                    cartItemCount = cartItemCount,
                    onRoleChangeClick = { showRoleSwitcher = true },
                    onCartClick = { showCartSheet = true }
                )
            },
            bottomBar = {
                if (currentRole == UserRole.CUSTOMER && activeTrackingOrderCode == null) {
                    NavigationBar(
                        containerColor = WarmCardBg,
                        tonalElevation = 8.dp
                    ) {
                        NavigationBarItem(
                            selected = customerTab == 0,
                            onClick = { customerTab = 0 },
                            icon = {
                                Icon(
                                    imageVector = if (customerTab == 0) Icons.Filled.RestaurantMenu else Icons.Outlined.RestaurantMenu,
                                    contentDescription = "Menu"
                                )
                            },
                            label = { Text("Menu · মেনু") },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = ForestGreen,
                                selectedTextColor = ForestGreen,
                                indicatorColor = ForestGreen.copy(alpha = 0.15f)
                            ),
                            modifier = Modifier.testTag("nav_menu_tab")
                        )

                        NavigationBarItem(
                            selected = customerTab == 1,
                            onClick = { customerTab = 1 },
                            icon = {
                                Icon(
                                    imageVector = if (customerTab == 1) Icons.Filled.CalendarMonth else Icons.Outlined.CalendarMonth,
                                    contentDescription = "Subscriptions"
                                )
                            },
                            label = { Text("Plans · প্ল্যান") },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = ForestGreen,
                                selectedTextColor = ForestGreen,
                                indicatorColor = ForestGreen.copy(alpha = 0.15f)
                            ),
                            modifier = Modifier.testTag("nav_plans_tab")
                        )

                        NavigationBarItem(
                            selected = customerTab == 2,
                            onClick = { customerTab = 2 },
                            icon = {
                                Icon(
                                    imageVector = if (customerTab == 2) Icons.Filled.LocalShipping else Icons.Outlined.LocalShipping,
                                    contentDescription = "Tracking"
                                )
                            },
                            label = { Text("Track · ট্র্যাকিং") },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = ForestGreen,
                                selectedTextColor = ForestGreen,
                                indicatorColor = ForestGreen.copy(alpha = 0.15f)
                            ),
                            modifier = Modifier.testTag("nav_track_tab")
                        )
                    }
                }
            },
            containerColor = WarmCreamBg,
            contentWindowInsets = WindowInsets.safeDrawing
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when (currentRole) {
                    UserRole.CUSTOMER -> {
                        if (activeTrackingOrderCode != null) {
                            OrderTrackingScreen(
                                orderCode = activeTrackingOrderCode!!,
                                onBackToMenu = { activeTrackingOrderCode = null }
                            )
                        } else {
                            when (customerTab) {
                                0 -> CustomerMenuScreen(
                                    onOpenCart = { showCartSheet = true },
                                    onTrackOrder = { code -> activeTrackingOrderCode = code }
                                )
                                1 -> SubscriptionsScreen()
                                2 -> {
                                    val latestCode = orders.firstOrNull()?.orderCode ?: "BJ-20261014-9FA2E078B1"
                                    OrderTrackingScreen(
                                        orderCode = latestCode,
                                        onBackToMenu = { customerTab = 0 }
                                    )
                                }
                            }
                        }
                    }
                    UserRole.EMPLOYEE -> {
                        EmployeeScreen()
                    }
                    UserRole.ADMIN -> {
                        AdminDeskScreen(
                            onTrackOrder = { code ->
                                BhojonRepository.setUserRole(UserRole.CUSTOMER)
                                activeTrackingOrderCode = code
                            }
                        )
                    }
                }
            }

            // Role Switcher Dialog
            if (showRoleSwitcher) {
                RoleSwitcherDialog(
                    currentRole = currentRole,
                    customerUser = currentCustomerUser,
                    employeeUser = currentEmployeeUser,
                    isAdminAuthenticated = isAdminAuthenticated,
                    onRoleSelected = { role ->
                        when (role) {
                            UserRole.CUSTOMER -> {
                                if (currentCustomerUser == null) {
                                    showCustomerAuthDialog = true
                                } else {
                                    BhojonRepository.setUserRole(UserRole.CUSTOMER)
                                    activeTrackingOrderCode = null
                                    showSplashScreen = false
                                }
                            }
                            UserRole.EMPLOYEE -> {
                                // Prompt employee login with registered mobile and password
                                showEmployeeAuthDialog = true
                            }
                            UserRole.ADMIN -> {
                                // Prompt admin login (Administrator / Admin1234)
                                if (isAdminAuthenticated) {
                                    BhojonRepository.setUserRole(UserRole.ADMIN)
                                    activeTrackingOrderCode = null
                                    showSplashScreen = false
                                } else {
                                    showAdminAuthDialog = true
                                }
                            }
                        }
                    },
                    onSignOut = {
                        BhojonRepository.logoutAll()
                        activeTrackingOrderCode = null
                    },
                    onDismiss = { showRoleSwitcher = false }
                )
            }

            // Customer Sign In / Sign Up Dialog
            if (showCustomerAuthDialog) {
                CustomerAuthDialog(
                    onSuccess = { user ->
                        BhojonRepository.setUserRole(UserRole.CUSTOMER)
                        showCustomerAuthDialog = false
                        showSplashScreen = false
                        activeTrackingOrderCode = null
                    },
                    onDismiss = { showCustomerAuthDialog = false }
                )
            }

            // Employee Login Dialog
            if (showEmployeeAuthDialog) {
                EmployeeLoginDialog(
                    onSuccess = { emp ->
                        BhojonRepository.setUserRole(UserRole.EMPLOYEE)
                        showEmployeeAuthDialog = false
                        showSplashScreen = false
                        activeTrackingOrderCode = null
                    },
                    onDismiss = { showEmployeeAuthDialog = false }
                )
            }

            // Admin Login Dialog (Administrator / Admin1234)
            if (showAdminAuthDialog) {
                AdminLoginDialog(
                    onSuccess = {
                        BhojonRepository.setUserRole(UserRole.ADMIN)
                        showAdminAuthDialog = false
                        showSplashScreen = false
                        activeTrackingOrderCode = null
                    },
                    onDismiss = { showAdminAuthDialog = false }
                )
            }

            // Cart Checkout Bottom Sheet
            if (showCartSheet) {
                CartCheckoutSheet(
                    onDismiss = { showCartSheet = false },
                    onOrderPlaced = { code ->
                        activeTrackingOrderCode = code
                    }
                )
            }
        }
    }
}
