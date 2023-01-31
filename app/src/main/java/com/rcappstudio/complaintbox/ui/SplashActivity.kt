package com.rcappstudio.complaintbox.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.google.firebase.auth.FirebaseAuth
import com.rcappstudio.complaintbox.R
import com.rcappstudio.complaintbox.notification.NotificationAPI

import com.rcappstudio.complaintbox.ui.user.UserActivity
import javax.inject.Inject

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        supportActionBar!!.hide()
        Handler().postDelayed({
            if(FirebaseAuth.getInstance().currentUser != null){
                //TODO: Based on category redirect to their respective pages
                startActivity(Intent(this , UserActivity::class.java))
                finish()
            } else {

                startActivity(Intent(this , LoginActivity::class.java))
                finish()
            }
        }, 3000)
    }
}