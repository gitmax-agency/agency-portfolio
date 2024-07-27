package com.hypelist.data.database.realm

import com.hypelist.entities.hypelist.Hypelist
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class RealmHypelist(
    @PrimaryKey
    var id: String? = null,
    var authorID: String? = null,

    var name: String? = null,
    var author: String? = null,
    var category: String? = null,
    var coverURL: String? = null,
    var isPrivate: Boolean = true,
    var isFavorite: Boolean = false,

    var isDebugFollowersList: Boolean = false,
    var isAutocoloredBlurBackground: Boolean = false,

    var items: RealmList<RealmHypelistItem> = RealmList()
) : RealmObject() {

    companion object {

        fun fromHypelist(hypelist: Hypelist) : RealmHypelist {
            val realmHypelist = RealmHypelist(
                id = hypelist.id,
                authorID = hypelist.authorID,
                name = hypelist.name,
                author = hypelist.author,
                category = hypelist.category,
                coverURL = hypelist.coverURL,
                isPrivate = hypelist.isPrivate,
                isFavorite = hypelist.isFavorite,
                isDebugFollowersList = hypelist.isDebugFollowersList,
                isAutocoloredBlurBackground = hypelist.isAutocoloredBlurBackground
            )

            for (item in hypelist.items) {
                val realmHypelistItem = RealmHypelistItem.fromHypelistItem(item)
                realmHypelist.items.add(realmHypelistItem)
            }

            return realmHypelist
        }
    }

    fun asHypelist() : Hypelist {
        val hypelist = Hypelist(
            id = id,
            authorID = authorID,
            name = name,
            author = author,
            category = category,
            coverURL = coverURL,
            isPrivate = isPrivate,
            isFavorite = isFavorite,
            items = ArrayList(),
            isDebugFollowersList = isDebugFollowersList,
            isAutocoloredBlurBackground = isAutocoloredBlurBackground
        )

        for (item in items) {
            hypelist.items.add(item.asHypelistItem())
        }

        return hypelist
    }
}