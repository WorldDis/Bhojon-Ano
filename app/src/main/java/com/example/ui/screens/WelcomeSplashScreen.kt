package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.*

/**
 * Customer App Splash & Onboarding Screen matching the center smartphone mockup
 * from the uploaded visual direction.
 */
@Composable
fun WelcomeSplashScreen(
    onGetStarted: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        DeepForestGreen,
                        DeepForestGreenDark,
                        Color(0xFF0A1F15)
                    )
                )
            )
    ) {
        // Decorative background aura & subtle leaves
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 40.dp)
                .size(320.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            MustardGold.copy(alpha = 0.15f),
                            Color.Transparent
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(top = 48.dp, bottom = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Section: Emblem + Wordmark
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Large circular Brass Thali Emblem with dual golden border rings
                BhojonEmblemBadge(
                    size = 190.dp,
                    showRings = true,
                    modifier = Modifier.testTag("welcome_thali_emblem")
                )

                Spacer(modifier = Modifier.height(22.dp))

                // Wordmark: Bhojon in cream/white, .food in Mustard Gold
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Bhojon",
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 38.sp,
                        color = Color(0xFFFFF9EE),
                        letterSpacing = (-0.5).sp
                    )
                    Text(
                        text = ".food",
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 38.sp,
                        color = MustardGold,
                        letterSpacing = (-0.5).sp
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Golden Tagline
                Surface(
                    color = Color.Black.copy(alpha = 0.25f),
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MustardGold.copy(alpha = 0.4f))
                ) {
                    Text(
                        text = "— ${BhojonBrand.TAGLINE} —",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MustardGold,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            // Center Welcome Card
            Surface(
                shape = RoundedCornerShape(22.dp),
                color = WarmCreamBg,
                shadowElevation = 8.dp,
                border = androidx.compose.foundation.BorderStroke(1.5.dp, MustardGold.copy(alpha = 0.6f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Welcome to Bhojon.food",
                        style = MaterialTheme.typography.titleLarge,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        color = CharcoalText,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Authentic Bengali Food, Delivered Fresh to Your Home",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MutedText,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Prominent Terracotta "Get Started" Action Button
                    Button(
                        onClick = onGetStarted,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = FoodTerracotta,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("get_started_button")
                    ) {
                        Text(
                            text = "Get Started",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Bottom Trust Strip
            BhojonTrustBadgesStrip(isDark = true)
        }
    }
}
