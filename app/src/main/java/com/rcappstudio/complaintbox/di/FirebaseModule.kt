package com.rcappstudio.complaintbox.di

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.FirebaseDatabase
import com.rcappstudio.complaintbox.model.Complaint
import com.rcappstudio.complaintbox.ui.FirebaseData
import com.rcappstudio.complaintbox.ui.FirebaseWorkersData
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class FirebaseModule {

    @Singleton
    @Provides
    fun provideFirebaseDatabaseInstance(): FirebaseDatabase {
        return FirebaseDatabase.getInstance()
    }

    @Singleton
    @Provides
    fun providesFirebaseData(): FirebaseData{
        val fb =  FirebaseData()
        fb.providesListData()
        return fb
    }

    @Singleton
    @Provides
    fun providesFirebaseWorkersData(sharedPreferences: SharedPreferences): FirebaseWorkersData{
        val fb = FirebaseWorkersData(sharedPreferences)
        fb.getWorkerData()
        return fb
    }
}