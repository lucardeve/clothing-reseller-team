package com.cegep.reseller.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = Ink,
    onPrimary = Cloud,
    secondary = Slate,
    onSecondary = Cloud,
    background = Cloud,
    onBackground = Ink,
    surface = Cloud,
    onSurface = Ink,
    surfaceVariant = Mist,
    onSurfaceVariant = Slate,
    error = Danger,
    onError = Cloud
)

private val DarkColors = darkColorScheme(
    primary = Cloud,
    onPrimary = Ink,
    secondary = Mist,
    onSecondary = Ink,
    background = Ink,
    onBackground = Cloud,
    surface = Slate,
    onSurface = Cloud,
    surfaceVariant = Slate,
    onSurfaceVariant = Mist,
    error = Danger,
    onError = Cloud
)

@Composable
fun ResellerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = ResellerTypography,
        content = content
    )
}
