package com.hypelist.domain.hypelist

import com.hypelist.domain.home.BaseHomeRepository
import com.hypelist.entities.hypelist.Hypelist
import com.hypelist.entities.hypelist.HypelistItem

interface CreationRepository : BaseHomeRepository {

    suspend fun createDebugHypelist(
        id: String, name: String, author: String, isPrivate: Boolean,
        isDebugFollowersList: Boolean, items: List<HypelistItem>
    )

    suspend fun purgeDebugHypelists()

    suspend fun uploadHypelistCover(coverBytes: ByteArray, onCompletion: (String?) -> Unit)
    suspend fun createOnlineHypelist(
        title: String, isPublic: Boolean, imageURL: String?
    )
    suspend fun createHypelist(
        id: String, name: String, author: String, isPrivate: Boolean, isDebugFollowersList: Boolean
    )

    suspend fun updateHypelist(hypelist: Hypelist)
}