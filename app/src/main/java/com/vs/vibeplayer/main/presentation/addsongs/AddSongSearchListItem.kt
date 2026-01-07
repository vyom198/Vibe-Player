package com.vs.vibeplayer.main.presentation.addsongs

import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.ui.graphics.Color
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.VectorProperty
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.vs.vibeplayer.R
import com.vs.vibeplayer.core.theme.DarkBlueGrey28
import com.vs.vibeplayer.core.theme.LightSteelBlue
import com.vs.vibeplayer.core.theme.bodyMediumRegular
import kotlin.math.floor


@Composable
fun AddSongSearchResultItem(modifier: Modifier = Modifier, item : AddSongResult) {
    Row(modifier = modifier
        .fillMaxWidth()
        .height(90.dp)
        .clickable {

        }, verticalAlignment = Alignment.CenterVertically) {

        Checkbox(
            checked = false,
            onCheckedChange = null ,

            modifier = Modifier
                .size(28.dp)
                .border(
                    width = 1.dp,
                    color = DarkBlueGrey28,
                    shape = CircleShape

                ),
            colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.primary,
                    uncheckedColor = MaterialTheme.colorScheme.secondary,
                checkmarkColor = Color.White

            )

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
            fallback =painterResource(R.drawable.song_cover_small),
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


