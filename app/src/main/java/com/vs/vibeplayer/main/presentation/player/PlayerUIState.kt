package com.vs.vibeplayer.main.presentation.player

import com.vs.vibeplayer.core.database.track.TrackEntity

data class PlayerUIState(
    val isPlaying: Boolean = false,
    val currentSong:  TrackEntity? = null,
    val currentPosition: Long = 0L,
    val duration: Long = 0L,
    val canGoNext: Boolean = false,
    val canGoPrevious: Boolean = false
){
    val progress: Float
        get() = if (duration > 0) {
            currentPosition.toFloat() / duration.toFloat()
        } else {
            0f
        }
}