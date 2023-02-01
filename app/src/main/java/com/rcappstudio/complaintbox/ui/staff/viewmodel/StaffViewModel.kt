package com.rcappstudio.complaintbox.ui.staff.viewmodel

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.rcappstudio.complaintbox.model.Complaint
import com.rcappstudio.complaintbox.ui.FirebaseData

class StaffViewModel(
    private val app: Application,
    private val database: FirebaseDatabase
) : AndroidViewModel(app) {

    private var compList: MutableLiveData<List<Complaint?>> = MutableLiveData()
    private var sharedPref: SharedPreferences = app.getSharedPreferences(
        "shared_pref",
        Context.MODE_PRIVATE
    )
    private val dept: String? by lazy { sharedPref.getString("department", "") }
    private lateinit var navController: NavController

    fun setNavController(navController: NavController) {
        this.navController = navController
    }

    fun switchToFragment(destinationId: Int) {
        if (isFragmentInBackStack(destinationId)) {
            navController.popBackStack(destinationId, false)
        } else {
            navController.navigate(destinationId)
        }
    }

    private fun isFragmentInBackStack(destinationId: Int) =
        try {
            navController.getBackStackEntry(destinationId)
            true
        } catch (e: Exception) {
            false
        }

    fun switchToViewFragment(actions: NavDirections, destinationId: Int){
        if (isFragmentInBackStack(destinationId)) {
            navController.popBackStack(destinationId, false)
        } else {
            navController.navigate(actions)
        }
    }

    fun getPendingData(): MutableLiveData<List<Complaint?>> {
        compList.postValue(mutableListOf())
        FirebaseData.liveData.observeForever {
            if (it.isNotEmpty()) {
                compList.postValue(
                    it.filter { comp ->
                        comp.solved == 0
                                && comp.department?.trim()?.split(",")?.contains(dept)!!
                    }
                )
            }
        }
        return compList
    }

}