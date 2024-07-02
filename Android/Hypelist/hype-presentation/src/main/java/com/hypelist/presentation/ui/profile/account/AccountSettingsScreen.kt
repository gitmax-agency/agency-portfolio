package com.hypelist.presentation.ui.profile.account

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.hypelist.resources.R
import com.hypelist.presentation.navigation.NavScreenRoutes
import com.hypelist.presentation.uicomponents.header.TopTitleBar
import com.hypelist.presentation.uicomponents.collection.SettingsListItem
import org.koin.androidx.compose.koinViewModel

@Composable
fun AccountSettingsScreen(navController: NavHostController) {
    val accountViewModel = koinViewModel<AccountSettingsViewModel>()

    Column(Modifier.fillMaxSize().background(color = Color(red = 230, green = 230, blue = 230))) {
        TopTitleBar(
            title = LocalContext.current.getString(R.string.account_screen),
            navController = navController,
            isWhiteColor = false)

        Column(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, bottom = 10.dp)
            ) {
                SettingsListItem(
                    Modifier.clickable {
                        accountViewModel.logOut()
                        navController.navigate(NavScreenRoutes.WELCOME.value) { popUpTo(0) }
                    },
                    R.drawable.settings_account,
                    LocalContext.current.getString(R.string.account_logout),
                    RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp)
                )
                SettingsListItem(
                    Modifier.clickable {
                        accountViewModel.deleteAccount()
                        navController.navigate(NavScreenRoutes.WELCOME.value) { popUpTo(0) }
                    },
                    R.drawable.settings_delete,
                    LocalContext.current.getString(R.string.account_delete),
                    RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp)
                )
            }
        }
    }
}