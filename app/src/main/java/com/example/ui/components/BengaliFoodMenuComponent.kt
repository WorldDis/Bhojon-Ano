package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.CartItem
import com.example.model.MenuItem
import com.example.ui.theme.*

/**
 * Reusable Bengali Food Menu Component in Jetpack Compose.
 * Displays Bengali culinary items with photography, prices, Bengali typography,
 * category filters, dietary indicators, and prominent 'Add to Cart' actions.
 */
@Composable
fun BengaliFoodMenuComponent(
    items: List<MenuItem>,
    cart: List<CartItem>,
    onAddToCart: (MenuItem) -> Unit,
    onDecrement: (MenuItem) -> Unit,
    modifier: Modifier = Modifier,
    headerContent: (@Composable () -> Unit)? = null
) {
    var selectedCategory by remember { mutableStateOf("All") }
    var selectedDietary by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }

    val categories = listOf("All", "Morning Tiffin", "Lunch Thali", "Dinner")

    val filteredItems = remember(items, selectedCategory, selectedDietary, searchQuery) {
        items.filter { item ->
            val matchesCategory = selectedCategory == "All" || item.category.equals(selectedCategory, ignoreCase = true)
            val matchesDietary = selectedDietary == "All" || item.dietary.contains(selectedDietary, ignoreCase = true)
            val matchesSearch = searchQuery.isBlank() ||
                    item.name.contains(searchQuery, ignoreCase = true) ||
                    item.bengaliName.contains(searchQuery, ignoreCase = true) ||
                    item.description.contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesDietary && matchesSearch
        }
    }

    LazyColumn(
        contentPadding = PaddingValues(bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier.fillMaxSize()
    ) {
        if (headerContent != null) {
            item {
                headerContent()
            }
        }

        // Search & Filter Header
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                // Search Input
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            text = "Search Bengali dishes (e.g. thali, luchi, fish)...",
                            fontSize = 13.sp,
                            color = MutedText
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Terracotta
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear",
                                    tint = MutedText
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ForestGreen,
                        unfocusedBorderColor = WarmBorder,
                        focusedContainerColor = WarmCardBg,
                        unfocusedContainerColor = WarmCardBg
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("menu_search_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Meal Categories
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(categories) { category ->
                        val isSelected = category == selectedCategory
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedCategory = category },
                            label = {
                                Text(
                                    text = category,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 13.sp
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ForestGreen,
                                selectedLabelColor = Color(0xFFFFF8E8),
                                containerColor = WarmCardBg,
                                labelColor = CharcoalText
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = if (isSelected) ForestGreen else WarmBorder
                            ),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.testTag("filter_chip_$category")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Dietary Filters
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Diet:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MutedText,
                        fontWeight = FontWeight.Bold
                    )
                    listOf("All", "Pure Veg", "Non-Veg").forEach { diet ->
                        val isSel = diet == selectedDietary
                        Surface(
                            onClick = { selectedDietary = diet },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSel) SageGreenLight else Color.Transparent,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSel) ForestGreen else WarmBorder.copy(alpha = 0.5f)
                            )
                        ) {
                            Text(
                                text = diet,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSel) ForestGreen else MutedText,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        // Section Title & Item Count
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (selectedCategory == "All") "TODAY'S SPECIAL DISHES" else selectedCategory.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = Terracotta,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Ghar Jaisa Swad · Bengali Home Meals",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = CharcoalText,
                        fontFamily = FontFamily.Serif
                    )
                }
                Text(
                    text = "${filteredItems.size} dishes",
                    style = MaterialTheme.typography.labelMedium,
                    color = MutedText
                )
            }
        }

        // Empty Search State
        if (filteredItems.isEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = WarmCardBg,
                    border = androidx.compose.foundation.BorderStroke(1.dp, WarmBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.SearchOff,
                            contentDescription = null,
                            tint = Terracotta,
                            modifier = Modifier.size(44.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No Bengali dishes found",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = CharcoalText
                        )
                        Text(
                            text = "Try switching the category or clearing your search term.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MutedText
                        )
                    }
                }
            }
        } else {
            // Render Bengali Food Item Cards
            items(
                items = filteredItems,
                key = { it.id }
            ) { dish ->
                val quantityInCart = cart.find { it.menuItem.id == dish.id }?.quantity ?: 0
                BengaliFoodItemCard(
                    dish = dish,
                    quantityInCart = quantityInCart,
                    onAddToCart = { onAddToCart(dish) },
                    onDecrement = { onDecrement(dish) },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }
}

/**
 * Individual Bengali Food Item Card with Image, Bengali Name,
 * Serving Time, Dietary Tag, Price, and prominent 'Add to Cart' action.
 */
@Composable
fun BengaliFoodItemCard(
    dish: MenuItem,
    quantityInCart: Int,
    onAddToCart: () -> Unit,
    onDecrement: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = WarmCardBg,
        border = androidx.compose.foundation.BorderStroke(1.dp, WarmBorder),
        shadowElevation = 2.dp,
        modifier = modifier
            .fillMaxWidth()
            .testTag("bengali_dish_card_${dish.id}")
    ) {
        Column {
            // Food Image Container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
                    .background(Color(0xFFE8DFCD))
            ) {
                if (dish.imageRes != null) {
                    Image(
                        painter = painterResource(id = dish.imageRes),
                        contentDescription = dish.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    // Rustic Bengali kitchen illustration fallback
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0xFFDED3BF),
                                        Color(0xFFC7BBA3)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Outlined.Restaurant,
                                contentDescription = null,
                                tint = ForestGreenDark,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "BHOJON KITCHEN FRESH",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = ForestGreenDark
                            )
                        }
                    }
                }

                // Scrim overlay gradient for readable badges
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.35f),
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.55f)
                                )
                            )
                        )
                )

                // Top badges row (Dietary + Special Highlight)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Dietary Badge
                    Surface(
                        color = if (dish.dietary == "Pure Veg") Color(0xFF1B5E20) else Color(0xFFB71C1C),
                        shape = RoundedCornerShape(6.dp),
                        shadowElevation = 2.dp
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                            )
                            Text(
                                text = dish.dietary,
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Highlight badge
                    if (dish.highlight.isNotBlank()) {
                        Surface(
                            color = MustardGold,
                            shape = RoundedCornerShape(12.dp),
                            shadowElevation = 2.dp
                        ) {
                            Text(
                                text = dish.highlight,
                                style = MaterialTheme.typography.labelSmall,
                                color = CharcoalText,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 9.dp, vertical = 3.dp)
                            )
                        }
                    }
                }

                // Bottom badge on image: Serving time slot
                Surface(
                    color = Color.Black.copy(alpha = 0.65f),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Schedule,
                            contentDescription = null,
                            tint = Color(0xFFFFF7E6),
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = "${dish.category} · ${dish.servingTime}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFFFFF7E6),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Food Card Content Area
            Column(modifier = Modifier.padding(16.dp)) {
                // Dish Name Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = dish.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = CharcoalText
                        )
                        Text(
                            text = dish.bengaliName,
                            style = MaterialTheme.typography.bodySmall,
                            color = Terracotta,
                            fontStyle = FontStyle.Italic,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // Price
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "₹${dish.price.toInt()}",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = ForestGreen
                        )
                        Text(
                            text = "per portion",
                            style = MaterialTheme.typography.labelSmall,
                            color = MutedText
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Description
                Text(
                    text = dish.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MutedText,
                    lineHeight = 19.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                HorizontalDivider(color = WarmBorder.copy(alpha = 0.7f))

                Spacer(modifier = Modifier.height(12.dp))

                // Action Row: Fresh Status & Add to Cart Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (dish.available) SuccessGreen else MaterialTheme.colorScheme.error)
                        )
                        Text(
                            text = if (dish.available) "Freshly Cooked Today" else "Sold Out",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (dish.available) SuccessGreen else MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (dish.available) {
                        AnimatedContent(
                            targetState = quantityInCart,
                            label = "CartAction"
                        ) { qty ->
                            if (qty > 0) {
                                QuantityStepper(
                                    quantity = qty,
                                    onIncrement = onAddToCart,
                                    onDecrement = onDecrement
                                )
                            } else {
                                Button(
                                    onClick = onAddToCart,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = ForestGreen,
                                        contentColor = Color(0xFFFFF8E8)
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                                    modifier = Modifier.testTag("add_to_cart_${dish.id}")
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.AddShoppingCart,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Add to Cart",
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.labelMedium
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
