package com.hypelist.data.home.profile

import android.app.Application
import com.hypelist.data.repositories.BaseRepositoryImpl
import com.hypelist.domain.home.profile.AccountSettingsRepository
import com.hypelist.data.database.realm.RealmUser
import io.realm.Realm
import io.realm.kotlin.executeTransactionAwait
import java.io.File

class AccountSettingsRepositoryImpl(
    private val application: Application
) : BaseRepositoryImpl(application), AccountSettingsRepository {

    override suspend fun deleteLoggedInUser() {
        Realm.getDefaultInstance().executeTransactionAwait {
            it.where(RealmUser::class.java).findAll().deleteAllFromRealm()
        }
    }

    override suspend fun deleteCachedUserImages() {
        Realm.getDefaultInstance().executeTransactionAwait {
            val cachedUser = it.where(RealmUser::class.java).findFirst()
            cachedUser?.id?.let { userID ->
                val cacheDir = application.filesDir.absolutePath + "/CachedUsers"
                val userCacheDir = "$cacheDir/User_$userID"

                if (File(userCacheDir).exists()) {
                    File(userCacheDir).delete()
                }
            }
        }
    }
}