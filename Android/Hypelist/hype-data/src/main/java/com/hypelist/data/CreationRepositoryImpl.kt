package com.hypelist.data

import android.app.Application
import com.google.firebase.storage.FirebaseStorage
import com.hypelist.data.hypelist.api.CreationApi
import com.hypelist.data.home.BaseHomeRepositoryImpl
import com.hypelist.domain.hypelist.CreationRepository
import com.hypelist.entities.hypelist.Hypelist
import com.hypelist.entities.hypelist.HypelistItem
import com.hypelist.data.database.realm.RealmHypelist
import com.hypelist.data.database.realm.RealmHypelistItem
import com.hypelist.data.hypelist.api.CreationCover
import com.hypelist.data.hypelist.api.CreationRequest
import com.hypelist.data.hypelist.api.CreationResponse
import io.realm.Realm
import io.realm.kotlin.executeTransactionAwait
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.UUID

class CreationRepositoryImpl(application: Application) : BaseHomeRepositoryImpl(application),
    CreationRepository, KoinComponent {

    private val apiService: CreationApi by inject()

    private val storage = FirebaseStorage.getInstance()
    private val storageReference = storage.reference

    override suspend fun purgeDebugHypelists() {
        Realm.getDefaultInstance().executeTransactionAwait {
            it.where(RealmHypelist::class.java).equalTo("isDebugFollowersList", true).findAll().deleteAllFromRealm()
        }
    }

    override suspend fun createDebugHypelist(
        id: String, name: String, author: String, isPrivate: Boolean, isDebugFollowersList: Boolean, items: List<HypelistItem>
    ) {
        Realm.getDefaultInstance().executeTransactionAwait {
            val hypelist = RealmHypelist(
                id = id,
                authorID = "1",
                name = name,
                author = author,
                isPrivate = isPrivate,
                isFavorite = false,
                isDebugFollowersList = isDebugFollowersList
            )

            for (item in items) {
                hypelist.items.add(RealmHypelistItem.fromHypelistItem(item))
            }

            it.copyToRealm(hypelist)
        }
    }

    override suspend fun uploadHypelistCover(
        coverBytes: ByteArray,
        onCompletion: (String?) -> Unit
    ) {
        loggedInUserID()?.let { userID ->
            if (false) {
                val reference = storageReference.child("Cover_images/$userID/${UUID.randomUUID()}.jpg")

                reference.putBytes(coverBytes).addOnSuccessListener { // Image uploaded successfully
                    reference.downloadUrl.addOnSuccessListener { imageURL ->
                        onCompletion(imageURL.toString())
                    }
                }.addOnFailureListener { e -> // Error, Image not uploaded
                    onCompletion(null)
                }
            } else {
                onCompletion(null)
            }
        }
    }
    override suspend fun createOnlineHypelist(
        title: String, isPublic: Boolean, imageURL: String?
    ) {
        var response: CreationResponse? = null

        loggedInUserID()?.let { userID ->
            response = apiService.createHypelist(userID, CreationRequest(
                title = title,
                cover = CreationCover(
                    //hex1 = "#ff9999",
                    //hex2 = "#9999ff",
                    image = imageURL ?: ""
                ),
                type = "Entertainment",
                rotation = 0,
                scale = 0,
                isPublic = true,
                userId = userID,
                openAIStatus = ""
            )
            )
        }

    }
    override suspend fun createHypelist(
        id: String, name: String, author: String, isPrivate: Boolean, isDebugFollowersList: Boolean
    ) {
        loggedInUserID()?.let { userID ->
            Realm.getDefaultInstance().executeTransactionAwait {
                val hypelist = RealmHypelist(
                    id = id,
                    authorID = userID,
                    name = name,
                    author = author,
                    isPrivate = isPrivate,
                    isFavorite = false,
                    isDebugFollowersList = isDebugFollowersList
                )

                it.copyToRealm(hypelist)
            }
        }
    }

    override suspend fun updateHypelist(hypelist: Hypelist) {
        Realm.getDefaultInstance().executeTransactionAwait {
            val oldHypelist = it.where(RealmHypelist::class.java).equalTo("id", hypelist.id).findFirst()
            oldHypelist?.name = hypelist.name
            oldHypelist?.isPrivate = hypelist.isPrivate
        }
    }
}