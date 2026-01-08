package com.vs.vibeplayer.main.presentation.addsongs

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vs.vibeplayer.core.database.playlist.PlaylistDao
import com.vs.vibeplayer.core.database.playlist.PlaylistEntity
import com.vs.vibeplayer.core.database.track.TrackDao
import com.vs.vibeplayer.main.presentation.playlist.PlaylistEvent
import com.vs.vibeplayer.main.presentation.search.SearchResult
import com.vs.vibeplayer.main.presentation.search.SearchState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

class AddSongsViewModel(
    private val trackDao: TrackDao,
    private val savedStateHandle: SavedStateHandle,
    private val playlistDao : PlaylistDao
) : ViewModel() {

    private var hasLoadedInitialData = false
    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()
    private  val playlistTitle = savedStateHandle.get<String?>("playlistTitle")

    private val _allTracks = trackDao.observeTracks()
    private  var  totalCount = 0

    private val eventChannel = Channel<AddSongEvent>()
    val events = eventChannel.receiveAsFlow()
    private val _state = MutableStateFlow(AddSongsState())
    val state = _state.onStart {
            if (!hasLoadedInitialData) {
               totalCount = trackDao.getTrackCount()
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
                    _state.update { state ->
                        state.copy(
                            searchResults = results.map {
                                it.copy(
                                    isSelected = it.id in state.selectedIds
                                )

                            } ,

                            selectedIds = state.selectedIds ,
                            isSelectAll = state.selectedIds.size == totalCount

                        )
                    }
                }
        }
    }

    private fun insertSongs(playlistTitle : String){
        viewModelScope.launch {
           val  playlistEntityfromDb= playlistDao.getplaylistByTitle(playlistTitle)
            Timber.d(playlistEntityfromDb.toString())
            val coverArt = getCoverArt(_state.value.selectedIds.first())
            val newplayListEntity = PlaylistEntity(
                id = playlistEntityfromDb.id,
                title = playlistTitle,
                trackIds = _state.value.selectedIds.toList(),
                coverArt = coverArt
            )
            Timber.d(newplayListEntity.toString())
                playlistDao.insert(
                    newplayListEntity
                )

            eventChannel.send(AddSongEvent.onInsertEvent)

        }

    }
    suspend  fun getCoverArt(trackId: Long): ByteArray?{
        val trackEntity = trackDao.getTrackById(trackId)
        Timber.d("trackEntity $trackEntity")
        return trackEntity?.cover

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

                    _state.update {
                        it.copy(
                            selectedIds = if(action.isSelectAll)it.searchResults.map { it.id }.toSet() else emptySet(),
                            isSelectAll = action.isSelectAll,
                            searchResults = it.searchResults.map {
                                it.copy(isSelected = action.isSelectAll)
                            }

                        )
                    }

                }
            }
            is AddSongsAction.onToggleClickbyItem -> {

                viewModelScope.launch {
                    _state.update { state->
                       state.copy(
                           selectedIds =  if(action.isSelected){ state.selectedIds.plus(action.id)} else {
                               state.selectedIds.minus(action.id)
                           },
                           isSelectAll = state.selectedIds.size == totalCount,
                           searchResults = state.searchResults.map {
                               if(it.id == action.id){
                                   it.copy(isSelected = !it.isSelected)
                               }else{
                                   it.copy(isSelected = it.isSelected)
                               }
                           }
                       )
                    }


                }
            }

            AddSongsAction.onInsertSong -> playlistTitle?.let {
                Timber.d(it)
                insertSongs(it) }
        }
    }

}
