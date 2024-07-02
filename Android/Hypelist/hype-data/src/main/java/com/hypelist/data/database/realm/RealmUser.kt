package com.hypelist.data.database.realm

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class RealmUser(
    var id: String? = null,
    var uuid: String? = null,
    var accessToken: String? = null,
    var isLoggedIn: Boolean = false,
    var displayName: String? = null,
    var username: String? = null,
    var email: String? = null,
    var photoURL: String? = null,
    var backgroundURL: String? = null,
    var amIFollowing: Boolean? = null,
    var isUserFollowed: Boolean? = null,
    var socialNetworks: String? = null,
    var bio: String? = null,
    var token: String? = null,
    var notifications: Boolean? = null,
    var privateSavedList: Boolean? = null,
    var invitation: String? = null,
    var rotation: Double? = null,
    var invited: Boolean? = null,
    var accepted: Boolean? = null,
    var verified: Boolean? = null,

) : RealmObject()