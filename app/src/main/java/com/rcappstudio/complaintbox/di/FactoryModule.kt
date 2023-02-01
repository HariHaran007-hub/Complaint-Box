package com.rcappstudio.complaintbox.di

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.google.firebase.database.FirebaseDatabase
import com.rcappstudio.complaintbox.ui.admin.viewmodel.AdminViewModelFactory
import com.rcappstudio.complaintbox.ui.staff.viewmodel.StaffViewModelFactory
import com.rcappstudio.complaintbox.ui.user.viewmodel.UserViewModelFactory
import com.rcappstudio.complaintbox.ui.viewcomplaint.ViewViewModelFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class FactoryModule {

    @Singleton
    @Provides
    fun provideUserViewModelFactory(app : Application, database: FirebaseDatabase)  : UserViewModelFactory {
        return UserViewModelFactory(app, database)
    }

    @Singleton
    @Provides
    fun provideStaffViewModelFactory(app: Application, database: FirebaseDatabase) : StaffViewModelFactory{
        return StaffViewModelFactory(app, database)
    }

    @Singleton
    @Provides
    fun provideAdminViewModelFactory(app: Application, database: FirebaseDatabase) : AdminViewModelFactory{
        return AdminViewModelFactory(app, database)
    }

    @Singleton
    @Provides
    fun providesViewViewModelFactory(app: Application, database: FirebaseDatabase) : ViewViewModelFactory{
        return ViewViewModelFactory(app, database)
    }

    @Singleton
    @Provides
    fun providesSharedPref(app: Application) : SharedPreferences{
        return app.getSharedPreferences("shared_pref", MODE_PRIVATE)
    }
}