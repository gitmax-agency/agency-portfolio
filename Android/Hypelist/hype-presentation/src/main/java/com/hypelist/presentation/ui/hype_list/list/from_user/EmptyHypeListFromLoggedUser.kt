package com.hypelist.presentation.ui.hype_list.list.from_user

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.hypelist.presentation.theme.TextPrimary
import com.hypelist.presentation.theme.TextSecondary
import com.hypelist.resources.R

@Composable
fun EmptyHypeListFromLoggedUser(
    modifier: Modifier = Modifier,
) {
    ConstraintLayout(modifier = modifier) {
        val (carousel, spacer, title, message, indicator) = createRefs()

        HomeFrameCarousel(
            modifier = Modifier
                .constrainAs(carousel) {
                    top.linkTo(parent.top)
                    end.linkTo(parent.end)
                    start.linkTo(parent.start)
                    bottom.linkTo(spacer.top)
                    height = Dimension.fillToConstraints
                }
                .padding(top = 12.dp),
        )

        Spacer(
            modifier = Modifier
                .constrainAs(spacer) {
                    end.linkTo(parent.end)
                    start.linkTo(parent.start)
                    bottom.linkTo(title.top)
                }
                .height(26.dp)
        )

        Text(
            text = stringResource(id = R.string.home_create_title),
            style = TextStyle(
                color = TextPrimary,
                fontSize = 19.sp,
                lineHeight = 22.sp,
                textAlign = TextAlign.Center,
                fontFamily = FontFamily(Font(R.font.hkgroteskbold)),
            ),
            modifier = Modifier
                .constrainAs(title) {
                    end.linkTo(parent.end)
                    start.linkTo(parent.start)
                    bottom.linkTo(message.top)
                }
                .padding(bottom = 18.dp),
        )

        Text(
            text = stringResource(id = R.string.home_create_message),
            style = TextStyle(
                color = TextSecondary,
                fontSize = 13.sp,
                lineHeight = 20.sp,
                textAlign = TextAlign.Center,
                fontFamily = FontFamily(Font(R.font.technoscriptef)),
            ),
            modifier = Modifier
                .constrainAs(message) {
                    end.linkTo(parent.end)
                    start.linkTo(parent.start)
                    bottom.linkTo(indicator.top)
                }
                .padding(bottom = 18.dp),
        )

        Image(
            painter = painterResource(id = R.drawable.onboarding_pointer),
            contentDescription = "onboarding_pointer",
            modifier = Modifier
                .width(50.dp)
                .constrainAs(indicator) {
                    end.linkTo(parent.end)
                    start.linkTo(parent.start)
                    bottom.linkTo(parent.bottom)
                }
                .offset(x = 12.dp),
        )
    }
}


@Composable
fun HomeFrameCarousel(modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        val localDensity = LocalDensity.current

        val frameWidth = remember { mutableStateOf(0.dp) }
        val frameHeight = remember { mutableStateOf(0.dp) }
        val offsetX = remember { mutableStateOf(0.dp) }
        val offsetY = remember { mutableStateOf(0.dp) }

        Image(
            painter = painterResource(id = R.drawable.ic_frame),
            contentScale = ContentScale.FillBounds,
            contentDescription = "photo_resource",
            modifier = Modifier
                .aspectRatio(306f / 371f)
                .onSizeChanged {
                    val targetFrameWidth = it.width * 0.75
                    val targetFrameHeight = it.height * 0.9
                    offsetX.value = ((it.width - targetFrameWidth) / 10 / localDensity.density).dp
                    offsetY.value = ((it.height - targetFrameHeight) / 6 / localDensity.density).dp

                    frameWidth.value = (targetFrameWidth / localDensity.density).dp
                    frameHeight.value = (targetFrameHeight / localDensity.density).dp
                },
        )

        val composition by rememberLottieComposition(
            LottieCompositionSpec.RawRes(R.raw.animation_home_carousel_lottie),
        )
        LottieAnimation(
            composition = composition,
            iterations = LottieConstants.IterateForever,
            modifier = Modifier
                .align(Alignment.Center)
                .width(frameWidth.value)
                .offset(x = -offsetX.value, y = offsetY.value)
        )
    }
}