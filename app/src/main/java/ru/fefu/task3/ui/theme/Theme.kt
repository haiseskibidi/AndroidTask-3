package ru.fefu.task3.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private val DarkColorScheme = darkColorScheme(
    primary = BurgundyPrimary,
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = BurgundyTrack,
    onPrimaryContainer = Color(0xFFFFDAD9),
    secondary = BurgundyLight,
    onSecondary = Color(0xFF40000A),
    secondaryContainer = Color(0xFF2C2C2E),
    onSecondaryContainer = Color(0xFFF5F5F5),
    tertiary = BurgundyLight,
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = BurgundyTrack,
    onTertiaryContainer = Color(0xFFFFDAD9),
    background = DarkBackground,
    surface = DarkSurface,
    onBackground = Color(0xFFF5F5F5),
    onSurface = Color(0xFFF5F5F5),
    surfaceVariant = Color(0xFF2C2C2E),
    onSurfaceVariant = Color(0xFFC4C6D0),
    outline = Color(0xFF8E9099),
    error = Color(0xFFE53935),
    onError = Color(0xFFFFFFFF)
)

private val LightColorScheme = lightColorScheme(
    primary = BurgundyPrimary,
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFFFDAD9),
    onPrimaryContainer = Color(0xFF40000A),
    secondary = Color(0xFF775656),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFE7E0EC),
    onSecondaryContainer = Color(0xFF49454F),
    tertiary = BurgundyPrimary,
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFFFDAD9),
    onTertiaryContainer = Color(0xFF40000A),
    background = LightBackground,
    surface = LightSurface,
    onBackground = DarkBackground,
    onSurface = DarkBackground,
    surfaceVariant = Color(0xFFE7E0EC),
    onSurfaceVariant = Color(0xFF49454F),
    outline = Color(0xFF79747E),
    error = Color(0xFFBA1A1A),
    onError = Color(0xFFFFFFFF)
)

data class Spacing(
    val spacing2: Dp = 2.dp,
    val extraSmall: Dp = 4.dp,
    val spacing6: Dp = 6.dp,
    val small: Dp = 8.dp,
    val spacing12: Dp = 12.dp,
    val medium: Dp = 16.dp,
    val spacing20: Dp = 20.dp,
    val large: Dp = 24.dp,
    val extraLarge: Dp = 32.dp,
    val starSize: Dp = 36.dp,
    val starSizeSmall: Dp = 14.dp,
    val detailsImageHeight: Dp = 350.dp,
    val settingsSpacer: Dp = 16.dp,
    val cardHeight: Dp = 220.dp
)

val LocalSpacing = staticCompositionLocalOf { Spacing() }

val MaterialTheme.spacing: Spacing
    @Composable
    @androidx.compose.runtime.ReadOnlyComposable
    get() = LocalSpacing.current

@Composable
fun Task3Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
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

    CompositionLocalProvider(LocalSpacing provides Spacing()) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
