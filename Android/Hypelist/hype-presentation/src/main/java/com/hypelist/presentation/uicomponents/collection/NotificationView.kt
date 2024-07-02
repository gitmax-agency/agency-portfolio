package com.hypelist.presentation.uicomponents.collection

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.navigation.NavHostController
import com.hypelist.resources.R
import com.hypelist.presentation.uicomponents.control.SmallButton
import java.util.Random

@Composable
fun NotificationView(
    debugName: Int,
    navController: NavHostController,
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
    val debugAction = Random().nextInt(2)

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
            ))

            if (debugAction == 0) {
                Text(text = "started following you. ${Random().nextInt(50) + 1}m", fontSize = 16.sp, fontFamily = FontFamily(
                    Font(R.font.hkgroteskmedium),
                ))
            } else {
                Text(text = "saved your hypelist. ${Random().nextInt(50) + 1}m", fontSize = 16.sp, fontFamily = FontFamily(
                    Font(R.font.hkgroteskmedium),
                ))
            }
        }

        if (debugAction == 0) {
            val isFollowed = remember {
                mutableStateOf(false)
            }

            if (isFollowed.value) {
                SmallButton(modifier = Modifier.constrainAs(right) {
                    end.linkTo(parent.end, 10.dp)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }.fillMaxWidth(0.2f), text = "Following", isGray = true)
            } else {
                SmallButton(modifier = Modifier.constrainAs(right) {
                    end.linkTo(parent.end, 10.dp)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }.fillMaxWidth(0.2f).clickable {
                    isFollowed.value = true
                }, text = LocalContext.current.getString(R.string.follow))
            }
        } else {
            Image(
                contentScale = ContentScale.Crop,
                modifier = Modifier.constrainAs(right) {
                    end.linkTo(parent.end, 10.dp)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }.fillMaxWidth(0.2f).height(45.dp).clip(RoundedCornerShape(8.dp)).clickable {
                    navController.navigateUp()
                },
                painter = painterResource(id = R.drawable.stock1),
                contentDescription = "avatar"
            )
        }
    }
}