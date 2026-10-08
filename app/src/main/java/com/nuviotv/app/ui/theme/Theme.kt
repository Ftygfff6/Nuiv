package com.nuviotv.app.ui.theme

import androidx.compose.runtime.Composable
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.darkColorScheme

private val NiovColorScheme = darkColorScheme(
    primary = NiovPrimary,
    onPrimary = NiovOnBackground,
    primaryContainer = NiovPrimaryVariant,
    secondary = NiovSecondary,
    background = NiovBackground,
    onBackground = NiovOnBackground,
    surface = NiovSurface,
    onSurface = NiovOnBackground,
    surfaceVariant = NiovSurfaceVariant,
    onSurfaceVariant = NiovOnSurfaceVariant,
    error = NiovError
)

@Composable
fun NiovTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = NiovColorScheme,
        typography = NiovTypography,
        content = content
    )
}
