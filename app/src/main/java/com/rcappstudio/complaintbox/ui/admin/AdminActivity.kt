package com.rcappstudio.complaintbox.ui.admin

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import com.rcappstudio.complaintbox.R
import com.rcappstudio.complaintbox.databinding.ActivityAdminBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AdminActivity : AppCompatActivity() {
    private lateinit var binding : ActivityAdminBinding

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    private val department by lazy {
        sharedPreferences.getString("department","")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setNotificationToken()
    }

    private fun setNotificationToken(){
        FirebaseMessaging.getInstance().token.addOnSuccessListener {
                FirebaseDatabase.getInstance().getReference("Staff/$department/admin/${FirebaseAuth.getInstance().uid}/token")
                    .setValue(it)
        }
    }
}