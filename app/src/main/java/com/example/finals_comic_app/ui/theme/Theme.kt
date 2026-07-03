package com.example.finals_comic_app.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryBlue,
    secondary = PurpleGrey80,
    tertiary = Pink80,
    background = AppBackground,
    surface = AppSurface,
    onBackground = OnAppBackground,
    onSurface = OnAppBackground
)

private val LightColorScheme = darkColorScheme( // Use dark scheme even for light for consistency with requirements
    primary = PrimaryBlue,
    secondary = PurpleGrey80,
    tertiary = Pink80,
    background = AppBackground,
    surface = AppSurface,
    onBackground = OnAppBackground,
    onSurface = OnAppBackground
)

@Composable
fun Finals_comic_appTheme(
    darkTheme: Boolean = true, // Force dark theme
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false, // Disable dynamic color to use custom palette
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