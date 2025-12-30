package com.vs.vibeplayer.main.presentation.miniplayer

import com.vs.vibeplayer.core.database.track.TrackEntity
import com.vs.vibeplayer.main.presentation.player.RepeatType

data class MiniPlayerState(
    val isPlaying: Boolean = false,
    val currentSong:  TrackEntity? = null,
    val currentPosition: Long = 0L,
    val duration: Long = 0L,
    val canGoNext: Boolean = false,
){

    val progress: Float
        get() = if (duration > 0) {
            currentPosition.toFloat() / duration.toFloat()
        } else {
            0f
        }
}