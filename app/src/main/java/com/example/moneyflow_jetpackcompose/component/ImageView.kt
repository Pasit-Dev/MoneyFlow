package com.example.moneyflow_jetpackcompose.component

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.moneyflow_jetpackcompose.ui.theme.PrimaryColor
import com.example.moneyflow_jetpackcompose.utils.base64ToBitmap

@Composable
fun CustomImageView(modifier: Modifier = Modifier, imgUrl: String, selectedImageUri: Uri?, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .padding(top = 32.dp)
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(Color(0xFF121212), RoundedCornerShape(32.dp))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            // ตรวจสอบว่า base64 สามารถแปลงเป็น Bitmap ได้หรือไม่
            if (base64ToBitmap(imgUrl) != null) {
                selectedImageUri?.let { uri ->
                    Image(
                        painter = rememberAsyncImagePainter(uri),
                        contentDescription = null,
                        modifier = Modifier.size(120.dp).clip(RoundedCornerShape(16.dp)),
                    )
                } ?: run {
                    Image(
                        bitmap = base64ToBitmap(imgUrl)!!.asImageBitmap(),
                        contentDescription = "Profile Image",
                        modifier = Modifier.size(120.dp).clip(RoundedCornerShape(16.dp))
                    )
                }
            } else {
                Box(modifier = Modifier.size(120.dp)) {
                    CircularProgressIndicator(
                        color = PrimaryColor,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }

        // ปุ่ม Edit อยู่ที่มุมล่างขวา
        IconButton(
            onClick = onClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = (8).dp, y = (8).dp)  // Move the button towards the center
                .size(36.dp)
                .background(Color(0xFF4CAF50), RoundedCornerShape(12.dp))
                .padding(4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit Icon",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}