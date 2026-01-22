package com.vs.vibeplayer.core.database.playlist

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(playlistEntity: PlaylistEntity)

    @Query("SELECT * FROM playlistentity")
    fun getAllPlaylist(): Flow<List<PlaylistEntity>>

    @Transaction
    suspend fun insertIfNotExists(playlistEntity: PlaylistEntity): Boolean {
        return if (!playlistExists(playlistEntity.title)) {
            insert(playlistEntity)
            true
        } else {
            false
        }
    }

    @Query("SELECT EXISTS(SELECT 1 FROM playlistentity WHERE title = :title)")
    suspend fun playlistExists(title: String): Boolean



    @Query("SELECT * FROM playlistentity WHERE title = :title")
    suspend fun getplaylistByTitle(title: String) : PlaylistEntity

    @Query("SELECT * FROM playlistentity WHERE id = :id")
    suspend fun getplaylistById ( id : Long ) : PlaylistEntity


    @Query("DELETE FROM playlistentity WHERE id = :id")
    suspend fun deletePlaylistById (id : Long)

    @Query("SELECT * FROM playlistentity WHERE id = :id")
    fun getPlaylistByIdFlow(id: Long): Flow<PlaylistEntity>

}