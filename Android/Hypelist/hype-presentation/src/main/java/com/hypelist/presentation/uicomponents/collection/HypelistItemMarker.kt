package com.hypelist.presentation.uicomponents.collection

import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class HypelistItemMarker(
    mapView: MapView, val hypelistItemID: String
) : Marker(mapView)