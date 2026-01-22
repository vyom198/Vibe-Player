package com.vs.vibeplayer.main.presentation.playlistDetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vs.vibeplayer.core.database.playlist.PlaylistDao
import com.vs.vibeplayer.core.database.track.TrackDao
import com.vs.vibeplayer.main.domain.player.PlayerManager
import com.vs.vibeplayer.main.presentation.model.toAudioTrackUI
import com.vs.vibeplayer.main.presentation.model.toTrackEntity
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PlaylistDetailViewModel(
    private val playerManager: PlayerManager,
    private val savedStateHandle: SavedStateHandle,
    private val trackDao: TrackDao,
    private val playlistDao: PlaylistDao

) : ViewModel() {
    private val eventChannel = Channel<PlaylistDetailEvent>()
    val events = eventChannel.receiveAsFlow()
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
           playlistDao.getPlaylistByIdFlow(playlistId!!).collect { entity ->
             val songs =  entity.trackIds?.map {id->
                 async{
                     trackDao.getTrackById(id)!!.toAudioTrackUI()
                 }
             }?.awaitAll()?: emptyList()
               _state.update {
                   it.copy(
                       playlistTitle = entity.title,
                       cover = entity.coverArt,
                       songList = songs
                   )
               }
           }
       }
   }

    fun onAction(action: PlaylistDetailAction) {
        when (action) {
            PlaylistDetailAction.onPlayClick -> {
                viewModelScope.launch {
                    val currentPlayingSong = playerManager.playerState.value.currentSong
                    val currentPosition = playerManager.playerState.value.currentPosition
                        val songList = _state.value.songList.map {
                            it.toTrackEntity()
                        }
                    playerManager.release()
                    playerManager.initialize(playlist = songList , clickedSong = currentPlayingSong)
                    playerManager.seekTo(currentPosition)


                    eventChannel.send(PlaylistDetailEvent.onNavigateChannel)
                }

            }
            PlaylistDetailAction.onShuffleClick -> {
                viewModelScope.launch {
                        val currentPlayingSong = playerManager.playerState.value.currentSong
                        val currentPosition = playerManager.playerState.value.currentPosition
                        val songList = _state.value.songList.map{
                            it.toTrackEntity()
                        }
                        playerManager.release()
                        playerManager.initialize(playlist = songList , clickedSong = currentPlayingSong)
                        playerManager.seekTo(currentPosition)
                        playerManager.shuffleSong()

                    eventChannel.send(PlaylistDetailEvent.onNavigateChannel)

                }
            }
        }
    }

}