package com.hypelist.entities.hypelist

class HypelistItem(
    var id: String?,
    var originalHypelistID: String? = null,
    var originalHypelistItemID: String? = null,
    var name: String?,
    var description: String?,
    var category: String?,
    var note: String?,
    var link: String?,
    var gpsLatitude: Double?,
    var gpsLongitude: Double?,
    var gpsPlaceName: String?
)