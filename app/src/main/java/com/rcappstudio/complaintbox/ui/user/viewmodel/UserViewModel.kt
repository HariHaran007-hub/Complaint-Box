package com.rcappstudio.complaintbox.ui.user.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.rcappstudio.complaintbox.model.Complaint
import com.rcappstudio.complaintbox.ui.FirebaseData

class UserViewModel(
    private val app: Application,
    private val database: FirebaseDatabase
) : AndroidViewModel(app) {

    private lateinit var navController: NavController
    private var compList: MutableLiveData<List<Complaint?>> = MutableLiveData()

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

    fun getAllData(): LiveData<List<Complaint?>> {
        compList.postValue(mutableListOf())
        FirebaseData.liveData.observeForever {
            compList.postValue(it)
        }

        /*database.getReference("Complaints")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = mutableListOf<Complaint?>()
                    if (snapshot.exists()) {
                        for (c in snapshot.children) {
                            list.add(c.getValue(Complaint::class.java))
                        }
                        compList.postValue(list)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
//                    TODO("Not yet implemented")
                }
            })*/
        return compList
    }

    fun getAssignedData(): LiveData<List<Complaint?>> {
        compList.postValue(mutableListOf())
        FirebaseData.liveData.observeForever {
            compList.postValue(it.filter {
                it.solved == 1 || it.solved==2
            })
        }
        return compList
    }

    fun getSolvedData(): LiveData<List<Complaint?>> {
        compList.postValue(mutableListOf())
        FirebaseData.liveData.observeForever {
            compList.postValue(it.filter {
                it.solved == 3
            })
        }
        return compList
    }



}