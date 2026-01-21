package com.vs.vibeplayer.main.presentation.player

import com.vs.vibeplayer.main.presentation.playlist.PlaylistEvent

interface PlayerEvent {
    data class OnCreateChannel(val isExists : Boolean , val title : String ? = null) : PlayerEvent
    data class OnSongAdd (val title: String , val isExists: Boolean): PlayerEvent

    data class OnAddedToFavourites(val isAdded: Boolean): PlayerEvent
}