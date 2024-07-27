package com.hypelist.presentation.ui.categories

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomSheetScaffoldState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.hypelist.presentation.extensions.collectState
import com.hypelist.presentation.extensions.createViewModel
import com.hypelist.presentation.navigation.NavScreenRoutes
import com.hypelist.presentation.theme.BottomSheetDialogSubtitle
import com.hypelist.presentation.theme.BottomSheetDialogTitle
import com.hypelist.resources.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CategorySelectionPopUp(
    scaffoldState: BottomSheetScaffoldState,
    navController: NavHostController,
) {
    val scope = rememberCoroutineScope()
    val viewModel: CategorySelectionPopUpViewModel = createViewModel()
    val viewModelState = viewModel.collectState<CategorySelectionState?>().value ?: return

    Column {
        // Title
        Box(modifier = Modifier.padding(top = 28.dp, start = 16.dp, end = 16.dp)) {
            Image(
                painter = painterResource(id = R.drawable.whiteback),
                contentDescription = "Navigate Back",
                modifier = Modifier
                    .width(24.dp)
                    .height(24.dp)
                    .padding(3.dp)
                    .clickable {
                        scope.launch {
                            scaffoldState.bottomSheetState.collapse()
                        }
                    },
            )
            Text(
                text = stringResource(id = R.string.select_category),
                color = BottomSheetDialogTitle,
                textAlign = TextAlign.Center,
                fontSize = 16.sp,
                letterSpacing = 0.1.sp,
                fontFamily = FontFamily(Font(R.font.hkgroteskbold)),
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Subtitle
        Text(
            text = LocalContext.current.getString(R.string.bottom_description),
            color = BottomSheetDialogSubtitle,
            textAlign = TextAlign.Center,
            fontSize = 12.sp,
            fontFamily = FontFamily(Font(R.font.hkgrotesk)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = 9.dp,
                    end = 50.dp,
                    start = 50.dp,
                    bottom = 26.dp,
                ),
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.padding(start = 12.dp, end = 12.dp, bottom = 32.dp)
        ) {
            items(viewModelState.categories) { category ->
                CategorySelectionView(
                    state = category,
                    modifier = Modifier
                        .aspectRatio(1F)
                        .clickable {
                            navController.navigate("${NavScreenRoutes.CREATION.value}?editMode=false")
                            scope.launch {
                                scaffoldState.bottomSheetState.collapse()
                            }
                        }
                )
            }
        }
    }
}

@Composable
fun CategorySelectionView(state: CategoryState, modifier: Modifier) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .padding(4.dp)
            .border(
                width = 1.dp,
                color = Color(52, 52, 52),
                shape = RoundedCornerShape(20.dp),
            ),
    ) {
        val composition by rememberLottieComposition(
            LottieCompositionSpec.RawRes(state.iconResource),
        )
        LottieAnimation(
            composition = composition,
            iterations = LottieConstants.IterateForever,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .padding(top = 18.dp)
                .width(80.dp)
                .height(80.dp),
        )
        Text(
            text = stringResource(id = state.titleResource),
            color = Color.White,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            fontFamily = FontFamily(
                Font(R.font.hkgroteskbold),
            ),
            letterSpacing = 0.1.sp,
        )
        Text(
            text = stringResource(id = state.descriptionResource),
            color = Color(185, 185, 185),
            fontSize = 10.sp,
            fontFamily = FontFamily(Font(R.font.hkgrotesk)),
            textAlign = TextAlign.Center,
            lineHeight = 15.sp,
            modifier = Modifier.padding(bottom = 18.dp, start = 12.dp, end = 12.dp)
        )
    }
}