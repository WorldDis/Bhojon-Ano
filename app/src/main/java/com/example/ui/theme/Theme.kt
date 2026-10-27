package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = ForestGreen,
    onPrimary = Color(0xFFFFF8E8),
    primaryContainer = SageGreenLight,
    onPrimaryContainer = ForestGreenDark,
    secondary = Terracotta,
    onSecondary = Color.White,
    secondaryContainer = TerracottaLight,
    onSecondaryContainer = TerracottaDark,
    tertiary = MustardGold,
    onTertiary = CharcoalText,
    tertiaryContainer = MustardGoldLight,
    onTertiaryContainer = CharcoalText,
    background = WarmCreamBg,
    onBackground = CharcoalText,
    surface = WarmCardBg,
    onSurface = CharcoalText,
    surfaceVariant = Color(0xFFF3ECE0),
    onSurfaceVariant = MutedText,
    outline = WarmBorder,
    outlineVariant = Color(0xFFDED5C3)
)

@Composable
fun BhojonTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
