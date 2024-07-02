package com.hypelist.entities.hypelist

class Hypelist(
    var id: String?,
    var authorID: String?,
    var name: String?,
    var author: String?,
    var category: String?,
    var coverURL: String?,
    var isPrivate: Boolean,
    var isFavorite: Boolean,
    var isDebugFollowersList: Boolean,
    var isAutocoloredBlurBackground: Boolean,
    var items: ArrayList<HypelistItem>
)