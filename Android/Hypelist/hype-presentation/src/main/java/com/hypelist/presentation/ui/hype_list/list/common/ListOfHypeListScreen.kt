package com.hypelist.presentation.ui.hype_list.list.common

import android.view.MotionEvent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.hypelist.entities.hypelist.Hypelist
import com.hypelist.presentation.uicomponents.collection.HypelistView
import com.hypelist.presentation.uicomponents.input.SingleLineTextField
import com.hypelist.resources.R


@OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
@Composable
fun ColumnScope.ListOfHypeListScreen(
    hypeListsData: List<Hypelist>,
    navController: NavHostController,
    onRefreshDataRequested: () -> Unit,
) {
    val refreshing = remember {
        mutableStateOf(false)
    }
    val pullRefreshState = rememberPullRefreshState(
        refreshing = refreshing.value,
        onRefresh = {
            onRefreshDataRequested()
        }
    )
    val search = rememberSaveable {
        mutableStateOf("")
    }
    val possibleOverscroll = remember {
        mutableStateOf(false)
    }
    val lazyListState = rememberLazyListState()

    val searchFieldPresent = remember {
        mutableStateOf(false)
    }
    val awaitForTouchRelease = remember {
        mutableStateOf(false)
    }
    val allowUpdateSwipe = remember {
        mutableStateOf(false)
    }

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                possibleOverscroll.value = available.y >= 20.0 && lazyListState.firstVisibleItemIndex == 0

                if (possibleOverscroll.value && !searchFieldPresent.value && hypeListsData.isNotEmpty()) {
                    searchFieldPresent.value = true
                    awaitForTouchRelease.value = true
                    allowUpdateSwipe.value = false
                }

                if (search.value.isEmpty()) {
                    if (available.y <= -30.0 && searchFieldPresent.value) {
                        searchFieldPresent.value = false
                        awaitForTouchRelease.value = false
                        allowUpdateSwipe.value = false
                    }

                    if (lazyListState.firstVisibleItemIndex == 1 && searchFieldPresent.value) {
                        searchFieldPresent.value = false
                        awaitForTouchRelease.value = false
                        allowUpdateSwipe.value = false
                    }
                }

                return super.onPostScroll(consumed, available, source)
            }
        }
    }

    val listModifier = if (allowUpdateSwipe.value) {
        Modifier
            .fillMaxWidth()
            .weight(1.0f)
            .pullRefresh(pullRefreshState)
    } else {
        Modifier
            .fillMaxWidth()
            .weight(1.0f)
    }

    Box(modifier = listModifier.pointerInteropFilter {
        println(it)
        if (it.action == MotionEvent.ACTION_DOWN) {
            if (awaitForTouchRelease.value) {
                awaitForTouchRelease.value = false
                allowUpdateSwipe.value = true
            }
        }
        false
    }
    ) {
        val filteredLists = ArrayList<Hypelist>()
        for (list in hypeListsData) {
            if (search.value.isEmpty() || list.name?.lowercase()
                    ?.contains(search.value.lowercase()) == true
            ) {
                filteredLists.add(list)
            }
        }

        val forceClear = remember {
            mutableStateOf(false)
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(nestedScrollConnection),
            state = lazyListState
        ) {
            if (searchFieldPresent.value) {
                items(filteredLists.size + 1) {
                    if (it == 0) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            SingleLineTextField(
                                modifier = Modifier
                                    .weight(1.0f)
                                    .padding(
                                        start = 10.dp,
                                        end = 10.dp,
                                        top = 5.dp,
                                        bottom = 5.dp
                                    ),
                                textValue = search,
                                placeholder = LocalContext.current.getString(R.string.hypelists_search),
                                leadingIcon = {
                                    Image(
                                        modifier = Modifier
                                            .width(30.dp)
                                            .height(30.dp),
                                        painter = painterResource(id = R.drawable.search),
                                        contentDescription = "search"
                                    )
                                },
                                forceClear = forceClear
                            )

                            if (search.value.isNotEmpty()) {
                                Text(
                                    modifier = Modifier
                                        .padding(
                                            start = 0.dp, end = 10.dp,
                                            top = 5.dp, bottom = 5.dp,
                                        )
                                        .clickable {
                                            search.value = ""
                                            forceClear.value = true
                                        },
                                    text = LocalContext.current.getString(R.string.cancel),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    } else {
                        filteredLists[it - 1].id?.let { hypelistID ->
                            HypelistView(
                                navController,
                                modifier = Modifier,
                                filteredLists[it - 1]
                            )
                        }
                    }
                }
            } else {
                items(hypeListsData.size) {
                    hypeListsData[it].id?.let { hypelistID ->
                        HypelistView(
                            navController, modifier = Modifier, hypeListsData[it]
                        )
                    }
                }
            }
        }

        PullRefreshIndicator(
            refreshing.value,
            pullRefreshState,
            Modifier.align(Alignment.TopCenter)
        )
    }

    DisposableEffect(Unit) {
        onDispose { search.value = "" }
    }
}