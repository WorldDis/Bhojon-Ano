package com.example.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R

object BhojonBrand {
    const val NAME = "Bhojon.food"
    const val TAGLINE = "Gharer Swad, Sabar Jonno"
    const val CONCEPT = "Authentic Bengali home-style food delivered conveniently."
    const val LOCATION = "Kolkata, West Bengal"
}

/**
 * Ornate round Bengali thali emblem badge matching the reference visual identity.
 * Features the brass thali with dual golden metallic border rings.
 */
@Composable
fun BhojonEmblemBadge(
    modifier: Modifier = Modifier,
    size: Dp = 120.dp,
    showRings: Boolean = true
) {
    Box(
        modifier = modifier
            .size(size)
            .shadow(12.dp, CircleShape)
            .then(
                if (showRings) {
                    Modifier
                        .border(3.dp, MustardGold, CircleShape)
                        .padding(4.dp)
                        .border(1.5.dp, MustardGold.copy(alpha = 0.6f), CircleShape)
                } else Modifier
            )
            .clip(CircleShape)
            .background(DeepForestGreen),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.img_bhojon_emblem),
            contentDescription = "Bhojon.food Brass Thali Emblem",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}

/**
 * Main Horizontal Brand Logo for Bhojon.food.
 * Supports both dark (for DeepForestGreen app headers / splash) and light (for WarmCreamBg / invoices) surfaces.
 */
@Composable
fun BhojonBrandLogo(
    modifier: Modifier = Modifier,
    isDarkTheme: Boolean = false,
    emblemSize: Dp = 38.dp,
    showTagline: Boolean = true
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier
    ) {
        // Round Thali Emblem
        Box(
            modifier = Modifier
                .size(emblemSize)
                .clip(CircleShape)
                .border(1.5.dp, MustardGold, CircleShape)
                .background(DeepForestGreen),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_bhojon_emblem),
                contentDescription = "Bhojon Emblem",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        // Wordmark & Tagline
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Bhojon",
                    style = MaterialTheme.typography.titleLarge,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = (emblemSize.value * 0.52).sp,
                    color = if (isDarkTheme) Color(0xFFFFF9EE) else CharcoalText,
                    letterSpacing = (-0.3).sp
                )
                Text(
                    text = ".food",
                    style = MaterialTheme.typography.titleLarge,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = (emblemSize.value * 0.52).sp,
                    color = MustardGold,
                    letterSpacing = (-0.3).sp
                )
            }
            if (showTagline) {
                Text(
                    text = "— ${BhojonBrand.TAGLINE} —",
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = (emblemSize.value * 0.24).sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isDarkTheme) MustardGold else MutedText,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}

/**
 * Reusable Trust and Quality Badges Strip matching the bottom banner of the reference.
 * "Authentic Bengali Food | Fresh Ingredients | Safe & Secure Payments | Fast & Reliable Delivery | Real Food Real Happiness"
 */
@Composable
fun BhojonTrustBadgesStrip(
    modifier: Modifier = Modifier,
    isDark: Boolean = true
) {
    Surface(
        color = if (isDark) DeepForestGreenDark else WarmCardBg,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isDark) MustardGold.copy(alpha = 0.25f) else WarmBorder
        ),
        shape = RoundedCornerShape(14.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TrustBadgeItem(
                icon = Icons.Outlined.DinnerDining,
                label = "Authentic\nBengali Food",
                isDark = isDark
            )
            TrustBadgeItem(
                icon = Icons.Outlined.Eco,
                label = "Fresh\nIngredients",
                isDark = isDark
            )
            TrustBadgeItem(
                icon = Icons.Outlined.Shield,
                label = "Safe & Secure\nPayments",
                isDark = isDark
            )
            TrustBadgeItem(
                icon = Icons.Outlined.ElectricMoped,
                label = "Fast & Reliable\nDelivery",
                isDark = isDark
            )
            TrustBadgeItem(
                icon = Icons.Outlined.Favorite,
                label = "Real Food\nReal Happiness",
                isDark = isDark
            )
        }
    }
}

@Composable
private fun TrustBadgeItem(
    icon: ImageVector,
    label: String,
    isDark: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MustardGold,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = label,
            fontSize = 9.sp,
            fontWeight = FontWeight.Medium,
            color = if (isDark) Color(0xFFE0EADF) else CharcoalText,
            lineHeight = 11.sp,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}
