package com.hypelist.data.user

import com.hypelist.data.database.realm.RealmUser
import com.hypelist.data.user.api.UserInformationApi
import com.hypelist.data.user.api.UsernameAvailabilityRequest
import com.hypelist.domain.architecture.CoroutineDispatchers
import com.hypelist.domain.auth.AuthRepository
import com.hypelist.domain.user.UserInformationException
import com.hypelist.domain.user.UserInformationRepository
import com.hypelist.entities.user.UserInformation
import io.realm.Realm
import io.realm.kotlin.executeTransactionAwait
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class UserInformationRepositoryImpl(
    private val dispatchers: CoroutineDispatchers,
    private val authRepository: AuthRepository,
    private val userInformationApi: UserInformationApi,
) : UserInformationRepository {

    override suspend fun loadLoggedUser(): UserInformation? = withContext(dispatchers.io) {
        try {
            loadUserById(authRepository.getLoggedUserId().orEmpty())
        } catch (exception: Exception) {
            null
        }
    }

    override suspend fun loadUserForSignIn(): UserInformation = withContext(dispatchers.io) {
        try {
            loadUserById(authRepository.getLoggedUserId().orEmpty())
        } catch (exception: Exception) {
            throw mapUserException(exception)
        }
    }

    override suspend fun checkUsernameAvailability(username: String): Boolean {
        try {
            val response = userInformationApi.checkUsernameAvailability(
                UsernameAvailabilityRequest(username = username)
            ).response()

            if (response == "taken") {
                throw UserInformationException.UsernameNotAvailable
            }

            return true
        } catch (exception: Exception) {
            throw mapUserException(exception)
        }
    }


    override suspend fun signUp(user: UserInformation): UserInformation {
        try {
            return userInformationApi.createUser(user).response()
        } catch (exception: Exception) {
            throw mapUserException(exception)
        }
    }

    override suspend fun loadUserById(userId: String): UserInformation {
        return userInformationApi.getUserInfo(userId).response()
    }

    override suspend fun updateUser(userInformation: UserInformation) {
        Realm.getDefaultInstance().executeTransactionAwait {
            it.where(RealmUser::class.java).equalTo("id", userInformation.id).findAll()
                .deleteAllFromRealm()
            it.copyToRealm(userInformation.asDatabaseUser())
        }
    }

    private fun mapUserException(exception: Exception): UserInformationException {
        return when (exception) {
            is UserInformationException -> exception
            is HttpException -> mapNetworkException(exception)
            else -> throw UserInformationException.GenericUserException
        }
    }

    private fun mapNetworkException(exception: HttpException): UserInformationException {
        return when (exception.code()) {
            400 -> UserInformationException.NotFoundUserException
            else -> UserInformationException.GenericUserException
        }
    }
}