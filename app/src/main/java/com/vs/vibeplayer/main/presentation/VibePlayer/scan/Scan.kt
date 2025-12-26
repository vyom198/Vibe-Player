package com.vs.vibeplayer.main.presentation.VibePlayer.scan

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vs.vibeplayer.R
import com.vs.vibeplayer.core.theme.DarkBlueGrey28
import com.vs.vibeplayer.core.theme.bodyLargeMedium
import com.vs.vibeplayer.core.theme.hover
import com.vs.vibeplayer.main.presentation.VibePlayer.VibePlayerAction
import com.vs.vibeplayer.main.presentation.VibePlayer.VibePlayerEvent
import com.vs.vibeplayer.main.presentation.VibePlayer.VibePlayerState
import com.vs.vibeplayer.main.presentation.VibePlayer.VibePlayerViewModel
import com.vs.vibeplayer.main.presentation.components.Loader
import com.vs.vibeplayer.main.presentation.components.ObserveAsEvents
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel
import timber.log.Timber

@Composable
fun ScanRoot(
    viewModel: VibePlayerViewModel = koinViewModel(),
    onBackClick: () -> Unit,
    onMainScreen:()->Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var message = remember { mutableStateOf("")}
    var scanCompleted = remember { mutableStateOf(false)}
    ObserveAsEvents(
        flow = viewModel.events) {  event ->
        Timber.d("Received event: $event")
         when(event){
             is VibePlayerEvent.ScanCompleted -> {
                 val songCount = event.SongCount
                     scanCompleted.value = event.scanCompleted
                 Timber.d("${songCount}")
                  message.value = if (songCount > 0) {
                     "Scan complete — $songCount ${if(songCount == 1) "song" else "songs"} found"
                 } else {
                     "Scan complete — No songs found with current filters"
                 }

             }
             else -> {}
         }

    }


    ScanScreen(
        state = state,
        onAction = viewModel::onAction,
        onBackClick = onBackClick,
        snackbarHostState = snackbarHostState,
        message = message,
        scanCompleted = scanCompleted,
        onMainScreen = onMainScreen
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanScreen(
    state: VibePlayerState,
    onBackClick : () -> Unit,
    scanCompleted : MutableState<Boolean>,
    snackbarHostState: SnackbarHostState,
    onAction: (VibePlayerAction) -> Unit,
    message : MutableState<String>,
    onMainScreen: () -> Unit
) {
    val durationOptions = listOf(30 to "30s", 60 to "60s")
    val sizeOptions = listOf(100 to "100KB", 500 to "500KB")
    var isScanning by remember { mutableStateOf(false) }

    LaunchedEffect(!state.loadingInReScan) {
        isScanning = false
    }
    LaunchedEffect(state.trackList) {
        if (message.value != "") {
            Timber.d("snackbar is called" )
            snackbarHostState.showSnackbar(
                message = message.value,
                duration = SnackbarDuration.Short
            )
            if (state.trackList.size > 0) {
                onMainScreen.invoke()
                Timber.d("invoke is called" )
            }
        }

    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            CenterAlignedTopAppBar(

                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .padding(start = 4.dp)
                            .background(
                                color = MaterialTheme.colorScheme.hover, shape = CircleShape
                            )
                    ) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                    }
                },
                title = {

                    Text(
                        modifier = Modifier.padding(top = 12.dp),
                        text = stringResource(R.string.scan_screen),
                        style = MaterialTheme.typography.bodyLargeMedium,
                        color = MaterialTheme.colorScheme.onPrimary
                    )


                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues).padding(top = 18.dp).padding(
                horizontal = 12.dp
            ).fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Loader(isReScan = state.loadingInReScan)
            Spacer(modifier = Modifier.height(28.dp))
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center
            ) {

                Text(
                    text = "Ignore duration less than",
                    style = MaterialTheme.typography.bodyLargeMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    durationOptions.forEach { (value, label) ->
                        // Using OutlinedCard for the container
                        OutlinedCard(
                            modifier = Modifier
                                .width(180.dp)
                                .height(44.dp)
                                .border(
                                    width = 0.dp,
                                    color = if(state.durationValue == value){MaterialTheme.colorScheme.primary}
                                    else{
                                        DarkBlueGrey28
                                    },
                                    shape = RoundedCornerShape(25.dp)
                                )

                                ,shape = RoundedCornerShape(25.dp),
                            colors = CardDefaults.outlinedCardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Start
                            ) {
                                RadioButton(
                                    selected = (state.durationValue == value),
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = MaterialTheme.colorScheme.primary,
                                        unselectedColor = MaterialTheme.colorScheme.onPrimary

                                    ),
                                    onClick ={ onAction(VibePlayerAction.onDurationSelect(value))},
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.bodyLargeMedium,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                    }
                }
                    Spacer(modifier = Modifier.height(18.dp))
                    Text(
                        text = "Ignore size less than",
                        style = MaterialTheme.typography.bodyLargeMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        sizeOptions.forEach { (value, label) ->
                            // Using OutlinedCard for the container
                            OutlinedCard(
                                modifier = Modifier
                                    .width(180.dp)
                                    .height(44.dp)
                                    .border(
                                        width = 0.dp,
                                        color = if(state.sizeValue == value){MaterialTheme.colorScheme.primary}
                                                 else{
                                            DarkBlueGrey28
                                        },
                                        shape = RoundedCornerShape(25.dp)
                                    ),

                                shape = RoundedCornerShape(25.dp),
                                colors = CardDefaults.outlinedCardColors(
                                    containerColor =  MaterialTheme.colorScheme.surface,

                                )
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Start
                                ) {
                                    RadioButton(
                                        selected =  state.sizeValue== value,
                                        colors = RadioButtonDefaults.colors(
                                            selectedColor = MaterialTheme.colorScheme.primary,
                                            unselectedColor = MaterialTheme.colorScheme.onPrimary

                                        ),
                                        onClick = {onAction(VibePlayerAction.onSizeSelect(value))}, // Handled by selectable modifier
                                        modifier = Modifier.padding(start = 8.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.bodyLargeMedium,
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                }

                            }

                        }

                    }


            }
           Spacer(modifier = Modifier.height(18.dp))
            Button(
                onClick = { onAction(VibePlayerAction.onScanButton)
                            isScanning = true
                            },
                shape = RoundedCornerShape(18.dp),
                enabled = !isScanning,
                modifier = Modifier.width(400.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if(state.loadingInReScan ){
                        MaterialTheme.colorScheme.hover
                    }else{
                        MaterialTheme.colorScheme.primary
                    }
                )
            ){
                if(state.loadingInReScan){
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Scaning", style = MaterialTheme.typography.bodyLargeMedium,
                            color = MaterialTheme.colorScheme.secondary
                        )

                    }
                }else{
                    Text(text = "Scan", style = MaterialTheme.typography.bodyLargeMedium,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }


            }


        }
    }
}

//@Preview
//@Composable
//private fun Preview() {
//    VibePlayerTheme {
//        ScanScreen(
//            state = VibePlayerState(),
//            onAction = {},
//            onToMainScreen = {},
//            onBackClick = {}
//        )
//    }
//}