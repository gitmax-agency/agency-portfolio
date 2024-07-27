package com.hypelist.presentation.uicomponents.header

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout

@Composable
fun SegmentedControlView(
    modifier: Modifier, items: List<String>, currentSegment: MutableIntState, onClick: (Int) -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    Row(modifier.fillMaxWidth().background(color = Color(245, 242, 240), shape = RoundedCornerShape(5.dp)).clip(RoundedCornerShape(5.dp))) {
        for (item in items) {
            Box(
                Modifier
                    .weight(1.0f)
                    .padding(5.dp)
                    .background(color = if (items.indexOf(item) == currentSegment.intValue)
                        Color.White
                    else
                        Color.Transparent, shape = RoundedCornerShape(5.dp))
                    .clip(RoundedCornerShape(5.dp)).clickable(interactionSource = interactionSource, indication = null) {
                        currentSegment.intValue = items.indexOf(item)
                        onClick(items.indexOf(item))
                    }
            ) {
                ConstraintLayout(Modifier.fillMaxWidth()) {
                    val (text) = createRefs()

                    Text(modifier = Modifier.constrainAs(text) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        top.linkTo(parent.top, 9.dp)
                        bottom.linkTo(parent.bottom, 12.dp)
                    }, text = item, fontSize = 14.sp, fontFamily = FontFamily(Font(com.hypelist.resources.R.font.hkgroteskbold)))
                }
            }
        }
    }
}