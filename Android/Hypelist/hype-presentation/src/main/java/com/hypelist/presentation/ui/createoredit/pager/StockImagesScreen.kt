package com.hypelist.presentation.ui.createoredit.pager

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.hypelist.resources.R
import com.hypelist.presentation.ui.createoredit.CreateOrEditViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun StockImagesScreen(modifier: Modifier) {

    val creationViewModel = koinViewModel<CreateOrEditViewModel>()

    ConstraintLayout(modifier) {
        val (contentsLayout, next) = createRefs()

        ConstraintLayout {
            LazyVerticalGrid(columns = GridCells.Fixed(3), Modifier.constrainAs(contentsLayout) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }.height(200.dp)) {
                items(9) {
                    val drawable = when (it) {
                        0 -> R.drawable.stock1
                        1 -> R.drawable.des_fraises
                        2 -> R.drawable.stock2
                        3 -> R.drawable.primula_aka
                        4 -> R.drawable.stock3
                        5 -> R.drawable.stock4
                        6 -> R.drawable.dionea_in_action
                        7 -> R.drawable.stock5
                        8 -> R.drawable.stock6
                        else -> R.drawable.stock1
                    }

                    Image(
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxWidth(1.0f / 3.0f).aspectRatio(1.0f).clickable {
                            creationViewModel.stockPhotoClicked(it)
                        },
                        painter = painterResource(id = drawable),
                        contentDescription = "stock1"
                    )
                }
            }
        }
    }
}