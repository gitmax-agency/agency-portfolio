package com.hypelist.data.home.contents

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import com.hypelist.data.hypelist.api.CreationApi
import com.hypelist.data.hypelist.api.HypelistsApi
import com.hypelist.data.home.BaseHomeRepositoryImpl
import com.hypelist.domain.home.contents.HypelistRepository
import com.hypelist.entities.hypelist.Hypelist
import com.hypelist.entities.hypelist.HypelistItem
import com.hypelist.data.database.realm.RealmHypelist
import com.hypelist.data.hypelist.api.ItemCreationRequest
import io.realm.Realm
import io.realm.kotlin.executeTransactionAwait
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File

open class HypelistRepositoryImpl(
    private val application: Application
) : BaseHomeRepositoryImpl(application), HypelistRepository, KoinComponent {

    private val apiService: CreationApi by inject()
    private val hypelistsService: HypelistsApi by inject()

    override suspend fun isMyHypelist(id: String): Boolean? {
        var isMyHypelist: Boolean? = null

        loggedInUserID()?.let { userID ->
            Realm.getDefaultInstance().executeTransactionAwait {
                val existingHypelist = it.where(RealmHypelist::class.java).equalTo("id", id).findFirst()
                if (existingHypelist != null) {
                    isMyHypelist = existingHypelist.authorID == userID
                }
            }
        }

        return isMyHypelist
    }

    override suspend fun loadHypelist(id: String): Hypelist? {
        var hypelist: Hypelist? = null

        Realm.getDefaultInstance().executeTransactionAwait {
            val existingHypelist = it.where(RealmHypelist::class.java).equalTo("id", id).findFirst()
            if (existingHypelist != null) {
                val realmHypelist = it.copyFromRealm(existingHypelist)
                hypelist = realmHypelist.asHypelist()
            }
        }

        return hypelist
    }

    override suspend fun changeFavoriteStatus(hypelist: Hypelist): Hypelist {
        hypelist.isFavorite = !hypelist.isFavorite

        Realm.getDefaultInstance().executeTransactionAwait {
            val realmHypelist = RealmHypelist.fromHypelist (hypelist)
            it.insertOrUpdate(realmHypelist)
        }

        return Hypelist(
            id = hypelist.id,
            name = hypelist.name,
            items = hypelist.items,
            author = hypelist.author,
            authorID = hypelist.authorID,
            category = hypelist.category,
            coverURL = hypelist.coverURL,
            isPrivate = hypelist.isPrivate,
            isFavorite = hypelist.isFavorite,
            isDebugFollowersList = hypelist.isDebugFollowersList,
            isAutocoloredBlurBackground = hypelist.isAutocoloredBlurBackground
        )
    }

    override suspend fun createOnlineHypelistItem(
        hypelistID: String,
        itemTitle: String,
        itemDescription: String,
        itemCategory: String
    ) {
        loggedInUserID()?.let { userID ->
            apiService.createHypelistItem(hypelistID, userID, ItemCreationRequest(
                itemTitle, itemDescription, "Android app testing", itemCategory, 0, 0, 0, 0
            )
            )
        }
    }
    override suspend fun createHypelistItem(
        hypelist: Hypelist, originalHypelistID: String?, originalHypelistItemID: String?,
        hypelistItemID: String, itemTitle: String, itemDescription: String, itemCategory: String,
        itemNote: String?, itemAddress: String?, itemLocation: Location?, itemLink: String?
    ) : Hypelist {
        val hypelistItem = HypelistItem(
            id = hypelistItemID, originalHypelistID = originalHypelistID, originalHypelistItemID = originalHypelistItemID,
            name = itemTitle, description = itemDescription, category = itemCategory,
            note = itemNote, link = itemLink,
            gpsLatitude = itemLocation?.latitude, gpsLongitude = itemLocation?.longitude,
            gpsPlaceName = itemAddress
        )
        hypelist.items.add(hypelistItem)

        Realm.getDefaultInstance().executeTransactionAwait {
            val realmHypelist = RealmHypelist.fromHypelist(hypelist)

            it.insertOrUpdate(realmHypelist)
        }

        return hypelist
    }

    override suspend fun updateHypelistItem(
        hypelist: Hypelist, hypelistItemID: String,
        itemTitle: String, itemDescription: String, itemCategory: String,
        itemNote: String?, itemAddress: String?, itemLocation: Location?, itemLink: String?
    ): Hypelist {
        var index: Int? = null
        for (item in hypelist.items) {
            if (item.id == hypelistItemID) {
                index = hypelist.items.indexOf(item)
                hypelist.items.remove(item)
                break
            }
        }
        val hypelistItem = HypelistItem(
            id = hypelistItemID,
            name = itemTitle, description = itemDescription, category = itemCategory,
            note = itemNote, link = itemLink,
            gpsLatitude = itemLocation?.latitude, gpsLongitude = itemLocation?.longitude,
            gpsPlaceName = itemAddress
        )

        index?.let {
            hypelist.items.add(it, hypelistItem)
        }

        //hypelist.items.add(hypelistItem)

        Realm.getDefaultInstance().executeTransactionAwait {
            val realmHypelist = RealmHypelist.fromHypelist(hypelist)

            it.insertOrUpdate(realmHypelist)
        }

        return hypelist
    }

    ///

    override suspend fun deleteHypelistItem(
        hypelist: Hypelist,
        idListToDelete: ArrayList<String>
    ): Hypelist {
        for (id in idListToDelete) {
            for (item in hypelist.items) {
                if (item.id == id) {
                    hypelist.items.remove(item)
                    break
                }
            }
        }

        Realm.getDefaultInstance().executeTransactionAwait {
            val realmHypelist = RealmHypelist.fromHypelist (hypelist)
            it.insertOrUpdate(realmHypelist)
        }

        return hypelist
    }

    override suspend fun updateSingleHypelist(hypelist: Hypelist): Hypelist {
        Realm.getDefaultInstance().executeTransactionAwait {
            it.insertOrUpdate(RealmHypelist.fromHypelist(hypelist))
        }

        return hypelist
    }

    override suspend fun updateHypelists(hypelist: Hypelist, targetHypelist: Hypelist): Hypelist {
        Realm.getDefaultInstance().executeTransactionAwait {
            it.insertOrUpdate(RealmHypelist.fromHypelist(hypelist))
            it.insertOrUpdate(RealmHypelist.fromHypelist(targetHypelist))
        }

        return hypelist
    }

    override suspend fun updateBookmarksStatus(
        hypelist: Hypelist, bookmarksFlows: HashMap<String, MutableStateFlow<Boolean>>
    ) {
        loggedInUserID()?.let { userID ->
            Realm.getDefaultInstance().executeTransactionAwait {
                val hypelists = it.where(RealmHypelist::class.java).equalTo("authorID", userID).findAll()

                for (item in hypelist.items) {
                    item.id?.let { hypelistItemID ->
                        var bookmarkStatus = false

                        hypelists.forEach {
                            if (it.items.filter {
                                    it.originalHypelistID == hypelist.id && it.originalHypelistItemID == hypelistItemID
                                }.isNotEmpty()) {
                                bookmarkStatus = true
                            }
                        }

                        bookmarksFlows[hypelistItemID]?.value = bookmarkStatus
                    }
                }
            }
        }
    }

    override suspend fun deleteBookmarkedItems(hypelistID: String, hypelistItemID: String) {
        loggedInUserID()?.let { userID ->
            Realm.getDefaultInstance().executeTransactionAwait { realm ->
                val hypelists =
                    realm.where(RealmHypelist::class.java).equalTo("authorID", userID).findAll()

                hypelists.forEach { hypelist ->
                    val items = hypelist.items
                    val itemsToDelete = items.filter { item ->
                        item.originalHypelistID == hypelistID && item.originalHypelistItemID == hypelistItemID
                    }
                    for (item in itemsToDelete) {
                        item.id?.let { itemToDeleteID ->
                            val currentCacheDir = application.filesDir.absolutePath + "/CachedHypelists"
                            val currentHypelistCacheDir = "$currentCacheDir/Hypelist_${hypelist.id}"
                            val currentHypelistItemsCacheDir = "$currentHypelistCacheDir/Items"
                            val currentHypelistItemCacheDir = "$currentHypelistItemsCacheDir/Item_${itemToDeleteID}"

                            if (File("$currentHypelistItemCacheDir/HypelistItemCover.jpg").exists()) {
                                File("$currentHypelistItemCacheDir/HypelistItemCover.jpg").delete()
                            }

                            if (File("$currentHypelistItemCacheDir/SmallHypelistItemCover.jpg").exists()) {
                                File("$currentHypelistItemCacheDir/SmallHypelistItemCover.jpg").delete()
                            }
                        }

                        item.deleteFromRealm()
                    }
                }
            }
        }
    }

    override suspend fun loadHypelistCover(coverURL: String?) : Bitmap? {
        val response = hypelistsService.loadImage(coverURL)
        response?.bytes()?.let { body ->
            return BitmapFactory.decodeByteArray(body, 0, body.size)
        } ?: run {
            return null
        }
    }
}