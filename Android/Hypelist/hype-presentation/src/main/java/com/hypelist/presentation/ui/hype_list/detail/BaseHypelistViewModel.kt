package com.hypelist.presentation.ui.hype_list.detail

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewModelScope
import com.hypelist.domain.error.ErrorNotifier
import com.hypelist.domain.home.contents.HypelistRepository
import com.hypelist.entities.hypelist.Hypelist
import com.hypelist.presentation.ui.home.BaseHomeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

open class BaseHypelistViewModel(
    private val application: Application,
    private val errorNotifier: ErrorNotifier,
) : BaseHomeViewModel(errorNotifier), KoinComponent {

    private val repository: HypelistRepository by inject()

    private var locationManager: LocationManager? = null

    var latestLocation: Location? = null

    val hypelistFlow = MutableStateFlow<Hypelist?>(null)

    fun loadHypelist(hypelistID: String, onLoaded: (Hypelist?) -> Unit = {}) {
        viewModelScope.launch(exceptionHandler) {
            withContext(Dispatchers.Main) {
                val hypelist = repository.loadHypelist(hypelistID)
                hypelistFlow.update { hypelist }
                onLoaded(hypelist)
            }
        }
    }

    fun fetchCurrentLocation(onSuccess: (Location) -> Unit) {
        nowLoadingFlow.update { true }

        locationManager = application.getSystemService(Context.LOCATION_SERVICE) as? LocationManager

        if (ActivityCompat.checkSelfPermission(
                application,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                application, Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            //GPS_PROVIDER
            locationManager?.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, {
                latestLocation = it
                onSuccess(it)
            }, Looper.getMainLooper())
        } else {
            nowLoadingFlow.update { false }
            handleException(Throwable("Location permissions not granted!"))
        }
    }
}