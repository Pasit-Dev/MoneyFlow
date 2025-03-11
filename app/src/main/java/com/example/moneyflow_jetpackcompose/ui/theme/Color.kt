package com.example.moneyflow_jetpackcompose.ui.theme

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.toColorInt


fun Color.Companion.parse(colorString: String): Color = Color(color = android.graphics.Color.parseColor(colorString))
fun colorToHex(color: Color) : String {
    val argb = color.toArgb()
    return String.format("%08X", argb)
}

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)


val PrimaryColor = Color(0xFF1E90FF)
val AccountColor = Color(0xFF92C5FF)
val CategoryColor = Color(0xFFE7AAFF)
val DateColor = Color(0xFFBCFF92)
val ErrorColor = Color(0xFFFF4040)
val SuccessColor = Color(0xFF63CD81)
val WhiteColor = Color(0xFFFFFFFF)
val BlackColor = Color(0xFF202020)
val GrayColor = Color(0xFF858585)
val WhiteBackgroundColor = Color(0xFFFAFAFA)
val DarkBackgroundColor = Color(0xFF121212)