package com.vs.vibeplayer.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.vs.vibeplayer.core.database.track.TrackDao
import com.vs.vibeplayer.core.database.track.TrackEntity

@Database(
    entities = [TrackEntity::class],
    version = 1,
)
abstract class TrackDataBase: RoomDatabase() {
    abstract val trackdao : TrackDao
}