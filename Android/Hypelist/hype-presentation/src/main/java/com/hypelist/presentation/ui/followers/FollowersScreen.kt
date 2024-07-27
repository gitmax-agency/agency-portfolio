@file:OptIn(ExperimentalMaterial3Api::class)

package com.hypelist.presentation.ui.followers

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavHostController
import com.hypelist.resources.R
import com.hypelist.presentation.navigation.NavScreenRoutes
import com.hypelist.presentation.ui.profile.my.MyProfileViewModel
import com.hypelist.presentation.uicomponents.collection.FollowerView
import com.hypelist.presentation.uicomponents.collection.FollowingView
import com.hypelist.presentation.uicomponents.header.TabsRowView
import com.hypelist.presentation.uicomponents.header.TopTitleBar
import org.koin.androidx.compose.koinViewModel
import java.util.Random

@Composable
fun FollowersScreen(
    navController: NavHostController, numberOfFollowers: Int, numberOfFollowing: Int
) {
    val profileViewModel = koinViewModel<MyProfileViewModel>()
    val user by profileViewModel.userFlow.collectAsState()

    val activeTab = remember {
        mutableIntStateOf(0)
    }

    var search by remember {
        mutableStateOf(TextFieldValue(""))
    }

    val focusManager = LocalFocusManager.current
    Column(Modifier.fillMaxSize().background(color = Color(red = 230, green = 230, blue = 230))) {
        TopTitleBar(
            title = user?.first ?: "Anonymous",
            navController = navController,
            isWhiteColor = false)

        TabsRowView(Modifier.fillMaxWidth(), activeTab, listOf(
            "$numberOfFollowers followers",
            "$numberOfFollowing following"
        ))

        TextField(
            leadingIcon = {
                Image(
                    modifier = Modifier.width(30.dp).height(30.dp),
                    painter = painterResource(id = R.drawable.search), contentDescription = "search")
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 10.dp, end = 10.dp, top = 5.dp, bottom = 5.dp
                ),
            shape = RoundedCornerShape(10.dp),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            value = search,
            onValueChange = { newInput ->
                val newValue = newInput.text

                search = newInput.copy(
                    text = newValue
                )
            },
            placeholder = { Text(text = LocalContext.current.getString(R.string.search)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                },
                onGo = {},
                onNext = {},
                onPrevious ={},
                onSearch ={},
                onSend = {}
            )
        )

        if (activeTab.intValue == 0) {
            FollowersList(search, navController)
        } else {
            FollowingList(search, navController)
        }
    }
}

@Composable
fun FollowersList(search: TextFieldValue, navController: NavHostController) {
    ConstraintLayout {
        LazyColumn(Modifier.fillMaxWidth().fillMaxHeight()) {
            items(33) {
                val debug = Random().nextInt(3)
                val nameStr = when (debug) {
                    0 -> "Taylor Swift"
                    1 -> "Harry Kane"
                    else -> "Carolina Herrera"
                }
                if (search.text.isEmpty() || nameStr.lowercase().contains(search.text.lowercase())) {
                    FollowerView(debug, modifier = Modifier
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

@Composable
fun FollowingList(search: TextFieldValue, navController: NavHostController) {
    ConstraintLayout {
        LazyColumn(Modifier.fillMaxWidth().fillMaxHeight()) {
            items(33) {
                val debug = Random().nextInt(3)
                val nameStr = when (debug) {
                    0 -> "Taylor Swift"
                    1 -> "Harry Kane"
                    else -> "Carolina Herrera"
                }
                if (search.text.isEmpty() || nameStr.lowercase().contains(search.text.lowercase())) {
                    FollowingView(debug, modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            navController.navigate("${NavScreenRoutes.OTHERSPROFILE.value}?userID=$debug")
                        })
                }
            }
        }
    }
}