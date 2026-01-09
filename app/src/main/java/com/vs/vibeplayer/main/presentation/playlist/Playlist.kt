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
                        text = "2 Songs", color = MaterialTheme.colorScheme.secondary,
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
            var title by remember { mutableStateOf("") }
            if (state.isShowing) {
                ModalBottomSheet(
                    dragHandle = null,
                    onDismissRequest = { onAction(PlaylistAction.onDismissSheet) },
                    sheetState = modalState,
                    containerColor = MaterialTheme.colorScheme.surface,
                ) {

                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
                            .wrapContentSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Text(
                            text = stringResource(R.string.create_new_playlist),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(modifier = Modifier.height(18.dp))
                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth(),
                            shape = CircleShape,
                            value = title,
                            trailingIcon = {
                                Text(

                                    text = "${title.length}/40",
                                    style = MaterialTheme.typography.bodySmallRegular,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            },
                            textStyle = MaterialTheme.typography.bodyLargeRegular,
                            onValueChange = {
                                val filtered = it.take(40)

                                title = filtered

                            },
                            singleLine = true,
                            placeholder = {
                                Text(
                                    text = stringResource(R.string.enter_playlist_name),
                                    style = MaterialTheme.typography.bodyLargeRegular,

                                    )
                            },


                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = MaterialTheme.colorScheme.onPrimary,
                                focusedBorderColor = MaterialTheme.colorScheme.surface,
                                unfocusedBorderColor = MaterialTheme.colorScheme.surface,
                                unfocusedTextColor = MaterialTheme.colorScheme.secondary,
                                focusedContainerColor = MaterialTheme.colorScheme.hover,
                                unfocusedContainerColor = MaterialTheme.colorScheme.hover,
                            )
                        )

                        Spacer(modifier = Modifier.height(18.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            OutlinedButton(
                                onClick = { onAction(PlaylistAction.onDismissSheet)
                                        title = ""
                                    }, modifier = Modifier.width(180.dp)
                                    .height(44.dp)
                            ) {
                                Text(
                                    text = stringResource(R.string.cancel),
                                    style = MaterialTheme.typography.bodyLargeMedium,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )

                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = { onAction(PlaylistAction.onCreateClick(title))
                                           title = ""

                                          },
                                enabled = title.isNotEmpty(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.hover,
                                    disabledContentColor = MaterialTheme.colorScheme.secondary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary,
                                    disabledContainerColor = MaterialTheme.colorScheme.hover

                                ), modifier = Modifier.fillMaxWidth().width(180.dp).height(44.dp)
                            ) {
                                Text(
                                    text = stringResource(R.string.create),
                                    style = MaterialTheme.typography.bodyLargeMedium,
                                )

                            }

                        }


                    }
                }
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
