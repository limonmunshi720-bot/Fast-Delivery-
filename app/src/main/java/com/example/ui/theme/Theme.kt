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

private val DarkColorScheme = darkColorScheme(
    primary = FastGreenDark,
    onPrimary = Color(0xFF00381E),
    primaryContainer = Color(0xFF00522E),
    onPrimaryContainer = Color(0xFFA5E6BA),
    secondary = FastOrangeDark,
    onSecondary = Color(0xFF451A00),
    secondaryContainer = Color(0xFF632800),
    onSecondaryContainer = Color(0xFFFFD180),
    background = BackgroundDark,
    surface = SurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onBackground = Color(0xFFE1E5E1),
    onSurface = Color(0xFFE1E5E1),
    outline = Color(0xFF49544B)
)

private val LightColorScheme = lightColorScheme(
    primary = FastGreenPrimary,
    onPrimary = FastGreenOnPrimary,
    primaryContainer = FastGreenContainer,
    onPrimaryContainer = FastGreenOnContainer,
    secondary = FastOrangeAccent,
    onSecondary = Color.White,
    secondaryContainer = FastOrangeContainer,
    onSecondaryContainer = FastOrangeOnContainer,
    tertiary = FastBlueTertiary,
    onTertiary = Color.White,
    tertiaryContainer = FastBlueContainer,
    background = BackgroundLight,
    surface = SurfaceLight,
    surfaceVariant = SurfaceVariantLight,
    onBackground = Color(0xFF181C19),
    onSurface = Color(0xFF181C19),
    outline = OutlineLight
)

@Composable
fun FastDeliveryTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Set to false by default to ensure Fast Delivery signature branding
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
