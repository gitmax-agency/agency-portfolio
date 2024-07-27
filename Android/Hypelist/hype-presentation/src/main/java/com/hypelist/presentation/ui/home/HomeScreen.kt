@file:OptIn(ExperimentalMaterialApi::class)

package com.hypelist.presentation.ui.home

import android.app.Activity
import android.os.Build
import android.view.MotionEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.BottomSheetScaffoldState
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material.rememberBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.hypelist.presentation.navigation.NavScreenRoutes
import com.hypelist.presentation.theme.Background
import com.hypelist.presentation.theme.BottomSheetDialog
import com.hypelist.presentation.theme.SuaveRed
import com.hypelist.presentation.ui.categories.CategorySelectionPopUp
import com.hypelist.presentation.ui.home.bottom.BottomNavigationBar
import com.hypelist.presentation.ui.home.pager.DiscoverTabContents
import com.hypelist.presentation.ui.home.top.HomeTopAppBar
import com.hypelist.presentation.ui.hype_list.list.from_following.HypeListFromLoggedUserFollowingPagerColumn
import com.hypelist.presentation.ui.hype_list.list.from_saved.HypeListFromLoggedUserSavedPagerColumn
import com.hypelist.presentation.ui.hype_list.list.from_user.HypeListFromLoggedUserPagerColumn
import com.hypelist.presentation.uicomponents.header.TabsRowView
import com.hypelist.resources.R
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

/**
 * Creates the main home screen ui and also holds the creating flow inside BottomSheetScaffold
 */
@Composable
fun HomeScreen(navController: NavHostController) {
    val systemUiController = rememberSystemUiController()
    systemUiController.setSystemBarsColor(color = Color.White, darkIcons = true)

    val homeViewModel = koinViewModel<HomeViewModel>()
    homeViewModel.updateLoggedInUser()
    homeViewModel.updateAvatarFromCache()

    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberBottomSheetState(BottomSheetValue.Collapsed)
    )
    val activeViewPagerTabState = rememberSaveable { mutableIntStateOf(0) }

    BottomSheetScaffold(
        sheetShape = RoundedCornerShape(
            topStart = 12.dp,
            topEnd = 12.dp,
        ),
        sheetBackgroundColor = BottomSheetDialog,
        sheetPeekHeight = 0.dp,
        scaffoldState = scaffoldState,
        sheetGesturesEnabled = true,
        sheetContent = {
            CategorySelectionPopUp(
                scaffoldState = scaffoldState,
                navController = navController,
            )
        },
    ) {
        HomeScreenContent(
            homeViewModel = homeViewModel,
            navController = navController,
            scaffoldState = scaffoldState,
            activeViewPagerTabState = activeViewPagerTabState,
        )
    }
}

/**
 * Creates the main home screen ui content.
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeScreenContent(
    activeViewPagerTabState: MutableState<Int>,
    homeViewModel: HomeViewModel,
    navController: NavHostController,
    scaffoldState: BottomSheetScaffoldState,
) {
    val scope = rememberCoroutineScope()
    val homeTabActive by homeViewModel.homeTabActiveFlow.collectAsState()
    val photoAvatar by homeViewModel.userAvatarFlow.collectAsState()
    val activity = LocalContext.current as Activity

    BackHandler {
        // It's showing the category bottom sheet.
        if (scaffoldState.bottomSheetState.isExpanded) {
            scope.launch { scaffoldState.bottomSheetState.collapse() }
        }
        // It's already on the home tab.
        else if (homeTabActive) {
            activity.finishAffinity()
        }
        // Show home screen.
        else {
            homeViewModel.toggleHomePage(true)
        }
    }

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Background),
    ) {
        val (appBar, content, bottomBar) = createRefs()
        // Renders the top app bar
        HomeTopAppBar(
            userAvatarPhoto = photoAvatar,
            onUserPhotoClicked = {
                navController.navigate(NavScreenRoutes.PROFILE.value)
            },
            onNotificationClicked = {
                navController.navigate(NavScreenRoutes.NOTIFICATIONS.value)
            },
            modifier = Modifier.constrainAs(appBar) {
                top.linkTo(parent.top)
                end.linkTo(parent.end)
                start.linkTo(parent.start)
            },
        )

        // Renders content
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .constrainAs(content) {
                    end.linkTo(parent.end)
                    start.linkTo(parent.start)
                    top.linkTo(appBar.bottom)
                    bottom.linkTo(bottomBar.top)
                    height = Dimension.fillToConstraints
                },
        ) {
            if (homeTabActive) {
                HomeScreenHypeListPages(
                    navController = navController,
                    activeViewPagerTabState = activeViewPagerTabState,
                )
            } else {
                DiscoverTabContents(
                    navController = navController,
                )
            }

        }

        // Renders bottom bar
        BottomNavigationBar(
            homeViewModel = homeViewModel,
            scaffoldState = scaffoldState,
            modifier = Modifier
                .constrainAs(bottomBar) {
                    end.linkTo(parent.end)
                    start.linkTo(parent.start)
                    bottom.linkTo(parent.bottom)
                },
        )
    }
}

@Composable
fun ColumnScope.HomeScreenHypeListPages(
    navController: NavHostController,
    activeViewPagerTabState: MutableState<Int>,
) {
    TabsRowView(
        tabTitles = listOf(
            LocalContext.current.getString(R.string.your_lists),
            LocalContext.current.getString(R.string.followers_lists),
            LocalContext.current.getString(R.string.saved_lists),
        ),
        activeTab = activeViewPagerTabState,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp),
    )

    when (activeViewPagerTabState.value) {
        0 -> HypeListFromLoggedUserPagerColumn(navController = navController)
        1 -> HypeListFromLoggedUserFollowingPagerColumn(navController = navController)
        2 -> HypeListFromLoggedUserSavedPagerColumn(navController = navController)
    }
}