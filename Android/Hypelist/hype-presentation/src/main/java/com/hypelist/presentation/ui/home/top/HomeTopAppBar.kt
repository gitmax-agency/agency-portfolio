package com.hypelist.presentation.ui.home.top

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hypelist.presentation.uicomponents.image.ShadowedAvatarView
import com.hypelist.resources.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopAppBar(
    onUserPhotoClicked: () -> Unit,
    onNotificationClicked: () -> Unit,
    userAvatarPhoto: String?,
    modifier: Modifier = Modifier,
) {
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
        navigationIcon = {
            ShadowedAvatarView(
                imageUrl = userAvatarPhoto,
                modifier = Modifier
                    .padding(start = 10.dp)
                    .clickable {
                        onUserPhotoClicked()
                    },
            )
        },
        title = {
            Image(
                modifier = Modifier,
                painter = painterResource(id = R.drawable.ic_hypelist_logo),
                contentDescription = "chooser_logo",
            )
        },
        actions = {
            Image(
                contentDescription = "chooser_logo",
                painter = painterResource(id = R.drawable.notifications),
                modifier = Modifier
                    .width(50.dp)
                    .clickable {
                        onNotificationClicked()
                    },
            )
        },
        modifier = modifier.padding(top = 10.dp),
    )
}