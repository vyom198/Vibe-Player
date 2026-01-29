package com.vs.vibeplayer.main.domain.audio

import android.net.Uri

data class AudioTrack(
    val id : Long ,
    val cover: String? = null ,
    val title : String ,
    val artist : String ,
    val path : Uri,
    val totalDuration: Long
)
