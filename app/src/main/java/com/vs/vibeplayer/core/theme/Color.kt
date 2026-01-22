package com.vs.vibeplayer.core.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color



val LightPurple = Color(0xFFDE84FF)
val LightPurple30 = Color(0x4DDE84FF).copy(alpha = 0.3f)
val DarkBlueGrey28 = Color(0x471A2735).copy(alpha = 0.28f)
val White = Color(0xFFFFFFFF)
val LightSteelBlue = Color(0xFFA7BBD1)
val ButtonDestructive = Color(0xffFF5667)
val SlateGrey = Color(0xFF4C647C)
val AlmostBlack = Color(0xFF0A131D)
val DarkSlateGrey = Color(0xFF1A2735)
val Accent = Color(0xFFF1FF95)


val ColorScheme.primary30 :Color
    get() = LightPurple30

val ColorScheme.accent :Color
    get() = Accent

val ColorScheme.hover : Color
    get() = DarkSlateGrey

val ColorScheme.disabled :Color
    get() = SlateGrey




