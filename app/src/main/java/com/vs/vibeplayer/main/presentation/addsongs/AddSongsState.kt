package com.vs.vibeplayer.main.presentation.addsongs


data class AddSongsState(
    val searchResults : List<AddSongResult> = emptyList(),
    val isSelectAll : Boolean = false,
    val selectedIds : Set<Long> =  emptySet()
)