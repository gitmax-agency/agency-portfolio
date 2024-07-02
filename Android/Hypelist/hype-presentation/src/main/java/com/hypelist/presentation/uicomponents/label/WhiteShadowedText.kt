package com.hypelist.presentation.uicomponents.label

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.hypelist.resources.R

@Composable
fun WhiteShadowedText(
    modifier: Modifier,
    text: String,
    fontSize: TextUnit,
    font: Int = R.font.hkgroteskmedium,
    isSingleLine: Boolean = false,
    customAlign: TextAlign? = null
) {
    ConstraintLayout(modifier = modifier) {
        val (textView, textViewL) = createRefs()

        Text(text = text, modifier = Modifier.constrainAs(textViewL) {
            start.linkTo(parent.start, 1.dp)
            end.linkTo(parent.end, (-1).dp)
            top.linkTo(parent.top, 1.dp)
            bottom.linkTo(parent.bottom, (-1).dp)
        }, color = Color.Black, fontSize = fontSize,
            fontFamily = FontFamily(Font(font)), textAlign = customAlign ?: TextAlign.Center,
            maxLines = if (isSingleLine) 2 else Int.MAX_VALUE,
            overflow = if (isSingleLine) TextOverflow.Ellipsis else TextOverflow.Clip
        )
        Text(text = text, modifier = Modifier.constrainAs(textView) {
            start.linkTo(parent.start)
            end.linkTo(parent.end)
            top.linkTo(parent.top)
            bottom.linkTo(parent.bottom)
        }, color = Color.White, fontSize = fontSize,
            fontFamily = FontFamily(Font(font)), textAlign = customAlign ?: TextAlign.Center,
            maxLines = if (isSingleLine) 2 else Int.MAX_VALUE,
            overflow = if (isSingleLine) TextOverflow.Ellipsis else TextOverflow.Clip
        )
    }
}