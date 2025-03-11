package com.example.moneyflow_jetpackcompose.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moneyflow_jetpackcompose.R

@Composable
fun NumericKeypad(
    modifier: Modifier = Modifier,
    onNumberClick: (String) -> Unit,
    onDeleteClick: () -> Unit,
    onConfirmClick: () -> Unit
) {
    // Calculate button height based on the grid's expected size
    val padding = 8.dp
    val buttonHeight = (100.dp * 2)

    Box(
        modifier = modifier
            .fillMaxSize() // Make sure the Box takes up the full screen size
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight() // Ensure Row only takes the height it needs
                .align(Alignment.BottomStart), // Align Row at the bottom of the Box
            horizontalArrangement = Arrangement.spacedBy(padding)
        ) {
            // Number grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .weight(1f) // Take up remaining space
                    .fillMaxHeight(), // Make sure the grid fills the available height
                verticalArrangement = Arrangement.spacedBy(padding),
                horizontalArrangement = Arrangement.spacedBy(padding)
            ) {
                items(11) { index ->
                    val number = if (index == 9) "." else if (index == 10) "0" else (index + 1).toString()
                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFF5F5F5))
                            .clickable { onNumberClick(number) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = number,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Control buttons
            Column(
                modifier = Modifier
                    .width(80.dp)
                    .wrapContentHeight() // Let Column take only the height it needs
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(buttonHeight)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFFF4444))
                        .clickable { onDeleteClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.delete_back_fill),
                        contentDescription = "Delete",
                        tint = Color.White
                    )
                }
                Spacer(Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(buttonHeight)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.Black)
                        .clickable { onConfirmClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.check_fill),
                        contentDescription = "Confirm",
                        tint = Color.White
                    )
                }
            }
        }
    }
}




@Preview (showBackground = true)
@Composable
fun NumericKeypadPreview() {
    NumericKeypad(
        onNumberClick = {},
        onDeleteClick = {},
        onConfirmClick = {}
    )
}
