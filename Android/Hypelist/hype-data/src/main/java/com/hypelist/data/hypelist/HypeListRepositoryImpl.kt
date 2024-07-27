package com.hypelist.data.hypelist

import com.hypelist.data.database.realm.RealmHypelist
import com.hypelist.domain.hypelist.HypeListRepository
import com.hypelist.domain.user.UserInformationRepository
import com.hypelist.entities.hypelist.Hypelist
import io.realm.Realm
import io.realm.kotlin.toFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class HypeListRepositoryImpl(
    private val userInformationRepository: UserInformationRepository,
) : HypeListRepository {

    override suspend fun loadUserHypeLists(): Flow<List<Hypelist>> {
        val loggedUserId = userInformationRepository.loadLoggedUser()?.id
        val realm = Realm.getDefaultInstance()
        return Realm.getDefaultInstance()
            .where(RealmHypelist::class.java)
            .equalTo("authorID", loggedUserId)
            .findAll()
            .toFlow<RealmHypelist>()
            .map { results ->
                realm.copyFromRealm(results)
                    .map { it.asHypelist() }
                    .reversed()
            }

    }

    override suspend fun loadUserSavedHypeLists(): Flow<List<Hypelist>> {
        val realm = Realm.getDefaultInstance()
        return realm
            .where(RealmHypelist::class.java)
            .equalTo("isFavorite", true)
            .findAll()
            .toFlow<RealmHypelist>()
            .map { results ->
                realm.copyFromRealm(results)
                    .map { it.asHypelist() }
                    .reversed()
            }
    }

    override suspend fun loadUserFollowingHypeLists(): Flow<List<Hypelist>> {
        val realm = Realm.getDefaultInstance()
        return realm
            .where(RealmHypelist::class.java)
            .equalTo("isDebugFollowersList", true)
            .findAll()
            .toFlow<RealmHypelist>()
            .map { results ->
                realm.copyFromRealm(results)
                    .map { it.asHypelist() }
                    .reversed()
            }

    }



    override suspend fun deleteHypeList(id: String) {
        Realm.getDefaultInstance()
            .where(RealmHypelist::class.java)
            .equalTo("id", id)
            .findAll()
            .deleteAllFromRealm()
    }

    override suspend fun refreshData() {
        // Will be implemented once backend is connected.
    }
}