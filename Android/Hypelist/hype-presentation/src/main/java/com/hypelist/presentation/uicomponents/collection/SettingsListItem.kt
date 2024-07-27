package com.hypelist.presentation.uicomponents.collection

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.hypelist.resources.R

@Composable
fun SettingsListItem(
    modifier: Modifier, iconDrawable: Int, title: String, shape: RoundedCornerShape
) {
    ConstraintLayout(modifier = modifier.fillMaxWidth()) {
        val (background) = createRefs()

        Box(modifier = Modifier.constrainAs(background) {
            start.linkTo(parent.start, 10.dp)
            end.linkTo(parent.end, 10.dp)
            top.linkTo(parent.top, 1.dp)
            bottom.linkTo(parent.bottom, 1.dp)
            width = Dimension.fillToConstraints
        }.background(color = Color.White, shape = shape)) {
            ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
                val (leftItem, titleView, rightItem) = createRefs()

                Image(modifier = Modifier.constrainAs(leftItem) {
                    start.linkTo(parent.start, 10.dp)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }, painter = painterResource(id = iconDrawable),
                    contentDescription = "left")

                Text(
                    modifier = Modifier.constrainAs(titleView) {
                        start.linkTo(parent.start, 35.dp)
                        top.linkTo(parent.top, 10.dp)
                        bottom.linkTo(parent.bottom, 10.dp)
                    }.padding(start = 10.dp),
                    text = title, fontSize = 20.sp, fontFamily = FontFamily(
                        Font(R.font.hkgroteskmedium),
                    )
                )

                Image(modifier = Modifier.constrainAs(rightItem) {
                    end.linkTo(parent.end, 10.dp)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }, painter = painterResource(id = R.drawable.rightarrow),
                    contentDescription = "right")
            }
        }
    }
}