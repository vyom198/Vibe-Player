package com.vs.vibeplayer.main.domain.audio

import android.graphics.Bitmap
import android.net.Uri
import kotlin.time.Duration

data class AudioTrack(
    val id : Long ,
    val cover: ByteArray? = null ,
    val title : String ,
    val artist : String ,
    val path : Uri,
    val totalDuration: Long
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AudioTrack

        if (id != other.id) return false
        if (totalDuration != other.totalDuration) return false
        if (!cover.contentEquals(other.cover)) return false
        if (title != other.title) return false
        if (artist != other.artist) return false
        if (path != other.path) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + totalDuration.hashCode()
        result = 31 * result + (cover?.contentHashCode() ?: 0)
        result = 31 * result + title.hashCode()
        result = 31 * result + artist.hashCode()
        result = 31 * result + path.hashCode()
        return result
    }
}
