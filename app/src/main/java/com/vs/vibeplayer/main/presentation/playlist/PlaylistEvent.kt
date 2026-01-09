package com.vs.vibeplayer.main.presentation.playlist
interface PlaylistEvent {
    data class OnCreateChannel(val isExists : Boolean , val title : String ? = null) : PlaylistEvent

}