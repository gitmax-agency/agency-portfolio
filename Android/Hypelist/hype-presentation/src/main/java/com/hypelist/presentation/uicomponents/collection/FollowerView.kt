package com.hypelist.presentation.uicomponents.collection

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.hypelist.resources.R
import com.hypelist.presentation.uicomponents.control.SmallButton

@Composable
fun FollowerView(
    debugName: Int,
    modifier: Modifier
) {
    val nameStr = when (debugName) {
        0 -> "Taylor Swift"
        1 -> "Harry Kane"
        else -> "Carolina Herrera"
    }
    val drawable = when (debugName) {
        0 -> R.drawable.taylorswift
        1 -> R.drawable.harrykane
        else -> R.drawable.herrera
    }

    ConstraintLayout(modifier) {
        val (avatar, name, right) = createRefs()

        Image(
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .constrainAs(avatar) {
                    start.linkTo(parent.start, 10.dp)
                    top.linkTo(parent.top, 10.dp)
                    bottom.linkTo(parent.bottom, 10.dp)
                }
                .width(50.dp)
                .height(50.dp)
                .clip(RoundedCornerShape(25.dp)),
            painter = painterResource(id = drawable),
            contentDescription = "avatar"
        )

        Column(modifier = Modifier.constrainAs(name) {
            start.linkTo(avatar.end, 10.dp)
            top.linkTo(parent.top, 10.dp)
            bottom.linkTo(parent.bottom, 10.dp)
        }.fillMaxWidth(0.55f)) {
            Text(text = nameStr, fontSize = 20.sp, fontFamily = FontFamily(
                Font(R.font.hkgroteskbold),
            )
            )

            Text(text = "@debug", fontSize = 16.sp, fontFamily = FontFamily(
                Font(R.font.hkgroteskmedium),
            )
            )
        }

        SmallButton(modifier = Modifier.constrainAs(right) {
            end.linkTo(parent.end, 10.dp)
            top.linkTo(parent.top)
            bottom.linkTo(parent.bottom)
        }.width(100.dp).height(45.dp), text = LocalContext.current.getString(R.string.follow))
    }
}