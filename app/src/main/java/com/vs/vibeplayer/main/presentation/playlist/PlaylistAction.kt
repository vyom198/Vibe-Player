package com.vs.vibeplayer.main.presentation.playlist

import com.vs.vibeplayer.main.presentation.model.PlaylistUI

sealed interface PlaylistAction {
   data object AddIconClickedOrCreatePlaylist : PlaylistAction
   data object onDismissSheet : PlaylistAction
   data object onSnackBarDismissed : PlaylistAction
   data class onCreateClick (val title : String) : PlaylistAction

   data class onTextChange (val title: String): PlaylistAction
   data object  OnFavMenuIconClick : PlaylistAction
   data object onDismissFavSheet : PlaylistAction
   data object onDismissPlayListSheet : PlaylistAction

   data class onPlaylistMenuIconClick(val playlistUI: PlaylistUI ): PlaylistAction


   data object onDeleteButtonClick : PlaylistAction
   data object onRenameButtonClick : PlaylistAction

   object onDismissRenameSheet: PlaylistAction
   data class onPrefilledTextChange(val prefilledText : String) : PlaylistAction
   data class onRenameConfirm (val title : String) : PlaylistAction

}