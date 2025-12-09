package com.vs.vibeplayer.core.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.vs.vibeplayer.R

val Grot = FontFamily(
    Font(R.font.hostgrotesk_semibold, FontWeight.SemiBold),
    Font(R.font.hostgrotesk_medium, FontWeight.Medium),
    Font(R.font.hostgrotesk_regular,FontWeight.Normal),
//    Font(R.font.hostgrotesk_bold,FontWeight.Bold),
//    Font(R.font.hostgrotesk_extrabold,FontWeight.ExtraBold),
)
val Typography = Typography(
    bodyMedium = TextStyle(
        fontFamily = Grot,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 18.sp,
    ),
    bodyLarge=  TextStyle(
        fontFamily = Grot,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 22.sp,
    ),
    titleLarge = TextStyle(
        fontFamily = Grot,
        fontWeight = FontWeight.Medium,
        fontSize = 24.sp,
        lineHeight = 28.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = Grot,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 24.sp,
    ),
    labelMedium = TextStyle(
        fontFamily = Grot,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 22.sp,
    ),


)