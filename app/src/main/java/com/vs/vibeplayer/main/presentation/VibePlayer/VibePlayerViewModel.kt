package com.vs.vibeplayer.main.presentation.VibePlayer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vs.vibeplayer.main.domain.audio.AudioDataSource
import com.vs.vibeplayer.main.presentation.model.AudioTrackUI
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.delayFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

class VibePlayerViewModel(
    private val audioDataSource: AudioDataSource
) : ViewModel() {

    private val _state = MutableStateFlow(VibePlayerState(scanning = true))
    val state = _state.asStateFlow()

    init {
        Timber.d("VibePlayerViewModel - init() called")
        Timber.d("Initial state: scanning=${_state.value.scanning}, trackList size=${_state.value.trackList.size}")
        loadInitialAudioTracks()
    }

    private fun loadInitialAudioTracks() {
        Timber.d("loadInitialAudioTracks() called")

        viewModelScope.launch {
            Timber.d("Starting coroutine to load audio tracks")

            try {
                audioDataSource.getAudioTracks().
                onStart { delay(4000) }
                    .collect { audioTracks ->
                        Timber.d("AudioDataSource returned ${audioTracks.size} tracks")

                        // Log first few tracks for debugging
                        if (audioTracks.isNotEmpty()) {
                            Timber.d("First track: ${audioTracks.first().title} by ${audioTracks.first().artist} and ${audioTracks.first().cover}")
                        } else {
                            Timber.d("AudioDataSource returned empty list")
                        }

                        _state.update { currentState ->
                            Timber.d("Updating state: scanning=false, trackList=${audioTracks.size} items")
                            currentState.copy(
                                scanning = false,
                                trackList = audioTracks.map { track ->
                                    AudioTrackUI(
                                        id = track.id,
                                        cover = track.cover,
                                        title = track.title,
                                        artist = track.artist,
                                        path = track.path,
                                        totalDurationMs = track.totalDuration
                                    )
                                }
                            )
                        }

                        Timber.d("State updated successfully")
                        Timber.d("Final state: scanning=${_state.value.scanning}, trackList size=${_state.value.trackList.size}")
                    }
            } catch (e: Exception) {
                Timber.e(e, "ERROR in loadInitialAudioTracks: ${e.message}")
                _state.update {
                    Timber.e("Setting scanning=false due to error")
                    it.copy(scanning = false)
                }
            }
        }

        Timber.d("Coroutine launched for loading audio tracks")
    }

    fun onAction(action: VibePlayerAction) {
        Timber.d("onAction called: action=$action")

        when (action) {
            VibePlayerAction.onScanIcon -> {

            }

            VibePlayerAction.onFabClicked -> {

            }

            VibePlayerAction.onScanAgain -> {
                Timber.d("Scan again clicked - restarting scan")
                _state.update {
                    Timber.d("Setting scanning=true for scan again")
                    it.copy(scanning = true)
                }
                loadInitialAudioTracks()
            }

            is VibePlayerAction.onTrackClicked -> {

            }
        }
    }
}

