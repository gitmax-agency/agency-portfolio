package com.hypelist.data.hypelist.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreationData(
    @SerialName("id") var id: String?
)