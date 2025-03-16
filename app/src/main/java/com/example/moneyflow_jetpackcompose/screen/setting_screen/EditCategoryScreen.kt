package com.example.moneyflow_jetpackcompose.screen.setting_screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.moneyflow_jetpackcompose.api.CategoryRequest
import com.example.moneyflow_jetpackcompose.component.InputField
import com.example.moneyflow_jetpackcompose.component.TopBar
import com.example.moneyflow_jetpackcompose.datastore.DataStoreManager
import com.example.moneyflow_jetpackcompose.model.CategoryModel
import com.example.moneyflow_jetpackcompose.ui.theme.DarkBackgroundColor
import com.example.moneyflow_jetpackcompose.ui.theme.ErrorColor
import com.example.moneyflow_jetpackcompose.ui.theme.ThemeMode
import com.example.moneyflow_jetpackcompose.ui.theme.ThemePreference
import com.example.moneyflow_jetpackcompose.ui.theme.WhiteBackgroundColor
import com.example.moneyflow_jetpackcompose.ui.theme.colorToHex
import com.example.moneyflow_jetpackcompose.ui.theme.parse
import com.example.moneyflow_jetpackcompose.viewmodel.CategoryViewModel
import com.github.skydoves.colorpicker.compose.AlphaSlider
import com.github.skydoves.colorpicker.compose.AlphaTile
import com.github.skydoves.colorpicker.compose.BrightnessSlider
import com.github.skydoves.colorpicker.compose.ColorEnvelope
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.drawColorIndicator
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import com.makeappssimple.abhimanyu.composeemojipicker.ComposeEmojiPickerBottomSheetUI
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditCategoryScreen(navController: NavController, categoryViewModel: CategoryViewModel = viewModel(), categoryModel: CategoryModel?) {
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val themeMode = ThemePreference(context).themeFlow.collectAsState(initial = ThemeMode.SYSTEM.value).value
    val isDarkTheme = when (ThemeMode.fromInt(themeMode)) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val colorController = rememberColorPickerController()

    var newSelectColor by remember { mutableStateOf(Color.Transparent) }
    var newSelectHexColor by remember { mutableStateOf("") }
    var isDialogVisible by remember { mutableStateOf(false) }
    var isModalBottomSheetVisible by remember { mutableStateOf(false) }

    var searchText by remember { mutableStateOf("") }

    var selectedColor by remember { mutableStateOf<Color?>(null) }
    var selectedHexColor by remember { mutableStateOf("ff121212") }
    var selectEmoji by remember { mutableStateOf<String?>(null) }
    var categoryName by remember { mutableStateOf<String?>(null) }
    var selectedType by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(categoryModel) {
        categoryModel?.let {
            selectedColor = Color.parse("#${it.color?.drop(2)}")
            selectedHexColor = it.color
            selectEmoji = it.emoji
            categoryName = it.categoryName
            selectedType = it.type
        }
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
                title = "Edit Category",
                actions = {
                    IconButton(onClick = {
                        categoryModel?.let { categoryViewModel.deleteCategory(context, it.categoryId).let { navController.popBackStack() } }
                    }) {
                        Icon(Icons.Default.Delete, tint = ErrorColor, contentDescription = null)
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding).padding(horizontal = 16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Row(
                modifier = Modifier
                    .padding(top = 24.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Row(modifier = Modifier.wrapContentWidth().background(Color.LightGray, shape = RoundedCornerShape(12.dp)).padding(horizontal = 12.0.dp, vertical = 8.0.dp)) {
                    ToggleCategoryTypeButton(
                        text = "Expense",
                        isSelected = selectedType == "expense",
                        onClick = { selectedType = "expense" }
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    ToggleCategoryTypeButton(
                        text = "Income",
                        isSelected = selectedType == "income",
                        onClick = { selectedType = "income" }
                    )
                }
            }
            // Category Icon
            Box(
                modifier = Modifier
                    .padding(top = 32.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                selectedColor?.let {
                    Modifier
                        .size(120.dp)
                        .background(it, RoundedCornerShape(16.dp))
                }?.let {
                    Box(
                        modifier = it,
                        contentAlignment = Alignment.Center
                    ) {
                        selectEmoji?.let { Text(it, color = Color.White, fontSize = 72.sp) }
                    }
                }

                // Edit Icon Button
                IconButton(
                    onClick = { isModalBottomSheetVisible = true },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(16.dp)
                        .background(Color(0xFF4CAF50), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Icon",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            if (isModalBottomSheetVisible) {
                ModalBottomSheet(sheetState = sheetState,
                    shape = RectangleShape,
                    tonalElevation = 0.dp,
                    onDismissRequest = {
                        isModalBottomSheetVisible = false
                        searchText = ""
                    },
                    dragHandle = null,
                    windowInsets = WindowInsets(0)
                ) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        ComposeEmojiPickerBottomSheetUI(
                            onEmojiClick = { emoji ->
                                isModalBottomSheetVisible = false
                                selectEmoji = emoji.character
                            },
                            onEmojiLongClick = { emoji ->
                                Toast.makeText(context,
                                    emoji.unicodeName.replaceFirstChar {
                                        if (it.isLowerCase()) it.titlecase(
                                            Locale.ROOT
                                        ) else it.toString()
                                    }, Toast.LENGTH_SHORT).show()
                            },
                            searchText = searchText,
                            updateSearchText = { updatedSearchText ->
                                searchText = updatedSearchText
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {isDialogVisible = true}) {
                Text("Select Color")
            }

            if (isDialogVisible) {
                AlertDialog(
                    onDismissRequest = { isDialogVisible = false},
                    confirmButton = {
                        TextButton(onClick = {
                            isDialogVisible = false
                            selectedColor = newSelectColor
                            selectedHexColor = newSelectHexColor
                        }) {
                            Text("Confirm")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = {
                            isDialogVisible = false
                        }) {
                            Text("Cancel")
                        }
                    },
                    title = { Text("Select Color") },
                    text = {
                        Column {
                            HsvColorPicker(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(300.dp)
                                    .padding(10.dp),
                                controller = colorController,
                                drawOnPosSelected = {
                                    drawColorIndicator(
                                        colorController.selectedPoint.value,
                                        colorController.selectedColor.value
                                    )
                                },
                                onColorChanged = { colorEnvelope: ColorEnvelope ->
                                    newSelectColor = colorEnvelope.color
                                    newSelectHexColor = colorEnvelope.hexCode
                                },
                                initialColor = selectedColor
                            )

                            AlphaSlider(
                                modifier = Modifier
                                    .testTag("HSV_AlphaSlider")
                                    .fillMaxWidth()
                                    .padding(10.dp)
                                    .height(35.dp)
                                    .align(Alignment.CenterHorizontally),
                                controller = colorController,
                            )

                            BrightnessSlider(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp)
                                    .height(35.dp)
                                    .align(Alignment.CenterHorizontally),
                                controller = colorController,
                            )
                            Text(
                                text = "#$newSelectHexColor",
                                color = newSelectColor,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.align(Alignment.CenterHorizontally),
                            )
                            AlphaTile(
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .align(Alignment.CenterHorizontally),
                                controller = colorController,
                            )

                        }
                    },
                    modifier = Modifier.wrapContentSize()
                )
            }
            Spacer(Modifier.height(32.dp))
            // Category Name Input
            categoryName?.let {
                InputField(
                    text = "Enter category name",
                    value = it,
                    onValueChange = {
                        categoryName = it
                    }
                )
            }
            // Add Category Button
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = {

                    categoryName?.let {
                        selectEmoji?.let { it1 ->
                            selectedHexColor?.let { it2 ->
                                selectedType?.let { it3 ->
                                    CategoryRequest(
                                        categoryName = it,
                                        emoji = it1,
                                        color = it2,
                                        type = it3.lowercase(Locale.getDefault()),

                                    )
                                }
                            }
                        }
                    }?.let {
                        categoryModel?.let { it1 ->
                            categoryViewModel.updateCategory(context, id = it1.categoryId, it
                            ).let {
                                navController.popBackStack()
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = buttonColors(
                ).copy(containerColor = Color.Black),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "Update Category",
                    color = Color.White
                )
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}



