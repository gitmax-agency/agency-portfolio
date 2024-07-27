package com.hypelist.domain.auth

data class AuthData(
    val uuid: String,
    val email: String,
    val displayName: String,
    val imageProfileUrl: String?,
)
