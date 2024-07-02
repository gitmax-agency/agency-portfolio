package com.hypelist.data.hypelist.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreationRequest(
    @SerialName("title") val title: String,
    @SerialName("cover") val cover: CreationCover,
    @SerialName("type") val type: String,
    @SerialName("rotation") val rotation: Int,
    @SerialName("scale") val scale: Int,
    @SerialName("isPublic") val isPublic: Boolean,
    @SerialName("userId") val userId: String,
    @SerialName("openAIStatus") val openAIStatus: String
)