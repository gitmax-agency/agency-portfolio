package com.hypelist.entities.invitation

data class Invitation(
    var sender: String,
    var accepted: Boolean,
    var hypelist: String,
)
