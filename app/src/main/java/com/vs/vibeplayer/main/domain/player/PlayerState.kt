package com.vs.vibeplayer.main.domain.player

import com.vs.vibeplayer.core.database.track.TrackEntity
import com.vs.vibeplayer.main.presentation.player.RepeatType

data class PlayerState(
    val isPlaying: Boolean = false,
    val currentSong: TrackEntity? = null,
    val currentPosition: Long = 0L,
    val duration: Long = 0L,
    val canGoNext: Boolean = false,
    val canGoPrevious: Boolean = false,
    val repeatType: RepeatType = RepeatType.OFF,
    val isShuffleEnabled: Boolean = false,
    val currentPositionFraction: Float = 0f
)