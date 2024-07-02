package com.hypelist.data.home

import android.app.Application
import com.hypelist.domain.home.SearchRepository
import com.hypelist.entities.hypelist.Hypelist
import com.hypelist.data.database.realm.RealmHypelist
import io.realm.Realm
import io.realm.kotlin.executeTransactionAwait

class SearchRepositoryImpl(application: Application) : SearchRepository {

    override suspend fun loadAllHypelists(): List<Hypelist> {
        val list = ArrayList<Hypelist>()

        Realm.getDefaultInstance().executeTransactionAwait {
            val result = it.where(RealmHypelist::class.java).findAll()
            for (item in result) {
                list.add(it.copyFromRealm(item).asHypelist())
            }
        }

        val names = arrayOf("Tokyo", "Okinawa", "Kyoto", "Neofauna", "Sands")

        for (i in 0 until 3) {
            val hypelist = Hypelist(i.toString(), "0", names[i], "Ayumu Uehara",
                "", null, false, i != 1, false, false, ArrayList())
            list.add(hypelist)
        }

        return list
    }
}