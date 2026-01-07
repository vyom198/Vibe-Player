package com.vs.vibeplayer.main.presentation.addsongs

import com.vs.vibeplayer.main.presentation.search.SearchResult

data class AddSongsState(
    val isSearching: Boolean = false,
    val searchResults : List<AddSongResult> = emptyList(),
)