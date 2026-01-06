package com.vs.vibeplayer.core.database.di

import androidx.room.Room
import com.vs.vibeplayer.core.database.VibePlayerDataBase
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val databaseModule = module {
    single<VibePlayerDataBase> {
        Room.databaseBuilder(
            androidApplication(),
            VibePlayerDataBase::class.java,
            "tracks.db",
        ).fallbackToDestructiveMigration(true).build()
    }
    single {
        get<VibePlayerDataBase>().trackdao
    }

    single {
        get<VibePlayerDataBase>().playlistdao
    }
}