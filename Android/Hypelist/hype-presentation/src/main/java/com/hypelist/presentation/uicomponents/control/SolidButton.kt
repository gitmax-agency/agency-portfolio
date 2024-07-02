package com.hypelist.presentation.uicomponents.control

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hypelist.resources.R

@Composable
fun SolidButton(
    modifier: Modifier, text: String, isActive: State<Boolean>? = null
) {
    Text(modifier = modifier.background(
        color = if (isActive == null || isActive.value) Color.Black else Color.LightGray,
        shape = RoundedCornerShape(8.dp)).fillMaxWidth(0.8f).height(45.dp).wrapContentSize(),
        text = text,
        color = Color.White,
        textAlign = TextAlign.Center,
        fontSize = 14.sp,
        fontFamily = FontFamily(
            Font(R.font.hkgroteskmedium),
        )
    )
}

@Composable
fun SolidRoundedButton(modifier: Modifier, text: String) {
    Text(modifier = modifier.background(
        color = Color.Black,
        shape = RoundedCornerShape(45.dp)).fillMaxWidth(0.8f).height(45.dp).wrapContentSize(),
        text = text,
        color = Color.White,
        textAlign = TextAlign.Center,
        fontSize = 14.sp,
        fontFamily = FontFamily(
            Font(R.font.hkgroteskmedium),
        )
    )
}

@Composable
fun SmallButton(
    modifier: Modifier, text: String, isWhite: Boolean = false, isGray: Boolean = false, customRadius: Dp? = null
) {
    Text(modifier = modifier.background(
        color = if (isGray) Color.Transparent else if (isWhite) Color.White else Color.Black,
        shape = RoundedCornerShape(customRadius ?: 8.dp)).height(45.dp)
        .border(width = if (isGray) 1.dp else 0.dp,
            color = if (isGray) Color.Gray else Color.Transparent,
            shape = RoundedCornerShape(customRadius ?: 8.dp)).wrapContentSize(),
        text = text,
        color = if (isGray) Color.Gray else if (isWhite) Color.Black else Color.White,
        textAlign = TextAlign.Center,
        fontSize = 12.sp,
        fontFamily = FontFamily(
            Font(if (isWhite || isGray) R.font.hkgroteskbold else R.font.hkgroteskmedium),
        )
    )
}