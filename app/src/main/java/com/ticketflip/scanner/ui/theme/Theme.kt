package com.ticketflip.scanner.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable


private val colorScheme = lightColorScheme(
    primary = Orange,
    secondary = Grey,
    background = LightGrey
)

@Composable
fun TicketflipscannerTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = colorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}