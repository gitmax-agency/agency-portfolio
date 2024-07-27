package com.hypelist.data.hypelist.api

import kotlinx.serialization.SerialName

data class CreationResponse(
    //@SerialName("error") var error: String? = null,
    @SerialName("errorMessage") var errorMessage: String? = null,
    @SerialName("data") var data: CreationData? = null
)