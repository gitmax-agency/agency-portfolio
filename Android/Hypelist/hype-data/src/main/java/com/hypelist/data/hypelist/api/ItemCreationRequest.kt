package com.hypelist.data.hypelist.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ItemCreationRequest(
    @SerialName("title") var title: String,
    @SerialName("subtitle") var subtitle: String,
    @SerialName("notes") var notes: String,
    @SerialName("categoryId") var categoryId: String,
    @SerialName("order") var order: Int,
    @SerialName("rate") var rate: Int,
    @SerialName("rotation") var rotation: Int,
    @SerialName("scale") var scale: Int
)