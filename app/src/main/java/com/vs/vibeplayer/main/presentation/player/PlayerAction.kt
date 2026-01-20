package com.vs.vibeplayer.main.presentation.player

sealed interface PlayerAction {
    object PlayOrPause : PlayerAction
    object Stop : PlayerAction
    object Next : PlayerAction
    object Previous : PlayerAction
    object BackPressed : PlayerAction

    object ShuffleClick : PlayerAction

   object OnRepeatClick: PlayerAction

    data class OnSeek(val position : Long): PlayerAction


    object  onDismissBs : PlayerAction

    object ToggleFavourite  : PlayerAction

    object onPlaylistIconClick : PlayerAction

    data class  onPlaylistItemClicked(val playlistId : Long ) : PlayerAction

    object  onCreatePlaylist : PlayerAction
}