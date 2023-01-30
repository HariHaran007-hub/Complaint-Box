package com.rcappstudio.complaintbox.ui.staff.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.database.FirebaseDatabase


class StaffViewModelFactory(
private val app : Application,
private val database: FirebaseDatabase
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return StaffViewModel(app, database) as T
    }
}