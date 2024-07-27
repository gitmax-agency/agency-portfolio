package com.hypelist.data.hypelist.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HypelistResponse(
    @SerialName("id") var id: String?,
    @SerialName("title") var title: String?,
    @SerialName("type") var type: String?,
    @SerialName("cover") val cover: com.hypelist.data.hypelist.api.CreationCover,
    @SerialName("likes") val likes: Int?,
    @SerialName("uid") var uid: String?,
    @SerialName("userId") var userId: String?,
    @SerialName("isPublic") var isPublic: Boolean?,
    @SerialName("isSaved") var isSaved: Boolean?,
)