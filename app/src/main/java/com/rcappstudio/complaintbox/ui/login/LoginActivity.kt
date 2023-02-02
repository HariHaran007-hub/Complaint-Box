package com.rcappstudio.complaintbox.ui.login

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.rcappstudio.complaintbox.databinding.ActivityLoginBinding
import com.rcappstudio.complaintbox.model.AdminKey
import com.rcappstudio.complaintbox.model.KeyData
import com.rcappstudio.complaintbox.ui.admin.AdminActivity
import com.rcappstudio.complaintbox.ui.staff.StaffActivity
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

    private val alert: AlertDialog.Builder by lazy {
        AlertDialog.Builder(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar!!.hide()
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
        binding.forgetPassword.setOnClickListener {
            showAlertDialog()
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
            adminCheck()
        } else {
            //Student validation
            studentCheck()
        }
    }

    private fun studentCheck(){
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
                    database.getReference("AdminKey/${FirebaseAuth.getInstance().uid}")
                        .get()
                        .addOnSuccessListener {
                            if(it.exists()){
                                Toast.makeText(this, "Don't log in with staff account", Toast.LENGTH_LONG)
                                    .show()
                                FirebaseAuth.getInstance().signOut()
                            } else {
                                startActivity(Intent(this, UserActivity::class.java))
                                finish()
                            }
                        }
                }
            }
    }

    private fun adminCheck(){
        database.getReference("AdminKey/${FirebaseAuth.getInstance().uid}")
            .get()
            .addOnSuccessListener {
                if(it.exists()){
                    val admin = it.getValue(AdminKey::class.java)
                    Toast.makeText(this, "Admin login successful", Toast.LENGTH_LONG)
                        .show()
                    sharedPreferences.edit()
                        .putBoolean("isAdmin", true)
                        .putString("department", admin!!.department.toString())
                        .apply()

                    startActivity(Intent(this , AdminActivity::class.java))
                    finish()
                } else {
                    staffCheck()
                }
            }
    }

    private fun staffCheck(){
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
                    startActivity(Intent(this , StaffActivity::class.java))
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
    }

    private fun showAlertDialog(){
        val edittext = EditText(this)
        alert.setMessage("Enter Your Message")
        alert.setTitle("Enter Your Title")

        alert.setView(edittext)

        alert.setPositiveButton("Yes Option") { dialog, whichButton ->
            if(edittext.text.toString().isNotEmpty()){
                resetPassword(edittext.text.toString())
            }
        }

        alert.setNegativeButton("No Option") { dialog, whichButton ->

        }
        
        alert.show()

    }


    private fun resetPassword(email: String){
        FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener {
            if(it.isSuccessful){
                Toast.makeText(this,"Reset email sent successfully", Toast.LENGTH_LONG).show()
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
                    startActivity(Intent(this, UserActivity::class.java))
                    finish()
                    Log.d("TAGData", "createUserWithEmail:success")
                } else {
                    Log.d("TAGData", "createUserWithEmail:failure")
                    Toast.makeText(this, "Unable to create account", Toast.LENGTH_LONG).show()
                }
            }
    }
}