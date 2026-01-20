package com.vs.vibeplayer.main.presentation.player

import androidx.core.net.toUri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.ui.compose.indicators.TimeText
import com.vs.vibeplayer.core.database.playlist.PlaylistDao
import com.vs.vibeplayer.core.database.track.TrackDao
import com.vs.vibeplayer.core.database.track.TrackEntity
import com.vs.vibeplayer.main.domain.player.PlayerManager
import com.vs.vibeplayer.main.presentation.model.AudioTrackUI
import com.vs.vibeplayer.main.presentation.model.PlaylistUI
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

class PlayerViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val playerManager: PlayerManager,
    private val trackDao: TrackDao ,
    private val playlistDao: PlaylistDao
) : ViewModel() {

    private var hasLoadedInitialData = false
    private val trackId = savedStateHandle.get<Long?>("trackId")
    private val _playlist = MutableStateFlow<List<TrackEntity>>(emptyList())
    private val _state = MutableStateFlow(PlayerUIState())
    val state = _state
        .onStart {
            if (!hasLoadedInitialData) {
                loadSongData(trackId ?: 0L)
                observePlayerState()
                loadPlaylist()
                loadFavouriteSongs()
                hasLoadedInitialData = true
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = PlayerUIState()
        )

    private fun loadFavouriteSongs(){


    }

    private fun loadPlaylist (){

        viewModelScope.launch {
            playlistDao.getAllPlaylist().collect { playlists ->


                _state.update {
                    it.copy(
                        playlists = playlists.map {
                            PlaylistUI(
                                id = it.id,
                                title = it.title,
                                coverArt = it.coverArt,
                                trackIds = it.trackIds
                            )
                        }
                    )
                }
            }
        }
    }
    private fun loadSongData(trackId: Long) {
        viewModelScope.launch {
            try {
                val clickedSong = trackDao.getTrackById(trackId)
                 trackDao.observeTracks().collect {
                    if (clickedSong != null) {
                        playerManager.initialize(clickedSong,  it)
                    }
                }

            } catch (e: Exception) {
                Timber.d(e.message)
            }
        }
    }
    private fun observePlayerState() {
        viewModelScope.launch {

            playerManager.playerState.collect { playerState ->
                _state.value = _state.value.copy(
                    isPlaying = playerState.isPlaying,
                    currentSong = playerState.currentSong,
                    currentPosition = playerState.currentPosition,
                    duration = playerState.duration,
                    canGoNext = playerState.canGoNext,
                    canGoPrevious = playerState.canGoPrevious,
                    repeatType = playerState.repeatType,
                    isShuffleEnabled = playerState.isShuffleEnabled,
                    currentPositionFraction = playerState.currentPositionFraction
                )
            }
        }

    }

    private fun toggleFavourite() {

    }

    fun onAction(action: PlayerAction) {
        when (action) {
            PlayerAction.BackPressed -> handleBackPressed()
            PlayerAction.Next -> playerManager.next()
            PlayerAction.PlayOrPause -> {
                if(_state.value.isPlaying)playerManager.pause()else playerManager.play()
            }
            PlayerAction.Previous -> playerManager.previous()
            PlayerAction.Stop -> playerManager.stop()
            PlayerAction.OnRepeatClick -> playerManager.onRepeatClick()
            PlayerAction.ShuffleClick -> playerManager.shuffleSong()
            is PlayerAction.OnSeek ->  playerManager.seekTo(action.position)
            PlayerAction.ToggleFavourite -> toggleFavourite()
            PlayerAction.onDismissBs -> {
                _state.value = _state.value.copy(
                    isBottomSheetShowing = false
                )
            }
            PlayerAction.onPlaylistIconClick -> {
                _state.value = _state.value.copy(
                    isBottomSheetShowing = true
                )
            }
            is PlayerAction.onPlaylistItemClicked ->{

            }

            PlayerAction.onCreatePlaylist -> {

            }
        }
    }
    private fun handleBackPressed() {
        //playerManager.stop()
    }

    override fun onCleared() {
        playerManager.release()
        super.onCleared()
    }
}