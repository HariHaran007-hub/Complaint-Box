package com.rcappstudio.complaintbox.ui.admin.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.database.FirebaseDatabase
import com.rcappstudio.complaintbox.ui.staff.viewmodel.StaffViewModel

class AdminViewModelFactory (
    private val app : Application,
    private val database: FirebaseDatabase
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AdminViewModel(app, database) as T
    }
}