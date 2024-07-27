package com.hypelist.entities.user

data class UserInformation(
    // Identification
    val id: String,
    val uid: String,
    val token: String,
    val email: String,

    // Profile data
    val bio: String,
    val username: String,
    val displayName: String,
    val socialNetworks: SocialNetwork?,
    val photoURL: String,
    val backgroundURL: String,
    val inviteCode: String?,
    val profileRotation: Int,
    val profileScale: Int,
    val backgroundRotation: Int,
    val backgroundScale: Int,

    // Settings
    val notifications: Boolean,

    // Social
    val amIFollowing: Boolean,
    val isOtherFollowing: Boolean,
)