package com.example.moneyflow_jetpackcompose.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moneyflow_jetpackcompose.R


@Composable
fun DataNotFound(@DrawableRes image: Int = R.drawable.undraw_no_data_ig65, text: String = "Empty!", onClick: () -> Unit = {} ) {
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Image(
            painter = painterResource(id = image),
            contentDescription = null,
            Modifier.size(200.dp)
        )
        Spacer(Modifier.height(32.dp))
        Text(text, fontSize = 24.sp, fontWeight = FontWeight.Medium)
        TextButton(onClick = onClick) {
            Text("Refresh")
        }
        Spacer(Modifier.height(32.dp))
    }
}

@Preview (showBackground = true)
@Composable
fun DataNotFoundPreview() {
    DataNotFound()
}