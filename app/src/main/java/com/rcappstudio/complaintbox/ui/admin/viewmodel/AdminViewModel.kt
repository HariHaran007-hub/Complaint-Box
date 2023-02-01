package com.rcappstudio.complaintbox.ui.admin.viewmodel

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import com.rcappstudio.complaintbox.model.Complaint
import com.rcappstudio.complaintbox.ui.FirebaseData

class AdminViewModel(
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

    fun getApprovedData(): MutableLiveData<List<Complaint?>> {
        compList.postValue(mutableListOf())
        FirebaseData.liveData.observeForever {
            if (it.isNotEmpty()) {
                compList.postValue(
                    it.filter { comp ->
                        comp.solved == 3
                                && comp.department?.trim()?.split(",")?.contains(dept)!!
                    }
                )
            }
        }
        return compList
    }

    fun getSolvedData(): MutableLiveData<List<Complaint?>> {
        compList.postValue(mutableListOf())
        FirebaseData.liveData.observeForever {
            if (it.isNotEmpty()) {
                compList.postValue(
                    it.filter { comp ->
                        comp.department?.trim()?.split(",")?.contains(dept)!!
                                && comp.solved == 2
                    }
                )
            }
        }
        return compList
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

    fun getAssigned(): MutableLiveData<List<Complaint?>> {
        compList.postValue(mutableListOf())
        FirebaseData.liveData.observeForever {
            if (it.isNotEmpty()) {
                compList.postValue(
                    it.filter { comp ->
                        comp.solved == 1
                                && comp.department?.trim()?.split(",")?.contains(dept)!!
                    }
                )
            }
        }
        return compList
    }

    fun setNotificationToken(department: String){
        FirebaseMessaging.getInstance().token.addOnSuccessListener {
          database.getReference("Staff/$department/admin/${FirebaseAuth.getInstance().uid}/token")
                .setValue(it)
        }
    }
}