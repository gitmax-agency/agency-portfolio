package com.hypelist.presentation.uicomponents.header

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hypelist.presentation.theme.TabSelected
import com.hypelist.presentation.theme.TabUnselected
import com.hypelist.resources.R

@Composable
fun TabsRowView(
    modifier: Modifier,
    activeTab: MutableState<Int>,
    tabTitles: List<String>
) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Row(horizontalArrangement = Arrangement.spacedBy(30.dp)) {
            for (i in tabTitles.indices) {
                val isTabSelected = activeTab.value == i
                Column(
                    modifier = Modifier.clickable {
                        activeTab.value = i
                    },
                ) {
                    val localDensity = LocalDensity.current
                    val textWidth = remember { mutableStateOf(1.dp) }

                    Text(
                        text = tabTitles[i],
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontFamily = FontFamily(Font(R.font.hkgrotesk)),
                            fontWeight = FontWeight(600),
                            color = when {
                                isTabSelected -> TabSelected
                                else -> TabUnselected
                            },
                            textAlign = TextAlign.Center,
                        ),
                        modifier = Modifier
                            .onSizeChanged {
                                if (it.width != textWidth.value.value.toInt()) {
                                    textWidth.value = (it.width / localDensity.density).dp
                                }
                            },
                    )

                    if (isTabSelected) {
                        Image(
                            contentScale = ContentScale.FillBounds,
                            modifier = Modifier
                                .width(textWidth.value)
                                .height(4.dp),
                            painter = painterResource(
                                id = R.drawable.tabunderline,
                            ),
                            contentDescription = "tabunderline",
                        )
                    }
                }
            }
        }
    }
}