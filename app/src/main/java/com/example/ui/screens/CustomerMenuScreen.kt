package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BhojonRepository
import com.example.model.Order
import com.example.ui.components.BengaliFoodMenuComponent
import com.example.ui.theme.*

@Composable
fun CustomerMenuScreen(
    onOpenCart: () -> Unit,
    onTrackOrder: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val menuItems by BhojonRepository.menuItems.collectAsState()
    val cart by BhojonRepository.cart.collectAsState()
    val orders by BhojonRepository.orders.collectAsState()

    val cartCount = remember(cart) { cart.sumOf { it.quantity } }
    val cartTotal = remember(cart) { cart.sumOf { it.menuItem.price * it.quantity } }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(WarmCreamBg)
    ) {
        // Bengali Food Menu Component with food images, prices, and Add to Cart action
        BengaliFoodMenuComponent(
            items = menuItems,
            cart = cart,
            onAddToCart = { dish -> BhojonRepository.addToCart(dish) },
            onDecrement = { dish -> BhojonRepository.decrementCart(dish) },
            headerContent = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    HeroHeader(
                        onTrackOrderClick = {
                            val recentOrder = orders.firstOrNull()
                            if (recentOrder != null) onTrackOrder(recentOrder.orderCode)
                        }
                    )
                    QuickOrderTracker(
                        orders = orders,
                        onTrackOrder = onTrackOrder
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        )

        // Floating Bottom Cart Banner
        AnimatedVisibility(
            visible = cartCount > 0,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Surface(
                onClick = onOpenCart,
                shape = RoundedCornerShape(16.dp),
                color = ForestGreen,
                shadowElevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("floating_cart_bar")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFFF8E8)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = cartCount.toString(),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = ForestGreen
                            )
                        }
                        Column {
                            Text(
                                text = "$cartCount items in basket",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Total: ₹${cartTotal.toInt()} · Free Delivery",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFFD3E4D0)
                            )
                        }
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "View Basket",
                            style = MaterialTheme.typography.titleSmall,
                            color = Color(0xFFFFF8E8),
                            fontWeight = FontWeight.Bold
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Review",
                            tint = Color(0xFFFFF8E8),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HeroHeader(
    onTrackOrderClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFEBE4D2),
                        WarmCreamBg
                    )
                )
            )
            .padding(horizontal = 20.dp, vertical = 20.dp)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(SuccessGreen)
                )
                Text(
                    text = "COOKED FRESH, RIGHT HERE · লাইভ কিচেন খোলা",
                    style = MaterialTheme.typography.labelSmall,
                    color = ForestGreen,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "A little taste of home.",
                style = MaterialTheme.typography.headlineLarge,
                color = CharcoalText,
                fontFamily = FontFamily.Serif
            )
            Text(
                text = "Ghar Jaisa Swad — Freshly Cooked, Daily",
                style = MaterialTheme.typography.titleMedium,
                color = Terracotta,
                fontStyle = FontStyle.Italic
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "From our Bengali kitchen to your doorstep. Authentic homestyle meals, sonamug dal, fresh thalis, crisp luchis, and traditional dhaba dishes prepared with honest care.",
                style = MaterialTheme.typography.bodyMedium,
                color = MutedText,
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
private fun QuickOrderTracker(
    orders: List<Order>,
    onTrackOrder: (String) -> Unit
) {
    var trackCodeInput by remember { mutableStateOf("") }
    var trackError by remember { mutableStateOf<String?>(null) }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = WarmCardBg,
        border = androidx.compose.foundation.BorderStroke(1.dp, WarmBorder),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = null,
                    tint = Terracotta,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "Track Live Order · অর্ডার ট্র্যাক করুন",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = CharcoalText
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = trackCodeInput,
                    onValueChange = {
                        trackCodeInput = it
                        trackError = null
                    },
                    placeholder = { Text("e.g. BJ-20261014-...", fontSize = 13.sp) },
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("track_code_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ForestGreen,
                        unfocusedBorderColor = WarmBorder
                    )
                )
                Button(
                    onClick = {
                        val code = trackCodeInput.trim()
                        if (code.isBlank()) {
                            val latest = orders.firstOrNull()?.orderCode
                            if (latest != null) {
                                onTrackOrder(latest)
                            } else {
                                trackError = "Please enter an order code"
                            }
                        } else {
                            onTrackOrder(code)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("track_order_button")
                ) {
                    Text("Track", fontWeight = FontWeight.Bold)
                }
            }
            if (trackError != null) {
                Text(
                    text = trackError!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            if (orders.isNotEmpty()) {
                Text(
                    text = "Recent: ${orders.first().orderCode} (${orders.first().status.displayName})",
                    style = MaterialTheme.typography.labelSmall,
                    color = Terracotta,
                    modifier = Modifier
                        .padding(top = 6.dp)
                        .clickable {
                            onTrackOrder(orders.first().orderCode)
                        }
                )
            }
        }
    }
}
