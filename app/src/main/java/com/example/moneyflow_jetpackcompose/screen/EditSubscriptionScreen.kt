package com.example.moneyflow_jetpackcompose.screen

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.moneyflow_jetpackcompose.R
import com.example.moneyflow_jetpackcompose.api.SubscriptionRequest
import com.example.moneyflow_jetpackcompose.component.AmountText
import com.example.moneyflow_jetpackcompose.component.CustomImageView
import com.example.moneyflow_jetpackcompose.component.InputField
import com.example.moneyflow_jetpackcompose.component.NumericKeypad
import com.example.moneyflow_jetpackcompose.component.SelectedButton
import com.example.moneyflow_jetpackcompose.component.TopBar
import com.example.moneyflow_jetpackcompose.datastore.DataStoreManager
import com.example.moneyflow_jetpackcompose.model.SubscriptionModel
import com.example.moneyflow_jetpackcompose.ui.theme.CategoryColor
import com.example.moneyflow_jetpackcompose.ui.theme.DateColor
import com.example.moneyflow_jetpackcompose.ui.theme.WhiteBackgroundColor
import com.example.moneyflow_jetpackcompose.utils.uriToBase64
import com.example.moneyflow_jetpackcompose.viewmodel.SubscriptionViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditSubscriptionScreen(navController: NavController, subscriptionModel: SubscriptionModel?, viewModel: SubscriptionViewModel = viewModel()) {
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    var base64String by remember { mutableStateOf<String?>(subscriptionModel?.icon?:"") }
    var imgUrl by remember { mutableStateOf(subscriptionModel?.icon?:"") }
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


    var note by remember { mutableStateOf(subscriptionModel?.name?: "") }
    var repeatValue by remember { mutableStateOf(subscriptionModel?.frequency?: "") }
    var amount by remember { mutableStateOf(subscriptionModel?.amount?.toDouble().toString()?: "0.00") }
    var selectedDate by remember { mutableStateOf<LocalDate?>(subscriptionModel?.currentBill?.let {
        Instant.parse(it).atZone(ZoneId.systemDefault()).toLocalDate()
    } ?: LocalDate.now()) }

    var showRepeatDialog by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    val formatter = DateTimeFormatter.ofPattern("dd, MMM yyyy")
    val formattedDate = selectedDate?.format(formatter)
    var userId by remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        userId = DataStoreManager.getToken(context) ?: ""
    }




    Scaffold(
        modifier = Modifier.clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) {
            focusManager.clearFocus()
        },
        containerColor = WhiteBackgroundColor,
        topBar = {
            TopBar(
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                title = "Edit Subscription",
                actions = {
                    IconButton(
                        onClick = {
                            subscriptionModel?.let { viewModel.deleteSubscription(it.id, navController) }
                        },
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
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize().padding(innerPadding).padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,

        )  {
            CustomImageView(
                imgUrl = imgUrl,
                selectedImageUri = selectedImageUri,
                onClick = { imagePickerLauncher.launch("image/") },
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
                        (if (selectedDate == null) "Select Date" else formattedDate)?.let {
                            SelectedButton(
                                textColor = Color.Black,
                                iconColor = Color.Black,
                                title = "Select Date",
                                value = it,
                                icon = R.drawable.calendar_2_fill,
                                color = DateColor,
                                onClick = { showDatePicker = true }
                            )
                        }
                        Spacer(Modifier.width(8.dp))
                        Box() {
                            SelectedButton(
                                textColor = Color.Black,
                                iconColor = Color.Black,
                                title = "Repeat",
                                value = repeatValue,
                                icon = R.drawable.calendar_2_fill,
                                color = CategoryColor,
                                onClick = { showRepeatDialog = true }
                            )
                            DropdownMenu(expanded = showRepeatDialog, onDismissRequest = { showRepeatDialog = false}) {
                                listOf("monthly", "yearly").forEach { item ->
                                    DropdownMenuItem(
                                        text = { Text(text = item) },
                                        onClick = {
                                            repeatValue = item
                                            showRepeatDialog = false
                                        }
                                    )
                                }
                            }
                        }
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
                            if (note.isNotEmpty()) {
                                if (base64String.isNullOrEmpty()) {
                                    Toast.makeText(context, "Please select icon for your subscription.", Toast.LENGTH_SHORT).show()
                                } else {
                                    if (selectedDate != null) {
                                        val formattedDate =  selectedDate?.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                                        val isoDate = ZonedDateTime.of(LocalDate.parse(formattedDate).atStartOfDay(), ZoneId.systemDefault()).withZoneSameInstant(
                                            ZoneId.of("UTC")).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                                        // create subscription
                                        val newSubscriptionRequest = SubscriptionRequest(
                                            note = note,
                                            icon = base64String!!,
                                            amount = amount.toDouble(),
                                            currentBill = isoDate,
                                            frequency = repeatValue,
                                            userId = userId,
                                        )
                                        Log.d("DATA", "Data: id:${subscriptionModel?.id} note:$note, amount:$amount, currentBill:$isoDate, frequency:$repeatValue, userId:$userId ")
                                        subscriptionModel?.id?.let {
                                            viewModel.updateSubscription(
                                                it,
                                                newSubscriptionRequest,
                                                navController
                                            )
                                        }
                                    } else {
                                        Toast.makeText(context, "Please select date", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            } else {
                                Toast.makeText(context, "Input note can't be empty", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }
            }

            if (showDatePicker) {
                val datePickerState = rememberDatePickerState(
                    initialSelectedDateMillis = LocalDate.now().atStartOfDay()
                        .atZone(ZoneId.systemDefault())
                        .toInstant()
                        .toEpochMilli()
                )
                DatePickerDialog(onDismissRequest = { showDatePicker = false}, confirmButton = {
                    TextButton(onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val newDate = LocalDate.ofEpochDay(millis / (24 * 60 * 60 * 1000))
                            selectedDate = newDate

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
        }
    }
}