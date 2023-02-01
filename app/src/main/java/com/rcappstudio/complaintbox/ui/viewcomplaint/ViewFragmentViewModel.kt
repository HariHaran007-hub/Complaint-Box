package com.rcappstudio.complaintbox.ui.viewcomplaint

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.FirebaseDatabase
import com.karumi.dexter.PermissionToken
import com.rcappstudio.complaintbox.model.ComplaintId
import com.rcappstudio.complaintbox.notification.NotificationAPI
import com.rcappstudio.complaintbox.notification.NotificationData
import com.rcappstudio.complaintbox.notification.PushNotification
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ViewFragmentViewModel(
    private val app: Application,
    private val database: FirebaseDatabase
) : AndroidViewModel(app){


    val statusChangeSuccessfulLiveData = MutableLiveData(false)


    fun assignTaskToWorker(department: String,uid: String, token: String, notificationAPI: NotificationAPI,complaintId: String){
        database.getReference("Staff/$department/workers/$uid/assignments/$complaintId")
            .setValue(ComplaintId(complaintId))
            .addOnSuccessListener {
                changeComplaintStatus(complaintId)
                setNotificationToken(token,notificationAPI)
            }
    }

    private fun changeComplaintStatus(complaintId: String){
        database.getReference("Complaints/$complaintId/solved")
            .setValue(1)
            .addOnSuccessListener {
                statusChangeSuccessfulLiveData.postValue(true)
            }

    }

    private fun setNotificationToken(token: String, notificationAPI: NotificationAPI) {
        PushNotification(
            NotificationData("Got an problem", "Some one has posted an problem click to view."),
            token
        ).also {
            sendNotification(it,notificationAPI)
        }
    }

    private fun sendNotification(notification: PushNotification, notificationAPI: NotificationAPI) =
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = notificationAPI.postNotification(notification)
                if (response.isSuccessful) {
                    Toast.makeText(
                        app,
                        "Notification has been sent successfully",
                        Toast.LENGTH_LONG
                    ).show()

                } else {
                    Toast.makeText(app, "Error occured", Toast.LENGTH_LONG).show()

                }
            } catch (e: Exception) {
                Log.e("TAGData", e.toString())
            }
        }


}