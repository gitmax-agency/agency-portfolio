package com.hypelist.presentation.ui.hype_list.list.from_saved

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hypelist.presentation.theme.TextPrimary
import com.hypelist.presentation.theme.TextSecondary
import com.hypelist.resources.R

@Composable
fun EmptyHypeListFromSaved(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(id = R.drawable.starinactive),
            contentDescription = "favorite",
            colorFilter = ColorFilter.tint(Color.Black),
            modifier = Modifier
                .width(48.dp)
                .height(48.dp)
                .clip(CircleShape)
                .background(Color.White)
                .padding(12.dp),
        )
        Text(
            text = stringResource(id = R.string.list_no_saved_hypelist),
            style = TextStyle(
                color = TextPrimary,
                fontSize = 19.sp,
                lineHeight = 22.sp,
                textAlign = TextAlign.Center,
                fontFamily = FontFamily(Font(R.font.hkgroteskbold)),
            ),
            modifier = Modifier.padding(bottom = 18.dp, top = 18.dp),
        )

        Text(
            text = stringResource(id = R.string.list_no_saved_hypelist_msg),
            style = TextStyle(
                color = TextSecondary,
                fontSize = 13.sp,
                lineHeight = 20.sp,
                textAlign = TextAlign.Center,
                fontFamily = FontFamily(Font(R.font.technoscriptef)),
            ),
            modifier = Modifier.padding(bottom = 18.dp),
        )
    }
}