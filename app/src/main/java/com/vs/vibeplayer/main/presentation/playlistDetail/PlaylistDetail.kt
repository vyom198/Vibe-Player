package com.vs.vibeplayer.main.presentation.playlistDetail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.vs.vibeplayer.core.theme.DarkBlueGrey28
import com.vs.vibeplayer.core.theme.DarkSlateGrey
import com.vs.vibeplayer.core.theme.VibePlayerTheme
import com.vs.vibeplayer.core.theme.bodyLargeMedium
import com.vs.vibeplayer.core.theme.bodyLargeRegular
import com.vs.vibeplayer.core.theme.bodyMediumRegular
import com.vs.vibeplayer.core.theme.hover
import com.vs.vibeplayer.main.presentation.VibePlayer.components.AudioListItem
import com.vs.vibeplayer.main.presentation.playlist.PlaylistAction
import org.koin.androidx.compose.koinViewModel
import java.util.concurrent.atomic.LongAdder

@Composable
fun PlaylistDetailRoot(
    viewModel: PlaylistDetailViewModel = koinViewModel(),
    onBackClick : () -> Unit,
    onAddClick: (String) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    PlaylistDetailScreen(
        state = state,
        onAction = viewModel::onAction,
          popBackStack = onBackClick,
        onAddClick = onAddClick,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistDetailScreen(
    state: PlaylistDetailState,
    onAction: (PlaylistDetailAction) -> Unit,
    popBackStack : () -> Unit,
    onAddClick : (String)-> Unit,

) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = MaterialTheme.colorScheme.surface
            ),
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "")
                },
                navigationIcon = {
                    IconButton(onClick = popBackStack,
                        modifier = Modifier.padding(start = 16.dp)
                            .size(36.dp)
                            .clip(
                                shape = CircleShape
                            )
                            .background(
                                color = MaterialTheme.colorScheme.hover
                            )
                        ){
                        Icon(imageVector = Icons.Default.ArrowBack,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(16.dp))
                    }
                }
            )
        }


    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(paddingValues)
                .padding(
                    horizontal = 16.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            AsyncImage(
                model = state.cover,
                modifier = Modifier
                    .size(200.dp)
                    .clip(
                        CircleShape
                    ),
                contentScale = ContentScale.Crop,
                contentDescription = null,
                placeholder = painterResource(R.drawable.playlist_img),
                fallback = painterResource(R.drawable.playlist_img)


            )
            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "My Playlist",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimary
            )
            Spacer(modifier = Modifier.height(40.dp))
            if (state.songList.isEmpty()) {
                Text(
                    text = stringResource(R.string.no_songs_found),
                    style = MaterialTheme.typography.bodyLargeRegular,
                    color = MaterialTheme.colorScheme.secondary

                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedButton(
                    onClick = { onAddClick(state.playlistTitle) },
                    shape = RoundedCornerShape(100),
                    border = BorderStroke(
                        width = 1.dp,
                        color = DarkSlateGrey
                    ),
                    modifier = Modifier.height(44.dp).wrapContentWidth()


                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Add Songs",
                        style = MaterialTheme.typography.bodyLargeMedium,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth().height(60.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    OutlinedButton(
                        onClick = {}, modifier =
                            Modifier.width(186.dp).height(44.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.shuffle),
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Shuffle", style = MaterialTheme.typography.bodyLargeMedium,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    OutlinedButton(onClick = {}, modifier =
                        Modifier.width(186.dp).height(44.dp)) {
                        Icon(
                            painter = painterResource(id = R.drawable.outlined_play),
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Play", style = MaterialTheme.typography.bodyLargeMedium,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }



            Row(
                modifier = Modifier.height(60.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${state.songSize} Songs",
                    style = MaterialTheme.typography.bodyLargeMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
                IconButton(
                    onClick = { onAddClick(state.playlistTitle) },
                    modifier = Modifier.size(36.dp).clip(
                        shape = CircleShape
                    ).background(
                        color = MaterialTheme.colorScheme.hover,
                        shape = CircleShape
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Add, contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(state.songList) {
                    AudioListItem(
                        item = it,
                        onTrackClick = {})
                    HorizontalDivider(
                        modifier = Modifier.fillMaxWidth(), thickness = 1.dp,
                        color = DarkBlueGrey28
                    )

                }
            }
        }


        }



    }
}

