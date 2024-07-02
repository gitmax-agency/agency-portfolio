package com.hypelist.services

import com.google.firebase.messaging.FirebaseMessagingService

class NoticationsService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        println(token)
        super.onNewToken(token)
    }
}