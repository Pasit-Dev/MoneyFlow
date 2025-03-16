package com.example.moneyflow_jetpackcompose.screen

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.moneyflow_jetpackcompose.R
import com.example.moneyflow_jetpackcompose.api.GoalRequest
import com.example.moneyflow_jetpackcompose.component.AmountText
import com.example.moneyflow_jetpackcompose.component.CustomImageView
import com.example.moneyflow_jetpackcompose.component.InputField
import com.example.moneyflow_jetpackcompose.component.NumericKeypad
import com.example.moneyflow_jetpackcompose.component.SelectedButton
import com.example.moneyflow_jetpackcompose.component.TopBar
import com.example.moneyflow_jetpackcompose.datastore.DataStoreManager
import com.example.moneyflow_jetpackcompose.ui.theme.DarkBackgroundColor
import com.example.moneyflow_jetpackcompose.ui.theme.DateColor
import com.example.moneyflow_jetpackcompose.ui.theme.ThemeMode
import com.example.moneyflow_jetpackcompose.ui.theme.ThemePreference
import com.example.moneyflow_jetpackcompose.ui.theme.WhiteBackgroundColor
import com.example.moneyflow_jetpackcompose.utils.base64ToBitmap
import com.example.moneyflow_jetpackcompose.utils.formatDate
import com.example.moneyflow_jetpackcompose.utils.uriToBase64
import com.example.moneyflow_jetpackcompose.viewmodel.GoalViewModel
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGoalScreen(navController: NavController, goalViewModel: GoalViewModel = viewModel()) {
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    var themePreference = ThemePreference(context)
    val themeMode = themePreference.themeFlow.collectAsState(initial = ThemeMode.SYSTEM.value).value
    val isDarkTheme = when (ThemeMode.fromInt(themeMode)) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }
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


    var note by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("0.00") }
    var startDate by remember { mutableStateOf<LocalDate?>(LocalDate.now()) }
    var endDate by remember { mutableStateOf<LocalDate?>(LocalDate.now().plusMonths(3)) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false)}
    var userId by remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        userId = DataStoreManager.getToken(context) ?: ""
    }
    Scaffold(
        modifier = Modifier.clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) {
            focusManager.clearFocus()
        },
        containerColor = if (isDarkTheme) DarkBackgroundColor else WhiteBackgroundColor,
        topBar = {
            TopBar(
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = if (isDarkTheme) Color.White else Color.Black
                        )
                    }
                },
                title = "Add Goal"
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize().padding(innerPadding).padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally

        )  {
            CustomImageView(
                imgUrl = imgUrl,
                selectedImageUri = selectedImageUri,
                onClick = { imagePickerLauncher.launch("image/") }
            )

            // Amount Display
            AmountText(amount)

            // Note Input
            InputField(
                text = "Add note...",
                value = note,
                onValueChange = {
                    note = it
                }
            )
            // Number Pad
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                        val startFormat =  startDate?.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                        val endFormat =  endDate?.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                        SelectedButton(
                            textColor = Color.Black,
                            iconColor = Color.Black,
                            title = "StartDate",
                            value = if (startDate != null) formatDate(ZonedDateTime.of(LocalDate.parse(startFormat).atStartOfDay(), ZoneId.systemDefault()).withZoneSameInstant(
                                ZoneId.of("UTC")).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)) else "",
                            icon = R.drawable.calendar_2_fill,
                            color = DateColor,
                            onClick = { showDatePicker = true }
                        )
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                        SelectedButton(
                            textColor = Color.Black,
                            iconColor = Color.Black,
                            title = "End Date",
                            value = if (startDate != null) formatDate(ZonedDateTime.of(LocalDate.parse(endFormat).atStartOfDay(), ZoneId.systemDefault()).withZoneSameInstant(
                                ZoneId.of("UTC")).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)) else "",

                            icon = R.drawable.calendar_2_fill,
                            color = DateColor,
                            onClick = { showEndDatePicker = true }
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    NumericKeypad(
                        onNumberClick = { digit ->
                            if (amount == "0.00") amount = digit
                            else amount += digit
                        },
                        onDeleteClick = {
                            if (amount != "0.00") {
                                amount = if (amount.length > 1) amount.dropLast(1)
                                else "0.00"
                            }
                        },
                        onConfirmClick = {
                            val startFormat =  startDate?.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                            val endFormat =  endDate?.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                            Log.d("API", "CREATE NOW GOAL")
                           if (note.isNotEmpty()) {
                                if (startDate != null && endDate != null) {
                                    val newGoalRequest = base64String?.let {
                                        GoalRequest(
                                            name = note,
                                            img = it,
                                            startDate = ZonedDateTime.of(LocalDate.parse(startFormat).atStartOfDay(), ZoneId.systemDefault()).withZoneSameInstant(
                                                ZoneId.of("UTC")).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
                                            endDate = ZonedDateTime.of(LocalDate.parse(endFormat).atStartOfDay(), ZoneId.systemDefault()).withZoneSameInstant(
                                                ZoneId.of("UTC")).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
                                            targetAmount = amount.toDouble(),
                                            currentAmount = 0.00,
                                            userId = userId
                                        )
                                    }
                                    newGoalRequest?.let { goalViewModel.createGoal(newGoal = it, navController) }
                                }
                            }
                        }
                    )
                }
            }

            if (showDatePicker) {
                val datePickerState = rememberDatePickerState(
                    initialSelectedDateMillis = startDate?.atStartOfDay()
                        ?.atZone(ZoneId.systemDefault())
                        ?.toInstant()
                        ?.toEpochMilli()
                )
                DatePickerDialog(onDismissRequest = { showDatePicker = false}, confirmButton = {
                    TextButton(onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val newDate = LocalDate.ofEpochDay(millis / (24 * 60 * 60 * 1000))
                            startDate = newDate

                        }
                        showDatePicker = false
                    }) {
                        Text("OK")
                    }
                }, dismissButton = {
                    TextButton(onClick = { showDatePicker = false}) {
                        Text("Cancel")
                    }
                }) {
                    DatePicker(
                        state = datePickerState,
                        showModeToggle = false
                    )
                }
            }
            if (showEndDatePicker) {
                val datePickerState = rememberDatePickerState(
                    initialSelectedDateMillis = endDate?.atStartOfDay()
                        ?.atZone(ZoneId.systemDefault())
                        ?.toInstant()
                        ?.toEpochMilli()
                )
                DatePickerDialog(onDismissRequest = { showEndDatePicker = false}, confirmButton = {
                    TextButton(onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val newDate = LocalDate.ofEpochDay(millis / (24 * 60 * 60 * 1000))
                            endDate = newDate

                        }
                        showEndDatePicker = false
                    }) {
                        Text("OK")
                    }
                }, dismissButton = {
                    TextButton(onClick = { showEndDatePicker = false}) {
                        Text("Cancel")
                    }
                }) {
                    DatePicker(
                        state = datePickerState,
                        showModeToggle = false
                    )
                }
            }
        }
    }
}


@Preview (showBackground = true)
@Composable
fun AddGoalScreenPreview() {
    AddGoalScreen(rememberNavController())
}