package com.vs.vibeplayer.main.domain.favourite

import kotlinx.coroutines.flow.Flow


interface FavouritePrefs {
    suspend fun toggleFavourite(trackId: Long)
    suspend fun getfavouriteList() : Flow<Set<Long>>

}


