package com.hypelist.presentation.ui.auth

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavHostController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.hypelist.resources.R

@Composable
fun TermsOfUseScreen(navController: NavHostController) {
    val systemUiController = rememberSystemUiController()
    systemUiController.setSystemBarsColor(color = Color.White, darkIcons = true)

    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val (closeIcon, webView) = createRefs()

        Box(modifier = Modifier.constrainAs(webView) {
            top.linkTo(closeIcon.bottom, 10.dp)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
            bottom.linkTo(parent.bottom)
            width = Dimension.fillToConstraints
            height = Dimension.fillToConstraints
        }.background(color = Color.Green)) {
            AndroidView(factory = {
                WebView(it).apply {
                    //layoutParams = ViewGroup.LayoutParams(
                    //ViewGroup.LayoutParams.MATCH_PARENT, 300
                    //ViewGroup.LayoutParams.MATCH_PARENT
                    //)
                    webViewClient = WebViewClient()
                    //loadUrl("https://techwings.com/privacy-policy")
                }
            }, update = {
                //Handler(Looper.getMainLooper()).postDelayed({
                it.loadUrl("https://techwings.com/privacy-policy")
                //}, 1000)
            })
        }

        Image(
            modifier = Modifier
                .constrainAs(closeIcon) {
                    start.linkTo(parent.start, 30.dp)
                    top.linkTo(parent.top, 40.dp)
                }
                .width(30.dp)
                .height(30.dp)
                .clickable {
                    navController.navigateUp()
                },
            painter = painterResource(id = R.drawable.icon_close),
            contentDescription = "icon_close",
        )
    }
}
