@file:OptIn(ExperimentalMaterialApi::class)

package com.hypelist.presentation.ui.home.bottom

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.BottomSheetScaffoldState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.hypelist.presentation.ui.home.HomeViewModel
import com.hypelist.resources.R
import kotlinx.coroutines.launch

@Composable
fun BottomNavigationBar(
    homeViewModel: HomeViewModel,
    modifier: Modifier, scaffoldState: BottomSheetScaffoldState
) {
    val homeTabActive by homeViewModel.homeTabActiveFlow.collectAsState()
    val scope = rememberCoroutineScope()

    ConstraintLayout(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(color = Color(230, 230, 230, 255))
    ) {
        val (home, centerButton, discover, systemBar) = createRefs()

        Image(
            painter = painterResource(
                id = if (homeTabActive) R.drawable.hometab else R.drawable.hometab_inactive
            ),
            contentDescription = "hometab",
            modifier = Modifier
                .constrainAs(home) {
                    top.linkTo(centerButton.top)
                    bottom.linkTo(centerButton.bottom)
                    end.linkTo(centerButton.start)
                    start.linkTo(parent.start)
                }
                .clickable {
                    homeViewModel.toggleHomePage(true)
                },
        )

        Image(
            painter = painterResource(id = R.drawable.create_hype_list),
            contentDescription = "addition",
            modifier = Modifier
                .constrainAs(centerButton) {
                    top.linkTo(parent.top)
                    end.linkTo(parent.end)
                    start.linkTo(parent.start)
                    bottom.linkTo(systemBar.top)
                }
                .width(40.dp)
                .height(40.dp)
                .clickable {
                    scope.launch {
                        scaffoldState.bottomSheetState.expand()
                    }
                },
        )

        Image(
            painter = painterResource(
                id = if (homeTabActive) R.drawable.discovertab_inactive else R.drawable.discovertab
            ),
            contentDescription = "hometab",
            modifier = Modifier
                .constrainAs(discover) {
                    top.linkTo(centerButton.top)
                    bottom.linkTo(centerButton.bottom)
                    end.linkTo(parent.end)
                    start.linkTo(centerButton.end)
                }
                .clickable {
                    homeViewModel.toggleHomePage(false)
                },
        )

        Spacer(
            modifier = Modifier
                .constrainAs(systemBar) {
                    bottom.linkTo(parent.bottom)
                    end.linkTo(parent.end)
                    start.linkTo(parent.start)
                }
                .height(16.dp)
        )
    }
}