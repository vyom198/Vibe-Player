package com.vs.vibeplayer.main.presentation.VibePlayer.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.vs.vibeplayer.R
import com.vs.vibeplayer.core.theme.LightSteelBlue
import com.vs.vibeplayer.core.theme.SlateGrey
import com.vs.vibeplayer.core.theme.VibePlayerTheme
import com.vs.vibeplayer.core.theme.bodyMediumRegular
import com.vs.vibeplayer.main.presentation.VibePlayer.VibePlayerState
import com.vs.vibeplayer.main.presentation.model.AudioTrackUI

@Composable
fun AudioList(
    modifier: Modifier = Modifier,
    state: LazyListState,
    audioList : List<AudioTrackUI>,
    onTrackClick: (Long) -> Unit
    ) {
    LazyColumn(
        modifier = modifier,
        state = state
    ) {
   items(audioList){
       AudioListItem(item = it,
                     onTrackClick = onTrackClick)
       HorizontalDivider(modifier = Modifier.fillMaxWidth(), thickness = 1.dp,
           color = SlateGrey
       )
   }


    }


}

@Composable
fun AudioListItem(modifier: Modifier = Modifier, item : AudioTrackUI,
                  onTrackClick : (Long) -> Unit ) {
    Row(modifier = modifier.fillMaxWidth().height(90.dp).clickable {
        onTrackClick(item.id)
    }, verticalAlignment = Alignment.CenterVertically) {
        AsyncImage(
            modifier = Modifier.size(60.dp).clip(
                shape = RoundedCornerShape(8.dp),
            ),
            contentScale = ContentScale.Crop,
            model = item.cover,
            fallback =painterResource(R.drawable.item_placeholder),
            contentDescription = null,
            placeholder = painterResource(R.drawable.item_placeholder)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.height(IntrinsicSize.Min).weight(1f),
                verticalArrangement = Arrangement.Center) {
            Text(text = item.title , style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimary, maxLines = 1 ,
                overflow = TextOverflow.Ellipsis)
            Text(text = item.artist , style = MaterialTheme.typography.bodyMediumRegular,
                color = LightSteelBlue
            )

        }
        Text(text = item.totalDurationString , style = MaterialTheme.typography.bodyMediumRegular,
            color = LightSteelBlue
        )


    }
}

//@Preview
//@Composable
//private fun AudioListItemPreview() {
//    VibePlayerTheme {
//        AudioListItem(item = AudioTrackUI(
//            cover = null,
//            id = 1,
//            title = "505",
//            artist = "Artic Monkey",
//            totalDurationMs = 240098L,
//
//        ))
//    }
//
//}