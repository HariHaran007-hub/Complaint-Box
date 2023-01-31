package com.rcappstudio.complaintbox.ui.user.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.firebase.database.FirebaseDatabase
import com.rcappstudio.complaintbox.R

class UserViewModel(
    private val app : Application,
    private val database: FirebaseDatabase
) :AndroidViewModel(app) {

    private lateinit var navController: NavController

    fun setNavController(navController : NavController){
        this.navController = navController
    }

     fun switchToFragment(destinationId: Int) {
        if (isFragmentInBackStack(destinationId)) {
            navController.popBackStack(destinationId, false)
        } else {
            navController.navigate(destinationId)
        }
    }

     private fun isFragmentInBackStack(destinationId: Int) =
        try {
            navController.getBackStackEntry(destinationId)
            true
        } catch (e: Exception) {
            false
        }


}