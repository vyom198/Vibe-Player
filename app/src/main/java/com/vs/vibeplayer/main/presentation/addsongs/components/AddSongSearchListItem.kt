package com.vs.vibeplayer.main.presentation.addsongs.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil3.compose.AsyncImage
import com.vs.vibeplayer.R
import com.vs.vibeplayer.core.theme.LightSteelBlue
import com.vs.vibeplayer.core.theme.bodyMediumRegular
import com.vs.vibeplayer.main.presentation.addsongs.AddSongResult


@Composable
fun AddSongSearchResultItem(modifier: Modifier = Modifier, item : AddSongResult,
                            onToggleSelection: (Long, Boolean) -> Unit
) {
    Row(modifier = modifier
        .fillMaxWidth()
        .height(90.dp)
        .clickable {

        }, verticalAlignment = Alignment.CenterVertically) {


         CustomCheckBox(
             checked = item.isSelected,
             onCheckedChange = {
                 onToggleSelection(item.id , it )
             },

         )



        Spacer(modifier = Modifier.width(11.dp))
        AsyncImage(
            modifier = Modifier
                .size(60.dp)
                .clip(
                    shape = RoundedCornerShape(8.dp),
                ),
            contentScale = ContentScale.Crop,
            model = item.cover,
            error =painterResource(R.drawable.song_cover_small),
            contentDescription = null,
            placeholder = painterResource(R.drawable.song_cover_small)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier
            .height(IntrinsicSize.Min)
            .weight(1f),
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


