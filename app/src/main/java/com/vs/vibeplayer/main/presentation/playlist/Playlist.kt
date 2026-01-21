package com.vs.vibeplayer.main.presentation.playlist

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.vs.vibeplayer.R
import com.vs.vibeplayer.app.navigation.NavigationRoute
import com.vs.vibeplayer.core.theme.VibePlayerTheme
import com.vs.vibeplayer.core.theme.bodyLargeMedium
import com.vs.vibeplayer.core.theme.bodyLargeRegular
import com.vs.vibeplayer.core.theme.bodyMediumRegular
import com.vs.vibeplayer.core.theme.bodySmallRegular
import com.vs.vibeplayer.core.theme.hover
import com.vs.vibeplayer.main.presentation.components.CreatePlaylistBottomSheet
import com.vs.vibeplayer.main.presentation.components.ObserveAsEvents
import com.vs.vibeplayer.main.presentation.playlist.components.Playlist
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import kotlin.time.Duration

@Composable
fun PlaylistRoot(
    viewModel: PlaylistViewModel = koinViewModel(),
    onCreateClick: (String) -> Unit
) {
    val scope = rememberCoroutineScope()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    ObserveAsEvents(flow = viewModel.events) { event ->

        when(event) {
            is PlaylistEvent.OnCreateChannel -> {
                if (event.isExists) {
                    scope.launch {

                        snackbarHostState.showSnackbar(
                            message = "Playlist already exists",
                            duration = SnackbarDuration.Short
                        )

                    }

                } else {
                    onCreateClick(event.title!!)
                }
            }

        }

    }
    PlaylistScreen(
        state = state,
        onAction = viewModel::onAction,
        snackbarHostState = snackbarHostState
    )
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistScreen(
    state: PlaylistState,
    onAction: (PlaylistAction) -> Unit,
    snackbarHostState : SnackbarHostState

) {
    val modalState = rememberModalBottomSheetState()


    Scaffold(
        modifier = Modifier.fillMaxSize() ,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 16.dp , end = 12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if(state.playlists.isEmpty())"1 Playlist" else "${state.playlists.size + 1} Playlists",
                    style = MaterialTheme.typography.bodyLargeMedium,
                    color = MaterialTheme.colorScheme.secondary
                )

                IconButton(
                    onClick = {
                        onAction(PlaylistAction.AddIconClickedOrCreatePlaylist)
                    },
                    modifier = Modifier.background(
                        color = MaterialTheme.colorScheme.hover,
                        shape = CircleShape
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Add, contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }


            }
            Row(
                modifier = Modifier
                    .fillMaxWidth().padding(end= 4.dp)
                    .height(88.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = R.drawable.favourite_playlist,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp) ,
                    contentScale = ContentScale.Fit

                    )
                Spacer(modifier = Modifier.width(8.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Favourites", color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "${state.favouriteSongssize} Songs", color = MaterialTheme.colorScheme.secondary,
                        style = MaterialTheme.typography.bodyMediumRegular
                    )

                }

                Icon(
                    painter = painterResource(id = R.drawable.menu_dots), contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary
                )

            }
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "My Playlists (${state.playlists.size})",
                style = MaterialTheme.typography.bodyLargeMedium,
                color = MaterialTheme.colorScheme.secondary
            )

            Spacer(modifier = Modifier.height(8.dp))
            if (state.playlists.isEmpty()) {
                OutlinedButton(
                    onClick = { onAction(PlaylistAction.AddIconClickedOrCreatePlaylist) },
                    modifier = Modifier.fillMaxWidth(),

                    ) {
                    Icon(
                        imageVector = Icons.Default.Add, contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.create_playlist),
                        style = MaterialTheme.typography.bodyLargeMedium,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            } else {
                Playlist(playlists = state.playlists)
            }

            if (state.isShowing) {
                CreatePlaylistBottomSheet(
                    modalState = modalState,
                    onDismiss = {
                        onAction(PlaylistAction.onDismissSheet)
                    },
                    onValueChange = {
                        onAction(PlaylistAction.onTextChange(it))
                    },
                    onCreate = {
                        onAction(PlaylistAction.onCreateClick(it))
                        onAction(PlaylistAction.onDismissSheet)
                    },
                    title = state.title

                )

            }
        }
    }
}



//@Preview
//@Composable
//private fun PlaylistScreenPreview() {
//    VibePlayerTheme {
//        PlaylistScreen(
//            state = PlaylistState(),
//            onAction = {}
//        )
//
//        }
//    }
