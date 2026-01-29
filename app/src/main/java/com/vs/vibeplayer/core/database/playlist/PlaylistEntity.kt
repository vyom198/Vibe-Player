package com.vs.vibeplayer.core.database.playlist

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlistentity")
data class PlaylistEntity (
    @PrimaryKey(autoGenerate = true)
    val id : Long = 0,
    val title : String,
    val trackIds : List<Long>? = null,
    val coverArt : String? = null
)


