package com.hypelist.presentation.ui.auth

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavHostController
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.hypelist.presentation.extensions.collectAsEffect
import com.hypelist.presentation.extensions.collectState
import com.hypelist.presentation.extensions.createViewModel
import com.hypelist.resources.R
import com.hypelist.presentation.navigation.NavScreenRoutes
import com.hypelist.presentation.theme.SuaveRed

@Composable
fun WelcomeScreen(
    navController: NavHostController,
) {
    // System bar
    val context = LocalContext.current
    val systemUiController = rememberSystemUiController()
    systemUiController.setSystemBarsColor(color = Color.Black, darkIcons = false)

    // View model state
    val welcomeViewModel = createViewModel<WelcomeViewModel>()
    welcomeViewModel.command.collectAsEffect { command ->
        when(command) {
            is WelcomeCommands.ShowError -> showAuthError(context)
            is WelcomeCommands.GoToHomeScreen -> navigateToHome(navController)
            is WelcomeCommands.GoToSignupScreen -> navigateToSignup(navController)
        }
    }
    val welcomeState = welcomeViewModel.collectState<WelcomeState>().value ?: return

    // Auth Launcher
    val signInLauncher = rememberLauncherForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { authResult ->
        if (authResult.resultCode == ComponentActivity.RESULT_OK) {
            welcomeViewModel.processAction(WelcomeInteractions.AuthSuccessfully)
        } else {
            welcomeViewModel.processAction(WelcomeInteractions.AuthFailed)
        }
    }
    Image(
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize(),
        painter = painterResource(
        id = R.drawable.welcome_background),
        contentDescription = "welcome_background",
    )

    Box(
        modifier = Modifier
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color.Black
                    )
                )
            )
            .fillMaxSize()
    )

    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val (logoLayout, buttonsLayout, nowLoading) = createRefs()

        Image(
            modifier = Modifier.constrainAs(logoLayout) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                top.linkTo(parent.top)
                bottom.linkTo(buttonsLayout.top)
            },
            painter = painterResource(id = R.drawable.welcome_logo),
            contentDescription = "welcome_logo",
        )

        ConstraintLayout(modifier = Modifier
            .constrainAs(buttonsLayout) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(parent.bottom)
            }
            .fillMaxWidth()
            .fillMaxHeight(0.5f)) {

            val (buttonsColumn, legalNotice) = createRefs()

            Column(modifier = Modifier
                .constrainAs(buttonsColumn) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                },
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally) {

                Image(
                    modifier = Modifier.clickable {
                        launchGoogleAuthFlow(signInLauncher)
                    },
                    painter = painterResource(id = R.drawable.google_button),
                    contentDescription = "google_button",
                )
                Image(
                    modifier = Modifier.clickable {
                        launchGoogleAuthFlow(signInLauncher)
                    },
                    painter = painterResource(id = R.drawable.sign_in_button),
                    contentDescription = "sign_in_button",
                )
            }

            Image(
                modifier = Modifier
                    .constrainAs(legalNotice) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom, 50.dp)
                    }
                    .clickable {
                        navController.navigate(NavScreenRoutes.TERMSOFUSE.value)
                    },
                painter = painterResource(id = R.drawable.legal_notice),
                contentDescription = "legal_notice",
            )
        }

        if (welcomeState.isShowingLoading) {
            CircularProgressIndicator(
                strokeWidth = 3.dp,
                color = SuaveRed,
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
    }
}

fun navigateToHome(navController: NavHostController) {
    navController.navigate(NavScreenRoutes.HOME.value)
}

fun navigateToSignup(navController: NavHostController) {
    navController.navigate(NavScreenRoutes.SIGNUP.value)
}

fun showAuthError(context: Context) {
    Toast.makeText(context, context.getString(R.string.auth_error), Toast.LENGTH_SHORT).show()
}

fun launchGoogleAuthFlow(signInLauncher: ManagedActivityResultLauncher<Intent, FirebaseAuthUIAuthenticationResult>) {
    val signInIntent = AuthUI.getInstance()
        .createSignInIntentBuilder()
        .setAvailableProviders(
            arrayListOf(
                AuthUI.IdpConfig.GoogleBuilder().build(),
            )
        )
        .build()
    signInLauncher.launch(signInIntent)
}