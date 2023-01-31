package com.rcappstudio.complaintbox.ui

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.rcappstudio.complaintbox.R
import com.rcappstudio.complaintbox.databinding.ActivityLoginBinding
import com.rcappstudio.complaintbox.model.KeyData
import com.rcappstudio.complaintbox.ui.user.UserActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    @Inject
    lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        clickListener()
    }

    private fun clickListener(){
        binding.joinNow.setOnClickListener {
            joinNow()
        }

        binding.loginNow.setOnClickListener {
            loginNow()
        }

        binding.siginIn.setOnClickListener {
            if(binding.emailId.text.toString() != "" && binding.password.text.toString() != ""){
                //Login has seperate validation
                login()
            }
        }

        binding.reisterButton.setOnClickListener {
            if(binding.registerEmailId.text.toString().trim() != "" && binding.registerPassword.text.toString().trim() != ""){
                //Register has seperat validation
                registerNow()
            }
        }
    }

    private fun login(){
        FirebaseAuth.getInstance()
            .signInWithEmailAndPassword(binding.emailId.text.toString(), binding.password.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    verifyUser()
                    Log.d("TAGData", "createUserWithEmail:success")
                } else {
                    Log.d("TAGData", "createUserWithEmail:failure")
                    Toast.makeText(this, "Failure", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun verifyUser(){
        if(binding.checkboxSTaff.isChecked){
            //Staff validation
           database
                .getReference("StaffKey/${FirebaseAuth.getInstance().uid}")
                .get()
                .addOnSuccessListener {
                    if(it.exists()){
                        val keyData = it.getValue(KeyData::class.java)
                        sharedPreferences.edit().putBoolean("isStaff", true)
                            .putString("department", keyData!!.department.toString())
                            .apply()
                        //TODO: Make an navigation to StaffActivity.kt
                        startActivity(Intent(this , MainActivity::class.java))
                        finish()
                        Log.d("TAGData", "getData: ${keyData.department}")
                    } else {
                        Toast.makeText(this, "Make sure you are a staff", Toast.LENGTH_LONG)
                            .show()
                        FirebaseAuth.getInstance().signOut()
                    }

                }
                .addOnFailureListener {
                    Toast.makeText(this, "Make sure you are a staff", Toast.LENGTH_LONG)
                        .show()
                    FirebaseAuth.getInstance().signOut()
                }
        } else {
            //Student validation
         database
                .getReference("StaffKey/${FirebaseAuth.getInstance().uid}")
                .get()
                .addOnSuccessListener {
                    if(it.exists()){
                        Toast.makeText(this, "Don't log in with staff account", Toast.LENGTH_LONG)
                            .show()
                        FirebaseAuth.getInstance().signOut()
                    } else {
                        //TODO: Make an navigation to UserActivity .kt
                        startActivity(Intent(this, UserActivity::class.java))
                        finish()
                    }
                }
        }
    }

    private fun joinNow(){
        binding.loginLayout.visibility = View.INVISIBLE
        binding.registerLayout.visibility = View.VISIBLE
    }

    private fun loginNow() {
        binding.loginLayout.visibility = View.VISIBLE
        binding.registerLayout.visibility = View.INVISIBLE
    }

    private fun registerNow(){
        //Registering as student since we create staff in admin panel(Web)
        FirebaseAuth.getInstance()
            .createUserWithEmailAndPassword(binding.registerEmailId.text.toString(), binding.registerPassword.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                    Log.d("TAGData", "createUserWithEmail:success")
                } else {
                    Log.d("TAGData", "createUserWithEmail:failure")
                    Toast.makeText(this, "Unable to create account", Toast.LENGTH_LONG).show()
                }
            }
    }

    override fun onStart() {
        super.onStart()
        if(FirebaseAuth.getInstance().currentUser != null){
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}