package com.hypelist.presentation.uicomponents.header

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomSheetScaffoldState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.hypelist.resources.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SettingsBar(
    title: String,
    scaffoldState: BottomSheetScaffoldState,
    isWhite: Boolean = false,
    onDismiss: () -> Unit = {}) {
    val scope = rememberCoroutineScope()

    ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
        val (backIcon, titleView) = createRefs()

        Image(
            modifier = Modifier.constrainAs(backIcon) {
                start.linkTo(parent.start, 10.dp)
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
            }.clickable {
                onDismiss()
                scope.launch {
                    scaffoldState.bottomSheetState.collapse()
                }
            },
            painter = painterResource(
                id = if (isWhite) R.drawable.whiteback else R.drawable.back_arrow), contentDescription = "back"
        )

        Text(
            modifier = Modifier.constrainAs(titleView) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                top.linkTo(parent.top, 10.dp)
                bottom.linkTo(parent.bottom, 10.dp)
            }.padding(start = 10.dp),
            text = title, color = if (isWhite) Color.White else Color.Black,
            fontSize = 20.sp, fontFamily = FontFamily(
                Font(R.font.hkgroteskbold),
            )
        )
    }
}