package com.vs.vibeplayer.main.presentation.addsongs

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vs.vibeplayer.core.database.playlist.PlaylistDao
import com.vs.vibeplayer.core.database.playlist.PlaylistEntity
import com.vs.vibeplayer.core.database.track.TrackDao
import com.vs.vibeplayer.main.domain.player.PlayerManager
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
    private val playlistDao : PlaylistDao,
) : ViewModel() {

    private var hasLoadedInitialData = false
    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()
    private  val playlistTitle = savedStateHandle.get<String?>("playlistTitle")
    private var isInserting = false
    private val _allTracks = trackDao.observeTracks()
    private var totalTracks = emptyList<AddSongResult>()

    private val eventChannel = Channel<AddSongEvent>()
    val events = eventChannel.receiveAsFlow()
    private val _state = MutableStateFlow(AddSongsState())
    val state = _state.onStart {
            if (!hasLoadedInitialData) {
                getTotalSongs()
               setupSearchPipeline()
                hasLoadedInitialData = true
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = AddSongsState()
        )


    private fun getTotalSongs (){
        viewModelScope.launch {
           trackDao.observeTracks().collect {
               totalTracks = it.map {
                   it.toAddSongResult()
               }
           }
        }
    }
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
                            isSelectAll = state.selectedIds.size == totalTracks.size


                        )
                    }
                }
        }
    }

    private fun insertSongs(playlistTitle : String){
        viewModelScope.launch {
            if (isInserting) {
                return@launch
            }

            isInserting = true
           val  playlistEntityfromDb= playlistDao.getplaylistByTitle(playlistTitle)
            val coverArt = playlistEntityfromDb.coverArt ?: getCoverArt(_state.value.selectedIds.first())

             val updatedPlaylist = if(playlistEntityfromDb.trackIds.isNullOrEmpty() ){
                 playlistEntityfromDb.copy(
                     trackIds = _state.value.selectedIds.toList(),
                     coverArt = coverArt,
                     id = playlistEntityfromDb.id
                 )
             }else{
                 val newTrackIds = playlistEntityfromDb.trackIds!!.toSet() +  _state.value.selectedIds

                         playlistEntityfromDb.copy(
                     id = playlistEntityfromDb.id,
                     trackIds = newTrackIds.toList(),

                 )
             }
            playlistDao.insert(updatedPlaylist)

            eventChannel.send(AddSongEvent.onInsertEvent)
            isInserting = false
        }

    }
    suspend  fun getCoverArt(trackId: Long):String?{
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
                            selectedIds = if(action.isSelectAll)totalTracks.map { it.id }.toSet() else emptySet(),
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
                        val newSelectedIds = if(action.isSelected) {
                            state.selectedIds.plus(action.id)
                        } else {
                            state.selectedIds.minus(action.id)
                        }
                       state.copy(
                           selectedIds =  newSelectedIds,
                           isSelectAll = newSelectedIds.size == totalTracks.size  ,
                           searchResults = state.searchResults.map {
                               it.copy(isSelected = it.id in newSelectedIds)
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
