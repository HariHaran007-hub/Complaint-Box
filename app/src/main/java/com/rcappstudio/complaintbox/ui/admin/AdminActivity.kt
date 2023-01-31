package com.rcappstudio.complaintbox.ui.admin

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import com.rcappstudio.complaintbox.R
import com.rcappstudio.complaintbox.databinding.ActivityAdminBinding
import com.rcappstudio.complaintbox.ui.admin.viewmodel.AdminViewModel
import com.rcappstudio.complaintbox.ui.admin.viewmodel.AdminViewModelFactory
import com.rcappstudio.complaintbox.ui.user.viewmodel.UserViewModel
import com.rcappstudio.complaintbox.ui.user.viewmodel.UserViewModelFactory
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AdminActivity : AppCompatActivity() {

    private lateinit var binding : ActivityAdminBinding
    @Inject
    lateinit var factory: AdminViewModelFactory
    private lateinit var viewModel: AdminViewModel
    @Inject
    lateinit var sharedPreferences: SharedPreferences

    private val department by lazy {
        sharedPreferences.getString("department","")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBinding.inflate(layoutInflater)
        supportActionBar!!.hide()
        setContentView(binding.root)
        viewModel = ViewModelProvider(this, factory)[AdminViewModel::class.java]
        viewModel.setNavController(getNavController())
        initBottomNavigation()
        setNotificationToken()
    }

    private fun setNotificationToken(){
        FirebaseMessaging.getInstance().token.addOnSuccessListener {
                FirebaseDatabase.getInstance().getReference("Staff/$department/admin/${FirebaseAuth.getInstance().uid}/token")
                    .setValue(it)
        }
    }

    private fun initBottomNavigation() {
        binding.adminBottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.admin_pending -> {
                    viewModel.switchToFragment(R.id.adminFragment1)
                }
                R.id.admin_assigned -> {
                    viewModel.switchToFragment(R.id.adminFragment2)
                }
                R.id.admin_solved -> {
                    viewModel.switchToFragment(R.id.adminFragment3)
                }
                R.id.admin_approved -> {
                    viewModel.switchToFragment(R.id.adminFragment4)
                }
            }
            true
        }
    }


    private fun getNavController(): NavController {
        return (supportFragmentManager.findFragmentById(R.id.adminFragmentContainerView) as NavHostFragment).navController
    }


    override fun onBackPressed() {
        super.onBackPressed()
        binding.adminBottomNavigationView.selectedItemId = R.id.admin_pending
    }
}