package com.hypelist.data.auth

import com.google.firebase.auth.FirebaseAuth
import com.hypelist.domain.architecture.CoroutineDispatchers
import com.hypelist.domain.auth.AuthData
import com.hypelist.domain.auth.AuthRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class AuthRepositoryImpl(
    private val dispatchers: CoroutineDispatchers,
) : AuthRepository {
    private val auth: FirebaseAuth
        get() = FirebaseAuth.getInstance()

    override suspend fun isUserLoggedIn(): Boolean = withContext(dispatchers.io) {
        delay(100)
        getLoggedUserId() != null
    }

    override suspend fun getAuthData(): AuthData = withContext(dispatchers.io) {
        val currentUser = auth.currentUser
        AuthData(
            uuid = currentUser?.uid.orEmpty(),
            email = currentUser?.email.orEmpty(),
            displayName = "Maycon Cardoso",
            imageProfileUrl = currentUser?.photoUrl?.path
        )
    }

    override suspend fun getLoggedUserId(): String? = withContext(dispatchers.io) {
        auth.currentUser?.uid
    }
}