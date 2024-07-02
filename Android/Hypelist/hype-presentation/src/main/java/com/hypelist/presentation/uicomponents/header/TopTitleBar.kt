@file:OptIn(ExperimentalMaterial3Api::class)

package com.hypelist.presentation.uicomponents.header

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.hypelist.presentation.uicomponents.image.WhiteShadowedImage
import com.hypelist.presentation.uicomponents.label.WhiteShadowedText
import com.hypelist.resources.R

@Composable
fun TopTitleBar(
    modifier: Modifier = Modifier,
    title: String? = null, navController: NavHostController,
    isWhiteColor: Boolean, isRoundedBackIcon: Boolean = false,
    customBackAction: (() -> Unit)? = null,
    rightButtons: List<@Composable () -> Unit> = emptyList()
) {
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
        ),
        navigationIcon = {
            if (isWhiteColor) {
                WhiteShadowedImage(
                    drawable = R.drawable.whiteback,
                    drawableL = R.drawable.lwhiteback,
                    modifier = Modifier
                        .padding(start = 12.dp)
                        .clickable { navController.navigateUp() },
                )
            } else {
                Image(
                    modifier = Modifier
                        .padding(start = 5.dp)
                        .clickable {
                            if (customBackAction != null) {
                                customBackAction()
                            } else {
                                navController.navigateUp()
                            }
                        },
                    painter = painterResource(
                        id = if (isRoundedBackIcon) R.drawable.backrounded else
                            if (isWhiteColor)
                                R.drawable.whiteback
                            else
                                R.drawable.back_arrow
                    ), contentDescription = "back_arrow"
                )
            }
        },
        title = {
            if (title != null) {
                if (isWhiteColor) {
                    WhiteShadowedText(modifier = Modifier, text = title, fontSize = 20.sp)
                } else {
                    Text(
                        fontFamily = FontFamily(Font(R.font.hkgroteskmedium)),
                        text = title,
                        color = if (isWhiteColor) Color.White else Color.Black,
                        textAlign = TextAlign.Center,
                        fontSize = 20.sp
                    )
                }
            }
        },
        actions = {
            for (button in rightButtons) {
                button()
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 10.dp),
    )
}