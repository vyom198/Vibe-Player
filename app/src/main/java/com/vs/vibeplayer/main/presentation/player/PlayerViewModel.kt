package com.vs.vibeplayer.main.presentation.player

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.ui.compose.indicators.TimeText
import com.vs.vibeplayer.core.database.track.TrackDao
import com.vs.vibeplayer.core.database.track.TrackEntity
import com.vs.vibeplayer.main.domain.player.PlayerManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber

class PlayerViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val playerManager: PlayerManager,
    private val trackDao: TrackDao
) : ViewModel() {

    private var hasLoadedInitialData = false
    private val trackId = savedStateHandle.get<Long>("trackId")
    private val _playlist = MutableStateFlow<List<TrackEntity>>(emptyList())
    private val _state = MutableStateFlow(PlayerUIState())
    val state = _state
        .onStart {
            if (!hasLoadedInitialData) {
                loadInitialData(trackId ?: 0L)
                observePlayerState()
                hasLoadedInitialData = true
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = PlayerUIState()
        )

    private fun loadInitialData(trackId: Long) {
        viewModelScope.launch {
            try {
                val clickedSong = trackDao.getTrackById(trackId)
                Timber.d("clickedSong is $clickedSong")
                if (clickedSong != null) {
                    trackDao.observeTracks().collect { playlist ->
                        _playlist.value = playlist

                        if (playlist.isNotEmpty()) {
                            // 3. Initialize player
                            playerManager.initialize(clickedSong, playlist)
                            observePlayerState()

                        }
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
        }
    }
    private fun handleBackPressed() {
        playerManager.stop()
    }

    override fun onCleared() {
        playerManager.release()
        super.onCleared()
    }
}