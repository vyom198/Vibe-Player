package com.vs.vibeplayer.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.session.MediaController
import android.os.Build
import com.google.common.util.concurrent.ListenableFuture
import com.vs.vibeplayer.BuildConfig
import com.vs.vibeplayer.app.di.appModule
import com.vs.vibeplayer.core.database.di.databaseModule
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
                mainModule,
                databaseModule
            )
        }
        createNotificationChannel()
    }



    private fun  createNotificationChannel(){
         if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
             val channel = NotificationChannel(
                 "player",
                 "show_notification",
                 NotificationManager.IMPORTANCE_HIGH

             )
             channel.description = "used for showing foreground notification"
             val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
             notificationManager.createNotificationChannel(channel)
         }


    }
}