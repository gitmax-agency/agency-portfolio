package com.hypelist.presentation.uicomponents.image

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.hypelist.presentation.theme.ImagePlaceholder
import com.hypelist.presentation.theme.ImagePlaceholderBorder

@Composable
fun ProfileImage(
    drawable: Int,
    scaleType: ContentScale,
    modifier: Modifier
) {
    Box(modifier = modifier.background(color = Color(red = 240, green = 237, blue = 230))) {
        ConstraintLayout(modifier = Modifier.fillMaxSize()) {
            val (photo) = createRefs()

            Image(
                contentScale = scaleType,
                modifier = Modifier.constrainAs(photo) {
                    start.linkTo(parent.start, 10.dp)
                    end.linkTo(parent.end, 10.dp)
                    top.linkTo(parent.top, 10.dp)
                    bottom.linkTo(parent.bottom, 10.dp)
                    width = Dimension.fillToConstraints
                    height = Dimension.fillToConstraints
                },
                painter = painterResource(id = drawable),
                contentDescription = "photo",
            )
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ProfileImage(
    imageUrl: String,
    scaleType: ContentScale,
    modifier: Modifier
) {
    Box(modifier = modifier.background(color = Color(red = 240, green = 237, blue = 230))) {
        ConstraintLayout(modifier = Modifier.fillMaxSize()) {
            val (photo) = createRefs()

            GlideImage(
                model = imageUrl,
                contentScale = scaleType,
                modifier = Modifier.constrainAs(photo) {
                    start.linkTo(parent.start, 10.dp)
                    end.linkTo(parent.end, 10.dp)
                    top.linkTo(parent.top, 10.dp)
                    bottom.linkTo(parent.bottom, 10.dp)
                    width = Dimension.fillToConstraints
                    height = Dimension.fillToConstraints
                },
                contentDescription = "photo",
            )
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ProfileImage(
    imageUrl: String?,
    drawable: Int,
    scaleType: ContentScale,
    modifier: Modifier
) {
    Box(
        modifier = modifier.background(
            color = ImagePlaceholderBorder,
        )
    ) {
        ConstraintLayout(modifier = Modifier.fillMaxSize()) {
            val (photo) = createRefs()

            val imageModifier = Modifier
                .constrainAs(photo) {
                    start.linkTo(parent.start, 6.dp)
                    end.linkTo(parent.end, 6.dp)
                    top.linkTo(parent.top, 6.dp)
                    bottom.linkTo(parent.bottom, 6.dp)
                    width = Dimension.fillToConstraints
                    height = Dimension.fillToConstraints
                }
                .background(color = ImagePlaceholder)

            if (imageUrl != null) {
                GlideImage(
                    model = imageUrl,
                    contentScale = scaleType,
                    contentDescription = "photo",
                    modifier = imageModifier
                )
            } else {
                Box(modifier = imageModifier) {
                    Image(
                        contentScale = scaleType,
                        modifier = Modifier.align(Alignment.Center),
                        painter = painterResource(id = drawable),
                        contentDescription = "photo",
                    )
                }
            }
        }
    }
}