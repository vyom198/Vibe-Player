package com.vs.vibeplayer.main.presentation.search

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.util.query
import com.vs.vibeplayer.core.database.track.TrackDao
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SearchViewModel (
    private val trackDao: TrackDao,

): ViewModel() {



    private val _state = MutableStateFlow(SearchState())
    val state = _state.asStateFlow()

    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()
    private val _allTracks = trackDao.observeTracks()

    init {
        setupSearchPipeline()
    }

    private fun setupSearchPipeline() {
        viewModelScope.launch {
            _searchText
                .debounce(300).combine(_allTracks) { query, tracks ->
                    if (query.isBlank() || query.length <2) {
                        emptyList<SearchResult>()
                    } else {
                        tracks.filter { track ->
                            track.title.contains(query, ignoreCase = true) ||
                                    track.artist.contains(query, ignoreCase = true)
                        }.map { track ->
                            SearchResult(
                                id = track.id,
                                title = track.title,
                                artist = track.artist,
                                cover = track.cover,
                                totalDurationMs = track.totalDuration
                            )
                        }
                    }
                }.collect { results ->
                    _state.update {
                        it.copy(
                            searchResults = results,
                            isLoading = false
                        )
                    }
                }
        }
    }

    fun onAction(action: SearchAction) {
        when (action) {

            is SearchAction.OnSearchTextChange ->{
               _searchText.value = action.query
                _state.update {
                    it.copy(isLoading = true)
                }

            }
            is SearchAction.OnTrackClick -> TODO()
            SearchAction.OnCrossClick -> {
                _searchText.value = ""
                _state.update {
                    SearchState() // Reset to initial
                }
            }
        }
    }

}