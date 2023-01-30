package com.rcappstudio.complaintbox.ui.user.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.google.firebase.database.FirebaseDatabase

class UserViewModel(
    private val app : Application,
    private val database: FirebaseDatabase
) :AndroidViewModel(app) {

}