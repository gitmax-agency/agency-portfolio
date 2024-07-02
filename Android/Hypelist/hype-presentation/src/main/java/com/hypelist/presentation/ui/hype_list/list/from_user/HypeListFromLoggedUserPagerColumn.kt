package com.hypelist.presentation.ui.hype_list.list.from_user

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.hypelist.presentation.extensions.collectState
import com.hypelist.presentation.extensions.createViewModel
import com.hypelist.presentation.ui.hype_list.list.common.HypeListPagerState
import com.hypelist.presentation.ui.hype_list.list.common.HypeListPagerColumn
import com.hypelist.presentation.ui.hype_list.list.common.HypeListPagerInteractions

@Composable
fun ColumnScope.HypeListFromLoggedUserPagerColumn(
    navController: NavHostController,
) {

    val viewModel = createViewModel<HypeListFromLoggedUserViewModel>()
    val screenState = viewModel.collectState<HypeListPagerState>().value ?: return

    HypeListPagerColumn(
        navController = navController,
        hypeListData = screenState.hypeListsData,
        onRefreshDataRequested = {
            viewModel.processAction(HypeListPagerInteractions.RefreshData)
        },
        emptyDataContent = {
            EmptyHypeListFromLoggedUser(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 12.dp),
            )
        },
    )
}