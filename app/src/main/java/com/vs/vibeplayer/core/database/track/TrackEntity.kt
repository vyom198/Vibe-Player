package com.vs.vibeplayer.core.database.track

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trackentity")
data class TrackEntity(
    @PrimaryKey(autoGenerate = true)
    val id : Long ,
    val cover: String? = null ,
    val title : String ,
    val artist : String ,
    val path : String,
    val totalDuration: Long
)