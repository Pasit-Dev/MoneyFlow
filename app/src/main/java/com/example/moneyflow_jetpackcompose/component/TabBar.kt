package com.example.moneyflow_jetpackcompose.component

import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.moneyflow_jetpackcompose.ui.theme.BlackColor


@Composable
fun TabBar(selectedTab: Int, tabs: List<String>, onTabSelected: (Int) -> Unit,) {
    TabRow(
        containerColor = Color.Transparent,
        selectedTabIndex = selectedTab,
        indicator = { tabPositions ->
            if (selectedTab < tabPositions.size) {
                TabRowDefaults.PrimaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    width = tabPositions[selectedTab].contentWidth,
                    color = BlackColor
                )
            }
        },
        divider = {}
    ) {
        tabs.forEachIndexed { index, title ->
            Tab(
                selected = selectedTab == index,
                onClick = { onTabSelected(index) },
                text = { Text(text = title, color = if (selectedTab == index) BlackColor else Color.Gray) },

                )
        }
    }
}