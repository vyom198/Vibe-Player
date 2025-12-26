package com.vs.vibeplayer.main.domain.audio

import com.vs.vibeplayer.core.database.track.TrackEntity
import kotlinx.coroutines.flow.Flow

interface AudioDataSource {
   suspend fun  scanAndSave(duration : Int? =null , size : Int? = null): Boolean



}