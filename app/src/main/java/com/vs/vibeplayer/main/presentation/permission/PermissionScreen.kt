package com.vs.vibeplayer.main.presentation.permission

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vs.vibeplayer.R
import com.vs.vibeplayer.core.buttons.VButton
import com.vs.vibeplayer.core.theme.bodyMediumRegular
import org.koin.androidx.compose.koinViewModel


@Composable
fun PermissionScreen(modifier: Modifier = Modifier,
                     viewModel: PermissionViewModel = koinViewModel(),
                     onNavigateToScan: () -> Unit
                    ) {
    val context = LocalContext.current
    val permissionState = viewModel.permissionState.collectAsStateWithLifecycle().value
    val showDialog = viewModel.showDialog.collectAsStateWithLifecycle().value
    LaunchedEffect(Unit) {
        viewModel.checkPermissionStatus(context)
    }
    LaunchedEffect(permissionState) {
        if (permissionState == true) {
            onNavigateToScan()
        }
    }

    val permissionToRequest = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_AUDIO
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }

    val permissionResultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            viewModel.onPermissionResult(
                isGranted = isGranted
            )
        }
    )
    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface
    ){  paddingValues ->
        Column(
            modifier = modifier.fillMaxSize().padding(paddingValues), horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(R.drawable.vlogo),
                contentDescription = null,

                )
            Text(
                text = stringResource(R.string.vibe_player),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimary
            )
            Text(
                text = stringResource(R.string.permission_rationale),
                style = MaterialTheme.typography.bodyMediumRegular,
                color = MaterialTheme.colorScheme.secondary,
                textAlign = TextAlign.Center

            )
            Spacer(modifier = Modifier.height(16.dp))
            VButton(
                text = stringResource(R.string.allow_access),
                onClick = {

                    permissionResultLauncher.launch(permissionToRequest)

                }
            )


        }


    }

    if (showDialog) {
    PermissionDialog(
        onTryAgain = {
            permissionResultLauncher.launch(permissionToRequest)
        },
        onDismiss = {
            viewModel.hideDialog()
        },
        onOk = {
            viewModel.hideDialog()
        },


        )
    }
}




//@Preview(showBackground = true)
//@Composable
//private fun PermissionPreview() {
//    VibePlayerTheme {
//        PermissionScreen(modifier = Modifier)
//    }
//}