package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val YieldFlowColorScheme = darkColorScheme(
  primary = EmeraldNeon,
  onPrimary = Color(0xFF022C22),
  primaryContainer = EmeraldDark,
  onPrimaryContainer = EmeraldLight,
  secondary = CyanNeon,
  onSecondary = Color(0xFF082F49),
  secondaryContainer = Color(0xFF164E63),
  onSecondaryContainer = CyanLight,
  tertiary = CryptoBtcGold,
  onTertiary = Color(0xFF451A03),
  background = VaultDarkNavy,
  onBackground = TextPrimary,
  surface = VaultCardDark,
  onSurface = TextPrimary,
  surfaceVariant = VaultCardElevated,
  onSurfaceVariant = TextSecondary,
  outline = VaultBorder,
  error = AlertRed,
  onError = Color.White
)

@Composable
fun MyApplicationTheme(
  content: @Composable () -> Unit
) {
  MaterialTheme(
    colorScheme = YieldFlowColorScheme,
    typography = Typography,
    content = content
  )
}
