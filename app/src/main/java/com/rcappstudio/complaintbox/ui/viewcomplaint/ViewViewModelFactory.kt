package com.rcappstudio.complaintbox.ui.viewcomplaint

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.database.FirebaseDatabase
import com.rcappstudio.complaintbox.ui.admin.viewmodel.AdminViewModel

class ViewViewModelFactory(
    private val app : Application,
    private val database: FirebaseDatabase
): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ViewFragmentViewModel(app, database) as T
    }
}