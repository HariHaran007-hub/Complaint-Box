package com.rcappstudio.complaintbox.ui.staff.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.google.firebase.database.FirebaseDatabase

class StaffViewModel(
    private val app: Application,
    private val database: FirebaseDatabase
) : AndroidViewModel(app){

}