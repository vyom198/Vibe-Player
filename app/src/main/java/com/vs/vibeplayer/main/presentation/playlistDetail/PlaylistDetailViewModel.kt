package com.vs.vibeplayer.main.presentation.playlistDetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vs.vibeplayer.core.database.playlist.PlaylistDao
import com.vs.vibeplayer.core.database.track.TrackDao
import com.vs.vibeplayer.main.domain.player.PlayerManager
import com.vs.vibeplayer.main.presentation.model.toAudioTrackUI
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PlaylistDetailViewModel(
    private val playerManager: PlayerManager,
    private val savedStateHandle: SavedStateHandle,
    private val trackDao: TrackDao,
    private val playlistDao: PlaylistDao

) : ViewModel() {

    private var hasLoadedInitialData = false
    private val playlistId = savedStateHandle.get<Long>("playlistId")
    private val _state = MutableStateFlow(PlaylistDetailState())
    val state = _state
        .onStart {
            if (!hasLoadedInitialData) {
                loadInitialData()
                hasLoadedInitialData = true
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = PlaylistDetailState()
        )

   private fun loadInitialData() {
       viewModelScope.launch {
           val playlist = playlistDao.getplaylistById(playlistId!!)
           val songs = playlist.trackIds?.map {
               async {
                   trackDao.getTrackById(it)?.toAudioTrackUI()

               }
           }?.awaitAll()?.filterNotNull()?: emptyList()
           _state.update {
               it.copy(
                   playlistTitle = playlist.title,
                   cover = playlist.coverArt,
                   songList = songs
               )
           }

       }
   }

    fun onAction(action: PlaylistDetailAction) {
        when (action) {
            PlaylistDetailAction.onPlayClick -> TODO()
            PlaylistDetailAction.onShuffleClick -> TODO()
        }
    }

}