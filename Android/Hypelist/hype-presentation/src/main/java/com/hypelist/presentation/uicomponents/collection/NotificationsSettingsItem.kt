package com.hypelist.presentation.uicomponents.collection

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.hypelist.resources.R

@Composable
fun NotificationsSettingsItem() {
    ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
        val (background) = createRefs()

        Box(modifier = Modifier.constrainAs(background) {
            start.linkTo(parent.start, 10.dp)
            end.linkTo(parent.end, 10.dp)
            top.linkTo(parent.top, 1.dp)
            bottom.linkTo(parent.bottom, 1.dp)
            width = Dimension.fillToConstraints
        }.background(color = Color.White, shape = RoundedCornerShape(15.dp))) {
            ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
                val (leftItem, titleView, rightItem) = createRefs()

                Image(modifier = Modifier.constrainAs(leftItem) {
                    start.linkTo(parent.start, 10.dp)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }, painter = painterResource(id = R.drawable.notifications),
                    contentDescription = "left")

                Text(
                    modifier = Modifier.constrainAs(titleView) {
                        start.linkTo(parent.start, 35.dp)
                        top.linkTo(parent.top, 10.dp)
                        bottom.linkTo(parent.bottom, 10.dp)
                    }.padding(start = 10.dp),
                    text = LocalContext.current.getString(R.string.settings_notifications), fontSize = 20.sp, fontFamily = FontFamily(
                        Font(R.font.hkgroteskmedium),
                    )
                )

                var checked by remember { mutableStateOf(true) }
                Switch(
                    modifier = Modifier.constrainAs(rightItem) {
                        end.linkTo(parent.end, 10.dp)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                    }.semantics { contentDescription = "Demo" },
                    checked = checked,
                    onCheckedChange = { checked = it })
            }
        }
    }
}