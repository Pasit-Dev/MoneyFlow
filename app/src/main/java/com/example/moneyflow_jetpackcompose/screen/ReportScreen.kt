package com.example.moneyflow_jetpackcompose.screen

import android.annotation.SuppressLint
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Subscriptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moneyflow_jetpackcompose.R
import com.example.moneyflow_jetpackcompose.component.DataNotFound
import com.example.moneyflow_jetpackcompose.component.MonthYearPickerDialog
import com.example.moneyflow_jetpackcompose.component.TabBar
import com.example.moneyflow_jetpackcompose.component.ToggleButton
import com.example.moneyflow_jetpackcompose.component.TopBar
import com.example.moneyflow_jetpackcompose.datastore.DataStoreManager
import com.example.moneyflow_jetpackcompose.ui.theme.BlackColor
import com.example.moneyflow_jetpackcompose.ui.theme.DarkBackgroundColor
import com.example.moneyflow_jetpackcompose.ui.theme.PrimaryColor
import com.example.moneyflow_jetpackcompose.ui.theme.ThemeMode
import com.example.moneyflow_jetpackcompose.ui.theme.ThemePreference
import com.example.moneyflow_jetpackcompose.ui.theme.WhiteBackgroundColor
import com.example.moneyflow_jetpackcompose.ui.theme.parse
import com.example.moneyflow_jetpackcompose.utils.formatCompactValue
import com.example.moneyflow_jetpackcompose.viewmodel.TransactionViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.Month
import java.time.Year
import kotlin.math.exp
import kotlin.random.Random


@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun ReportScreen(viewModel: TransactionViewModel = viewModel()) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("By Category", "By Time")
    var selectType by remember { mutableStateOf("income") }
    var selectedMonth by remember { mutableStateOf(LocalDate.now().month) }
    var selectedYear by remember { mutableIntStateOf(Year.now().value) }
    var showDialog by remember { mutableStateOf(false) }
    // category model




    val monthlyData = listOf(
        45f, 60f, 75f, 80f, 65f, 55f,
        70f, 85f, 90f, 82f, 78f, 95f
    )

    LaunchedEffect(selectType, viewModel.transactions.value) {
        viewModel.groupTransaction(viewModel.transactions.value, selectType)
    }

    // ✅ ใช้ remember เพื่อให้ค่าคำนวณใหม่เมื่อ selectType หรือ transactions เปลี่ยน
//    val groupedTransactions by remember(selectType, viewModel.transactions.value) {
//        derivedStateOf {
//            viewModel.transactions.value
//                .filter { it.type == selectType }
//                .groupBy { it.categoryName ?: "Uncategorized" }
//                .mapValues { entry ->
//                    entry.value to entry.value.sumOf { it.amount }
//                }
//        }
//    }
//
//    val totalAmount by remember { derivedStateOf { groupedTransactions.values.sumOf { it.second } } }
//
//    val pieData by remember(totalAmount) {
//        derivedStateOf {
//            if (totalAmount > 0) {
//                groupedTransactions.map { (categoryName, pair) ->
//                    val percentage = (pair.second / totalAmount) * 100
//                    val categoryColor = pair.first.firstOrNull()?.color?.let { Color.parse("#${it.drop(2)}") } ?: Color.Gray
//                    PieData(
//                        value = percentage.toInt(),
//                        color = categoryColor
//                    )
//                }
//            } else {
//                emptyList()
//            }
//        }
//    }

    val groupedTransactions by viewModel.groupedTransactions.collectAsState()
    val totalAmount by viewModel.totalAmount.collectAsState()

    // แสดงผล
    val pieData = if (totalAmount > 0) {
        groupedTransactions.map { (categoryName, pair) ->
            val percentage = (pair.second / totalAmount) * 100
            val categoryColor = pair.first.firstOrNull()?.color?.let { Color.parse("#${it.drop(2)}") } ?: Color.Gray
            PieData(
                value = percentage.toInt(),
                color = categoryColor
            )
        }
    } else {
        emptyList()
    }



    val maxValue = monthlyData.maxOrNull() ?: 0f

    var selectedTime by remember { mutableStateOf("Month") }
    val themeMode = ThemePreference(context = LocalContext.current).themeFlow.collectAsState(initial = ThemeMode.SYSTEM.value).value
    val isDarkTheme = when (ThemeMode.fromInt(themeMode)) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }
    var userId by remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        userId = DataStoreManager.getToken(context) ?: ""
    }
    LaunchedEffect(userId, selectedMonth, selectedYear) {
        if (userId.isNotEmpty()) {
            viewModel.fetchTransactionByMonth(
                userId,
                selectedMonth.getDisplayName(java.time.format.TextStyle.SHORT, Locale.current.platformLocale),
                selectedYear.toString()
            )
        }
    }

    Scaffold(
        containerColor = if (isDarkTheme) DarkBackgroundColor else WhiteBackgroundColor,

        topBar = {
            TopBar(
                title = "Report",
                actions = {
                    ToggleButton(
                        text = "Month",
                        isSelected = selectedTime == "Month",
                        onClick = { selectedTime = "Month" }
                    )

                    Spacer(Modifier.width(8.dp))
                    ToggleButton(
                        text = "Year",
                        isSelected = selectedTime == "Year",
                        onClick = { selectedTime = "Year" }
                    )
                }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            TabBar(
                selectedTab = selectedTab,
                tabs = tabs,
                onTabSelected = { selectedTab = it }
            )
            Spacer(Modifier.height(16.dp))
            TextButton(onClick = { showDialog = true }) {
                Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                    androidx.compose.material.Text(
                        text = "${selectedMonth.getDisplayName(java.time.format.TextStyle.SHORT, Locale.current.platformLocale)} $selectedYear",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(Modifier.width(8.dp))
                    Icon(Icons.Filled.KeyboardArrowDown, contentDescription = null, Modifier.size(32.dp), tint= BlackColor)
                }
            }
            if (showDialog) {
                MonthYearPickerDialog(
                    showDialog = showDialog,
                    onDismiss = { showDialog = false },
                    onConfirm = { month, year ->
                        selectedMonth = month
                        selectedYear  = year
                        showDialog = false
                    }
                )
            }
            when (selectedTab) {
                0 -> Column(horizontalAlignment = Alignment.CenterHorizontally) {

                    if (viewModel.income.value.toInt() == 0 && viewModel.expense.value.toInt() == 0) {
                        DataNotFound()
                    } else {
                        AnimatedGapPieChart(modifier = Modifier.padding(32.dp), pieDataPoints = pieData, centerTitle = selectType )
                        SummariesByCategory(
                            selectExpense = {
                                selectType =  "expense"
                            } ,
                            selectIncome = {
                                selectType = "income"
                            },
                            income = viewModel.income.value.toString(),
                            expense = viewModel.expense.value.toString()
                        )
                        Spacer(Modifier.height(16.dp))





                        LazyColumn(modifier = Modifier.padding(horizontal = 16.dp)) {
                            items(groupedTransactions.toList(), key = { it.first }) { (categoryName, pair) ->
                                val totalAmount = pair.second
                                CategoryItem(categoryName, totalAmount.toString())
                            }
                        }
                    }
                }

                1 -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                   if (viewModel.transactions.value.isEmpty()) {
                       DataNotFound()
                   } else {
                       if (selectedTime == "Month") {
                           MonthlyBarChart(modifier = Modifier.fillMaxWidth())
                       } else {
                           YearlyBarChart(
                               data = monthlyData,
                               maxValue = maxValue,
                               modifier = Modifier.padding(16.dp)
                           )
                       }
                   }
                }
            }
        }
    }
}





@Composable
fun MonthlyBarChart(modifier: Modifier = Modifier) {
    val days = (1..31).map { it.toString() }
    val heights = List(days.size) { Random.nextFloat() * 0.9f + 0.1f }
    val scrollState = rememberScrollState()

    var selectedDay by remember { mutableStateOf<Int?>(null) }

    Column(modifier = modifier) {

        Row(
            modifier = Modifier
                .horizontalScroll(scrollState)
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            days.forEachIndexed { index, day ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(bottom = 8.dp).clip(RoundedCornerShape(8.dp)) .clickable { selectedDay = index }
                        .alpha(if (selectedDay == index) 1f else 0.7f)
                ) {
                    Text(
                        text = "${formatCompactValue((heights[index] * 100))}",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Box(
                        modifier = Modifier
                            .width(30.dp)
                            .height((150 * heights[index]).dp)
                            .background(
                                color = Color(0xFF4CAF50),
                                shape = MaterialTheme.shapes.small
                            )
                    )
                    Text(
                        text = day,
                        modifier = Modifier.padding(top = 8.dp),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        // Optional: Add ScrollBar
        ScrollBar(scrollState)
    }
}

@Composable
private fun ScrollBar(
    scrollState: ScrollState,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(2.dp)
            .background(Color.LightGray)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(
                    if (scrollState.maxValue == 0) 1f else
                        scrollState.value.toFloat() / scrollState.maxValue.toFloat()
                )
                .background(Color(0xFF4CAF50))
        )
    }
}



@Composable
fun YearlyBarChart(
    data: List<Float> = List(12) { Random.nextFloat() * 0.9f + 0.1f },
    maxValue: Float = data.maxOrNull() ?: 1f,
    modifier: Modifier = Modifier
) {
    val months = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
    var selectedMonth by remember { mutableStateOf<Int?>(null) }
    var m by remember { mutableStateOf("") }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            months.forEachIndexed { index, month ->
                // คำนวณความสูงตามสัดส่วนของค่าเทียบกับค่าสูงสุด
                val normalizedHeight = (data[index] / maxValue)

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { selectedMonth = index
                            m = months[index]
                        }
                        .alpha(if (selectedMonth == index) 1f else 0.7f)
                        .padding(bottom = 8.dp)
                ) {
                    Text(
                        text = "${formatCompactValue((data[index] * 100))}",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height((150 * normalizedHeight).dp)  // ใช้ค่าที่ normalize แล้ว
                            .background(
                                color = Color(0xFF4CAF50),
                                shape = MaterialTheme.shapes.small
                            )
                    )
                    Text(
                        text = month,
                        modifier = Modifier.padding(top = 8.dp),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
fun SummariesByCategory(selectIncome: () -> Unit, selectExpense: () -> Unit, income: String, expense: String) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SummaryItem(R.drawable.trending_up_line,"$income THB", Color(0xFF4CAF50), onClick = selectIncome)
        SummaryItem(R.drawable.trending_down_line, "$expense THB", Color(0xFFFF3C2F), onClick = selectExpense)
    }
}

@Composable
fun SummaryItem(icon: Int, amount: String, iconColor: Color, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.clip(shape = RoundedCornerShape(8.dp)).clickable {
            onClick()
        }.padding(4.dp)
    ) {
       Box(modifier = Modifier.size(32.dp).background(color = iconColor, shape = RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
           Icon(
            painter = painterResource(id = icon),
//               Icons.Filled.Subscriptions,
               contentDescription = null,
               tint = Color.White,
               modifier = Modifier.size(24.dp)
           )
       }
        Text(text = amount, color = Color.Gray)
    }

}
data class PieData (
    val value: Int,
    val color: Color
)

@Composable
fun CategoryList(dataList: List<Pair<String ,String>>) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        dataList.forEach { (category, amount) ->
            CategoryItem(category, amount)
//            HorizontalDivider(color = Color(0xFF0E0E0E), thickness = 1.dp)
        }
    }
}

@Composable
fun CategoryItem(category: String, amount: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(Color(0xFFD32F2F), shape = RoundedCornerShape(8.dp)).padding(6.dp),
                contentAlignment = Alignment.Center
            ) {
                // Replace with appropriate category icons
                Icon(Icons.Default.Fastfood, contentDescription = null, tint = Color.White)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = category, fontSize = 16.sp)
        }
        Text(text = amount, fontSize = 16.sp, color = Color(0xFFF44336))
    }
}

data class ArcData(
    val animation: Animatable<Float, AnimationVector1D>,
    val targetSweepAngle: Float,
    val startAngle: Float,
    val color: Color,
)

@Composable
fun AnimatedGapPieChart(
    modifier: Modifier = Modifier,
    pieDataPoints: List<PieData>,
    centerTitle: String,
    viewModel: TransactionViewModel = viewModel()
) {
    val gapDegrees = 16f
    val numberOfGaps = pieDataPoints.size
    val remainingDegrees = 360f - (gapDegrees * numberOfGaps)
    val localModifier = modifier.size(200.dp)
    val total = pieDataPoints.fold(0f) { acc, pieData -> acc + pieData.value }.div(remainingDegrees)
    var currentSum = 0f
    val arcs = pieDataPoints.mapIndexed { index, it ->
        val startAngle = currentSum + (index * gapDegrees)
        currentSum += it.value / total
        ArcData(
            targetSweepAngle = it.value / total,
            animation = Animatable(0f),
            startAngle = -90 + startAngle,
            color = it.color
        )
    }

    Box(modifier=  Modifier.fillMaxWidth(),  contentAlignment = Alignment.Center) {
        LaunchedEffect(arcs) {
            arcs.mapIndexed { index, it ->
                launch {
                    it.animation.animateTo(
                        targetValue = it.targetSweepAngle,
                        animationSpec = tween(
                            durationMillis = 1000,
                            easing = LinearEasing,
                            delayMillis = index * 1000
                        ),
                    )
                }
            }
        }

        Canvas(
            modifier = localModifier
                .scale(1f)
        ) {
            val stroke = Stroke(width = 50f, cap = StrokeCap.Round)

            arcs.map {
                drawArc(
                    startAngle = it.startAngle,
                    sweepAngle = it.animation.value,
                    color = it.color,
                    useCenter = false,
                    style = stroke,
                )
            }
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (centerTitle == "income") "TOTAL INCOME" else "TOTAL EXPENSES",
                style = TextStyle(
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            )
            Text(
                text = if (centerTitle == "income") "฿${viewModel.income.value}" else "฿${viewModel.expense.value}",
                style = TextStyle(
                    color = Color.Black,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

@Preview (showBackground = true)
@Composable
fun ReportScreenPreview() {
    ReportScreen()
}