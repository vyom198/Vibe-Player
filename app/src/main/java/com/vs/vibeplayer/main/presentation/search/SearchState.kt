package com.vs.vibeplayer.main.presentation.search


import com.vs.vibeplayer.core.database.track.TrackEntity

data class SearchState(
    val isLoading: Boolean = false,
    val searchResults : List<SearchResult> = emptyList(),
)