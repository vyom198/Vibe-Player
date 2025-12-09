package com.vs.vibeplayer.app

import android.app.Application
import com.vs.vibeplayer.BuildConfig
import com.vs.vibeplayer.app.di.appModule
import com.vs.vibeplayer.main.di.mainModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber

class VibePlayerApp: Application() {

    val applicationScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    override fun onCreate() {
        super.onCreate()
        if(BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        startKoin {
            androidContext(this@VibePlayerApp)
            modules(
                appModule,
                mainModule
            )
        }
    }
}