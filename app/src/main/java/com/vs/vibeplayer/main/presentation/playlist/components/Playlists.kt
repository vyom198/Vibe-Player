package com.vs.vibeplayer.main.presentation.playlist.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil3.compose.AsyncImage
import com.vs.vibeplayer.R
import com.vs.vibeplayer.core.theme.bodyMediumRegular
import com.vs.vibeplayer.core.theme.hover
import com.vs.vibeplayer.main.presentation.model.PlaylistUI
import timber.log.Timber

@Composable
fun Playlist( playlists : List<PlaylistUI>, onPlaylistClick : (PlaylistUI) -> Unit) {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(end = 4.dp)){
        items(playlists){ playlist->
            PlayListItem(playlist = playlist,
                onPlaylistClick = onPlaylistClick
                )
        }
    }
}

@Composable
fun PlayListItem(playlist: PlaylistUI, onPlaylistClick: (PlaylistUI) -> Unit) {
    Row(modifier = Modifier.height(88.dp).fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically) {

        AsyncImage(
            model = playlist.coverArt,
            modifier = Modifier.size(64.dp).clip(
                CircleShape
            ),
             error = painterResource( R.drawable.playlist_img),
            contentDescription = null,
            contentScale = ContentScale.Crop

        )

        Column(modifier = Modifier.weight(1f).padding(start = 8.dp)){
              Text(
                 text = playlist.title ,
                  maxLines = 1,
                  style = MaterialTheme.typography.titleMedium,
                  color = MaterialTheme.colorScheme.onPrimary
              )

                Text(
                    text = "${playlist.trackIds?.size ?: 0 } Songs",
                    style = MaterialTheme.typography.bodyMediumRegular,
                    color = MaterialTheme.colorScheme.secondary
                )

        }
        Icon(
            painter = painterResource(id = R.drawable.menu_dots), contentDescription = null,
            tint = MaterialTheme.colorScheme.secondary ,
            modifier = Modifier.clickable{
                onPlaylistClick(playlist)
            }
        )
    }
    HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.hover)
}