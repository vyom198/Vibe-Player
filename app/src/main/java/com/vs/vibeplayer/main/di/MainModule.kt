package com.vs.vibeplayer.main.di

import android.content.Context
import android.content.SharedPreferences
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.vs.vibeplayer.main.data.audio.ContentUriChecker
import com.vs.vibeplayer.main.data.audio.MediaAudioDataSource
import com.vs.vibeplayer.main.data.favourite.FavouritePrefsImpl
import com.vs.vibeplayer.main.data.player.ExoPlayerManager
import com.vs.vibeplayer.main.domain.audio.AudioDataSource
import com.vs.vibeplayer.main.domain.favourite.FavouritePrefs
import com.vs.vibeplayer.main.domain.player.PlayerManager
import com.vs.vibeplayer.main.presentation.VibePlayer.VibePlayerViewModel
import com.vs.vibeplayer.main.presentation.addsongs.AddSongsViewModel
import com.vs.vibeplayer.main.presentation.miniplayer.MiniPlayerViewModel
import com.vs.vibeplayer.main.presentation.permission.PermissionViewModel
import com.vs.vibeplayer.main.presentation.player.PlayerViewModel
import com.vs.vibeplayer.main.presentation.playlist.PlaylistViewModel
import com.vs.vibeplayer.main.presentation.search.SearchViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_fav")
val mainModule = module {
    single<SharedPreferences> {
        androidContext().getSharedPreferences("vibe_prefs", Context.MODE_PRIVATE)
    }
    single<ContentUriChecker>{
        ContentUriChecker(androidContext())
    }
    single<DataStore<Preferences>> {
        androidContext().dataStore
    }
    singleOf(:: FavouritePrefsImpl) bind FavouritePrefs::class
    singleOf(::MediaAudioDataSource) bind AudioDataSource::class
    singleOf(::ExoPlayerManager) bind PlayerManager::class
    viewModelOf(::PermissionViewModel)
    viewModelOf(::VibePlayerViewModel)
    viewModelOf(::PlayerViewModel)
    viewModelOf(::MiniPlayerViewModel)
    viewModelOf(::SearchViewModel)
    viewModelOf(::PlaylistViewModel)
    viewModelOf(::AddSongsViewModel)
}