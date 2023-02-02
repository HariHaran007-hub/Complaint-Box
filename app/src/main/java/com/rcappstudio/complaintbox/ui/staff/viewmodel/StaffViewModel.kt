package com.rcappstudio.complaintbox.ui.staff.viewmodel

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import com.google.firebase.auth.FirebaseAuth
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
    private var compIds: MutableLiveData<List<String?>> = MutableLiveData()
    private var sharedPref: SharedPreferences = app.getSharedPreferences(
        "shared_pref",
        Context.MODE_PRIVATE
    )
    private val dept: String? by lazy { sharedPref.getString("department", "") }
    private lateinit var navController: NavController

    init {
        database.getReference("Staff/$dept/workers/${FirebaseAuth.getInstance().uid}/assignments")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val list = mutableListOf<String?>()
                        for (c in snapshot.children) {
//                        Log.d("TAGData", "compIds: ${c.key}")
                            list.add(c.key)
                        }
                        compIds.postValue(list)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
//                    TODO("Not yet implemented")
                }

            })
    }

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

    fun switchToViewFragment(actions: NavDirections, destinationId: Int) {
        if (isFragmentInBackStack(destinationId)) {
            navController.popBackStack(destinationId, false)
        } else {
            navController.navigate(actions)
        }
    }

    fun getPendingData(): MutableLiveData<List<Complaint?>> {
        compList.postValue(mutableListOf())
        compIds.observeForever { cIds ->
            if (cIds.isNotEmpty()) {
                FirebaseData.liveData.observeForever { cList ->
                    if (cList.isNotEmpty()) {
                        compList.postValue(
                            cList.filter { comp ->
                                comp.solved == 1
                                        && comp.department?.trim()?.split(",")?.contains(dept)!!
                                        && cIds.contains(comp.complaintId)
                            }
                        )
                    }
                }
            }
        }
        return compList
    }

    fun getSolvedData(): MutableLiveData<List<Complaint?>> {
        compList.postValue(mutableListOf())
        compIds.observeForever { cIds ->
            if (cIds.isNotEmpty()) {
                FirebaseData.liveData.observeForever { cList ->
                    if (cList.isNotEmpty()) {
                        compList.postValue(
                            cList.filter { comp ->
                                comp.solved!! >= 2
                                        && comp.department?.trim()?.split(",")?.contains(dept)!!
                                        && cIds.contains(comp.complaintId)
                            }
                        )
                    }
                }
            }
        }
        return compList
    }

}