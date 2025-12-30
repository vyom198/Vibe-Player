package com.vs.vibeplayer.main.presentation.player.components


import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.vs.vibeplayer.core.theme.DarkBlueGrey28
import com.vs.vibeplayer.core.theme.DarkSlateGrey
import com.vs.vibeplayer.core.theme.SlateGrey
import com.vs.vibeplayer.core.theme.VibePlayerTheme

@Composable
fun LineProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    height: Dp = 5.dp,
    progressColor: Color = MaterialTheme.colorScheme.onPrimary,
    trackColor: Color = SlateGrey
) {
        Canvas(modifier = modifier.fillMaxWidth().height(height)) {
            // Draw track (full width rectangle)
            drawRoundRect(
                color = trackColor,
                topLeft = Offset.Zero,
                cornerRadius = CornerRadius(5L),
                size = Size(size.width, size.height)
            )

            // Draw progress (partial width rectangle)
            if (progress > 0) {
                drawRoundRect(
                    color = progressColor,
                    cornerRadius = CornerRadius(5L),
                    topLeft = Offset.Zero,
                    size = Size(size.width * progress, size.height)
                )
            }
        }

}

//@Preview
//@Composable
//private fun CustomProgressBARPreview() {
//    VibePlayerTheme { // Wrap with your theme
//
//            LineProgressBar(
//                progress = 0.4f,
//            )
//
//    }
//
//
//}