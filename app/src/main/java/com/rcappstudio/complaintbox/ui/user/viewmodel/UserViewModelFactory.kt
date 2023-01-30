package com.rcappstudio.complaintbox.ui.user.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.database.FirebaseDatabase

class UserViewModelFactory(
    private val app : Application,
    private val database: FirebaseDatabase
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return UserViewModel(app, database) as T
    }
}