package com.vs.vibeplayer.main.presentation.playlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vs.vibeplayer.core.database.playlist.PlaylistDao
import com.vs.vibeplayer.core.database.playlist.PlaylistEntity
import com.vs.vibeplayer.core.database.track.TrackDao
import com.vs.vibeplayer.main.presentation.model.PlaylistUI
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PlaylistViewModel(
    private val playlistDao: PlaylistDao,
    private val trackDao: TrackDao
) : ViewModel() {

    private var hasLoadedInitialData = false

    private val _state = MutableStateFlow(PlaylistState())
    val state = _state
        .onStart {
            if (!hasLoadedInitialData) {
                 getPlaylists()
                getPlaylistCount()
                hasLoadedInitialData = true
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = PlaylistState()
        )

    fun getPlaylists(){
        viewModelScope.launch {
            playlistDao.getAllPlaylist().collect { playlists ->
                _state.update {
                    it.copy(
                        isShowing = false,
                        playlists = playlists.map { playlist->
                            PlaylistUI(
                                id = playlist.id,
                                title = playlist.title,
                                 trackIds = playlist.trackIds
                            )
                        }
                    )
                }

            }

        }
    }

    suspend  fun getCoverArt(trackId: Long): ByteArray?{
        val trackEntity = trackDao.getTrackById(trackId)
        return trackEntity?.cover

    }


    fun insertIfNotExists(title : String) {
        viewModelScope.launch {
            val playlistEntity = PlaylistEntity(
                title = title
               )
            val inserted = playlistDao.insertIfNotExists(playlistEntity)
            if(!inserted){
                _state.update {
                    it.copy(
                        isExists = true

                    )
                }
            }else{
                _state.update {
                    it.copy(
                        isExists = false

                    )
                }
            }

        }
    }

    fun getPlaylistCount () {
        viewModelScope.launch {
            val count = playlistDao.getPlaylistCount()
            _state.update {
                it.copy(
                    playlistCount = count
                )

            }
        }

    }

    fun onAction(action: PlaylistAction) {
        when (action) {
            PlaylistAction.AddIconClickedOrCreatePlaylist -> {
                _state.update {
                    it.copy(
                        isShowing = true
                    )
                }
            }
            PlaylistAction.onDismissSheet ->{

                _state.update {
                    it.copy(
                        isShowing = false
                    )
                }

            }

            is PlaylistAction.onCreateClick -> {
                insertIfNotExists(action.title)
                _state.update {
                    it.copy(
                        isShowing = false
                    )
                }

            }

            PlaylistAction.onSnackBarDismissed -> {
                _state.update {
                    it.copy(
                        isExists = false
                    )
                }

            }
        }
    }

}