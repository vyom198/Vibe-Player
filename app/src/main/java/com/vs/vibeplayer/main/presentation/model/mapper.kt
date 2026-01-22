package com.vs.vibeplayer.main.presentation.model

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
        cover = cover
    )
