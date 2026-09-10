package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = MoraGreenPrimaryDark,
    onPrimary = Color(0xFF042F1C),
    primaryContainer = MoraEmeraldDark,
    onPrimaryContainer = Color(0xFFA7F3D0),
    secondary = MoraGoldSecondaryDark,
    onSecondary = Color(0xFF451A03),
    secondaryContainer = Color(0xFF78350F),
    onSecondaryContainer = Color(0xFFFDE68A),
    tertiary = MoraTeal,
    background = MoraDarkBg,
    onBackground = MoraDarkOnSurface,
    surface = MoraDarkSurface,
    onSurface = MoraDarkOnSurface,
    surfaceVariant = MoraDarkSurfaceVariant,
    onSurfaceVariant = Color(0xFFC0D5C9),
    outline = MoraDarkOutline,
    error = AlertRedDark,
    onError = Color.Black,
    errorContainer = AlertRedDarkBg,
    onErrorContainer = Color(0xFFFFDAD6)
  )

private val LightColorScheme =
  lightColorScheme(
    primary = MoraGreenPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFCCFBF1),
    onPrimaryContainer = Color(0xFF115E59),
    secondary = MoraGoldSecondary,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFEF3C7),
    onSecondaryContainer = Color(0xFF92400E),
    tertiary = MoraTeal,
    background = MoraLightBg,
    onBackground = MoraLightOnSurface,
    surface = MoraLightSurface,
    onSurface = MoraLightOnSurface,
    surfaceVariant = MoraLightSurfaceVariant,
    onSurfaceVariant = Color(0xFF335343),
    outline = MoraLightOutline,
    error = AlertRed,
    onError = Color.White,
    errorContainer = AlertRedBg,
    onErrorContainer = Color(0xFF991B1B)
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Use Mora custom brand palette consistently
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }
      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}

