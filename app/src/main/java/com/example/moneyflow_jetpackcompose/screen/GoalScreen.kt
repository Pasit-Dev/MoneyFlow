package com.example.moneyflow_jetpackcompose.screen

import android.os.Parcelable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.moneyflow_jetpackcompose.R
import com.example.moneyflow_jetpackcompose.component.DataNotFound
import com.example.moneyflow_jetpackcompose.component.TopBar
import com.example.moneyflow_jetpackcompose.datastore.DataStoreManager
import com.example.moneyflow_jetpackcompose.model.GoalModel
import com.example.moneyflow_jetpackcompose.ui.theme.DarkBackgroundColor
import com.example.moneyflow_jetpackcompose.ui.theme.PrimaryColor
import com.example.moneyflow_jetpackcompose.ui.theme.ThemeMode
import com.example.moneyflow_jetpackcompose.ui.theme.ThemePreference
import com.example.moneyflow_jetpackcompose.ui.theme.WhiteBackgroundColor
import com.example.moneyflow_jetpackcompose.utils.base64ToBitmap
import com.example.moneyflow_jetpackcompose.utils.formatDate
import com.example.moneyflow_jetpackcompose.viewmodel.GoalViewModel
import kotlinx.parcelize.Parcelize
import java.time.format.TextStyle

@Composable
fun GoalScreen(navController: NavController, goalViewModel: GoalViewModel = viewModel()) {
    val context = LocalContext.current
    var themePreference = ThemePreference(context)
    val themeMode = themePreference.themeFlow.collectAsState(initial = ThemeMode.SYSTEM.value).value
    val isDarkTheme = when (ThemeMode.fromInt(themeMode)) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }
    var userId by remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        userId = DataStoreManager.getToken(context) ?: ""
    }

    // ดึงข้อมูลเมื่อ userId หรือเดือน/ปีเปลี่ยน
    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            goalViewModel.fetchGoal(userId)
        }
    }
    Scaffold(
        containerColor = if (isDarkTheme) DarkBackgroundColor else WhiteBackgroundColor,
        topBar = {
            TopBar(
                title = "Goals"
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate("addGoal")
                },
                containerColor = PrimaryColor
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Goal",
                    tint = Color.White
                )
            }
        }
    ) { innerPadding ->
        if (goalViewModel.goals.value.isEmpty()) {
            DataNotFound()
        } else {
            Column(modifier = Modifier.fillMaxSize().padding(innerPadding).padding(horizontal = 16.dp)) {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(goalViewModel.goals.value) { goal ->
                        GoalListItem(goalItem =  goal, onClick = {
                            navController.currentBackStackEntry?.savedStateHandle?.set("goalItem", goal)
                            navController.navigate("goalDetail")
                        })
                        Spacer(Modifier.height(12.dp))
                    }
                }
            }
        }

    }
}

@Composable
fun GoalListItem(
    goalItem: GoalModel, onClick: () -> Unit
) {
    val context = LocalContext.current
    var themePreference = ThemePreference(context)
    val themeMode = themePreference.themeFlow.collectAsState(initial = ThemeMode.SYSTEM.value).value
    val isDarkTheme = when (ThemeMode.fromInt(themeMode)) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }
    Card(
        colors = CardDefaults.cardColors(
            if (isDarkTheme) Color.Black else Color.White
        ),
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable {
                onClick()
            },
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF121212))
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                if (base64ToBitmap(goalItem.img) != null) {
                    Image(
                        bitmap = base64ToBitmap(goalItem.img)!!.asImageBitmap(),
                        contentDescription = "Profile Image",
                        modifier = Modifier.size(60.dp).clip(RoundedCornerShape(4.dp))
                    )
                } else {
                    Box(modifier = Modifier.size(24.dp)) {
                        CircularProgressIndicator(
                            color = PrimaryColor,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = goalItem.name,
                    style = MaterialTheme.typography.titleMedium.copy(color = if (isDarkTheme) Color.White else Color.Black)
                )
                Row {
                    Text(
                        text = formatDate(goalItem.startDate),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                    Spacer(Modifier.width(8.dp))
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = formatDate(goalItem.endDate),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LinearProgressIndicator(
                        trackColor = WhiteBackgroundColor,
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
                        style = MaterialTheme.typography.bodySmall.copy(Color.Gray)
                    )
                }
            }
        }
    }
}

@Preview (showBackground = true)
@Composable
fun GoalScreenPreview() {
    GoalScreen(rememberNavController())
}