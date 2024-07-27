package com.hypelist.presentation.uicomponents.image

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout

@Composable
fun WhiteShadowedImage(modifier: Modifier, drawable: Int, drawableL: Int) {
    ConstraintLayout(modifier = modifier) {
        val (imageView, imageViewL) = createRefs()

        Image(
            modifier = Modifier.constrainAs(imageViewL) {
                start.linkTo(parent.start, 1.dp)
                end.linkTo(parent.end, (-1).dp)
                top.linkTo(parent.top, 1.dp)
                bottom.linkTo(parent.bottom, (-1).dp)
            },
            painter = painterResource(id = drawableL),
            contentDescription = "lower"
        )
        Image(
            modifier = Modifier.constrainAs(imageView) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
            },
            painter = painterResource(id = drawable),
            contentDescription = "upper"
        )
    }
}

@Composable
fun CustomWhiteShadowedImage(modifier: Modifier, drawable: Int, drawableL: Int, width: Dp, height: Dp) {
    ConstraintLayout(modifier = modifier) {
        val (imageView, imageViewL) = createRefs()

        Image(
            modifier = Modifier.constrainAs(imageViewL) {
                start.linkTo(parent.start, 1.dp)
                end.linkTo(parent.end, (-1).dp)
                top.linkTo(parent.top, 1.dp)
                bottom.linkTo(parent.bottom, (-1).dp)
            }.width(30.dp).height(30.dp),
            painter = painterResource(id = drawableL),
            contentDescription = "lower"
        )
        Image(
            modifier = Modifier.constrainAs(imageView) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
            }.width(30.dp).height(30.dp),
            painter = painterResource(id = drawable),
            contentDescription = "upper"
        )
    }
}