package com.example.moneyflow_jetpackcompose.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.Subscriptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.moneyflow_jetpackcompose.R
import com.example.moneyflow_jetpackcompose.component.CustomImageView
import com.example.moneyflow_jetpackcompose.component.InputField
import com.example.moneyflow_jetpackcompose.component.SelectedButton
import com.example.moneyflow_jetpackcompose.component.TopBar
import com.example.moneyflow_jetpackcompose.model.GoalModel
import com.example.moneyflow_jetpackcompose.ui.theme.DateColor
import com.example.moneyflow_jetpackcompose.ui.theme.PrimaryColor
import com.example.moneyflow_jetpackcompose.ui.theme.WhiteBackgroundColor
import com.example.moneyflow_jetpackcompose.utils.formatDate
import com.example.moneyflow_jetpackcompose.utils.uriToBase64
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter


@Composable
fun GoalDetailsScreen(
    navController: NavController,
    goalItem: GoalModel?
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    var base64String by remember { mutableStateOf<String?>(null) }
    var imgUrl by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            selectedImageUri = uri

            selectedImageUri?.let {
                base64String = uriToBase64(it, context)!!
                imgUrl = base64String!!
            }
        }
    )

    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }

    if (goalItem == null) {
        Text(text = "No data found", style = MaterialTheme.typography.headlineMedium)
        return
    }
    Scaffold(
        containerColor = WhiteBackgroundColor,
        topBar = {
            TopBar(
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                title = "Goal Details",
                actions = {
                    IconButton(
                        onClick = { /* Handle icon edit */ },
                        modifier = Modifier
                            .background(Color.Red, RoundedCornerShape(8.dp)).size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)

                        )
                    }
                }
            )
        },
        modifier = Modifier.clickable(interactionSource = remember { MutableInteractionSource()}, indication = null) {
            focusManager.clearFocus()
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .padding(top = 32.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                CustomImageView(
                    imgUrl = goalItem.img,
                    selectedImageUri = selectedImageUri,
                    onClick = {}
                )

                // Edit Icon Button
                IconButton(
                    onClick = { /* Handle icon edit */ },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(24.dp)
                        .background(Color(0xFF4CAF50), RoundedCornerShape(12.dp))
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Icon",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            Spacer(Modifier.height(32.dp))
            InputField(
                text = "Goal Name",
                value = goalItem.name,
                onValueChange = {},
            )
            Spacer(Modifier.height(32.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                SelectedButton(
                    textColor = Color.Black,
                    iconColor = Color.Black,
                    title = "StartDate",
                    value = formatDate(goalItem.startDate),
                    icon = R.drawable.calendar_2_fill,
                    color = DateColor,
                    onClick = { showStartDatePicker = true }
                )
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                SelectedButton(
                    textColor = Color.Black,
                    iconColor = Color.Black,
                    title = "End Date",
                    value = formatDate(goalItem.endDate),
                    icon = R.drawable.calendar_2_fill,
                    color = DateColor,
                    onClick = { showEndDatePicker = true }
                )
            }
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LinearProgressIndicator(
                    trackColor = Color.LightGray,
                    color = Color(0xFF121212),
                    progress = { goalItem.currentAmount / goalItem.targetAmount },
                    modifier = Modifier
                        .weight(1f)
                        .padding(top = 4.dp)
                        .clip(shape = RoundedCornerShape(16.dp)),
                )
                Spacer(Modifier.width(8.dp)) // เพิ่มระยะห่างระหว่าง ProgressBar กับ Text
                Text(
                    text = "${goalItem.currentAmount.toInt()} / ${goalItem.targetAmount.toInt()}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Spacer(Modifier.height(16.dp))
            LazyColumn {
                items(listOf("1","2","3","4","5","6","7","8","9","10")) {
                    ListItem(
                        colors = ListItemDefaults.colors(Color.Transparent),
                        leadingContent = { Box(modifier = Modifier.background(PrimaryColor, RoundedCornerShape(12.dp)).padding(8.dp)) {
                            Icon(Icons.Filled.Subscriptions, contentDescription = null, tint = Color.White)
                        } },
                        headlineContent = { Text("Headline Content") },
                        supportingContent = { Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.DateRange, contentDescription = null, Modifier.size(16.dp), Color.Gray)
                            Spacer(Modifier.width(8.dp))
                            Text("22 Dec 2025", color = Color.Gray)
                        } },
                        trailingContent = { Text("3000 THB", color = Color(0xFF63CD81)) }
                    )
                }
            }
        }
    }
}

@Composable
fun TransactionList(transactionList: List<String>) {

}




@Preview (showBackground = true)
@Composable
fun GoalDetailScreenPreview() {
    GoalDetailsScreen(rememberNavController(), goalItem = GoalModel(R.drawable.check_fill, "", "", "", "", 0f,0f))
}