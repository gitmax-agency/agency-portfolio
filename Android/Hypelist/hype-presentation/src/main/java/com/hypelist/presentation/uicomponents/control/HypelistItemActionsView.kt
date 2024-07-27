package com.hypelist.presentation.uicomponents.control

import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.hypelist.entities.hypelist.Hypelist
import com.hypelist.resources.R
import com.hypelist.presentation.extensions.deleteHypelistItem
import com.hypelist.presentation.ui.hype_list.detail.HypelistViewModel

@Composable
fun HypelistItemActionsView(
    hypelist: Hypelist,
    modifier: Modifier,
    hypelistViewModel: HypelistViewModel,
    showAddButton: MutableState<Boolean>,
    onEditClicked: () -> Unit,
    onCopyClicked: () -> Unit,
    onMoveClicked: () -> Unit
) {
    val activity = LocalContext.current as ComponentActivity

    Box(
        modifier
            .height(50.dp)
            .clip(RoundedCornerShape(100.dp))
            .background(Color.Black)) {
        ConstraintLayout(Modifier.fillMaxSize()) {

            val (row) = createRefs()

            Row(Modifier.constrainAs(row) {
                start.linkTo(parent.start, 100.dp)
                end.linkTo(parent.end, 100.dp)
                top.linkTo(parent.top, 5.dp)
                bottom.linkTo(parent.bottom, 5.dp)
            }, horizontalArrangement = Arrangement.Center) {
                for (i in 0 until 4) {
                    Column(modifier = Modifier.fillMaxWidth()
                        .weight(1f)
                        .clickable {
                            when (i) {
                                0 -> {
                                    onEditClicked()
                                }
                                1 -> {
                                    onMoveClicked()
                                }
                                2 -> {
                                    onCopyClicked()
                                }
                                3 -> {
                                    hypelistViewModel.deleteHypelistItem(hypelist)
                                }
                            }
                            hypelistViewModel.clearFlags()
                            showAddButton.value = true
                        }, horizontalAlignment = Alignment.CenterHorizontally) {

                        Image(
                            modifier = Modifier.width(30.dp).height(30.dp),
                            painter = painterResource(
                                id = when (i) {
                                    0 -> R.drawable.whiteedit
                                    1 -> R.drawable.whitemove
                                    2 -> R.drawable.whitecopy
                                    else -> R.drawable.whitedelete
                                }
                            ),
                            contentDescription = "tabunderline"
                        )

                        Text(modifier = Modifier
                            .fillMaxWidth()
                            .height(20.dp),
                            color = Color.White,
                            text = when (i) {
                                0 -> "Edit"
                                1 -> "Move"
                                2 -> "Duplicate"
                                else -> "Delete"
                            },
                            textAlign = TextAlign.Center, fontSize = 8.sp, fontFamily = FontFamily(
                                Font(R.font.hkgroteskmedium),
                            )
                        )
                    }
                }
            }
        }
    }
}