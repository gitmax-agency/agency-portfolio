package com.hypelist.presentation.ui.signup

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavHostController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.hypelist.presentation.extensions.collectAsEffect
import com.hypelist.presentation.extensions.collectState
import com.hypelist.presentation.extensions.createViewModel
import com.hypelist.presentation.navigation.NavScreenRoutes
import com.hypelist.presentation.theme.Background
import com.hypelist.presentation.theme.ErrorColor
import com.hypelist.presentation.theme.ImagePlaceholder
import com.hypelist.presentation.theme.SuaveRed
import com.hypelist.presentation.theme.TextPrimary
import com.hypelist.presentation.uicomponents.control.SolidButton
import com.hypelist.presentation.uicomponents.image.ProfileImage
import com.hypelist.presentation.uicomponents.input.SingleLineTextField
import com.hypelist.resources.R

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun SignupScreen(navController: NavHostController) {
    // App System bar
    val focusManager = LocalFocusManager.current
    val interactionSource = remember { MutableInteractionSource() }
    val systemUiController = rememberSystemUiController()
    systemUiController.setSystemBarsColor(color = Color.White, darkIcons = true)

    // View Model
    val signupViewModel = createViewModel<SignupViewModel>()
    signupViewModel.command.collectAsEffect { command ->
        when (command) {
            is SignupCommands.GoToHomeScreen -> {
                navController.navigate(NavScreenRoutes.NOWLOADING.value) { popUpTo(0) }
            }
        }
    }

    // State
    val signupState = signupViewModel.collectState<SignupScreenState>().value ?: return
    val error = rememberSaveable { mutableStateOf(signupState.error) }
    val yourNameState = rememberSaveable { mutableStateOf(signupState.userDisplayName) }
    val userNameState = rememberSaveable { mutableStateOf(signupState.userName) }

    // Photo Picker
    val avatarLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri = result.data?.data?.toString() ?: return@rememberLauncherForActivityResult
                signupViewModel.processAction(SignupInteractions.OnUserProfileImageChanged(uri))
            }
        }
    val coverLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri = result.data?.data?.toString() ?: return@rememberLauncherForActivityResult
                signupViewModel.processAction(SignupInteractions.OnUserCoverImageChanged(uri))
            }
        }

    // Renders UI
    ConstraintLayout(
        Modifier
            .fillMaxSize()
            .background(color = Background)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                focusManager.clearFocus()
            }
    ) {
        val (back, cover, avatar, message, displayName, userName, error, create, nowLoading) = createRefs()

        // Cover Modifier
        val coverModifier = Modifier
            .constrainAs(cover) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                top.linkTo(parent.top)
            }
            .fillMaxWidth()
            .height(140.dp)
            .clickable {
                val intent = Intent()
                intent.type = "image/*"
                intent.action = Intent.ACTION_GET_CONTENT
                coverLauncher.launch(intent)
            }

        // Cover real image.
        if (signupState.imageBackgroundUri != null) {
            GlideImage(
                model = signupState.imageBackgroundUri,
                contentScale = ContentScale.Crop,
                contentDescription = "cover",
                modifier = coverModifier,
            )
        }

        // Cover placeholder
        else {
            Box(
                modifier = coverModifier
                    .background(ImagePlaceholder),
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_add_image_icon),
                    contentDescription = "cover",
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 24.dp),
                )
            }
        }

        // Avatar Modifier
        val avatarModifier = Modifier
            .constrainAs(avatar) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                top.linkTo(cover.bottom)
                bottom.linkTo(cover.bottom)
            }
            .fillMaxWidth(0.33f)
            .aspectRatio(4.0f / 5.0f)
            .offset(y = (-28).dp)
            .clickable {
                val intent = Intent()
                intent.type = "image/*"
                intent.action = Intent.ACTION_GET_CONTENT
                avatarLauncher.launch(intent)
            }

        // Avatar real image.
        ProfileImage(
            imageUrl = signupState.imageProfileUri,
            drawable = R.drawable.ic_profile_icon,
            scaleType = ContentScale.Crop,
            modifier = avatarModifier,
        )

        // Back button
        Image(
            painter = painterResource(id = R.drawable.backrounded),
            contentDescription = "Back Button",
            modifier = Modifier
                .constrainAs(back) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                }
                .padding(start = 24.dp, top = 48.dp)
                .width(32.dp)
                .height(32.dp)
                .clickable {
                    navController.popBackStack()
                },
        )

        // Message
        Text(
            text = "Claim your username and upload a profile picture and cover",
            fontSize = 16.sp,
            lineHeight = 22.sp,
            fontFamily = FontFamily(Font(R.font.hkgroteskbold)),
            color = TextPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .constrainAs(message) {
                    start.linkTo(parent.start, 60.dp)
                    end.linkTo(parent.end, 60.dp)
                    top.linkTo(avatar.bottom)
                    width = Dimension.fillToConstraints
                },
        )

        SingleLineTextField(
            textValue = yourNameState,
            placeholder = LocalContext.current.getString(R.string.name_chooser),
            autofocus = true,
            modifier = Modifier
                .constrainAs(displayName) {
                    start.linkTo(parent.start, 32.dp)
                    end.linkTo(parent.end, 32.dp)
                    top.linkTo(message.bottom, 24.dp)
                    width = Dimension.fillToConstraints
                },
        )
        SingleLineTextField(
            textValue = userNameState,
            placeholder = LocalContext.current.getString(R.string.username_chooser),
            leadingIcon = {
                Image(
                    modifier = Modifier
                        .width(12.dp)
                        .height(12.dp),
                    painter = painterResource(id = R.drawable.username),
                    contentDescription = "user"
                )
            },
            noPadding = true,
            modifier = Modifier
                .constrainAs(userName) {
                    start.linkTo(parent.start, 32.dp)
                    end.linkTo(parent.end, 32.dp)
                    top.linkTo(displayName.bottom, 12.dp)
                    width = Dimension.fillToConstraints
                },
        )

        if (signupState.error != null) {
            Text(
                text = stringResource(id = signupState.error),
                fontSize = 12.sp,
                fontFamily = FontFamily(Font(R.font.hkgrotesk)),
                color = ErrorColor,
                modifier = Modifier.constrainAs(error) {
                    start.linkTo(parent.start, 32.dp)
                    end.linkTo(parent.end, 32.dp)
                    top.linkTo(userName.bottom, 6.dp)
                    width = Dimension.fillToConstraints
                },
            )
        }

        SolidButton(
            text = LocalContext.current.getString(R.string.button_create),
            modifier = Modifier
                .constrainAs(create) {
                    start.linkTo(parent.start, 32.dp)
                    end.linkTo(parent.end, 32.dp)
                    top.linkTo((if (signupState.error != null) error else userName).bottom, 16.dp)
                    width = Dimension.fillToConstraints
                }
                .clickable {
                    signupViewModel.processAction(
                        SignupInteractions.OnSignupClicked(
                            name = yourNameState.value,
                            userName = userNameState.value,
                        )
                    )
                },
        )

        // Progress bar
        if (signupState.isShowingLoading) {
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