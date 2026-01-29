package com.vs.vibeplayer.main.presentation.model

import android.graphics.Bitmap
import android.net.Uri

data class AudioTrackUI(
    val id: Long,
    val title: String,
    val artist: String,
    val cover: String? = null,
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


}
