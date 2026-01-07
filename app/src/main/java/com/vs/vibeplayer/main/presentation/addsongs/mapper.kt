package com.vs.vibeplayer.main.presentation.addsongs

import com.vs.vibeplayer.core.database.track.TrackEntity

fun TrackEntity.toAddSongResult(): AddSongResult {
    return AddSongResult(
        id = this.id,
        title = this.title,
        artist = this.artist,
        cover = this.cover,
        totalDurationMs = this.totalDuration

    )
}