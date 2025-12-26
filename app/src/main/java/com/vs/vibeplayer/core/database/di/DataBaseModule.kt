package com.vs.vibeplayer.core.database.di

import androidx.room.Room
import com.vs.vibeplayer.core.database.TrackDataBase
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val databaseModule = module {
    single<TrackDataBase> {
        Room.databaseBuilder(
            androidApplication(),
            TrackDataBase::class.java,
            "tracks.db",
        ).build()
    }
    single {
        get<TrackDataBase>().trackdao
    }
}