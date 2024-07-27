package com.hypelist.domain.hypelist

import com.hypelist.entities.hypelist.Hypelist
import kotlinx.coroutines.flow.Flow

interface HypeListRepository {
    suspend fun loadUserHypeLists(): Flow<List<Hypelist>>
    suspend fun loadUserSavedHypeLists(): Flow<List<Hypelist>>
    suspend fun loadUserFollowingHypeLists(): Flow<List<Hypelist>>
    suspend fun deleteHypeList(id: String)
    suspend fun refreshData()
}