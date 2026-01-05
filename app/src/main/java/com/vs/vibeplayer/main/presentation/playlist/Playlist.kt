package com.vs.vibeplayer.main.presentation.playlist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.vs.vibeplayer.R
import com.vs.vibeplayer.app.navigation.NavigationRoute
import com.vs.vibeplayer.core.theme.VibePlayerTheme
import com.vs.vibeplayer.core.theme.bodyLargeMedium
import com.vs.vibeplayer.core.theme.bodyMediumRegular
import com.vs.vibeplayer.core.theme.hover
import org.koin.androidx.compose.koinViewModel

@Composable
fun PlaylistRoot(
    viewModel: PlaylistViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    PlaylistScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@Composable
fun PlaylistScreen(
    state: PlaylistState,
    onAction: (PlaylistAction) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ){
       Row(modifier = Modifier
           .fillMaxWidth()
           .height(60.dp),
           verticalAlignment = Alignment.CenterVertically,
           horizontalArrangement = Arrangement.SpaceBetween
       ){
           Text(text = "${1} Playlist" ,
                style = MaterialTheme.typography.bodyLargeMedium,
                color = MaterialTheme.colorScheme.secondary)

           IconButton(
               onClick = {
                 onAction(PlaylistAction.AddIconClickedOrCreatePlaylist)
               },
               modifier = Modifier.background(color = MaterialTheme.colorScheme.hover,
                   shape = CircleShape)
           ) {
               Icon(imageVector = Icons.Default.Add, contentDescription = null,
                   tint = MaterialTheme.colorScheme.secondary)
           }


       }
        Row(modifier = Modifier
            .fillMaxWidth()
            .height(88.dp),
            verticalAlignment = Alignment.CenterVertically){
            AsyncImage(
                model =  R.drawable.favourite_playlist,
                contentDescription = null ,


                )
            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)){
                Text(text = "Favourites", color = MaterialTheme.colorScheme.onPrimary,
                    style =  MaterialTheme.typography.titleMedium)
                Text(text = "2 Songs", color = MaterialTheme.colorScheme.secondary,
                    style =  MaterialTheme.typography.bodyMediumRegular)

            }

            Icon(painter = painterResource(id = R.drawable.menu_dots),contentDescription = null,
                tint =MaterialTheme.colorScheme.secondary )

        }
        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "My Playlists (${0})" ,
            style = MaterialTheme.typography.bodyLargeMedium,
            color = MaterialTheme.colorScheme.secondary)

        Spacer(modifier = Modifier.height(8.dp))
        OutlinedButton(
            onClick = { onAction(PlaylistAction.AddIconClickedOrCreatePlaylist)},
            modifier = Modifier.fillMaxWidth(),

        ){
            Icon(imageVector = Icons.Default.Add, contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = stringResource(R.string.create_playlist),
                style = MaterialTheme.typography.bodyLargeMedium,
                color = MaterialTheme.colorScheme.onPrimary)
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
//    }
//}