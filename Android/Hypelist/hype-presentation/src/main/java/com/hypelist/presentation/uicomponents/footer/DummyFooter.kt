package com.hypelist.presentation.uicomponents.footer

import android.app.Activity
import android.util.DisplayMetrics
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun DummyFooter() {
    val activity = LocalContext.current as Activity
    val displayMetrics: DisplayMetrics = activity.resources.displayMetrics
    val height = displayMetrics.heightPixels
    val density = displayMetrics.density
    val dummyHeight = height * 0.05 / density

    Box(modifier = Modifier
        .background(color = Color.Transparent)
        .width(100.dp)
        .height((dummyHeight).dp))
}

@Composable
fun SmallDummyFooter() {
    val activity = LocalContext.current as Activity
    val displayMetrics: DisplayMetrics = activity.resources.displayMetrics
    val height = displayMetrics.heightPixels
    val density = displayMetrics.density
    val dummyHeight = height * 0.025 / density

    Box(modifier = Modifier
        .background(color = Color.Transparent)
        .width(100.dp)
        .height((dummyHeight).dp))
}