package com.vs.vibeplayer.main.data.player

import android.content.Context
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.vs.vibeplayer.core.database.track.TrackEntity
import com.vs.vibeplayer.main.domain.player.PlayerManager
import com.vs.vibeplayer.main.domain.player.PlayerState
import com.vs.vibeplayer.main.presentation.player.RepeatType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
    private var originalPlaylist: List<TrackEntity> = emptyList()
    private var shuffledPlaylist: List<TrackEntity> = emptyList()
    private var isShuffleEnabled = false

    private var currentIndex: Int = 0

    override fun initialize(
        clickedSong: TrackEntity,
        playlist: List<TrackEntity>
    ) {
        release()
        this.originalPlaylist = playlist
        this.isShuffleEnabled = false
       // this.shuffledPlaylist = playlist.shuffled()
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

    override fun seekTo(position: Long) {
        // CORRECTED: Seek within current song, not to index
        player?.seekTo(position)

        val currentDuration = player?.duration ?: 0L
        _playerState.value = _playerState.value.copy(
            currentPosition = position,
            currentPositionFraction = if (currentDuration > 0) {
                position.toFloat() / currentDuration.toFloat()
            } else 0f
        )
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
        originalPlaylist = emptyList()
        currentIndex = 0
        isShuffleEnabled = false
        _playerState.value = PlayerState()
        originalPlaylist = emptyList()
        shuffledPlaylist = emptyList()
    }

    override fun next() {
        if (currentIndex < getCurrentPlaylist().size - 1) {
            currentIndex++

        }else{
            currentIndex = 0
        }
        player?.seekTo(currentIndex, 0L)
        updateCurrentSongState()
    }

    override fun previous() {
        if (currentIndex > 0) {
            currentIndex--
        }else{
            currentIndex = getCurrentPlaylist().size -1
        }
        player?.seekTo(currentIndex, 0L)
        updateCurrentSongState()
    }



    override fun onRepeatClick() {
        val currentType = _playerState.value.repeatType
        val nextType = when (currentType) {
            RepeatType.OFF -> RepeatType.REPEAT_ALL
            RepeatType.REPEAT_ALL -> RepeatType.REPEAT_ONE
            RepeatType.REPEAT_ONE -> RepeatType.OFF
        }
        _playerState.value = _playerState.value.copy(
            repeatType = nextType
        )
        when(nextType) {
            RepeatType.OFF -> {
                player?.repeatMode = Player.REPEAT_MODE_OFF
            }
            RepeatType.REPEAT_ONE -> {
                player?.repeatMode = Player.REPEAT_MODE_ONE
            }
            RepeatType.REPEAT_ALL -> {
                player?.repeatMode = Player.REPEAT_MODE_ALL
            }
        }

    }

    override fun shuffleSong() {
        isShuffleEnabled = !isShuffleEnabled
        val currentSong = _playerState.value.currentSong

        if (isShuffleEnabled) {
            shuffledPlaylist = originalPlaylist.shuffled()
            if (currentSong != null) {
                val newIndex = shuffledPlaylist.indexOfFirst { it.id == currentSong.id }
                if (newIndex != -1) {
                    currentIndex = newIndex
                } else {
                    currentIndex = 0
                }
            }
        } else {
            if (currentSong != null) {
                val originalIndex = originalPlaylist.indexOfFirst { it.id == currentSong.id }
                if (originalIndex != -1) {
                    currentIndex = originalIndex
                }
            }
        }

        // Update UI state
        _playerState.value = _playerState.value.copy(
            isShuffleEnabled = isShuffleEnabled
        )
        Timber.d("Shuffle ${if (isShuffleEnabled) "enabled" else "disabled"}")
    }
    private fun getCurrentPlaylist(): List<TrackEntity> {
        return if (isShuffleEnabled) shuffledPlaylist else originalPlaylist
    }
    private fun startPositionUpdates() {
        stopPositionUpdates() // Clear previous

        positionUpdateJob = CoroutineScope(Dispatchers.Main).launch {
            while (isActive) {
                player?.let {
                    val currentPos = it.currentPosition
                    val currentDuration = it.duration
                    val currentMediaItemIndex = it.currentMediaItemIndex

                    // Update currentIndex if it doesn't match ExoPlayer's index
                    if (currentMediaItemIndex != -1 && currentMediaItemIndex != currentIndex) {
                        currentIndex = currentMediaItemIndex
                        updateCurrentSongState()
                    }

                    _playerState.value = _playerState.value.copy(
                        currentPosition = currentPos,
                        currentPositionFraction = if (currentDuration > 0) {
                            currentPos.toFloat() / currentDuration.toFloat()
                        } else 0f,
                        duration = currentDuration
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
        val currentPlaylist = getCurrentPlaylist()
        val currentSong = currentPlaylist.getOrNull(currentIndex) ?: return
        val player = this.player ?: return

        // Create MediaItem from current song
        val mediaItems = currentPlaylist.map { song ->
            MediaItem.Builder()
                .setUri(song.path.toUri())
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setTitle(song.title)
                        .setArtist(song.artist)
                        .build()
                )
                .build()
        }

        // Set to player and prepare
        player.setMediaItems(mediaItems)
        player.prepare()
        player.seekTo(currentIndex,0)
        Timber.d("currentSong is $currentSong")
        // Update state
        _playerState.value = _playerState.value.copy(
            currentSong = currentSong,
            currentPosition = 0L,
            currentPositionFraction = 0f,
            duration = 0L,  // Will be updated when STATE_READY
            canGoNext = currentIndex < getCurrentPlaylist().size - 1,
            canGoPrevious = currentIndex > 0,
            isPlaying = false  // Will be set to true when STATE_READY
        )
    }
    private fun updateCurrentSongState() {
        val currentPlaylist = getCurrentPlaylist()
        val currentSong = currentPlaylist.getOrNull(currentIndex)
        if (currentSong != null) {
            _playerState.value = _playerState.value.copy(
                currentSong = currentSong,
                currentPosition = 0L,
                currentPositionFraction = 0f,
                canGoNext = currentIndex < currentPlaylist.size - 1,
                canGoPrevious = currentIndex > 0
            )
        }
    }
    private val playerListener = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            when (playbackState) {
                Player.STATE_READY -> {
                    // Start playing automatically when ready
                    player?.play()
                    _playerState.value = _playerState.value.copy(
                        currentPositionFraction = 0f,
                        duration = player?.duration ?: 0L,
                        isPlaying = true
                    )
                    startPositionUpdates()
                }

                Player.STATE_ENDED -> {
                    // Auto-play next when current ends
                    stopPositionUpdates()
                    when (_playerState.value.repeatType) {
                        RepeatType.REPEAT_ONE -> {
                            player?.seekTo(currentIndex,0)
                            player?.play()

                            startPositionUpdates()
                        }

                        RepeatType.REPEAT_ALL -> {

                        }

                        RepeatType.OFF -> {
                            if (currentIndex < getCurrentPlaylist().size - 1) {
                                next()
                            } else {
                                _playerState.value = _playerState.value.copy(isPlaying = false)
                            }
                        }
                    }

                }

            }
        }

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            when (reason) {
                Player.MEDIA_ITEM_TRANSITION_REASON_AUTO -> {
                    // ExoPlayer auto-advanced to next song (e.g., in REPEAT_ALL mode)
                    val newIndex = player?.currentMediaItemIndex ?: currentIndex
                    if (newIndex != -1 && newIndex != currentIndex) {
                        Timber.d("Auto-advanced to index: $newIndex")
                        currentIndex = newIndex
                        updateCurrentSongState()
                    }
                }
                Player.MEDIA_ITEM_TRANSITION_REASON_SEEK -> {
                    // User clicked next/previous or seeked to different song
                    val newIndex = player?.currentMediaItemIndex ?: currentIndex
                    if (newIndex != -1 && newIndex != currentIndex) {
                        Timber.d("Seeked to index: $newIndex")
                        currentIndex = newIndex
                        updateCurrentSongState()
                    }
                }
            }
        }
        override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
            // Update song info if needed
            val currentSong = getCurrentPlaylist().getOrNull(currentIndex)
            if (currentSong != null) {
                _playerState.value = _playerState.value.copy(
                    currentSong = currentSong
                )
            }
        }

    }
}