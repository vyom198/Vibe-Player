package com.vs.vibeplayer.main.data.player

import android.content.Context
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.vs.vibeplayer.core.database.track.TrackDao
import com.vs.vibeplayer.core.database.track.TrackEntity
import com.vs.vibeplayer.main.domain.player.PlayerManager
import com.vs.vibeplayer.main.domain.player.PlayerState
import com.vs.vibeplayer.main.presentation.player.PlayerAction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.core.net.toUri
import androidx.media3.common.MediaMetadata
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import timber.log.Timber

class ExoPlayerManager(
    private val context : Context,
): PlayerManager {

    private var player: ExoPlayer? = null
    private val _playerState = MutableStateFlow(PlayerState())

    override val playerState: StateFlow<PlayerState> = _playerState.asStateFlow()
    private var positionUpdateJob: Job? = null
    private var playlist: List<TrackEntity> = emptyList()
    private var currentIndex: Int = 0

    override fun initialize(
        clickedSong: TrackEntity,
        playlist: List<TrackEntity>
    ) {
        release()
        this.playlist = playlist
        currentIndex = playlist.indexOfFirst { it.id == clickedSong.id }
            .takeIf { it != -1 } ?: 0

        // Create ExoPlayer
        player = ExoPlayer.Builder(context)
            .setHandleAudioBecomingNoisy(true)
            .build()
            .apply {
                addListener(playerListener)
            }

        // Load and play the clicked song
        loadAndPlayCurrentSong()
    }

    override fun play() {
       player?.play()
        _playerState.value = _playerState.value.copy(
            isPlaying = true
        )
    }

    override fun pause() {
         player?.pause()
        _playerState.value = _playerState.value.copy(
            isPlaying = false
        )
    }

    override fun stop() {
        player?.stop()
        _playerState.value = PlayerState()
    }

    override fun release() {
        player?.release()
        player = null
        playlist = emptyList()
        currentIndex = 0
        _playerState.value = PlayerState()
    }

    override fun next() {
        if (currentIndex < playlist.size - 1) {
            currentIndex++
            loadAndPlayCurrentSong()
        }
    }

    override fun previous() {
        if (currentIndex > 0) {
            currentIndex--
            loadAndPlayCurrentSong()
        }
    }

    override fun playPause() {
        TODO("Not yet implemented")
    }

    private fun startPositionUpdates() {
        stopPositionUpdates() // Clear previous

        positionUpdateJob = CoroutineScope(Dispatchers.Main).launch {
            while (isActive) {
                player?.let {
                    _playerState.value = _playerState.value.copy(
                        currentPosition = it.currentPosition
                    )
                }
                delay(1000) // Update every second
            }
        }
    }

    private fun stopPositionUpdates() {
        positionUpdateJob?.cancel()
        positionUpdateJob = null
    }
    private fun loadAndPlayCurrentSong() {
        val currentSong = playlist.getOrNull(currentIndex) ?: return
        val player = this.player ?: return

        // Create MediaItem from current song
        val mediaItem = MediaItem.Builder()
            .setUri(currentSong.path.toUri())
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(currentSong.title)
                    .setArtist(currentSong.artist)
                    .build()
            )
            .build()

        // Set to player and prepare
        player.setMediaItem(mediaItem)
        player.prepare()
        Timber.d("currentSong is $currentSong")
        // Update state
        _playerState.value = _playerState.value.copy(

            currentSong = currentSong,
            canGoNext = currentIndex < playlist.size - 1,
            canGoPrevious = currentIndex > 0
        )
    }
    private val playerListener = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            when (playbackState) {
                Player.STATE_READY -> {
                    // Start playing automatically when ready
                    player?.play()
                    _playerState.value = _playerState.value.copy(
                        duration = player?.duration ?: 0L,
                        isPlaying = true
                    )
                    startPositionUpdates()
                }

                Player.STATE_ENDED -> {
                    // Auto-play next when current ends
                    stopPositionUpdates()
                    if (currentIndex < playlist.size - 1) {
                        next()
                    } else {
                        _playerState.value = _playerState.value.copy(isPlaying = false)
                    }

                }

            }
        }
    }
}