package com.vs.vibeplayer.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.vs.vibeplayer.core.database.playlist.LongListConverter
import com.vs.vibeplayer.core.database.playlist.PlaylistDao
import com.vs.vibeplayer.core.database.playlist.PlaylistEntity
import com.vs.vibeplayer.core.database.track.TrackDao
import com.vs.vibeplayer.core.database.track.TrackEntity

@Database(
    entities = [TrackEntity::class, PlaylistEntity :: class],
    version = 2,
)

@TypeConverters(
    LongListConverter::class
)
abstract class VibePlayerDataBase: RoomDatabase() {
    abstract val trackdao : TrackDao
    abstract  val playlistdao : PlaylistDao
}