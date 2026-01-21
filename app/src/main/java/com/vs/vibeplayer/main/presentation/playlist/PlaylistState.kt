package com.vs.vibeplayer.main.presentation.playlist

import com.vs.vibeplayer.main.presentation.model.PlaylistUI

data class PlaylistState(
 val isShowing : Boolean = false,
 val isExists : Boolean = false,
 val playlists : List<PlaylistUI> = emptyList() ,
 val title : String = "",
 val favouriteSongs  : Set<Long> = emptySet(),
 val isFavSheetVisible : Boolean = false ,
 val isPlayListSheetVisible : Boolean = false,
 val currentPlaylist : PlaylistUI? = null,
 val isRenamingPlaylist : Boolean = false,
 val isDeletingPlaylist : Boolean = false,
 val prefilledTitle : String =  ""

){
 val favouriteSongssize : Int
  get() = if(favouriteSongs.isNotEmpty()) favouriteSongs.size else 0



}