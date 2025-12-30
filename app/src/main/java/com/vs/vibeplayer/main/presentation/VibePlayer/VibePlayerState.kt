package com.vs.vibeplayer.main.presentation.VibePlayer

import com.vs.vibeplayer.main.presentation.model.AudioTrackUI


data class VibePlayerState(
    val scanning: Boolean = false,
    val trackList: List<AudioTrackUI> = emptyList(),
    val loadingInReScan : Boolean = false,
    val durationValue : Int ? = null,
    val sizeValue : Int ? = null,
    val isPlaying : Boolean = false
    )