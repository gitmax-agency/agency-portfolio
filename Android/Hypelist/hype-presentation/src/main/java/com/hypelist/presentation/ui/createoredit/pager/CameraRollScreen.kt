package com.hypelist.presentation.ui.createoredit.pager

import android.content.Intent
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.hypelist.resources.R


@Composable
fun CameraRollScreen(
    modifier: Modifier, launcher: ManagedActivityResultLauncher<Intent, ActivityResult>
) {
    ConstraintLayout(modifier) {
        val (contentsLayout) = createRefs()

        Image(
            modifier = Modifier
                .constrainAs(contentsLayout) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                }
                .width(60.dp)
                .height(60.dp)
                .clickable {
                    val intent = Intent()
                    intent.type = "image/*"
                    intent.action = Intent.ACTION_GET_CONTENT
                    launcher.launch(intent)
                },
            painter = painterResource(
                id = R.drawable.addphoto),
            contentDescription = "addphoto"
        )
    }
}
