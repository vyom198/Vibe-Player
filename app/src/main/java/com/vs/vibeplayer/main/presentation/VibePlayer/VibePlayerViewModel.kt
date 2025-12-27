package com.vs.vibeplayer.main.presentation.VibePlayer

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vs.vibeplayer.core.database.track.TrackDao
import com.vs.vibeplayer.main.domain.audio.AudioDataSource
import com.vs.vibeplayer.main.presentation.model.AudioTrackUI
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import androidx.core.net.toUri
import com.vs.vibeplayer.core.database.track.TrackEntity
import com.vs.vibeplayer.main.data.audio.ContentUriChecker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext


class VibePlayerViewModel(
    private val audioDataSource: AudioDataSource,
    private val  trackDao: TrackDao,
    private val contentUriChecker: ContentUriChecker
) : ViewModel() {
    private val eventChannel = Channel<VibePlayerEvent>()
    val events = eventChannel.receiveAsFlow()
    private val _state = MutableStateFlow(VibePlayerState(scanning = true))
    val state = _state.asStateFlow()

    private var duration by mutableIntStateOf(0)
    private var size by mutableIntStateOf(0)

    init {
        viewModelScope.launch {
            Timber.d("trackDao.getTrackCount() = ${trackDao.getTrackCount()}")
            if (trackDao.getTrackCount() == 0) {
                loadInitialAudioTracksWithoutFilter()
            }
        }
        loadInitialAudioTracks()
    }

    private fun loadInitialAudioTracks() {
        viewModelScope.launch {
            Timber.d("loadInitialAudioTracks() called")
            try {
                trackDao.observeTracks().collect { audioTracks ->
                    Timber.d("AudioDataSource returned ${audioTracks.size} tracks")
                    val existingTracks = filterExistingTracks(audioTracks)


                    _state.update { currentState ->
                        currentState.copy(
                            scanning = false,
                            loadingInReScan = false,
                            trackList = existingTracks.map { track ->
                                AudioTrackUI(
                                    id = track.id,
                                    cover = track.cover,
                                    title = track.title,
                                    artist = track.artist,
                                    path = track.path.toUri(),
                                    totalDurationMs = track.totalDuration
                                )
                            }
                        )
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "ERROR in loadInitialAudioTracks: ${e.message}")
                _state.update {
                    Timber.e("Setting scanning=false due to error")
                    it.copy(scanning = false)
                }
            }
        }
    }
    private suspend fun filterExistingTracks(tracks: List<TrackEntity>): List<TrackEntity> {
        return withContext(Dispatchers.IO) {
            val existingTracks = mutableListOf<TrackEntity>()
            val tracksToDelete = mutableListOf<Long>()

            tracks.forEach { track ->
                val exists = contentUriChecker.checkContentUriExists(track.path)
                if (exists) {
                    existingTracks.add(track)
                } else {
                    tracksToDelete.add(track.id)
                }
            }

            // Delete non-existent tracks from database
            if (tracksToDelete.isNotEmpty()) {
                trackDao.deleteTracksfromIds(ids = tracksToDelete)
                Timber.d("Deleted ${tracksToDelete.size} tracks that no longer exist")
            }

            existingTracks
        }
    }
    private suspend fun loadAudioTracksWithFilter(duration: Int? = null, size: Int? = null) {
        try {
            val scanCompleted = audioDataSource.scanAndSave(duration, size)
            Timber.d("Scan completed: $scanCompleted")
            if (scanCompleted) {

                _state.update {
                    it.copy(

                        loadingInReScan = false
                    )
                }
                eventChannel.send(
                    VibePlayerEvent.ScanCompleted(
                        scanCompleted,
                        trackDao.getTrackCount()
                    )
                )
                Timber.d("State updated successfully")
                Timber.d("Final state: scanning=${_state.value.scanning}, trackList size=${trackDao.getTrackCount()}")
            }
        } catch (e: Exception) {
            _state.update {
                it.copy(

                    loadingInReScan = false
                )
            }
        }


    }

    fun onAction(action: VibePlayerAction) {
        Timber.d("onAction called: action=$action")

        when (action) {
            VibePlayerAction.onScanButton -> onScanButton()

            VibePlayerAction.onFabClicked -> {

            }

            VibePlayerAction.onScanAgain -> {
                viewModelScope.launch {
                    _state.update {
                        it.copy(scanning = true)
                    }
                    loadInitialAudioTracksWithoutFilter()
                }

            }

            is VibePlayerAction.onTrackClicked -> {

            }

            is VibePlayerAction.onDurationSelect -> {
                _state.update {
                    it.copy(
                        durationValue = action.seconds
                    )
                }
                duration = action.seconds

            }

            is VibePlayerAction.onSizeSelect -> {
                _state.update {
                    it.copy(
                        sizeValue = action.kb
                    )
                }
                size = action.kb
            }
        }
    }

    private suspend fun loadInitialAudioTracksWithoutFilter() {
        _state.update {
            it.copy(scanning = true)
        }
        val ScanningComplete = audioDataSource.scanAndSave(duration, size)
        if (ScanningComplete) {
            _state.update {
                it.copy(
                    scanning = false,

                    )

            }

        }
    }

    private fun onScanButton() {
        viewModelScope.launch {

            _state.update {
                it.copy(loadingInReScan = true)
            }
            delay(2000)
            loadAudioTracksWithFilter(duration, size)


        }
    }

      override  fun onCleared() {
            super.onCleared()
            eventChannel.close()
        }


}




