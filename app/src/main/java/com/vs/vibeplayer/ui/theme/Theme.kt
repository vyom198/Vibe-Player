package com.vs.vibeplayer.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme

import androidx.compose.runtime.Composable


private val DarkColorScheme = darkColorScheme(
    primary = LightPurple,
    secondary = LightSteelBlue,
    background = AlmostBlack,
    surface = AlmostBlack,
    onPrimary = White,
)

@Composable
fun VibePlayerTheme(
    content: @Composable () -> Unit
) {
    val colorScheme =  DarkColorScheme


    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}