package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BhojonRepository
import com.example.model.CustomerUser
import com.example.model.Employee
import com.example.ui.theme.*

@Composable
fun CustomerAuthDialog(
    initialTab: Int = 0, // 0 for Sign In, 1 for Sign Up
    onSuccess: (CustomerUser) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(initialTab) }

    // Sign In form state
    var signInMobile by remember { mutableStateOf("+91 98315 22001") }
    var signInPassword by remember { mutableStateOf("pass123") }
    var signInPasswordVisible by remember { mutableStateOf(false) }

    // Sign Up form state
    var signUpName by remember { mutableStateOf("") }
    var signUpMobile by remember { mutableStateOf("+91 ") }
    var signUpAddress by remember { mutableStateOf("") }
    var signUpPassword by remember { mutableStateOf("") }
    var signUpPasswordVisible by remember { mutableStateOf(false) }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(ForestGreen.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = ForestGreen,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column {
                        Text(
                            text = if (selectedTab == 0) "Customer Sign In" else "Create Customer Account",
                            style = MaterialTheme.typography.titleLarge,
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            color = ForestGreen
                        )
                        Text(
                            text = "গ্রাহক সাইন ইন / অ্যাকাউন্ট তৈরি",
                            style = MaterialTheme.typography.labelSmall,
                            color = MutedText
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Tab Switcher
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = WarmCardBg,
                    contentColor = ForestGreen
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = {
                            selectedTab = 0
                            errorMessage = null
                        },
                        text = {
                            Text(
                                "Sign In",
                                fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        modifier = Modifier.testTag("customer_tab_signin")
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = {
                            selectedTab = 1
                            errorMessage = null
                        },
                        text = {
                            Text(
                                "Sign Up",
                                fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        modifier = Modifier.testTag("customer_tab_signup")
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (errorMessage != null) {
                    Surface(
                        color = Color(0xFFFFEBEE),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF9A9A)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ErrorOutline,
                                contentDescription = null,
                                tint = Color(0xFFC62828),
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = errorMessage!!,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFC62828)
                            )
                        }
                    }
                }

                if (selectedTab == 0) {
                    // Sign In Form
                    OutlinedTextField(
                        value = signInMobile,
                        onValueChange = {
                            signInMobile = it
                            errorMessage = null
                        },
                        label = { Text("Mobile Number (মোবাইল নম্বর)") },
                        leadingIcon = {
                            Icon(Icons.Default.Phone, contentDescription = null, tint = ForestGreen)
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("customer_signin_mobile")
                    )

                    OutlinedTextField(
                        value = signInPassword,
                        onValueChange = {
                            signInPassword = it
                            errorMessage = null
                        },
                        label = { Text("Password (পাসওয়ার্ড)") },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = ForestGreen)
                        },
                        trailingIcon = {
                            IconButton(onClick = { signInPasswordVisible = !signInPasswordVisible }) {
                                Icon(
                                    imageVector = if (signInPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = null
                                )
                            }
                        },
                        visualTransformation = if (signInPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("customer_signin_password")
                    )

                    // Quick test accounts
                    Surface(
                        color = SageGreenLight.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "Quick Demo Customer Accounts:",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = ForestGreen
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                SuggestionChip(
                                    onClick = {
                                        signInMobile = "+91 98315 22001"
                                        signInPassword = "pass123"
                                    },
                                    label = { Text("Debashis (+91 98315)", fontSize = 11.sp) }
                                )
                                SuggestionChip(
                                    onClick = {
                                        signInMobile = "+91 98310 98765"
                                        signInPassword = "pass123"
                                    },
                                    label = { Text("Priya (+91 98310)", fontSize = 11.sp) }
                                )
                            }
                        }
                    }
                } else {
                    // Sign Up Form
                    OutlinedTextField(
                        value = signUpName,
                        onValueChange = {
                            signUpName = it
                            errorMessage = null
                        },
                        label = { Text("Full Name (আপনার নাম)") },
                        leadingIcon = {
                            Icon(Icons.Default.Person, contentDescription = null, tint = ForestGreen)
                        },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("customer_signup_name")
                    )

                    OutlinedTextField(
                        value = signUpMobile,
                        onValueChange = {
                            signUpMobile = it
                            errorMessage = null
                        },
                        label = { Text("Mobile Number (মোবাইল)") },
                        leadingIcon = {
                            Icon(Icons.Default.Phone, contentDescription = null, tint = ForestGreen)
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("customer_signup_mobile")
                    )

                    OutlinedTextField(
                        value = signUpAddress,
                        onValueChange = {
                            signUpAddress = it
                            errorMessage = null
                        },
                        label = { Text("Delivery Address (ঠিকানা)") },
                        leadingIcon = {
                            Icon(Icons.Default.Home, contentDescription = null, tint = ForestGreen)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("customer_signup_address"),
                        maxLines = 2
                    )

                    OutlinedTextField(
                        value = signUpPassword,
                        onValueChange = {
                            signUpPassword = it
                            errorMessage = null
                        },
                        label = { Text("Create Password (পাসওয়ার্ড)") },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = ForestGreen)
                        },
                        trailingIcon = {
                            IconButton(onClick = { signUpPasswordVisible = !signUpPasswordVisible }) {
                                Icon(
                                    imageVector = if (signUpPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = null
                                )
                            }
                        },
                        visualTransformation = if (signUpPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("customer_signup_password")
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (selectedTab == 0) {
                        val (user, msg) = BhojonRepository.loginCustomer(signInMobile, signInPassword)
                        if (user != null) {
                            onSuccess(user)
                        } else {
                            errorMessage = msg
                        }
                    } else {
                        if (signUpName.isBlank()) {
                            errorMessage = "Please enter your full name."
                            return@Button
                        }
                        if (signUpMobile.trim().length < 8) {
                            errorMessage = "Please enter a valid mobile number."
                            return@Button
                        }
                        if (signUpPassword.length < 4) {
                            errorMessage = "Password must be at least 4 characters."
                            return@Button
                        }
                        val (success, msg) = BhojonRepository.registerCustomer(
                            fullName = signUpName,
                            mobile = signUpMobile,
                            password = signUpPassword,
                            address = signUpAddress
                        )
                        if (success) {
                            val loggedInUser = BhojonRepository.currentCustomerUser.value
                            if (loggedInUser != null) {
                                onSuccess(loggedInUser)
                            } else {
                                onDismiss()
                            }
                        } else {
                            errorMessage = msg
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                modifier = Modifier.testTag("customer_auth_submit_button")
            ) {
                Text(if (selectedTab == 0) "Sign In" else "Create Account")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = CharcoalText)
            }
        },
        containerColor = WarmCardBg
    )
}

@Composable
fun EmployeeLoginDialog(
    onSuccess: (Employee) -> Unit,
    onDismiss: () -> Unit
) {
    val employees by BhojonRepository.employees.collectAsState()

    var staffInput by remember { mutableStateOf("+91 98301 22345") } // Default Chef Subhashish
    var password by remember { mutableStateOf("pass123") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(MustardGold.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Badge,
                        contentDescription = null,
                        tint = CharcoalText,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Column {
                    Text(
                        text = "Kitchen Team Login",
                        style = MaterialTheme.typography.titleLarge,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        color = CharcoalText
                    )
                    Text(
                        text = "রান্নাঘর স্টাফ শিফট প্রবেশ",
                        style = MaterialTheme.typography.labelSmall,
                        color = MutedText
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Info Banner: Staff registration handled by admin
                Surface(
                    color = MustardGoldLight,
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MustardGold.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = CharcoalText,
                            modifier = Modifier
                                .size(18.dp)
                                .padding(top = 2.dp)
                        )
                        Text(
                            text = "কর্মচারী রেজিস্ট্রেশন শুধুমাত্র রেস্তোরাঁ অ্যাডমিন ডেস্কে হয়। Employee registration is managed exclusively by Restaurant Administrator.",
                            style = MaterialTheme.typography.labelSmall,
                            color = CharcoalText,
                            fontSize = 11.sp
                        )
                    }
                }

                if (errorMessage != null) {
                    Surface(
                        color = Color(0xFFFFEBEE),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF9A9A)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ErrorOutline,
                                contentDescription = null,
                                tint = Color(0xFFC62828),
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = errorMessage!!,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFC62828)
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = staffInput,
                    onValueChange = {
                        staffInput = it
                        errorMessage = null
                    },
                    label = { Text("Registered Mobile or Employee Code") },
                    leadingIcon = {
                        Icon(Icons.Default.Phone, contentDescription = null, tint = CharcoalText)
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("employee_login_input")
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        errorMessage = null
                    },
                    label = { Text("Staff Password (পাসওয়ার্ড)") },
                    leadingIcon = {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = CharcoalText)
                    },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = null
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("employee_login_password")
                )

                // Selectable Staff quick chips
                Text(
                    text = "Select Team Member to Autofill:",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MutedText
                )

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    employees.take(3).forEach { emp ->
                        Surface(
                            onClick = {
                                staffInput = emp.mobile
                                password = emp.loginPassword
                                errorMessage = null
                            },
                            shape = RoundedCornerShape(8.dp),
                            color = SageGreenLight.copy(alpha = 0.5f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, WarmBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = "${emp.fullName} (${emp.role})",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold,
                                        color = CharcoalText
                                    )
                                    Text(
                                        text = "${emp.mobile} · Pass: ${emp.loginPassword}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = ForestGreen,
                                        fontSize = 11.sp
                                    )
                                }
                                Icon(
                                    imageVector = Icons.Default.ArrowForwardIos,
                                    contentDescription = null,
                                    modifier = Modifier.size(12.dp),
                                    tint = CharcoalText
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val (emp, msg) = BhojonRepository.loginEmployee(staffInput, password)
                    if (emp != null) {
                        onSuccess(emp)
                    } else {
                        errorMessage = msg
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = CharcoalText),
                modifier = Modifier.testTag("employee_login_submit_button")
            ) {
                Text("Sign In to Shift")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = CharcoalText)
            }
        },
        containerColor = WarmCardBg
    )
}

@Composable
fun AdminLoginDialog(
    onSuccess: () -> Unit,
    onDismiss: () -> Unit
) {
    var userId by remember { mutableStateOf("Administrator") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Terracotta.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AdminPanelSettings,
                        contentDescription = null,
                        tint = Terracotta,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Column {
                    Text(
                        text = "Restaurant Desk",
                        style = MaterialTheme.typography.titleLarge,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        color = Terracotta
                    )
                    Text(
                        text = "ম্যানেজার অ্যাডমিন কন্ট্রোল",
                        style = MaterialTheme.typography.labelSmall,
                        color = MutedText
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Info Banner: Fixed credentials note
                Surface(
                    color = TerracottaLight,
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Terracotta.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = Terracotta,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Restricted Administrator Terminal",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = Terracotta
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Admin sudhu ekta hobe: User ID 'Administrator' r Password 'Admin1234'.",
                            style = MaterialTheme.typography.bodySmall,
                            color = CharcoalText,
                            fontSize = 11.sp
                        )
                    }
                }

                if (errorMessage != null) {
                    Surface(
                        color = Color(0xFFFFEBEE),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF9A9A)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ErrorOutline,
                                contentDescription = null,
                                tint = Color(0xFFC62828),
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = errorMessage!!,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFC62828)
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = userId,
                    onValueChange = {
                        userId = it
                        errorMessage = null
                    },
                    label = { Text("Admin User ID") },
                    leadingIcon = {
                        Icon(Icons.Default.AccountCircle, contentDescription = null, tint = Terracotta)
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("admin_login_userid")
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        errorMessage = null
                    },
                    label = { Text("Admin Password") },
                    leadingIcon = {
                        Icon(Icons.Default.Key, contentDescription = null, tint = Terracotta)
                    },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = null
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("admin_login_password")
                )

                // Quick autofill chip
                Surface(
                    onClick = {
                        userId = "Administrator"
                        password = "Admin1234"
                        errorMessage = null
                    },
                    shape = RoundedCornerShape(8.dp),
                    color = SageGreenLight,
                    border = androidx.compose.foundation.BorderStroke(1.dp, ForestGreen.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.TouchApp,
                            contentDescription = null,
                            tint = ForestGreen,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Tap to autofill: Administrator / Admin1234",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = ForestGreen
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val (success, msg) = BhojonRepository.loginAdmin(userId, password)
                    if (success) {
                        onSuccess()
                    } else {
                        errorMessage = msg
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Terracotta),
                modifier = Modifier.testTag("admin_login_submit_button")
            ) {
                Text("Authorize & Enter")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = CharcoalText)
            }
        },
        containerColor = WarmCardBg
    )
}
