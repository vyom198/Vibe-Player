package com.vs.vibeplayer.main.presentation.addsongs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vs.vibeplayer.core.database.playlist.PlaylistDao
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
    private val trackDao: TrackDao,
    private val playlistDao : PlaylistDao
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

            }

           is  AddSongsAction.onSelectAll -> {
                viewModelScope.launch {
                    _state.value.searchResults.forEach {
                        it.copy(isSelected = action.isSelectAll)
                    }
                    _state.update {
                        it.copy(
                            selectedIds = if(action.isSelectAll)it.searchResults.map { it.id }.toSet() else emptySet(),
                            isSelectAll = action.isSelectAll
                        )
                    }
                }
            }
            is AddSongsAction.onToggleClickbyItem -> {

                viewModelScope.launch {
                    _state.update {
                       it.copy(
                           selectedIds =  if(action.isSelected){ it.selectedIds.plus(action.id)} else {
                               it.selectedIds.minus(action.id)
                           }
                       )
                    }
                    val index = _state.value.searchResults.indexOfFirst { it.id == action.id }
                    _state.value.searchResults[index].copy(isSelected = action.isSelected)

                }
            }
        }
    }

}