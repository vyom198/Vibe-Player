package com.vs.vibeplayer.main.presentation.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.vs.vibeplayer.core.theme.Accent


@Composable
fun Loader(size: Dp = 100.dp,isScannigInMainScreen: Boolean = false,
           isReScan : Boolean = false
          )  {
    val infinite = rememberInfiniteTransition()
        val angle by if(isScannigInMainScreen || isReScan){
            infinite.animateFloat(
                initialValue = 0f,
                targetValue = 360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 2000,
                        easing = LinearEasing
                    )
                )
            )
        } else {
            remember { mutableStateOf(0f) }
        }


    Canvas(
        modifier = Modifier
            .size(size)
    ) {
        val radius = size.toPx() / 2f // 50
        val center = Offset(radius, radius) // (50, 50)

        // rings
        val ringCount = 3
        for (i in 0..ringCount + 1) {
            when (i) {
                // center dot
                0 -> {
                    drawCircle(
                        color = Accent,
                        radius = radius * 0.08f, // 50 * 0.08
                        center = center
                    )
                }
                // radar outer circle
                3 -> {
                    drawCircle(
                        color = Accent,
                        radius = radius * (i / ringCount.toFloat()), // 50 * (i / 3)
                        center = center,
                        style = Stroke(width = 3f)
                    )
                }
                // outer circle
                ringCount + 1 -> {
                    drawCircle(
                        color = Accent.copy(alpha = 0.1f),
                        radius = radius + 20f, // 50 + 20
                        center = center,
                        style = Stroke(width = 3f)
                    )
                }
                // radar inner circle
                else -> {
                    drawCircle(
                        color = Accent.copy(alpha = 0.1f),
                        radius = radius * (i / ringCount.toFloat()), // 50 * (i / 3)
                        center = center,
                        style = Stroke(width = 3f)
                    )
                }
            }
        }

        // sweep
        rotate(
            degrees = angle, // 0 to 360
            pivot = center // (50, 50)
        ) {
            // sweep line
            drawLine(
                color = Accent.copy(alpha = 0.9f),
                start = center,
                end = center + Offset(radius, 0f),
                strokeWidth = 3f
            )

            val sweepBrush = Brush.sweepGradient(
                colors = listOf(
                    Accent.copy(alpha = 0.0f),
                    Accent.copy(alpha = 0.0f),
                    Accent.copy(alpha = 0.5f),

                ),
                center = center
            )

            // sweep arc
            drawArc(
                brush = sweepBrush,
                startAngle = -135f,
                sweepAngle = 135f,
                useCenter = true
            )
        }

    }


}

//@Preview
//@Composable
//private fun LoaderPreview () {
//    Box(
//        modifier = Modifier
//            .size(200.dp),
//        contentAlignment = Alignment.Center
//    ) {
//        Loader()
//    }
//}

