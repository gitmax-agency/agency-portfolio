package com.hypelist.domain.home

import com.hypelist.entities.hypelist.Hypelist


interface SearchRepository {

    suspend fun loadAllHypelists(): List<Hypelist>
}