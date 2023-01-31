package com.rcappstudio.complaintbox.di

import com.rcappstudio.complaintbox.notification.NotificationAPI
import com.rcappstudio.complaintbox.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RetrofitModule {

    @Singleton
    @Provides
    fun provideRetrofit() : Retrofit {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Singleton
    @Provides
    fun provideNotificationsAPIService(retrofit: Retrofit) : NotificationAPI{
        return retrofit.create(NotificationAPI::class.java)
    }
}