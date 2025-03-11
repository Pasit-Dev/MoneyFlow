package com.example.moneyflow_jetpackcompose.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun SelectedButton(title: String, onClick: () -> Unit, color: Color, @DrawableRes icon: Int, value: String, iconColor: Color = Color.White, textColor: Color = Color.White) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = color
        ),
        shape = RoundedCornerShape(24.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(painter = painterResource(icon) , contentDescription = title, tint = iconColor, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(8.dp))
            if (value.isNullOrEmpty()) {
                Text(text = title, color = textColor)
            } else {
                Text(text = value, color = textColor)
            }
        }
    }
}