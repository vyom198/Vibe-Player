package com.vs.vibeplayer.main.presentation.playlistDetail

import com.vs.vibeplayer.main.presentation.model.AudioTrackUI

data class PlaylistDetailState(
   val songList : List<AudioTrackUI> = emptyList(),
    val playlistTitle : String = "",
    val cover : ByteArray? = null,

){
    val songSize: Int
        get() = if(songList.isEmpty()) 0 else songList.size
}