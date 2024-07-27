package com.hypelist.presentation.uicomponents.collection

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.hypelist.resources.R

@Composable
fun DiscoverView(modifier: Modifier, title: String, drawable: Int) {
    Box(modifier = modifier) {
        ConstraintLayout(modifier = Modifier.fillMaxSize()) {
            val (cover, name, nameL) = createRefs()

            Image(
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .constrainAs(cover) {
                        start.linkTo(parent.start, 20.dp)
                        end.linkTo(parent.end, 20.dp)
                        top.linkTo(parent.top, 20.dp)
                        bottom.linkTo(parent.bottom, 20.dp)
                        width = Dimension.fillToConstraints
                        height = Dimension.fillToConstraints
                    }
                    .clip(RoundedCornerShape(20.dp)),
                painter = painterResource(id = drawable),
                contentDescription = "cover"
            )

            Text(text = title, modifier = Modifier.constrainAs(nameL) {
                start.linkTo(parent.start, 1.dp)
                end.linkTo(parent.end, (-1).dp)
                top.linkTo(parent.top, 1.dp)
                bottom.linkTo(parent.bottom, (-1).dp)
            }, color = Color.Black, fontSize = 24.sp, fontFamily = FontFamily(
                Font(R.font.hkgroteskmedium),
            ))
            Text(text = title, modifier = Modifier.constrainAs(name) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
            }, color = Color.White, fontSize = 24.sp, fontFamily = FontFamily(
                Font(R.font.hkgroteskmedium),
            ))
        }
    }
}