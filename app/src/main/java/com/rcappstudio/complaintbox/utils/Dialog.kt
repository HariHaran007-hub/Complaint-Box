package com.rcappstudio.complaintbox.utils

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.icu.text.SimpleDateFormat
import android.view.LayoutInflater
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.rcappstudio.complaintbox.R
import com.rcappstudio.complaintbox.ui.login.LoginActivity
import java.util.*

private lateinit var dialog: AlertDialog

fun initLoadingDialog(activity: Activity) {
    val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
    val inflater: LayoutInflater = activity.layoutInflater
    builder.setView(inflater.inflate(R.layout.loading_dialog, null))
    builder.setCancelable(false)
    dialog = builder.create()
}

fun showDialog() {
    dialog.show()
}

fun dismissDialog() {
    dialog.dismiss()
}

fun getDateTime(s: Long): String? {
    return try {
        val sdf = SimpleDateFormat("dd/MM/yyyy - HH:mm")
        val netDate = Date(s)
        sdf.format(netDate)
    } catch (e: Exception) {
        e.toString()
    }
}