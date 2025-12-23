package com.vs.vibeplayer.main.presentation.VibePlayer

import com.vs.vibeplayer.main.presentation.model.AudioTrackUI


data class VibePlayerState(
    val scanning: Boolean = false,
    val trackList: List<AudioTrackUI> = emptyList(),

    )