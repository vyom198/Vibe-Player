package com.vs.vibeplayer.main.presentation.playlist.components

import androidx.compose.material3.HorizontalDivider



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
import com.vs.vibeplayer.main.presentation.playlist.PlaylistState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistBottomSheet(
    state: PlaylistState,
    onDismiss : ()-> Unit ,
    onDeleteClick : (Long) -> Unit,
    onRenameClick : (String) -> Unit,
    onChangeCover : ()->Unit,
    onPlayClick : () -> Unit
) {
    ModalBottomSheet(
        dragHandle = null,
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(),
        containerColor = MaterialTheme.colorScheme.surface,
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth().height(322.dp)
                .padding(top = 12.dp , start = 12.dp, end = 12.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(88.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = state.currentPlaylist?.coverArt,
                    modifier = Modifier.size(64.dp).clip(
                        CircleShape
                    ),
                    fallback = painterResource( R.drawable.playlist_img),
                    contentDescription = null,
                    contentScale = ContentScale.Crop

                )

                Column(modifier = Modifier.weight(1f).padding(start = 8.dp)){
                    Text(
                        text = state.currentPlaylist?.title ?: "Unknown Title" ,
                        maxLines = 1,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimary
                    )

                    Text(
                        text = "${state.currentPlaylist?.trackIds?.size ?: 0 } Songs",
                        style = MaterialTheme.typography.bodyMediumRegular,
                        color = MaterialTheme.colorScheme.secondary
                    )

                }
            }
            HorizontalDivider(modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.hover
                )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp).padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically ,

                ) {
                IconButton(
                    onClick = onPlayClick,
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
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp).padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically ,

                ) {
                IconButton(
                    onClick = {
                         onRenameClick(state.currentPlaylist!!.title)
                    },
                    modifier = Modifier.size(36.dp).clip(
                        shape = CircleShape
                    ).background(
                        color = MaterialTheme.colorScheme.hover
                    )
                ) {
                    Icon(painter = painterResource(R.drawable.pen),
                        tint = MaterialTheme.colorScheme.secondary,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)

                    )

                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Rename",
                    style = MaterialTheme.typography.bodyLargeMedium,
                    color = Color.White

                )

            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp).padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically ,

                ) {
                IconButton(
                    onClick = onChangeCover,
                    modifier = Modifier.size(36.dp).clip(
                        shape = CircleShape
                    ).background(
                        color = MaterialTheme.colorScheme.hover
                    )
                ) {
                    Icon(painter = painterResource(R.drawable.img_edit),
                        tint = MaterialTheme.colorScheme.secondary,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)

                    )

                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Change Cover",
                    style = MaterialTheme.typography.bodyLargeMedium,
                    color = Color.White

                )

            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp).padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically ,

                ) {
                IconButton(
                    onClick = {
                       onDeleteClick(state.currentPlaylist!!.id)
                    },
                    modifier = Modifier.size(36.dp).clip(
                        shape = CircleShape
                    ).background(
                        color = MaterialTheme.colorScheme.hover
                    )
                ) {
                    Icon(painter = painterResource(R.drawable.bin),
                        tint = MaterialTheme.colorScheme.secondary,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)

                    )

                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Delete",
                    style = MaterialTheme.typography.bodyLargeMedium,
                    color = Color.White

                )

            }

        }
    }

}