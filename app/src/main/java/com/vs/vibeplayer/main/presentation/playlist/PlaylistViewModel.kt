package com.vs.vibeplayer.main.presentation.playlist

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vs.vibeplayer.core.database.playlist.PlaylistDao
import com.vs.vibeplayer.core.database.playlist.PlaylistEntity
import com.vs.vibeplayer.core.database.track.TrackDao
import com.vs.vibeplayer.main.domain.favourite.FavouritePrefs
import com.vs.vibeplayer.main.domain.player.PlayerManager
import com.vs.vibeplayer.main.presentation.model.PlaylistUI
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PlaylistViewModel(
    private val playlistDao: PlaylistDao,
    private val favouritePrefs: FavouritePrefs,
    private val context : Context,
    private val trackDao: TrackDao,
    private val playerManager: PlayerManager

) : ViewModel() {
    private val eventChannel = Channel<PlaylistEvent>()
    val events = eventChannel.receiveAsFlow()
    private var hasLoadedInitialData = false


    private val _state = MutableStateFlow(PlaylistState())
    val state = _state
        .onStart {
            if (!hasLoadedInitialData) {
                getPlaylists()
                loadFavouriteSongs()
                hasLoadedInitialData = true
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = PlaylistState()
        )

    private fun loadFavouriteSongs (){
        viewModelScope.launch {
            favouritePrefs.getfavouriteList().collect { favouriteSongs ->
                _state.update {
                    it.copy(
                        favouriteSongs = favouriteSongs
                    )

                }


            }
        }
    }

    private fun playingRegularPlaylist(){
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isPlayListSheetVisible = false
                )
            }
            val trackIds = _state.value.currentPlaylist?.trackIds
            if(trackIds.isNullOrEmpty()){
                eventChannel.send(PlaylistEvent.onRegularPlaylistPlay(isEmpty = true))
                return@launch
            }
            val playlist = trackIds.map {
                async{
                    trackDao.getTrackById(it)

                }
            }.awaitAll().filterNotNull()
            playerManager.initialize(
                playlist = playlist
            )

            eventChannel.send(PlaylistEvent.onRegularPlaylistPlay(isEmpty = false))


        }
    }
    private fun playingFavPlaylist(){
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isFavSheetVisible = false
                )
            }
            val trackIds = _state.value.favouriteSongs
            if(trackIds.isEmpty()) return@launch
            val playlist = trackIds.map {
                async{
                    trackDao.getTrackById(it)

                }
            }.awaitAll().filterNotNull()

            playerManager.initialize(
                playlist = playlist
            )

            eventChannel.send(PlaylistEvent.onfavPlayClicked)

        }
    }
    private fun renameTitle (title: String){
        viewModelScope.launch {
            val playlist = playlistDao.getplaylistById(_state.value.currentPlaylist!!.id)
            val renamedPlaylist = playlist.copy(
                title = title
            )
            playlistDao.insertIfNotExists(renamedPlaylist)
            _state.update {
                it.copy(
                    isRenamingPlaylist = false,
                    currentPlaylist = null
                )
            }

        }
    }

    private fun getPlaylists(){
        viewModelScope.launch {
            playlistDao.getAllPlaylist().collect { playlists ->
                _state.update {
                    it.copy(
                        isShowing = false,
                        playlists = playlists.map { playlist->
                            PlaylistUI(
                                id = playlist.id,
                                title = playlist.title,
                                 trackIds = playlist.trackIds ,
                                coverArt = playlist.coverArt
                            )
                        }
                    )
                }

            }

        }
    }




   private fun insertIfNotExists(title : String) {
        viewModelScope.launch {
            val playlistEntity = PlaylistEntity(
                title = title
               )
            val inserted = playlistDao.insertIfNotExists(playlistEntity)
            if(!inserted){
                _state.update {
                    it.copy(
                        isExists = true,
                        isShowing = false


                    )
                }
                eventChannel.send(PlaylistEvent.OnCreateChannel(true))
            }else{
                _state.update {
                    it.copy(
                        isExists = false,
                        isShowing = false
                    )
                }

                eventChannel.send(PlaylistEvent.OnCreateChannel(isExists = false,
                    title = title
                ))
            }

        }
    }



    fun onAction(action: PlaylistAction) {
        when (action) {
            PlaylistAction.AddIconClickedOrCreatePlaylist -> {
                viewModelScope.launch {
                    _state.update {
                        it.copy(
                            isShowing = true
                        )
                    }
                }

            }

            PlaylistAction.onDismissSheet -> {
             viewModelScope.launch {
                 _state.update {
                     it.copy(
                         isShowing = false,
                         title = ""

                     )
                 }
             }


            }

            is PlaylistAction.onCreateClick -> {

                insertIfNotExists(action.title)


            }

            PlaylistAction.onSnackBarDismissed -> {
                viewModelScope.launch {
                    _state.update {
                        it.copy(
                            isExists = false
                        )
                    }
                }


            }

            is PlaylistAction.onTextChange -> {
                viewModelScope.launch {
                    _state.update {
                        it.copy(
                            title = action.title
                        )
                    }
                }

            }

            PlaylistAction.OnFavMenuIconClick -> {
                viewModelScope.launch {
                    _state.update {
                        it.copy(
                            isFavSheetVisible = true
                        )
                    }
                }


            }

            PlaylistAction.onDismissFavSheet -> {
                viewModelScope.launch {
                    _state.update {
                        it.copy(
                            isFavSheetVisible = false
                        )
                    }
                }

            }

            PlaylistAction.onDismissPlayListSheet -> {
                viewModelScope.launch {
                    _state.update {
                        it.copy(
                            isPlayListSheetVisible = false
                        )
                    }
                }


            }

            is PlaylistAction.onPlaylistMenuIconClick -> {
                viewModelScope.launch {
                    _state.update {
                        it.copy(
                            isPlayListSheetVisible = true,
                            currentPlaylist = action.playlistUI
                        )
                    }
                }

            }

            is PlaylistAction.onDeleteButtonClick -> {
                viewModelScope.launch {
                    _state.update {
                        it.copy(
                            isPlayListSheetVisible = false,
                            isDeletingPlaylist = true
                        )
                    }

                }
            }

            is PlaylistAction.onRenameButtonClick -> {
                viewModelScope.launch {
                    _state.update {
                        it.copy(
                            isPlayListSheetVisible = false,
                            prefilledTitle = _state.value.currentPlaylist!!.title,
                            isRenamingPlaylist = true,
                        )
                    }

                }

            }

            is PlaylistAction.onPrefilledTextChange -> {
                viewModelScope.launch {
                    _state.update {
                        it.copy(
                            prefilledTitle = action.prefilledText

                        )
                    }
                }


            }

            PlaylistAction.onDismissRenameSheet -> {
                viewModelScope.launch {
                    _state.update {
                        it.copy(
                            isRenamingPlaylist = false,
                            prefilledTitle = ""
                        )
                    }
                }
            }
            is PlaylistAction.onRenameConfirm -> renameTitle(action.title)
            is PlaylistAction.onDeleteConfirm -> onDeleteConfirm(action.id)
            PlaylistAction.onDismissDeleteSheet -> {
                viewModelScope.launch {
                    _state.update {
                        it.copy(
                            isDeletingPlaylist = false
                        )
                    }
                }
            }

            is PlaylistAction.onChangeCover -> coverChange(action.uri)
            PlaylistAction.photoPickerLaunch -> {
                viewModelScope.launch {
                    eventChannel.send(
                        PlaylistEvent.onCoverChangeChannel
                    )
                }
            }

            PlaylistAction.onPlayClick -> playingFavPlaylist()
            PlaylistAction.onRegularPlaylistPlay -> playingRegularPlaylist()
        }
    }

    private fun coverChange(uri: Uri) {
        viewModelScope.launch {
            val cover = context.contentResolver.openInputStream(uri)?.use { inputStream ->
                inputStream.readBytes()

            }
            val playlist = playlistDao.getplaylistById(_state.value.currentPlaylist!!.id)
            val updatedPlaylist = playlist.copy(
                coverArt = cover
            )
            playlistDao.insert(updatedPlaylist)
          _state.update {
              it.copy(
                  isPlayListSheetVisible = false,
                  currentPlaylist = null
              )
          }
        }
    }

    private fun onDeleteConfirm(id: Long){
        viewModelScope.launch {
            playlistDao.deletePlaylistById(id)
            _state.update {
                it.copy(
                    isDeletingPlaylist = false
                )
            }
          eventChannel.send(PlaylistEvent.OnDeleteChannel)

        }
    }

}