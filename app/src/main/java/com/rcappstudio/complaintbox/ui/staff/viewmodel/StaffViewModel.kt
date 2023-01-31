package com.rcappstudio.complaintbox.ui.staff.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.rcappstudio.complaintbox.model.Complaint

class StaffViewModel(
    private val app: Application,
    private val database: FirebaseDatabase
) : AndroidViewModel(app){

    private var compList: MutableLiveData<List<Complaint?>> = MutableLiveData()

    fun getPendingData(): MutableLiveData<List<Complaint?>> {
        compList.postValue(mutableListOf())
        database.getReference("Complaints")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = mutableListOf<Complaint?>()
                    if (snapshot.exists()) {
                        for (c in snapshot.children) {
//                            Log.d("TAGinController", "onDataChange: ${c.key}")
                            val comp = c.getValue(Complaint::class.java)
                            if (!comp?.solved!!) list.add(comp)
                        }
                        compList.postValue(list)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
//                    TODO("Not yet implemented")
                }
            })
        return compList
    }

}