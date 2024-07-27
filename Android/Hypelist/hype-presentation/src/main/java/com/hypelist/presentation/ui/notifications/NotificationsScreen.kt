package com.hypelist.presentation.ui.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavHostController
import com.hypelist.resources.R
import com.hypelist.presentation.navigation.NavScreenRoutes
import com.hypelist.presentation.uicomponents.collection.NotificationView
import com.hypelist.presentation.uicomponents.header.TopTitleBar
import java.util.Random

@Composable
fun NotificationsScreen(navController: NavHostController) {
  Column(
      Modifier
          .fillMaxSize()
          .background(color = Color(red = 230, green = 230, blue = 230))) {

    TopTitleBar(
      title = LocalContext.current.getString(R.string.notifications),
      navController = navController,
      isWhiteColor = false,
    )

    ConstraintLayout {
      LazyColumn(
          Modifier
              .fillMaxWidth()
              .fillMaxHeight()) {
        items(33) {
          val debug = Random().nextInt(3)
          NotificationView(debug, navController, modifier = Modifier
              .fillMaxWidth()
              .clickable {
                  navController.navigate(
                      "${NavScreenRoutes.OTHERSPROFILE.value}?userID=$debug"
                  )
              })
        }
      }
    }
  }
}