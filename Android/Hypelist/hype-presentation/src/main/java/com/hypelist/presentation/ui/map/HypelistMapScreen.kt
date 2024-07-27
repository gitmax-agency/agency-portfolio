package com.hypelist.presentation.ui.map

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavHostController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.hypelist.resources.R
import com.hypelist.presentation.uicomponents.control.SolidRoundedButton
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView

@Composable
fun MapScreen(
    navController: NavHostController, mapViewModel: HypelistMapViewModel
) {
    val systemUiController = rememberSystemUiController()
    systemUiController.setSystemBarsColor(color = Color.White, darkIcons = true)

    val hypelist by mapViewModel.hypelistFlow.collectAsState()
    val myLocation by mapViewModel.myLocationFlow.collectAsState()

    val context = LocalContext.current

    val hasAskedForLocation = remember {
        mutableStateOf(false)
    }

    val locationLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            if (!hasAskedForLocation.value) {
                mapViewModel.fetchCurrentLocation {
                    mapViewModel.updateLocation(it)
                }
                hasAskedForLocation.value = false
            }
        }
    }

    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val (back, filter, send) = createRefs()

        AndroidView(modifier = Modifier.fillMaxSize(),
            factory = {
                val mapView = MapView(context)
                mapView
            }, update = {
                mapViewModel.updateMapIfNeeded(it)
            })

        Image(
            modifier = Modifier
                .constrainAs(back) {
                    start.linkTo(parent.start, 10.dp)
                    top.linkTo(parent.top, 50.dp)
                }
                .clickable {
                    navController.navigateUp()
                },
            painter = painterResource(id = R.drawable.mapback),
            contentDescription = "whiteback"
        )

        SolidRoundedButton(modifier = Modifier
            .constrainAs(filter) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                top.linkTo(parent.top, 50.dp)
            }
            .fillMaxWidth(1.0f / 3.0f)
            .clickable {
                navController.navigateUp()
            }, text = LocalContext.current.getString(R.string.map_filter))

        Image(
            modifier = Modifier
                .constrainAs(send) {
                    end.linkTo(parent.end, 10.dp)
                    top.linkTo(parent.top, 50.dp)
                }
                .clickable {
                    mapViewModel.latestLocation?.let {
                        mapViewModel.moveToMyLocation(GeoPoint(it.latitude, it.longitude))
                    }
                },
            painter = painterResource(id = R.drawable.mapsend), contentDescription = "send")
    }

    SideEffect {
        locationLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    mapViewModel.updateMyLocation(myLocation)

    hypelist?.items?.let {
        mapViewModel.updateItemMarkers(hypelist!!.id!!, it, navController)
    }
}