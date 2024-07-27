package com.hypelist.presentation.ui.hype_list.list.common

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.hypelist.entities.hypelist.Hypelist

@Composable
fun ColumnScope.HypeListPagerColumn(
    hypeListData: List<Hypelist>,
    onRefreshDataRequested: () -> Unit,
    emptyDataContent: @Composable () -> Unit,
    navController: NavHostController,
) {
    if (hypeListData.isEmpty()) {
        emptyDataContent()
    } else {
        ListOfHypeListScreen(
            navController = navController,
            hypeListsData = hypeListData,
            onRefreshDataRequested = onRefreshDataRequested,
        )
    }
}