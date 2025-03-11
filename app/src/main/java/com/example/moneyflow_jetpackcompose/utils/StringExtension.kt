package com.example.moneyflow_jetpackcompose.utils

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

fun formatDate(dateString: String): String {
    val formatter = DateTimeFormatter.ISO_DATE_TIME
    val date = LocalDate.parse(dateString, formatter)
    return date.format(DateTimeFormatter.ofPattern("dd, MMM yyyy", Locale.getDefault()))
}


fun base64ToBitmap(value: String) : Bitmap? {
    return try {
        val decodedBytes = Base64.decode(value, Base64.DEFAULT)
        BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    } catch (e: IllegalArgumentException) {
        Log.e("IMAGE", "Error: ${e.localizedMessage}")
        null
    }
}

fun uriToBase64(uri: Uri, context: Context): String? {
    return try {
        val contentResolver: ContentResolver = context.contentResolver
        // Convert the URI to an InputStream
        val inputStream: InputStream = contentResolver.openInputStream(uri) ?: return null

        // Convert the InputStream to a Bitmap
        val bitmap = BitmapFactory.decodeStream(inputStream)

        // Convert the Bitmap to a ByteArray
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()

        // Encode the ByteArray to a Base64 string
        Base64.encodeToString(byteArray, Base64.DEFAULT)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun formatCompactValue(value: Float): String {
    return when {
        value >= 1_000_000 -> {
            val millions = value / 1_000_000f
            String.format("%.1fM", millions)
        }
        value >= 1_000 -> {
            val thousands = value / 1_000f
            String.format("%.1fk", thousands)
        }
        else -> String.format("%.0f", value)
    }
}