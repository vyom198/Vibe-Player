package com.vs.vibeplayer.main.domain.player

import com.vs.vibeplayer.core.database.track.TrackEntity
import kotlinx.coroutines.flow.StateFlow


interface PlayerManager {
    val playerState: StateFlow<PlayerState>

    fun initialize(clickedSong: TrackEntity? = null ,
                   playlist: List<TrackEntity>,playlistId: Long? = null, isfavourite : Boolean = false)

    fun seekTo(position: Long)
    fun play()
    fun pause()
    fun stop()
    fun release()
    fun concatMediaSource(playlist: List<TrackEntity>)
    fun next()
    fun previous()


    fun onRepeatClick()

    fun shuffleSong()



    fun isPlayerEnabled(): Boolean

    fun isPlayingPlaylist(playlistId: Long): Boolean
    fun isFavouritePlaying(): Boolean
}

