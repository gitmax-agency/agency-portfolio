package com.hypelist.data.database.realm

import com.hypelist.entities.hypelist.HypelistItem
import io.realm.RealmObject

open class RealmHypelistItem(
    var id: String? = null,
    var originalHypelistID: String? = null,
    var originalHypelistItemID: String? = null,

    var name: String? = null,
    var description: String? = null,
    var category: String? = null,

    var note: String? = null,
    var link: String? = null,

    var gpsLatitute: Double? = null,
    var gpsLongitute: Double? = null,
    var gpsPlaceName: String? = null
) : RealmObject() {

    companion object {

        fun fromHypelistItem(hypelistItem: HypelistItem) = RealmHypelistItem(
            hypelistItem.id,
            hypelistItem.originalHypelistID,
            hypelistItem.originalHypelistItemID,
            hypelistItem.name,
            hypelistItem.description,
            hypelistItem.category,
            hypelistItem.note,
            hypelistItem.link,
            hypelistItem.gpsLatitude,
            hypelistItem.gpsLongitude,
            hypelistItem.gpsPlaceName
        )
    }

    fun asHypelistItem() = HypelistItem(
        id = id,
        originalHypelistID = originalHypelistID,
        originalHypelistItemID = originalHypelistItemID,
        name = name,
        description = description,
        category = category,
        note = note,
        link = link,
        gpsLatitude = gpsLatitute,
        gpsLongitude = gpsLongitute,
        gpsPlaceName = gpsPlaceName
    )
}