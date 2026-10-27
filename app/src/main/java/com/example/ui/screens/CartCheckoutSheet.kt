package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BhojonRepository
import com.example.ui.components.QuantityStepper
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartCheckoutSheet(
    onDismiss: () -> Unit,
    onOrderPlaced: (String) -> Unit
) {
    val cart by BhojonRepository.cart.collectAsState()
    val total = remember(cart) { cart.sumOf { it.menuItem.price * it.quantity } }
    val currentCustomerUser by BhojonRepository.currentCustomerUser.collectAsState()

    var customerName by remember(currentCustomerUser) {
        mutableStateOf(currentCustomerUser?.fullName ?: "Debashis Mukherjee")
    }
    var mobile by remember(currentCustomerUser) {
        mutableStateOf(currentCustomerUser?.mobile ?: "+91 98315 22001")
    }
    var address by remember(currentCustomerUser) {
        mutableStateOf(currentCustomerUser?.deliveryAddress ?: "12/A Raja Subodh Mallick Road, Jadavpur, Kolkata")
    }
    var instructions by remember { mutableStateOf("Extra sonamug dal and green chilies, please.") }
    var selectedMealSlot by remember { mutableStateOf("Lunch Thali (1:30 PM)") }
    var selectedPayment by remember { mutableStateOf("Pay on Delivery (Cash / UPI)") }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isPlacingOrder by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = WarmCardBg,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Your Meal Basket",
                        style = MaterialTheme.typography.headlineSmall,
                        fontFamily = FontFamily.Serif,
                        color = CharcoalText
                    )
                    Text(
                        text = "আপনার খাবারের ঝুড়ি",
                        style = MaterialTheme.typography.bodySmall,
                        color = Terracotta
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (cart.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Outlined.ShoppingBag,
                            contentDescription = null,
                            tint = MutedText,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Your basket is empty",
                            style = MaterialTheme.typography.titleMedium,
                            color = CharcoalText
                        )
                        Text(
                            text = "Add some delicious Bengali thalis to get started.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MutedText
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f, fill = false),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Basket Items
                    items(cart) { entry ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = SageGreenLight.copy(alpha = 0.5f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, WarmBorder)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = entry.menuItem.name,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = CharcoalText
                                    )
                                    Text(
                                        text = "₹${entry.menuItem.price.toInt()} each",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MutedText
                                    )
                                }
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    QuantityStepper(
                                        quantity = entry.quantity,
                                        onIncrement = { BhojonRepository.addToCart(entry.menuItem) },
                                        onDecrement = { BhojonRepository.decrementCart(entry.menuItem) }
                                    )
                                    Text(
                                        text = "₹${(entry.menuItem.price * entry.quantity).toInt()}",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = ForestGreen
                                    )
                                }
                            }
                        }
                    }

                    // Delivery Details Form
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "DELIVERY & CONTACT DETAILS",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MutedText
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        OutlinedTextField(
                            value = customerName,
                            onValueChange = { customerName = it },
                            label = { Text("Your Name · আপনার নাম") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("customer_name_input"),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        OutlinedTextField(
                            value = mobile,
                            onValueChange = { mobile = it },
                            label = { Text("Mobile Number · ফোন নম্বর") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("mobile_input"),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        OutlinedTextField(
                            value = address,
                            onValueChange = { address = it },
                            label = { Text("Delivery Address · ডেলিভারি ঠিকানা") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("address_input"),
                            maxLines = 3
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        OutlinedTextField(
                            value = instructions,
                            onValueChange = { instructions = it },
                            label = { Text("Cooking / Delivery Notes (Optional)") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("instructions_input"),
                            placeholder = { Text("e.g. less spicy, extra lemon...") }
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Meal Slot",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            listOf("Morning Tiffin", "Lunch Thali", "Dinner").forEach { slot ->
                                val isSelected = selectedMealSlot.startsWith(slot)
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { selectedMealSlot = slot },
                                    label = { Text(slot, fontSize = 12.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = ForestGreen,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Payment Method",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            listOf("Pay on Delivery", "UPI Online").forEach { method ->
                                val isSelected = selectedPayment.startsWith(method)
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { selectedPayment = method },
                                    label = { Text(method, fontSize = 12.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = ForestGreen,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Bill breakdown
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = WarmCreamBg,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Items Subtotal", color = MutedText, style = MaterialTheme.typography.bodySmall)
                                    Text("₹${total.toInt()}", color = CharcoalText, style = MaterialTheme.typography.bodySmall)
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Neighborhood Delivery", color = MutedText, style = MaterialTheme.typography.bodySmall)
                                    Text("FREE", color = SuccessGreen, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                                }
                                HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp), color = WarmBorder)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Total Payable", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                    Text("₹${total.toInt()}", color = ForestGreen, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                }
                            }
                        }
                    }
                }

                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(vertical = 6.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = {
                        if (customerName.isBlank() || mobile.isBlank() || address.isBlank()) {
                            errorMessage = "Please enter your name, mobile, and delivery address."
                            return@Button
                        }
                        isPlacingOrder = true
                        val order = BhojonRepository.placeOrder(
                            customerName = customerName.trim(),
                            mobile = mobile.trim(),
                            address = address.trim(),
                            instructions = instructions.trim(),
                            mealSlot = selectedMealSlot,
                            paymentMethod = selectedPayment
                        )
                        if (order != null) {
                            onDismiss()
                            onOrderPlaced(order.orderCode)
                        } else {
                            errorMessage = "Failed to place order. Basket is empty."
                            isPlacingOrder = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("place_order_button")
                ) {
                    Text(
                        text = "Confirm & Place Order · ₹${total.toInt()}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFF8E8)
                    )
                }
            }
        }
    }
}
