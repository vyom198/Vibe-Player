package com.vs.vibeplayer.main.presentation.playlist.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.vs.vibeplayer.R
import com.vs.vibeplayer.core.theme.bodyLargeMedium
import com.vs.vibeplayer.core.theme.bodyMediumRegular
import com.vs.vibeplayer.core.theme.hover
import com.vs.vibeplayer.main.presentation.playlist.PlaylistAction
import com.vs.vibeplayer.main.presentation.playlist.PlaylistState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavBottomSheet(
    state: PlaylistState,
    onDismiss : ()-> Unit ,
) {
    ModalBottomSheet(
        dragHandle = null,
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(),
        containerColor = MaterialTheme.colorScheme.surface,
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth().height(172.dp)
                .padding(top = 12.dp , bottom = 24.dp, start = 12.dp, end = 12.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
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
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp).padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically ,

                ) {
                IconButton(
                    onClick = {

                    },
                    modifier = Modifier.size(36.dp).clip(
                        shape = CircleShape
                    ).background(
                        color = MaterialTheme.colorScheme.hover
                    )
                ) {
                    Icon(painter = painterResource(R.drawable.outlined_play),
                        tint = MaterialTheme.colorScheme.secondary,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)

                    )

                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Play",
                    style = MaterialTheme.typography.bodyLargeMedium,
                    color = Color.White

                )

            }

        }
    }

}