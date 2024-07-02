package com.hypelist.presentation.ui.map

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.location.Location
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.hypelist.entities.hypelist.HypelistItem
import com.hypelist.data.extensions.roundedImage
import com.hypelist.domain.error.ErrorNotifier
import com.hypelist.resources.R
import com.hypelist.presentation.navigation.NavScreenRoutes
import com.hypelist.presentation.ui.hype_list.detail.BaseHypelistViewModel
import com.hypelist.presentation.uicomponents.collection.HypelistItemMarker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.osmdroid.tileprovider.MapTileProviderBasic
import org.osmdroid.tileprovider.tilesource.ITileSource
import org.osmdroid.tileprovider.tilesource.XYTileSource
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.DefaultOverlayManager
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.TilesOverlay
import java.io.File

class HypelistMapViewModel(
    private val application: Application,
    errorNotifier: ErrorNotifier,
) : BaseHypelistViewModel(application, errorNotifier), KoinComponent {

    val updateMapFlow = MutableStateFlow(true)
    val myLocationFlow = MutableStateFlow<GeoPoint?>(null)

    private var mapView: MapView? = null

    fun updateLocation(location: Location) {
        myLocationFlow.update { GeoPoint(location.latitude, location.longitude) }
    }

    fun resetState() {
        updateMapFlow.update { true }
    }

    fun updateMapIfNeeded(mapView: MapView) {
        this.mapView = mapView

        if (updateMapFlow.value) {
            val tileProvider = MapTileProviderBasic(application)
            val tileSource: ITileSource = XYTileSource(
                "hot",
                0,
                20,
                18,
                ".png",
                arrayOf("https://a.tile.openstreetmap.fr/hot/", "https://b.tile.openstreetmap.fr/hot/")
            )
            tileProvider.tileSource = tileSource
            val tilesOverlay = TilesOverlay(tileProvider, application)

            mapView.overlays.add(tilesOverlay)
            mapView.overlayManager = CustomOverlayManager(tilesOverlay)

            mapView.setMultiTouchControls(true)
            mapView.setBuiltInZoomControls(false)

            appendLocationMarkers(mapView)

            updateMapFlow.value = false
        }
    }

    fun updateMyLocation(myLocation: GeoPoint?) {
        mapView?.let { mapView ->
            mapView.overlays.clear()

            myLocation?.let {
                try {
                    val startMarker = Marker(mapView)
                    startMarker.position = GeoPoint(it.latitude, it.longitude)
                    startMarker.icon = createMarkerIcon()
                    startMarker.image = createMarkerIcon()
                    startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)

                    startMarker.setOnMarkerClickListener { _, _ ->
                        true
                    }

                    mapView.overlays.add(startMarker)
                } catch (e: Exception) {
                }
            }

            mapView.invalidate()
        }
    }

    fun updateItemMarkers(
        hypelistID: String, items: List<HypelistItem>, navController: NavHostController) {
        viewModelScope.launch(exceptionHandler) {
            withContext(Dispatchers.IO) {
                mapView?.let { mapView ->
                    try {
                        for (item in items) {
                            val latitude = item.gpsLatitude
                            val longitude = item.gpsLongitude
                            if (latitude != null && longitude != null) {
                                val icon = createItemIcon(hypelistID, item.id!!)
                                val startMarker = HypelistItemMarker(mapView, item.id!!)
                                startMarker.position = GeoPoint(latitude, longitude)
                                startMarker.icon = icon
                                startMarker.image = icon
                                startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)

                                startMarker.setOnMarkerClickListener { marker, mapView ->
                                    if (marker is HypelistItemMarker) {
                                        navController.navigate(
                                            "${NavScreenRoutes.FULLSCREEN.value}?hypelistID=${hypelistID}&hypelistItemID=${marker.hypelistItemID}"
                                        )
                                    }
                                    true
                                }

                                withContext(Dispatchers.Main) {
                                    mapView.overlays.add(startMarker)
                                }
                            }
                        }

                        withContext(Dispatchers.Main) {
                            mapView.invalidate()
                        }
                    } catch (e: Exception) {
                    }
                }
            }
        }
    }

    fun moveToMyLocation(myLocation: GeoPoint) {
        mapView?.controller?.animateTo(myLocation)
        mapView?.controller?.zoomTo(8.0)
    }

    private fun createMarkerIcon(): Drawable {
        val options = BitmapFactory.Options()
        options.inMutable = true
        val original = BitmapFactory.decodeResource(
            application.resources, R.drawable.myicon, options
        )
        val scaled = Bitmap.createScaledBitmap(
            original, original.width / 10, original.height / 10, true
        )
        original.recycle()

        return BitmapDrawable(application.resources, scaled)
    }

    private fun createItemIcon(hypelistID: String, hypelistItemID: String): Drawable {
        val options = BitmapFactory.Options()
        options.inMutable = true
        val original = BitmapFactory.decodeResource(
            application.resources, R.drawable.osmmarker, options
        )
        val scaled = Bitmap.createScaledBitmap(
            original, original.width / 10, original.height / 10, true
        )
        original.recycle()

        hypelistItemCover(hypelistID, hypelistItemID)?.let { coverOriginal ->
            val coverScaled = Bitmap.createScaledBitmap(
                coverOriginal, (original.width / 11).toInt(), (original.height / 11).toInt(), true
            )
            val coverRounded = coverScaled.roundedImage()
            coverOriginal.recycle()

            val markerCanvas = Canvas(scaled)
            val paint = Paint()
            paint.color = android.graphics.Color.BLACK // Text Color

            paint.textSize = 40f // Text Size
            paint.isFakeBoldText = true

            paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_OVER) // Text Overlapping Pattern

            markerCanvas.drawBitmap(coverRounded, 5F, 10F, paint)
        }

        return BitmapDrawable(application.resources, scaled)
    }

    private fun hypelistItemCover(hypelistID: String, hypelistItemID: String) : Bitmap? {
        val cacheDir = application.filesDir.absolutePath + "/CachedHypelists"
        val hypelistCacheDir = "$cacheDir/Hypelist_$hypelistID"
        val hypelistItemsCacheDir = "$hypelistCacheDir/Items"
        val hypelistItemCacheDir = "$hypelistItemsCacheDir/Item_${hypelistItemID}"
        val hypelistItemCoverPath = "$hypelistItemCacheDir/HypelistItemCover.jpg"

        return if (File(hypelistItemCoverPath).exists()) {
            BitmapFactory.decodeFile(hypelistItemCoverPath)
        } else {
            null
        }
    }

    private fun appendLocationMarkers(mapView: MapView) {
        val defaultCoord = GeoPoint(54.5259, 15.2551)
        mapView.controller.animateTo(defaultCoord)
        //mapView.controller.zoomTo(5.0)
        mapView.controller.zoomTo(5.0)
    }

    private class CustomOverlayManager(tilesOverlay: TilesOverlay?) :
        DefaultOverlayManager(tilesOverlay) {
        override fun onDraw(c: Canvas, pMapView: MapView) {
            super.onDraw(c, pMapView)
            pMapView.invalidate()

            //potential fix for #52 pMapView.invalidate();
        }
    }
}