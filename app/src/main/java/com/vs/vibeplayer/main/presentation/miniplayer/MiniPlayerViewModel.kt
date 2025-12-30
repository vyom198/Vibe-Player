package com.vs.vibeplayer.main.presentation.miniplayer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vs.vibeplayer.main.domain.player.PlayerManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MiniPlayerViewModel(
    private  val playerManager: PlayerManager
) : ViewModel() {

    private var hasLoadedInitialData = false

    private val _state = MutableStateFlow(MiniPlayerState())
    val state = _state
        .onStart {
            if (!hasLoadedInitialData) {
                observeMiniPlayerState()
                hasLoadedInitialData = true
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = MiniPlayerState()
        )
    private fun observeMiniPlayerState() {
        viewModelScope.launch {

            playerManager.playerState.collect { playerState ->
                _state.value = _state.value.copy(
                    isPlaying = playerState.isPlaying,
                    currentSong = playerState.currentSong,
                    currentPosition = playerState.currentPosition,
                    duration = playerState.duration,
                    canGoNext = playerState.canGoNext,

                    )
            }
        }
    }
    fun onAction(action: MiniPlayerAction) {
        when (action) {
            MiniPlayerAction.Next -> playerManager.next()
            MiniPlayerAction.PlayOrPause -> {
                if(_state.value.isPlaying)playerManager.pause()else playerManager.play()
            }
            MiniPlayerAction.Stop -> playerManager.stop()
        }
    }

}