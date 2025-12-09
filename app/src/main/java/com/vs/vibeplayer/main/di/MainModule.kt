package com.vs.vibeplayer.main.di

import android.content.Context
import android.content.SharedPreferences
import com.vs.vibeplayer.main.presentation.VibePlayer.VibePlayerViewModel
import com.vs.vibeplayer.main.presentation.permission.PermissionViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.compose.getKoin
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module


val mainModule = module {
    single<SharedPreferences> {
        androidContext().getSharedPreferences("vibe_prefs", Context.MODE_PRIVATE)
    }
    viewModelOf(::PermissionViewModel)
    viewModelOf(::VibePlayerViewModel)
}