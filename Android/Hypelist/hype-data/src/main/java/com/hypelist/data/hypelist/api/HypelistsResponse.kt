package com.hypelist.data.hypelist.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HypelistsResponse(
    @SerialName("error") var error: String? = null,
    @SerialName("errorMessage") var errorMessage: String? = null,
    @SerialName("data") var data: List<HypelistResponse>? = null
)