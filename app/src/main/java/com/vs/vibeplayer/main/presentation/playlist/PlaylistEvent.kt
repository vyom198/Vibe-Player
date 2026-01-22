package com.vs.vibeplayer.main.presentation.playlist

import androidx.datastore.preferences.protobuf.Empty

interface PlaylistEvent {
    data class OnCreateChannel(val isExists : Boolean , val title : String ? = null) : PlaylistEvent
    data object OnDeleteChannel : PlaylistEvent
    data object onCoverChangeChannel : PlaylistEvent
    data object onfavPlayClicked : PlaylistEvent
    data class onRegularPlaylistPlay(val isEmpty: Boolean) : PlaylistEvent
}


