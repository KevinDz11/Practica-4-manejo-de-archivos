package com.example.gestordearchivos.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// --- Colores Guinda (IPN) ---
private val GuindaPrimaryLight = Color(0xFF8C1A3A) // Guinda
private val GuindaSecondaryLight = Color(0xFF6F4E00) // Oro
private val GuindaTertiaryLight = Color(0xFF505050) // Gris
private val GuindaBackgroundLight = Color(0xFFFAF9F6) // Blanco Hueso

private val GuindaPrimaryDark = Color(0xFFD48F9F) // Guinda claro
private val GuindaSecondaryDark = Color(0xFFD4AF37) // Oro claro
private val GuindaTertiaryDark = Color(0xFFA9A9A9) // Gris claro
private val GuindaBackgroundDark = Color(0xFF1C1C1E) // Negro suave

private val GuindaLightColorScheme = lightColorScheme(
    primary = GuindaPrimaryLight,
    secondary = GuindaSecondaryLight,
    tertiary = GuindaTertiaryLight,
    background = GuindaBackgroundLight,
    surface = GuindaBackgroundLight,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1C1C),
    onSurface = Color(0xFF1C1C1C),
)

private val GuindaDarkColorScheme = darkColorScheme(
    primary = GuindaPrimaryDark,
    secondary = GuindaSecondaryDark,
    tertiary = GuindaTertiaryDark,
    background = GuindaBackgroundDark,
    surface = GuindaBackgroundDark,
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onTertiary = Color.Black,
    onBackground = Color(0xFFE6E6E6),
    onSurface = Color(0xFFE6E6E6),
)

// --- Colores Azul (ESCOM) ---
private val AzulPrimaryLight = Color(0xFF004A99) // Azul ESCOM
private val AzulSecondaryLight = Color(0xFF007BFF) // Azul brillante
private val AzulTertiaryLight = Color(0xFFB0B0B0) // Plata
private val AzulBackgroundLight = Color(0xFFF8F9FA) // Blanco grisáceo

private val AzulPrimaryDark = Color(0xFF5DA9FF) // Azul claro
private val AzulSecondaryDark = Color(0xFF3D9BFF)
private val AzulTertiaryDark = Color(0xFFDCDCDC) // Plata claro
private val AzulBackgroundDark = Color(0xFF1B263B) // Azul oscuro

private val AzulLightColorScheme = lightColorScheme(
    primary = AzulPrimaryLight,
    secondary = AzulSecondaryLight,
    tertiary = AzulTertiaryLight,
    background = AzulBackgroundLight,
    surface = AzulBackgroundLight,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.Black,
    onBackground = Color(0xFF1C1C1C),
    onSurface = Color(0xFF1C1C1C),
)

private val AzulDarkColorScheme = darkColorScheme(
    primary = AzulPrimaryDark,
    secondary = AzulSecondaryDark,
    tertiary = AzulTertiaryDark,
    background = AzulBackgroundDark,
    surface = AzulBackgroundDark,
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onTertiary = Color.Black,
    onBackground = Color(0xFFE6E6E6),
    onSurface = Color(0xFFE6E6E6),
)

// Enum para seleccionar el tema
enum class AppThemeType {
    GUINDA, AZUL
}

@Composable
fun FileExplorerTheme(
    theme: AppThemeType = AppThemeType.GUINDA, // Permite seleccionar el tema
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Deshabilitamos Dynamic Color para forzar nuestros temas
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        // Lógica para seleccionar el esquema de color
        theme == AppThemeType.GUINDA && darkTheme -> GuindaDarkColorScheme
        theme == AppThemeType.GUINDA && !darkTheme -> GuindaLightColorScheme
        theme == AppThemeType.AZUL && darkTheme -> AzulDarkColorScheme
        theme == AppThemeType.AZUL && !darkTheme -> AzulLightColorScheme
        else -> GuindaLightColorScheme // Default
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // Necesitarás definir tu propio Typography.kt
        content = content
    )
}