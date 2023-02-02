package com.rcappstudio.complaintbox.ui.login

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.rcappstudio.complaintbox.R
import com.rcappstudio.complaintbox.ui.admin.AdminActivity
import com.rcappstudio.complaintbox.ui.staff.StaffActivity

import com.rcappstudio.complaintbox.ui.user.UserActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        supportActionBar!!.hide()


        Handler().postDelayed({
            val isStaff = sharedPreferences.getBoolean("isStaff", false)
            val isAdmin = sharedPreferences.getBoolean("isAdmin", false)

            Log.d("TAGData", "onCreate: isStaff-> $isStaff , isAdmin->$isAdmin")

            if(FirebaseAuth.getInstance().currentUser != null){
                //TODO: Based on category redirect to their respective pages
                Log.d("TAGData", "onCreate: $isStaff, $isAdmin")
                if(isStaff){
                    startActivity(Intent(this , StaffActivity::class.java))
                    finish()
                } else if(isAdmin){
                    startActivity(Intent(this , AdminActivity::class.java))
                    finish()
                } else {
                    startActivity(Intent(this , UserActivity::class.java))
                    finish()
                }
            } else {
                startActivity(Intent(this , LoginActivity::class.java))
                finish()
            }
        }, 3000)
    }
}