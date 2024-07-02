package com.hypelist.presentation.uicomponents.header

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.hypelist.resources.R
import com.hypelist.presentation.ui.createoredit.CreateOrEditViewModel

@Composable
fun AccessSelector(
    creationViewModel: CreateOrEditViewModel,
    modifier: Modifier
) {
    val privateAccess by creationViewModel.privateAccessFlow.collectAsState()

    ConstraintLayout(modifier = modifier) {

        val (background, active, privateOption, publicOption) = createRefs()

        Image(
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .constrainAs(background) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    width = Dimension.fillToConstraints
                }
                .fillMaxWidth(),
            painter = painterResource(id = R.drawable.accessbackground),
            contentDescription = "accessbackground"
        )

        if (privateAccess) {
            Image(
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .constrainAs(active) {
                        start.linkTo(parent.start, 5.dp)
                        top.linkTo(parent.top, 5.dp)
                        bottom.linkTo(parent.bottom, 5.dp)
                        width = Dimension.fillToConstraints
                    }
                    .fillMaxWidth(0.5f),
                painter = painterResource(id = R.drawable.accessactive),
                contentDescription = "accessactive"
            )
        } else {
            Image(
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .constrainAs(active) {
                        end.linkTo(parent.end, 5.dp)
                        top.linkTo(parent.top, 5.dp)
                        bottom.linkTo(parent.bottom, 5.dp)
                        width = Dimension.fillToConstraints
                    }
                    .fillMaxWidth(0.5f),
                painter = painterResource(id = R.drawable.accessactive),
                contentDescription = "accessactive"
            )
        }

        Image(
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .constrainAs(privateOption) {
                    start.linkTo(parent.start, 50.dp)
                    top.linkTo(parent.top, 30.dp)
                    bottom.linkTo(parent.bottom, 30.dp)
                }
                .fillMaxWidth(0.25f)
                .clickable {
                    creationViewModel.togglePrivate(true)
                },
            painter = painterResource(id = R.drawable.resource_private),
            contentDescription = "accessactive"
        )

        Image(
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .constrainAs(publicOption) {
                    end.linkTo(parent.end, 45.dp)
                    top.linkTo(parent.top, 30.dp)
                    bottom.linkTo(parent.bottom, 30.dp)
                }
                .fillMaxWidth(0.25f)
                .clickable {
                    creationViewModel.togglePrivate(false)
                },
            painter = painterResource(id = R.drawable.resource_public),
            contentDescription = "accessactive"
        )
    }
}