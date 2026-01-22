package com.vs.vibeplayer.main.presentation.VibePlayer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SliderDefaults.Track
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vs.vibeplayer.R
import com.vs.vibeplayer.core.database.track.TrackEntity
import com.vs.vibeplayer.core.theme.AlmostBlack
import com.vs.vibeplayer.core.theme.SlateGrey
import com.vs.vibeplayer.core.theme.VibePlayerTheme
import com.vs.vibeplayer.core.theme.accent
import com.vs.vibeplayer.core.theme.bodyLargeMedium
import com.vs.vibeplayer.core.theme.bodyMediumRegular
import com.vs.vibeplayer.core.theme.hover
import com.vs.vibeplayer.main.presentation.VibePlayer.components.AudioList
import com.vs.vibeplayer.main.presentation.VibePlayer.components.EmptyScreen
import com.vs.vibeplayer.main.presentation.VibePlayer.model.TabItem
import com.vs.vibeplayer.main.presentation.components.Loader
import com.vs.vibeplayer.main.presentation.miniplayer.MiniPlayerRoot
import com.vs.vibeplayer.main.presentation.miniplayer.MiniPlayerScreen
import com.vs.vibeplayer.main.presentation.playlist.PlaylistRoot
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import timber.log.Timber

@Composable
fun VibePlayerRoot(
    viewModel: VibePlayerViewModel = koinViewModel(),
    NavigateToScanScreen : () -> Unit,
    NavigateWithTrackId : (Long) -> Unit,
    onSearchClick: () -> Unit,
    onShuffleClick: () -> Unit,
    onPlayClick: () -> Unit,
    onMiniPlayerClick : () -> Unit,
    onCreateClick: (String) -> Unit,
    onNavigatetoPlayer: () -> Unit,
    OnNavigateToPlaylistDetail : (Long) -> Unit
){
    val state by viewModel.state.collectAsStateWithLifecycle()
    VibePlayerScreen(
        state = state,
        onAction = viewModel::onAction,
        onScanClick = NavigateToScanScreen,
        NavigateWithTrackId = NavigateWithTrackId,
        onSearchClick = onSearchClick,
        onPlayClick = onPlayClick,
        onShuffleClick = onShuffleClick,
        onMiniPlayerClick = onMiniPlayerClick,
        onCreateClick = onCreateClick,
        onNavigatetoPlayer = onNavigatetoPlayer,
        OnNavigateToPlaylistDetail =OnNavigateToPlaylistDetail

    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VibePlayerScreen(
    state: VibePlayerState,
    onScanClick : () -> Unit,
    onAction: (VibePlayerAction) -> Unit,
    NavigateWithTrackId : (Long) -> Unit,
    onSearchClick : () -> Unit,
    onPlayClick : () -> Unit,
    onShuffleClick : () -> Unit,
    onMiniPlayerClick: () -> Unit,
    onCreateClick : (String) -> Unit,
    onNavigatetoPlayer :()-> Unit,
    OnNavigateToPlaylistDetail : (Long) -> Unit

) {
    val lazyListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabItems = listOf(
        TabItem("Songs"),
        TabItem("Playlist"),

        )

    val pagerState = rememberPagerState {
        tabItems.size
    }

    LaunchedEffect(pagerState.currentPage) {

            selectedTabIndex = pagerState.currentPage

    }
    LaunchedEffect(selectedTabIndex) {
        pagerState.animateScrollToPage(selectedTabIndex)
    }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {

         TopAppBar(
            modifier = Modifier.padding(end = 2.dp, start = 2.dp),
             title = {
                 Text(text = stringResource(R.string.vibe_player),
                     style =  MaterialTheme.typography.bodyLargeMedium,
                     color =  MaterialTheme.colorScheme.accent)

             },
             navigationIcon = {
                 Icon(
                     painter = painterResource(R.drawable.logo_small),
                     contentDescription = null,
                     tint = MaterialTheme.colorScheme.accent,
                     modifier = Modifier.size(24.dp)
                 )
             },
             actions = {
                 IconButton(onClick = onScanClick,
                     modifier = Modifier
                         .size(36.dp).padding(end = 14.dp)
                         .background(
                             color = MaterialTheme.colorScheme.hover, shape = CircleShape
                         ))
                          {
                     Icon(
                         painter = painterResource(R.drawable.scan),
                         contentDescription = null,

                     )
                 }
                 Spacer(modifier = Modifier.width(20.dp))
                 IconButton(onClick = onSearchClick,
                     modifier = Modifier
                         .size(36.dp)
                         .padding(end = 16.dp)
                         .background(
                             color = MaterialTheme.colorScheme.hover, shape = CircleShape
                         ))
                 {
                     Icon(
                         modifier = Modifier.size(16.dp),
                         painter = painterResource(R.drawable.search),
                         contentDescription = null,

                         )
                 }
             }

         )

        },
        floatingActionButton = {
            if(state.trackList.isNotEmpty() && selectedTabIndex != 1) {

                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            lazyListState.animateScrollToItem(0)
                        }


                    },
                    modifier = Modifier
                        .size(56.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        painter = painterResource(R.drawable.arrow_up),
                        contentDescription = "scrolltoUp",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(24.dp)
                    )

                }
            }
        },
        bottomBar = {
            if(state.isPlaying) {
                MiniPlayerRoot(onMiniPlayerClick =onMiniPlayerClick)
            }
        }

    ) { paddingValues ->

        Column(modifier = Modifier
            .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally ,
             verticalArrangement = Arrangement.Center) {

            when{
                state.scanning ->{
                   Column (modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally,
                       verticalArrangement = Arrangement.Center) {
                        Loader(isScannigInMainScreen = state.scanning)
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(text = stringResource(R.string.scanning_your_device_for_music),
                            style = MaterialTheme.typography.bodyMediumRegular,
                            color = MaterialTheme.colorScheme.secondary)
                    }

                }

                state.trackList.isEmpty() -> {
                    EmptyScreen(
                        onScanAgain = {
                            onAction(VibePlayerAction.onScanAgain)
                        }
                    )

                }

                else ->{
                    PrimaryTabRow(
                        selectedTabIndex = selectedTabIndex,
                        modifier = Modifier.fillMaxWidth(),

                    )  {
                        tabItems.forEachIndexed {index,item ->
                            Tab(
                                selected = selectedTabIndex == index,
                                onClick = {
                                    selectedTabIndex = index

                                },
                                text = {
                                    Text(text = item.title,
                                        style = MaterialTheme.typography.bodyMediumRegular,)
                                },
                                selectedContentColor = MaterialTheme.colorScheme.onPrimary,
                                unselectedContentColor = MaterialTheme.colorScheme.secondary,

                                )


                        }


                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ){index ->

                        when(index){
                            0 ->   AudioList(audioList = state.trackList,
                                state = lazyListState,
                                onTrackClick = NavigateWithTrackId,
                                onPlayClick = {
                                    onAction(VibePlayerAction.onPlayClick)
                                    onPlayClick.invoke()
                                },
                                onShuffleClick = {
                                    onAction(VibePlayerAction.shuffleClick)
                                    onShuffleClick.invoke()
                                }
                            )

                            1 -> PlaylistRoot(
                                onCreateClick = onCreateClick,
                                onNavigateToPlayer = onNavigatetoPlayer,
                                onNavigateToPlaylistplayBack ={id,empty ->
                                    if(empty){
                                       OnNavigateToPlaylistDetail(id)
                                    }else{
                                        OnNavigateToPlaylistDetail(id)
                                        onAction(VibePlayerAction.onUpdatingPlayingState)
                                    }
                                }
                            )


                        }

                    }



                }
            }


        }

    }



}


