package com.vs.vibeplayer.main.presentation.model

import com.vs.vibeplayer.core.database.playlist.PlaylistEntity

fun PlaylistEntity.toPlaylistUI() =
    PlaylistUI(
        id = id,
        title = title,
        trackIds = trackIds,
        coverArt = coverArt

    )
