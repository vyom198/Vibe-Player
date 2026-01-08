package com.vs.vibeplayer.main.presentation.addsongs

import com.vs.vibeplayer.main.presentation.playlist.PlaylistAction

sealed interface AddSongsAction {
    data class OnTextChange(val query : String) : AddSongsAction

    object OnClearClick : AddSongsAction
    data class onSelectAll(val isSelectAll : Boolean) : AddSongsAction
    data object onInsertSong  : AddSongsAction
    data class onToggleClickbyItem(val id : Long , val isSelected : Boolean) : AddSongsAction
}