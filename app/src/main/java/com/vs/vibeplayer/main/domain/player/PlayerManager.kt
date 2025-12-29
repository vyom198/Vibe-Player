package com.vs.vibeplayer.main.domain.player

import com.vs.vibeplayer.core.database.track.TrackEntity
import com.vs.vibeplayer.main.presentation.player.RepeatType
import kotlinx.coroutines.flow.StateFlow


interface PlayerManager {
    val playerState: StateFlow<PlayerState>

    fun initialize(clickedSong: TrackEntity, playlist: List<TrackEntity>)

    fun seekTo(position: Long)
    fun play()
    fun pause()
    fun stop()
    fun release()

    fun next()
    fun previous()


    fun onRepeatClick()

    fun shuffleSong()


}

