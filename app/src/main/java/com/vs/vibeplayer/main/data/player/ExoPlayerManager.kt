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

    private var currentIndex: Int = 0

    override fun initialize(
        clickedSong: TrackEntity?,
        playlist: List<TrackEntity>
    ) {
        release()
        this.originalPlaylist = playlist
        currentIndex = originalPlaylist.indexOfFirst { it.id == clickedSong?.id }
            .takeIf { it != -1 } ?: 0

        player = ExoPlayer.Builder(context)
            .setHandleAudioBecomingNoisy(true)
            .build()
            .apply {
                addListener(playerListener)
            }


        loadAndPlayCurrentSong()
    }

    override fun seekTo(position: Long) {

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
        _playerState.value = PlayerState()
        originalPlaylist = emptyList()

    }

    override fun next() {
        val player = this.player ?: return
        if (player.hasNextMediaItem()) {
            player.seekToNext()
        }else{
            player.seekTo(0, 0L)
        }
        updateCurrentSongState()
    }

    override fun previous() {
        val player = this.player ?: return
        if (player.hasPreviousMediaItem()) {
            player.seekToPrevious()
        } else {
            player.seekTo(0, 0L)
        }
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
        val player = this.player ?: return

        // Toggle shuffle mode
        player.shuffleModeEnabled = !player.shuffleModeEnabled

        // Update state
        _playerState.value = _playerState.value.copy(
            isShuffleEnabled = player.shuffleModeEnabled
        )

        Timber.d("Shuffle ${if (player.shuffleModeEnabled) "ENABLED" else "DISABLED"}")
    }

    override fun isPlayerEnabled(): Boolean {
        val player = this.player ?: return false
        return true
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
        val currentPlaylist = originalPlaylist
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
        // Update state
        _playerState.value = _playerState.value.copy(
            currentSong = currentSong,
            currentPosition = 0L,
            isShuffleEnabled = player.shuffleModeEnabled,
            currentPositionFraction = 0f,
            duration = 0L,  // Will be updated when STATE_READY
            canGoNext = player.hasNextMediaItem(),
            canGoPrevious = player.hasPreviousMediaItem(),
            isPlaying = false  // Will be set to true when STATE_READY
        )
    }
    private fun updateCurrentSongState() {
        val player = this.player ?: return
        val currentDuration = player.duration
        val currentPosition = player.currentPosition
        val currentMediaItemIndex = player.currentMediaItemIndex
        val currentSong = originalPlaylist.getOrNull(currentMediaItemIndex)
        if (currentSong != null) {
            _playerState.value = _playerState.value.copy(
                currentSong = currentSong,
                canGoNext = player.hasNextMediaItem(),
                currentPosition =currentPosition,
                currentPositionFraction = if (currentDuration > 0) {
                    currentPosition.toFloat() / currentDuration.toFloat()
                } else 0f,
                canGoPrevious = player.hasPreviousMediaItem()
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
                        currentPosition = 0L,
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
                            val currentIndex = player?.currentMediaItemIndex ?: 0
                            player?.seekTo(currentIndex,0)
                            player?.play()

                            startPositionUpdates()
                        }

                        RepeatType.REPEAT_ALL -> {

                        }

                        RepeatType.OFF -> {

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
                        currentIndex = newIndex
                        updateCurrentSongState()
                    }
                }
                Player.MEDIA_ITEM_TRANSITION_REASON_SEEK -> {
                    // User clicked next/previous or seeked to different song
                    val newIndex = player?.currentMediaItemIndex ?: currentIndex
                    if (newIndex != -1 && newIndex != currentIndex) {
                        currentIndex = newIndex
                        updateCurrentSongState()
                    }
                }
            }
        }
        override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
            // Update state when shuffle mode changes
            _playerState.value = _playerState.value.copy(
                isShuffleEnabled = shuffleModeEnabled
            )
            Timber.d("ExoPlayer shuffle mode changed: $shuffleModeEnabled")
        }
        override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
            val currentSong = originalPlaylist.getOrNull(currentIndex)
            if (currentSong != null) {
                _playerState.value = _playerState.value.copy(
                    currentSong = currentSong,

                )
            }
        }

    }
}