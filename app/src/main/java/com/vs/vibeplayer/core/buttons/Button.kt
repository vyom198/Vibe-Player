package com.vs.vibeplayer.core.buttons


import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun VButton(
    width: Dp = Dp.Unspecified,
    text: String,
    onClick: ()-> Unit
           ) {
      Button(
          onClick = onClick,
          modifier = Modifier.width(width),
          shape = RoundedCornerShape(20.dp),
      ) {
          Text(text, style = MaterialTheme.typography.labelMedium,
              color = MaterialTheme.colorScheme.onPrimary)
      }

     }