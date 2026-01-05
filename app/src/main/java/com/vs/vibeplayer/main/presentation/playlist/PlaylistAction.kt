package com.vs.vibeplayer.main.presentation.playlist

sealed interface PlaylistAction {
   data object AddIconClickedOrCreatePlaylist : PlaylistAction
   data object onDismissSheet : PlaylistAction
}