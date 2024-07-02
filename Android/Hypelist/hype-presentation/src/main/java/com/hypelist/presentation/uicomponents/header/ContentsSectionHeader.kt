package com.hypelist.presentation.uicomponents.header

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hypelist.resources.R

@Composable
fun ContentsSectionHeader(title: String) {
    Text(
        modifier = Modifier.padding(start = 10.dp, top = 10.dp),
        text = title, fontSize = 20.sp, fontFamily = FontFamily(
        Font(R.font.hkgroteskbold),
    ))
}