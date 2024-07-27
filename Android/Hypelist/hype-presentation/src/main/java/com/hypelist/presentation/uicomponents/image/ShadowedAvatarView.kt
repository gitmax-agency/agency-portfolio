package com.hypelist.presentation.uicomponents.image

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.hypelist.resources.R

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ShadowedAvatarView(modifier: Modifier, imageUrl: String?) {
    ConstraintLayout(modifier = modifier) {
        val (image) = createRefs()
        val imageModifier = Modifier
            .constrainAs(image) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
            }
            .width(30.dp)
            .height(30.dp)

        if (imageUrl != null) {
            GlideImage(
                model = imageUrl,
                contentScale = ContentScale.Crop,
                contentDescription = "chooser_logo",
                modifier = imageModifier
                    .border(
                        width = 1.dp,
                        color = Color.Black,
                        shape = CircleShape,
                    )
                    .clip(RoundedCornerShape(15.dp)),
            )
        } else {
            Image(
                contentScale = ContentScale.Crop,
                painter = painterResource(id = R.drawable.chooser_placeholder),
                contentDescription = "chooser_logo",
                modifier = imageModifier,
            )
        }
    }
}