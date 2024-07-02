package com.hypelist.presentation.ui.createoredit.pager

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.hypelist.presentation.ui.createoredit.CreateOrEditViewModel

@Composable
fun ColorOptionsScreen(
    creationViewModel: CreateOrEditViewModel, modifier: Modifier
) {
    val colors = arrayOf(
        "red", "magentared",
        "yellow", "redyellow",
        "green", "yellowgreen",
        "cyan", "greencyan",
        "blue", "cyanblue",
        "magenta", "bluemagenta", "greenmagenta", "yellowmagenta")
    /*val colors = arrayOf(
        "red",
        "yellow",
        "green",
        "cyan",
        "blue",
        "magenta")*/

    ConstraintLayout(modifier) {
        val (contentsLayout) = createRefs()

        ConstraintLayout(Modifier.constrainAs(contentsLayout) {
            top.linkTo(parent.top, 20.dp)
            start.linkTo(parent.start, (-20).dp)
            //end.linkTo(parent.end)
        }) {
            LazyRow(
                modifier = Modifier.fillMaxWidth().height(60.dp)
            ) {
                items(colors.size) {
                    when (it) {
                        0 -> {
                            Image(
                                modifier = Modifier.padding(start = 20.dp, end = 5.dp)
                                    .width(60.dp)
                                    .height(60.dp)
                                    .clickable {
                                        creationViewModel.loadDefaultCover(colors[it])
                                    },
                                bitmap = creationViewModel.createColoredChooser(colors[it]).asImageBitmap(),
                                contentDescription = "creationcolor"
                            )
                        }
                        colors.size - 1 -> {
                            Image(
                                modifier = Modifier.padding(start = 5.dp, end = 20.dp)
                                    .width(60.dp)
                                    .height(60.dp)
                                    .clickable {
                                        creationViewModel.loadDefaultCover(colors[it])
                                    },
                                bitmap = creationViewModel.createColoredChooser(colors[it]).asImageBitmap(),
                                contentDescription = "creationcolor"
                            )
                        }
                        else -> {
                            Image(
                                modifier = Modifier.padding(start = 5.dp, end = 5.dp)
                                    .width(60.dp)
                                    .height(60.dp)
                                    .clickable {
                                        creationViewModel.loadDefaultCover(colors[it])
                                    },
                                bitmap = creationViewModel.createColoredChooser(colors[it]).asImageBitmap(),
                                contentDescription = "creationcolor"
                            )
                        }
                    }
                }
            }
        }
    }
}