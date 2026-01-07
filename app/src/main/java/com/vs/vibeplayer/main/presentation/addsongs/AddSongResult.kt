package com.vs.vibeplayer.main.presentation.addsongs

data class AddSongResult(
    val id: Long,
    val title: String,
    val artist: String,
    val cover: ByteArray? = null,
    val totalDurationMs: Long
){
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
