package com.rcappstudio.complaintbox.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.rcappstudio.complaintbox.model.Complaint

class FirebaseData {

    companion object {
        val liveData: MutableLiveData<List<Complaint>> = MutableLiveData()
    }
    fun providesListData() {
        FirebaseDatabase.getInstance().getReference("Complaints")
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = mutableListOf<Complaint>()
                    for (c in snapshot.children) {
                        val comp = c.getValue(Complaint::class.java)
                        list.add(comp!!)
                    }
                    Log.d("TAGData", "onDataChange: ${list.size}")
                    liveData.postValue(list)
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
    }
}