package com.rcappstudio.complaintbox.utils

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.rcappstudio.complaintbox.ui.login.LoginActivity

class AccountUtils {

    companion object {

        fun userAccountConfirmationDialog(
            activity: Activity,
            mode: String,
            sharedPreferences: SharedPreferences,
        ): MaterialAlertDialogBuilder {
            return MaterialAlertDialogBuilder(activity)
                .setTitle("Confirmation")
                .setMessage("Are you sure want to $mode")
                .setPositiveButton("Ok") { dialog, _ ->
                    if (mode == "logout") {
                        FirebaseAuth.getInstance().signOut()
                        sharedPreferences.edit().clear().apply()
                        activity.startActivity(Intent(activity, LoginActivity::class.java))
                        activity.finish()
                    } else if (mode == "delete") {
                        FirebaseAuth.getInstance().currentUser!!.delete()
                        sharedPreferences.edit().clear().apply()
                        activity.startActivity(Intent(activity, LoginActivity::class.java))
                        activity.finish()
                    }
                }
                .setNegativeButton("Cancel", null)
        }

        fun staffAccountConfirmationDialog(
            activity: Activity,
            mode: String,
            sharedPreferences: SharedPreferences,
            database: FirebaseDatabase
        ): MaterialAlertDialogBuilder {

            val department = sharedPreferences.getString("department", "")
            val firebaseAuth = FirebaseAuth.getInstance()
            val uid = firebaseAuth.currentUser?.uid

            return MaterialAlertDialogBuilder(activity)
                .setTitle("Confirmation")
                .setMessage("Are you sure want to $mode")
                .setPositiveButton("Ok") { dialog, _ ->
                    if (mode == "logout") {
                        database.getReference("Staff/$department/workers" +
                                "/$uid/token").setValue("")
                            .addOnSuccessListener {
                                firebaseAuth.signOut()
                                sharedPreferences.edit().clear().apply()
                                activity.startActivity(Intent(activity, LoginActivity::class.java))
                                activity.finish()
                        }
                    } else if (mode == "delete") {
                        database.getReference("Staff/$department/workers" +
                                "/$uid").removeValue()
                            .addOnSuccessListener {
                                database.getReference("StaffKey/$uid").removeValue()
                                    .addOnSuccessListener {
                                        firebaseAuth.currentUser?.delete()
                                        sharedPreferences.edit().clear().apply()
                                        activity.startActivity(Intent(activity, LoginActivity::class.java))
                                        activity.finish()
                                    }
                            }
                    }
                }
                .setNegativeButton("Cancel", null)
        }

        fun adminAccountConfirmationDialog(
            activity: Activity,
            mode: String,
            sharedPreferences: SharedPreferences,
            database: FirebaseDatabase
        ): MaterialAlertDialogBuilder {

            val department = sharedPreferences.getString("department", "")
            val firebaseAuth = FirebaseAuth.getInstance()
            val uid = firebaseAuth.currentUser?.uid

            return MaterialAlertDialogBuilder(activity)
                .setTitle("Confirmation")
                .setMessage("Are you sure want to $mode")
                .setPositiveButton("Ok") { _, _ ->
                    if (mode == "logout") {
                        database.getReference("Staff/$department/admin" +
                                "/$uid/token").setValue("")
                            .addOnSuccessListener {
                                firebaseAuth.signOut()
                                sharedPreferences.edit().clear().apply()
                                activity.startActivity(Intent(activity, LoginActivity::class.java))
                                activity.finish()
                            }
                    } else if (mode == "delete") {
                        database.getReference("Staff/$department/admin" +
                                "/$uid").removeValue()
                            .addOnSuccessListener {
                                database.getReference("AdminKey/$uid").removeValue()
                                    .addOnSuccessListener {
                                        firebaseAuth.currentUser?.delete()
                                        sharedPreferences.edit().clear().apply()
                                        activity.startActivity(Intent(activity, LoginActivity::class.java))
                                        activity.finish()
                                    }
                            }
                    }
                }
                .setNegativeButton("Cancel", null)
        }

    }

}