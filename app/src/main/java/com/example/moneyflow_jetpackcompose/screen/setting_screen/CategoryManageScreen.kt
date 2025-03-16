package com.example.moneyflow_jetpackcompose.screen.setting_screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.moneyflow_jetpackcompose.component.DataNotFound
import com.example.moneyflow_jetpackcompose.component.TabBar
import com.example.moneyflow_jetpackcompose.component.TopBar
import com.example.moneyflow_jetpackcompose.model.CategoryModel
import com.example.moneyflow_jetpackcompose.ui.theme.DarkBackgroundColor
import com.example.moneyflow_jetpackcompose.ui.theme.PrimaryColor
import com.example.moneyflow_jetpackcompose.ui.theme.ThemeMode
import com.example.moneyflow_jetpackcompose.ui.theme.ThemePreference
import com.example.moneyflow_jetpackcompose.ui.theme.WhiteBackgroundColor
import com.example.moneyflow_jetpackcompose.ui.theme.parse
import com.example.moneyflow_jetpackcompose.viewmodel.CategoryViewModel

@Composable
fun CategoryManageScreen(navController: NavController) {
    val context = LocalContext.current
    var themePreference = ThemePreference(context)
    val themeMode = themePreference.themeFlow.collectAsState(initial = ThemeMode.SYSTEM.value).value
    val isDarkTheme = when (ThemeMode.fromInt(themeMode)) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }
    val categoryViewModel: CategoryViewModel = viewModel()
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Expense", "Income")
    var selectCategory by remember { mutableStateOf<List<CategoryModel>>(emptyList()) }
    LaunchedEffect(Unit) {
        categoryViewModel.fetchCategories(context)
        selectCategory = categoryViewModel.categories.value
    }

    LaunchedEffect(selectedTab, categoryViewModel.categories.value) {
        selectCategory = if (selectedTab == 0) {
            categoryViewModel.categories.value.filter { it.type == "expense" }

        } else {
            categoryViewModel.categories.value.filter { it.type == "income" }
        }
    }
    Scaffold(
        containerColor = if (isDarkTheme) DarkBackgroundColor else WhiteBackgroundColor,
        topBar = {
        TopBar(
            title = "Category Manage",
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
        )
    },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate("addCategory")
                },
                containerColor = PrimaryColor
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Category",
                    tint = Color.White
                )
            }
        }
        ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            TabBar(selectedTab= selectedTab, tabs = tabs , onTabSelected = {
                selectedTab = it
            })
            Spacer(Modifier.height(16.dp))
            if (selectCategory.isEmpty()) {
                DataNotFound()
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f)
                ) {
                    items(selectCategory) { category ->
                        CategoryItem(category, navController)
                    }
                }
            }
        }

    }
}



@Composable
fun CategoryItem(category: CategoryModel, navController: NavController) {
    val context = LocalContext.current
    var themePreference = ThemePreference(context)
    val themeMode = themePreference.themeFlow.collectAsState(initial = ThemeMode.SYSTEM.value).value
    val isDarkTheme = when (ThemeMode.fromInt(themeMode)) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp).clickable {
                navController.currentBackStackEntry?.savedStateHandle?.set("categoryModel", category)
                navController.navigate("editCategory")
            },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = Color.parse("#${category.color.drop(2)}"),
                        shape = RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(text = category.emoji)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = category.categoryName,
                style = MaterialTheme.typography.titleLarge.copy(color = if (isDarkTheme) Color.White else Color.Black)
            )
        }
    }
}




@Preview(showBackground = true)
@Composable
fun CategoryManageScreenPreview() {
    CategoryManageScreen(rememberNavController())
}