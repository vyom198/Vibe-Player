package com.vs.vibeplayer.main.presentation.model

import androidx.core.net.toUri
import com.vs.vibeplayer.core.database.playlist.PlaylistEntity
import com.vs.vibeplayer.core.database.track.TrackEntity

fun PlaylistEntity.toPlaylistUI() =
    PlaylistUI(
        id = id,
        title = title,
        trackIds = trackIds,
        coverArt = coverArt

    )

fun TrackEntity.toAudioTrackUI() =
    AudioTrackUI(
        id = id,
        title = title,
        artist = artist,
        totalDurationMs = totalDuration,
        cover = cover ,
        path = path.toUri()
    )

fun AudioTrackUI.toTrackEntity() =
    TrackEntity(
        id = id,
        title = title,
        artist = artist,
        totalDuration = totalDurationMs,
        cover = cover,
        path = path.toString()


    )




