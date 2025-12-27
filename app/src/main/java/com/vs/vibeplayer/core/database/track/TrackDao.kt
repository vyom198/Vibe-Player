package com.vs.vibeplayer.core.database.track

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackDao {

    @Query("SELECT * FROM trackentity ")
    fun observeTracks(): Flow<List<TrackEntity>>

    @Query("DELETE FROM trackentity")
    suspend fun deleteAllTracks()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllTracks(tracks : List<TrackEntity>)

    @Query("DELETE FROM trackentity WHERE id IN (:ids)")
    suspend fun deleteTracksfromIds(ids: List<Long>)


    @Query("SELECT * FROM trackentity WHERE id = :trackId")
    suspend fun getTrackById(trackId: Long): TrackEntity?

    @Query("SELECT COUNT(*) FROM trackentity")
    suspend fun getTrackCount(): Int
}