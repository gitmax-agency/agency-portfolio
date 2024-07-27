package com.hypelist.data.hypelist.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreationCover(
    @SerialName("image") val image: String,
    //@SerialName("hex1") val hex1: String,
    //@SerialName("hex2") val hex2: String
)