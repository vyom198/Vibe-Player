package com.vs.vibeplayer.main.presentation.playlist

import com.vs.vibeplayer.main.presentation.VibePlayer.VibePlayerEvent

interface PlaylistEvent {
    data class OnCreateChannel(val isExists : Boolean , val title : String ? = null) : PlaylistEvent

}