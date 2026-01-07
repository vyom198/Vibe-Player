package com.vs.vibeplayer.main.presentation.addsongs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vs.vibeplayer.core.database.track.TrackDao
import com.vs.vibeplayer.main.presentation.search.SearchResult
import com.vs.vibeplayer.main.presentation.search.SearchState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddSongsViewModel(
    private val trackDao: TrackDao
) : ViewModel() {

    private var hasLoadedInitialData = false
    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _allTracks = trackDao.observeTracks()
    private val _state = MutableStateFlow(AddSongsState())
    val state = _state.onStart {
            if (!hasLoadedInitialData) {
               setupSearchPipeline()
                hasLoadedInitialData = true
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = AddSongsState()
        )

    private fun setupSearchPipeline() {
        viewModelScope.launch {
            _searchText
                .debounce(300).combine(_allTracks) { query, tracks ->
                    if (query.isBlank() || query.length <2) {
                       tracks.map {
                           it.toAddSongResult()
                       }
                    } else {
                        tracks.filter { track ->
                            track.title.contains(query, ignoreCase = true) ||
                                    track.artist.contains(query, ignoreCase = true)
                        }.map { track ->
                            track.toAddSongResult()
                        }
                    }
                }.collect { results ->
                    _state.update {
                        it.copy(
                            searchResults = results ,
                           isSearching = false
                        )
                    }
                }
        }
    }


    fun onAction(action: AddSongsAction) {
        when (action) {
            AddSongsAction.OnClearClick -> {
                _searchText.value = ""

            }
            is AddSongsAction.OnTextChange ->{
                _searchText.value = action.query
                _state.update {
                    it.copy(isSearching = false)
                }
            }
        }
    }

}