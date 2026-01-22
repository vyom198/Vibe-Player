package com.vs.vibeplayer.main.presentation.playlistDetail

sealed interface PlaylistDetailAction {
     data object onShuffleClick : PlaylistDetailAction
    data object onPlayClick : PlaylistDetailAction
}