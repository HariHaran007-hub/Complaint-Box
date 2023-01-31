package com.rcappstudio.complaintbox.di

import android.app.Application
import com.google.android.exoplayer2.ExoPlayer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class ExoPlayerModule {

    @Provides
    fun provideExoplayerInstance(app: Application): ExoPlayer {
        return ExoPlayer.Builder(app.applicationContext).build()
    }
}