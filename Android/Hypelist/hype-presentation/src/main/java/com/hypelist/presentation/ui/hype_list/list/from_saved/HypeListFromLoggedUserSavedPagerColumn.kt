package com.hypelist.presentation.ui.hype_list.list.from_saved

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.hypelist.presentation.extensions.collectState
import com.hypelist.presentation.extensions.createViewModel
import com.hypelist.presentation.ui.hype_list.list.common.HypeListPagerColumn
import com.hypelist.presentation.ui.hype_list.list.common.HypeListPagerInteractions
import com.hypelist.presentation.ui.hype_list.list.common.HypeListPagerState

@Composable
fun ColumnScope.HypeListFromLoggedUserSavedPagerColumn(
    navController: NavHostController,
) {
    val viewModel = createViewModel<HypeListFromLoggedUserSavedViewModel>()
    val screenState = viewModel.collectState<HypeListPagerState>().value ?: return

    HypeListPagerColumn(
        navController = navController,
        hypeListData = screenState.hypeListsData,
        onRefreshDataRequested = {
            viewModel.processAction(HypeListPagerInteractions.RefreshData)
        },
        emptyDataContent = {
            EmptyHypeListFromSaved(modifier = Modifier.weight(1.0f))
        },
    )
}