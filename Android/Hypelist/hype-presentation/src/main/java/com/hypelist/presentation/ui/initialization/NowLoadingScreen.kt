package com.hypelist.presentation.ui.initialization

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavHostController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.hypelist.presentation.extensions.collectAsEffect
import com.hypelist.presentation.extensions.createViewModel
import com.hypelist.presentation.navigation.NavScreenRoutes
import com.hypelist.presentation.theme.ProgressBar

@Composable
fun NowLoadingScreen(navigator: NavHostController) {
    val systemUiController = rememberSystemUiController()
    systemUiController.setSystemBarsColor(color = Color.White, darkIcons = true)
    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val (nowLoading) = createRefs()

        CircularProgressIndicator(
            strokeWidth = 3.dp,
            color = ProgressBar,
            modifier = Modifier
                .constrainAs(nowLoading) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }
                .size(75.dp)
                .padding(vertical = 4.dp)
        )
    }

    val viewModel = createViewModel<NowLoadingViewModel>()
    viewModel.command.collectAsEffect { command ->
        when(command) {
            is NowLoadingCommands.NavigateToHomeScreen -> {
                navigator.navigate(NavScreenRoutes.HOME.value) { popUpTo(0) }
            }
            is NowLoadingCommands.NavigateToWelcomeScreen -> {
                navigator.navigate(NavScreenRoutes.WELCOME.value) { popUpTo(0) }
            }
        }
    }

}