package com.hypelist.domain.home.contents

import android.graphics.Bitmap
import android.location.Location
import com.hypelist.domain.home.BaseHomeRepository
import com.hypelist.entities.hypelist.Hypelist
import kotlinx.coroutines.flow.MutableStateFlow

interface HypelistRepository : BaseHomeRepository {

    suspend fun isMyHypelist(id: String): Boolean?

    suspend fun loadHypelist(id: String): Hypelist?

    suspend fun changeFavoriteStatus(hypelist: Hypelist): Hypelist

    suspend fun createOnlineHypelistItem(
        hypelistID: String, itemTitle: String, itemDescription: String, itemCategory: String
    )
    suspend fun createHypelistItem(
        hypelist: Hypelist, originalHypelistID: String?, originalHypelistItemID: String?,
        hypelistItemID: String, itemTitle: String, itemDescription: String, itemCategory: String,
        itemNote: String?, itemAddress: String?, itemLocation: Location?, itemLink: String?
    ): Hypelist

    suspend fun updateHypelistItem(
        hypelist: Hypelist, hypelistItemID: String,
        itemTitle: String, itemDescription: String, itemCategory: String,
        itemNote: String?, itemAddress: String?, itemLocation: Location?, itemLink: String?
    ): Hypelist

    ///

    suspend fun deleteHypelistItem(
        hypelist: Hypelist, idListToDelete: ArrayList<String>
    ): Hypelist

    suspend fun updateSingleHypelist(hypelist: Hypelist): Hypelist

    suspend fun updateHypelists(
        hypelist: Hypelist, targetHypelist: Hypelist
    ): Hypelist

    suspend fun updateBookmarksStatus(
        hypelist: Hypelist, bookmarksFlows: HashMap<String, MutableStateFlow<Boolean>>
    )

    suspend fun deleteBookmarkedItems(hypelistID: String, hypelistItemID: String)

    suspend fun loadHypelistCover(coverURL: String?) : Bitmap?
}