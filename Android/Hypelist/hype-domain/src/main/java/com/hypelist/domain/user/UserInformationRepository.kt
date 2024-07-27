package com.hypelist.domain.user

import com.hypelist.entities.user.UserInformation

interface UserInformationRepository {
    suspend fun loadLoggedUser(): UserInformation?
    suspend fun loadUserForSignIn(): UserInformation
    suspend fun checkUsernameAvailability(username: String): Boolean
    suspend fun signUp(user: UserInformation): UserInformation

    suspend fun loadUserById(userId: String): UserInformation
    suspend fun updateUser(userInformation: UserInformation)
}