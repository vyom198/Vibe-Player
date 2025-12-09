package com.vs.vibeplayer.app.di

import com.vs.vibeplayer.app.VibePlayerApp
import kotlinx.coroutines.CoroutineScope
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.Module
import org.koin.dsl.module

val appModule = module{
    single<CoroutineScope> {
        (androidApplication() as VibePlayerApp).applicationScope
    }
}