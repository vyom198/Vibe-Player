package com.vs.vibeplayer.main.di

import android.content.Context
import android.content.SharedPreferences
import com.vs.vibeplayer.main.data.audio.ContentUriChecker
import com.vs.vibeplayer.main.data.audio.MediaAudioDataSource
import com.vs.vibeplayer.main.data.player.ExoPlayerManager
import com.vs.vibeplayer.main.domain.audio.AudioDataSource
import com.vs.vibeplayer.main.domain.player.PlayerManager
import com.vs.vibeplayer.main.presentation.VibePlayer.VibePlayerViewModel
import com.vs.vibeplayer.main.presentation.miniplayer.MiniPlayerViewModel
import com.vs.vibeplayer.main.presentation.permission.PermissionViewModel
import com.vs.vibeplayer.main.presentation.player.PlayerViewModel
import com.vs.vibeplayer.main.presentation.search.SearchViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.compose.getKoin
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.parameter.parametersOf
import org.koin.dsl.bind
import org.koin.dsl.module


val mainModule = module {
    single<SharedPreferences> {
        androidContext().getSharedPreferences("vibe_prefs", Context.MODE_PRIVATE)
    }
    single<ContentUriChecker>{
        ContentUriChecker(androidContext())
    }

    singleOf(::MediaAudioDataSource) bind AudioDataSource::class
    singleOf(::ExoPlayerManager) bind PlayerManager::class
    viewModelOf(::PermissionViewModel)
    viewModelOf(::VibePlayerViewModel)
    viewModelOf(::PlayerViewModel)
    viewModelOf(::MiniPlayerViewModel)
    viewModelOf(::SearchViewModel)

}