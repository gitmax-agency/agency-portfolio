package com.hypelist.presentation.ui.search

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavHostController
import com.hypelist.entities.hypelist.Hypelist
import com.hypelist.resources.R
import com.hypelist.presentation.navigation.NavScreenRoutes
import com.hypelist.presentation.uicomponents.collection.FollowerView
import com.hypelist.presentation.uicomponents.collection.FollowingView
import com.hypelist.presentation.uicomponents.collection.HypelistView
import com.hypelist.presentation.uicomponents.footer.DummyFooter
import com.hypelist.presentation.uicomponents.header.TabsRowView
import com.hypelist.presentation.uicomponents.header.TopTitleBar
import com.hypelist.presentation.uicomponents.input.SingleLineTextField
import java.util.Random

@Composable
@Deprecated("Separate search screen is obsolete in UI designs, here it is only for some temporary compatibility")
fun SearchScreen(
    navController: NavHostController, searchViewModel: SearchViewModel
) {
    val searchableHypelists by searchViewModel.searchableHypelistsFlow.collectAsState()
    val loggedInUser by searchViewModel.loggedInUserFlow.collectAsState()

    val activeTab = rememberSaveable {
        mutableIntStateOf(0)
    }

    val search = rememberSaveable {
        mutableStateOf("")
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(color = Color(red = 230, green = 230, blue = 230))) {
        TopTitleBar(
            title = LocalContext.current.getString(R.string.search),
            navController = navController,
            isWhiteColor = false)

        SingleLineTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, end = 10.dp, top = 5.dp, bottom = 5.dp),
            textValue = search,
            placeholder = LocalContext.current.getString(R.string.search),
            leadingIcon = {
                Image(
                    modifier = Modifier
                        .width(30.dp)
                        .height(30.dp),
                    painter = painterResource(id = R.drawable.search), contentDescription = "search")
            },
            autofocus = true
        )

        TabsRowView(
            Modifier
                .fillMaxWidth()
                .padding(top = 20.dp), activeTab, listOf(
                LocalContext.current.getString(R.string.search_hypelists),
                LocalContext.current.getString(R.string.search_people)
            ))

        loggedInUser?.let { loggedInUserID ->
            if (activeTab.intValue == 0) {
                HypelistsToSearch(
                    loggedInUserID, search.value, searchableHypelists, navController
                )
            } else {
                PeopleToSearch(search.value, navController)
            }
        }
    }
}

@Composable
fun HypelistsToSearch(
    loggedInUserID: String,
    search: String,
    searchableHypelists: List<Hypelist>,
    navController: NavHostController
) {
    val filteredLists = ArrayList<Hypelist>()
    for (list in searchableHypelists) {
        if (search.isNotEmpty() && list.name?.lowercase()?.contains(search.lowercase()) == true) {
            filteredLists.add(list)
        }
    }

    ConstraintLayout(Modifier.fillMaxWidth()) {

        LazyColumn(Modifier.fillMaxWidth().fillMaxHeight()) {
            items(filteredLists.size + 1) {
                if (it < filteredLists.size) {
                    filteredLists[it].id?.let { hypelistID ->
                        HypelistView(
                            navController, modifier = Modifier, filteredLists[it]
                        )
                    }
                } else {
                    DummyFooter()
                }
            }
        }
    }
}

@Composable
fun PeopleToSearch(search: String, navController: NavHostController) {

    ConstraintLayout {
        LazyColumn(Modifier.fillMaxWidth().fillMaxHeight()) {
            items(33) {
                val debugType = Random().nextInt(2)
                val debug = Random().nextInt(3)
                val nameStr = when (debug) {
                    0 -> "Taylor Swift"
                    1 -> "Harry Kane"
                    else -> "Carolina Herrera"
                }
                if (search.isNotEmpty() && nameStr.lowercase().contains(search.lowercase())) {
                    if (debugType == 0) {
                        FollowingView(debug, modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                navController.navigate("${NavScreenRoutes.OTHERSPROFILE.value}?userID=$debug")
                            })
                    } else {
                        FollowerView(debug, modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                navController.navigate("${NavScreenRoutes.OTHERSPROFILE.value}?userID=$debug")
                            })
                    }
                }
            }
        }
    }
}