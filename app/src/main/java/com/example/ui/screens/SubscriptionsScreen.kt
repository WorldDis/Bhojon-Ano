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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BhojonRepository
import com.example.model.CustomerSubscription
import com.example.model.SubscriptionPlan
import com.example.ui.theme.*

@Composable
fun SubscriptionsScreen(
    modifier: Modifier = Modifier
) {
    val plans by BhojonRepository.subscriptionPlans.collectAsState()
    val activeSubscriptions by BhojonRepository.customerSubscriptions.collectAsState()

    var selectedPlanToSubscribe by remember { mutableStateOf<SubscriptionPlan?>(null) }
    var customerName by remember { mutableStateOf("Debashis Mukherjee") }
    var mobile by remember { mutableStateOf("+91 98315 22001") }
    var address by remember { mutableStateOf("12/A Raja Subodh Mallick Road, Kolkata") }
    var successNotice by remember { mutableStateOf<String?>(null) }

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
            // Header Banner
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = SageGreenLight,
                    border = androidx.compose.foundation.BorderStroke(1.dp, ForestGreen.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "NEVER WORRY ABOUT COOKING · দৈনিক খাবারের প্ল্যান",
                            style = MaterialTheme.typography.labelSmall,
                            color = ForestGreen,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Weekly & Monthly Subscriptions",
                            style = MaterialTheme.typography.headlineMedium,
                            fontFamily = FontFamily.Serif,
                            color = CharcoalText
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Enjoy hot, authentic Bengali meals delivered straight to your door at fixed meal slots every single day. Prepared fresh by Head Chef Subhashish.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MutedText,
                            lineHeight = 20.sp
                        )
                    }
                }
            }

            if (successNotice != null) {
                item {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = SuccessGreenLight,
                        border = androidx.compose.foundation.BorderStroke(1.dp, SuccessGreen.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = SuccessGreen
                            )
                            Text(
                                text = successNotice!!,
                                style = MaterialTheme.typography.bodySmall,
                                color = SuccessGreen,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Active Customer Subscriptions
            if (activeSubscriptions.isNotEmpty()) {
                item {
                    Text(
                        text = "YOUR ACTIVE MEAL PLANS · আপনার সক্রিয় প্ল্যান",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MutedText
                    )
                }

                items(activeSubscriptions) { sub ->
                    ActiveSubscriptionCard(sub)
                }
            }

            // Available Plans
            item {
                Text(
                    text = "EXPLORE SUBSCRIPTION PLANS",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MutedText,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            items(plans) { plan ->
                PlanCard(
                    plan = plan,
                    onSubscribe = { selectedPlanToSubscribe = plan }
                )
            }
        }

        // Subscribe Dialog
        selectedPlanToSubscribe?.let { plan ->
            AlertDialog(
                onDismissRequest = { selectedPlanToSubscribe = null },
                title = {
                    Column {
                        Text(
                            text = "Subscribe to ${plan.name}",
                            style = MaterialTheme.typography.titleLarge,
                            fontFamily = FontFamily.Serif
                        )
                        Text(
                            text = "₹${plan.price.toInt()} · ${plan.duration}",
                            style = MaterialTheme.typography.titleSmall,
                            color = ForestGreen,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "Includes: ${plan.includedMeals}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MutedText
                        )
                        OutlinedTextField(
                            value = customerName,
                            onValueChange = { customerName = it },
                            label = { Text("Customer Name") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = mobile,
                            onValueChange = { mobile = it },
                            label = { Text("Mobile Number") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = address,
                            onValueChange = { address = it },
                            label = { Text("Daily Delivery Address") },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 3
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (customerName.isNotBlank() && mobile.isNotBlank() && address.isNotBlank()) {
                                BhojonRepository.addCustomerSubscription(
                                    planId = plan.id,
                                    customerName = customerName.trim(),
                                    mobile = mobile.trim(),
                                    address = address.trim()
                                )
                                successNotice = "Subscribed to ${plan.name}! Starting today."
                                selectedPlanToSubscribe = null
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("confirm_subscription_button")
                    ) {
                        Text("Confirm & Start")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { selectedPlanToSubscribe = null }) {
                        Text("Cancel", color = MutedText)
                    }
                },
                containerColor = WarmCardBg
            )
        }
    }
}

@Composable
private fun ActiveSubscriptionCard(sub: CustomerSubscription) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = WarmCardBg,
        border = androidx.compose.foundation.BorderStroke(1.5.dp, ForestGreen.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = SuccessGreenLight,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "● ${sub.status}",
                        style = MaterialTheme.typography.labelSmall,
                        color = SuccessGreen,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
                Text(
                    text = "Valid: ${sub.startDate} to ${sub.endDate}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MutedText
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = sub.planName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = CharcoalText
            )

            Text(
                text = "Subscriber: ${sub.customerName} (${sub.mobile})",
                style = MaterialTheme.typography.bodySmall,
                color = MutedText
            )
            Text(
                text = "Delivery to: ${sub.deliveryAddress}",
                style = MaterialTheme.typography.bodySmall,
                color = MutedText
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Meal Progress
            val progress = sub.mealsDelivered.toFloat() / sub.totalMeals.toFloat()
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = ForestGreen,
                trackColor = SageGreenLight
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${sub.mealsDelivered} of ${sub.totalMeals} meals served",
                    style = MaterialTheme.typography.labelSmall,
                    color = CharcoalText,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${sub.totalMeals - sub.mealsDelivered} meals remaining",
                    style = MaterialTheme.typography.labelSmall,
                    color = Terracotta,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun PlanCard(
    plan: SubscriptionPlan,
    onSubscribe: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = WarmCardBg,
        border = androidx.compose.foundation.BorderStroke(1.dp, WarmBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = plan.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = CharcoalText
                    )
                    Text(
                        text = plan.bengaliName,
                        style = MaterialTheme.typography.bodySmall,
                        color = Terracotta,
                        fontStyle = FontStyle.Italic
                    )
                }
                Surface(
                    color = MustardGoldLight,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = plan.duration,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = CharcoalText,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = plan.includedMeals,
                style = MaterialTheme.typography.bodyMedium,
                color = MutedText
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "₹${plan.price.toInt()}",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = ForestGreen
                    )
                    Text(
                        text = "for ${plan.mealCount} meals",
                        style = MaterialTheme.typography.labelSmall,
                        color = MutedText
                    )
                }

                Button(
                    onClick = onSubscribe,
                    colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("subscribe_plan_${plan.id}")
                ) {
                    Text("Subscribe Now")
                }
            }
        }
    }
}
