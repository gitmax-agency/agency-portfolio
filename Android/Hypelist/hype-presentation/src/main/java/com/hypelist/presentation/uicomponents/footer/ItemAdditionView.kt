package com.hypelist.presentation.uicomponents.footer

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ItemAdditionView(modifier: Modifier, drawable: Int, color: Color, title: String, text: String) {
    Box(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .padding(vertical = 5.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(color.copy(alpha = 0.1f))
        ) {
            Row(modifier = Modifier.padding(start = 10.dp, top = 5.dp),
                verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                Image(
                    modifier = Modifier.width(30.dp).height(30.dp),
                    painter = painterResource(id = drawable),
                    contentDescription = "icon")
                Text(
                    text = title,
                    modifier = Modifier
                        .fillMaxWidth(),
                    color = Color.Black, fontSize = 16.sp,
                    fontFamily = FontFamily(Font(com.hypelist.resources.R.font.hkgroteskbold))
                )
            }
            Text(
                text = text,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 0.dp),
                color = Color.Black, fontSize = 16.sp,
                fontFamily = FontFamily(Font(com.hypelist.resources.R.font.hkgroteskmedium))
            )
        }
    }
}