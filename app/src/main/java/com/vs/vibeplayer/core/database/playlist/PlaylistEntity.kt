package com.vs.vibeplayer.core.database.playlist

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlistentity")
data class PlaylistEntity (
    @PrimaryKey(autoGenerate = true)
    val id : Long = 0,
    val title : String,
    val trackIds : List<Long>? = null,
    val coverArt : ByteArray? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PlaylistEntity

        if (id != other.id) return false
        if (title != other.title) return false
        if (trackIds != other.trackIds) return false
        if (!coverArt.contentEquals(other.coverArt)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + (trackIds?.hashCode() ?: 0)
        result = 31 * result + (coverArt?.contentHashCode() ?: 0)
        return result
    }
}


