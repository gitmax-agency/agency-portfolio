@file:OptIn(ExperimentalMaterialApi::class)

package com.hypelist.presentation.ui.hype_list.modal

import android.util.DisplayMetrics
import androidx.activity.ComponentActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.BottomSheetScaffoldState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.hypelist.entities.hypelist.Hypelist
import com.hypelist.presentation.extensions.copyItem
import com.hypelist.presentation.ui.hype_list.detail.HypelistViewModel
import com.hypelist.presentation.uicomponents.collection.bottom.HypelistSelectionView
import com.hypelist.presentation.uicomponents.header.SettingsBar
import com.hypelist.resources.R
import kotlinx.coroutines.launch

@Composable
fun BookmarkAdditionPopUp(
    scaffoldState: BottomSheetScaffoldState,
    hypelist: Hypelist,
    hypelists: List<Hypelist>,
    hypelistViewModel: HypelistViewModel
) {
    SettingsBar(title = LocalContext.current.getString(R.string.bookmark_action), scaffoldState) {
    }

    val hypelistItem by hypelistViewModel.hypelistItemFlow.collectAsState()

    val activity = LocalContext.current as ComponentActivity
    val displayMetrics: DisplayMetrics = activity.resources.displayMetrics
    val density = displayMetrics.density
    val width = displayMetrics.widthPixels
    val scrollWidth = width / density

    val scope = rememberCoroutineScope()

    LazyVerticalGrid( columns = GridCells.Fixed(2), Modifier.fillMaxHeight(0.5f)) {
        items(hypelists.size) {
            HypelistSelectionView(modifier = Modifier
                .width((scrollWidth / 2).dp)
                .aspectRatio(1.0f)
                .clickable {
                    hypelist.id?.let { hypelistID ->
                        hypelistItem?.let { hypelistItem ->
                            hypelistViewModel.copyItem(hypelistID, hypelistItem, hypelists[it])
                            scope.launch {
                                scaffoldState.bottomSheetState.collapse()
                            }
                        }
                    }
                }, hypelists[it]
            )
        }
    }
}