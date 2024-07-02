package com.hypelist.data.user

import com.hypelist.data.database.realm.RealmUser
import com.hypelist.entities.user.UserInformation

fun UserInformation.asDatabaseUser() = RealmUser(
    id = id,
    uuid = id,
    displayName = displayName,
    username = username,
    email = email,
    photoURL = photoURL,
    backgroundURL = backgroundURL,
    amIFollowing = amIFollowing,
    isUserFollowed = isOtherFollowing,
    socialNetworks = null,
    bio = bio,
    token = token,

    notifications = notifications,
    privateSavedList = false,
    invitation = null,
    rotation = 0.0,
    invited = false,
    accepted = false,
    verified = false,
)
