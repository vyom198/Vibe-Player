package com.vs.vibeplayer.main.domain.player

import com.vs.vibeplayer.core.database.track.TrackEntity

data class PlayerState(
    val isPlaying: Boolean = false,
    val currentSong: TrackEntity? = null,
    val currentPosition: Long = 0L,
    val duration: Long = 0L,
    val canGoNext: Boolean = false,
    val canGoPrevious: Boolean = false
)