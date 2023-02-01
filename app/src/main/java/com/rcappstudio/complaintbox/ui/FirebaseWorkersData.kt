package com.rcappstudio.complaintbox.ui

import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.rcappstudio.complaintbox.model.WorkerData

class FirebaseWorkersData(
    val sharedPreferences: SharedPreferences
) {

    companion object {
        val liveData: MutableLiveData<HashMap<String, WorkerData>> = MutableLiveData()
    }

    fun getWorkerData(){

        val department = sharedPreferences.getString("department", "")
        if(department != ""){
            FirebaseDatabase.getInstance().getReference("Staff/$department/workers")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.exists()){
                            val hashMap  = HashMap<String, WorkerData>()
                            for(c in snapshot.children){
                                val worker = c.getValue(WorkerData::class.java)
                                hashMap[c.key!!] = worker!!
                            }
                            liveData.postValue(hashMap)
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {}
                })
        }
    }
}