package com.hypelist.presentation.uicomponents.collection

import android.app.Activity
import android.util.DisplayMetrics
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
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
fun UserToFollow(
    debugFollower: Int, modifier: Modifier, isFollowed: MutableState<Boolean>, onRemoveClick: () -> Unit
    ) {
    val activity = LocalContext.current as Activity
    val displayMetrics: DisplayMetrics = activity.resources.displayMetrics
    val width = displayMetrics.widthPixels
    val density = displayMetrics.density
    val scrollWidth = width / density
    val clipSize = scrollWidth

    ConstraintLayout(modifier) {
        val (background, avatar, name, username, follow, close) = createRefs()

        Image(
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .constrainAs(background) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }
                .fillMaxWidth()
                .fillMaxHeight()
                .clip(RoundedCornerShape(30.dp)),
            painter = painterResource(id = R.drawable.followerbackground),
            contentDescription = "followerbackground")

        val drawable = when (debugFollower) {
            0 -> R.drawable.taylorswift
            1 -> R.drawable.harrykane
            else -> R.drawable.herrera
        }
        val debugNames = arrayOf("Taylor Swift", "Harry Kane", "Carolina Herrera")

        Image(
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .constrainAs(avatar) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(parent.top, 40.dp)
                }
                .fillMaxHeight(0.4f)
                .aspectRatio(1.0f)
                .clip(RoundedCornerShape(clipSize)),
            painter = painterResource(id = drawable),
            contentDescription = "avatar")

        Text(modifier = Modifier.constrainAs(name) {
            start.linkTo(parent.start)
            end.linkTo(parent.end)
            top.linkTo(avatar.bottom)
        }, text = debugNames[debugFollower], fontSize = 20.sp, fontFamily = FontFamily(
            Font(R.font.hkgroteskbold),
        ))

        Text(modifier = Modifier.constrainAs(username) {
            start.linkTo(parent.start)
            end.linkTo(parent.end)
            top.linkTo(name.bottom)
        }, text = "@debug", fontSize = 16.sp, fontFamily = FontFamily(
            Font(R.font.hkgroteskmedium),
        ))

        if (isFollowed.value) {
            SmallButton(
                modifier = Modifier.constrainAs(follow) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(username.bottom, 10.dp)
                }.width(120.dp).height(45.dp), text =
                "Following", isGray = true
            )
        } else {
            SmallButton(
                modifier = Modifier.constrainAs(follow) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(username.bottom, 10.dp)
                }.width(120.dp).height(45.dp).clickable {
                    isFollowed.value = true
                }, text =
                LocalContext.current.getString(R.string.follow)
            )
        }

        Image(
            modifier = Modifier
                .constrainAs(close) {
                    end.linkTo(parent.end, 20.dp)
                    top.linkTo(parent.top, 20.dp)
                }
                .width(20.dp)
                .height(20.dp).clickable {
                    onRemoveClick()
                },
            painter = painterResource(id = R.drawable.close),
            contentDescription = "close")
    }
}