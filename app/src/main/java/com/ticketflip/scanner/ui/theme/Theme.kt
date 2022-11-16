package com.ticketflip.scanner.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView


private val colorScheme = lightColorScheme(
    primary = Orange,
    secondary = Grey,
    background = LightGray
)

@Composable
fun TicketflipscannerTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = colorScheme;

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}