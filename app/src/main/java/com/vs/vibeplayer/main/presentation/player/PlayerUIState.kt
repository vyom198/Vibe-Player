package com.vs.vibeplayer.main.presentation.player

import com.vs.vibeplayer.core.database.track.TrackEntity


data class PlayerUIState(
    val isPlaying: Boolean = false,
    val currentSong:  TrackEntity? = null,
    val currentPosition: Long = 0L,
    val duration: Long = 0L,
    val canGoNext: Boolean = false,
    val canGoPrevious: Boolean = false,
    val currentPositionFraction: Float = 0f,
    val repeatType: RepeatType = RepeatType.OFF,
    val isShuffleEnabled: Boolean = false
){
    val progress: Float
        get() = if (duration > 0) {
            currentPosition.toFloat() / duration.toFloat()
        } else {
            0f
        }
    val  totalDuration : String
        get() = if(duration > 0){
            val totalSeconds = duration/ 1000
            val minutes = totalSeconds / 60
            val seconds = totalSeconds % 60
            String.format("%02d:%02d", minutes, seconds)
        }else{
            "00:00"
        }

    val currentDuration : String
        get() = if(currentPosition>=0){
            val totalSeconds = currentPosition / 1000
            val minutes = totalSeconds / 60
            val seconds = totalSeconds % 60
            String.format("%02d:%02d", minutes, seconds)
        }else{
            "00:00"
        }

}

