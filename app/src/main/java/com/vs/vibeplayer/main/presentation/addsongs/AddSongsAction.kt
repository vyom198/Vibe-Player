package com.vs.vibeplayer.main.presentation.addsongs

import com.vs.vibeplayer.main.presentation.search.SearchAction

sealed interface AddSongsAction {
    data class OnTextChange(val query : String) : AddSongsAction

    object OnClearClick : AddSongsAction
}