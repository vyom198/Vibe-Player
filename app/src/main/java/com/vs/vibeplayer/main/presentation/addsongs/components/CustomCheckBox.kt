package com.vs.vibeplayer.main.presentation.addsongs.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.vs.vibeplayer.R


@Composable
fun CustomCheckBox(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,

) {
    Box(modifier.height(28.dp).height(28.dp),
         contentAlignment = Alignment.Center) {
    Box(
        modifier = modifier
            .size(16.dp)
            .border(
                width = if (checked) 0.dp else 1.dp,
                color = if (checked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                shape = CircleShape
            )
            .clip(
                shape = CircleShape
            ).background(
                color = if (checked) MaterialTheme.colorScheme.primary else Color.Transparent
            )
            .toggleable(
                value = checked,
                onValueChange = { onCheckedChange?.invoke(!checked) },
                enabled = enabled,
                role = Role.Checkbox
            ),
        contentAlignment = Alignment.Center
    ) {
        if (checked) {
            Icon(
                painter = painterResource(id = R.drawable.check), contentDescription = null,
                tint = Color.Unspecified,

                )
        }


    }
}


}

//@Preview
//@Composable
//private fun CustomCheckBoxPreview() {
//    VibePlayerTheme {
//        CustomCheckBox(
//            checked = false,
//            onCheckedChange = {},
//            modifier = Modifier,
//            enabled = true,
//        )
//    }
//
//}