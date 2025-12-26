package com.vs.vibeplayer.main.presentation.model

import android.graphics.Bitmap
import android.net.Uri

data class AudioTrackUI(
    val id: Long,
    val title: String,
    val artist: String,
    val cover: ByteArray? = null,
    val path: Uri? = null,
    val totalDurationMs: Long
) {
    val totalDurationString: String
        get() = formatDuration(totalDurationMs)

    companion object {
        private fun formatDuration(milliseconds: Long): String {
            val totalSeconds = milliseconds / 1000
            val minutes = totalSeconds / 60
            val seconds = totalSeconds % 60
            return String.format("%02d:%02d", minutes, seconds)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AudioTrackUI

        if (id != other.id) return false
        if (totalDurationMs != other.totalDurationMs) return false
        if (title != other.title) return false
        if (artist != other.artist) return false
        if (!cover.contentEquals(other.cover)) return false
        if (path != other.path) return false
        if (totalDurationString != other.totalDurationString) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + totalDurationMs.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + artist.hashCode()
        result = 31 * result + (cover?.contentHashCode() ?: 0)
        result = 31 * result + (path?.hashCode() ?: 0)
        result = 31 * result + totalDurationString.hashCode()
        return result
    }
}
