package com.rcappstudio.complaintbox.ui.staff

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import com.rcappstudio.complaintbox.R
import com.rcappstudio.complaintbox.databinding.ActivityStaffBinding
import com.rcappstudio.complaintbox.ui.FirebaseData
import com.rcappstudio.complaintbox.ui.login.LoginActivity
import com.rcappstudio.complaintbox.ui.staff.viewmodel.StaffViewModel
import com.rcappstudio.complaintbox.ui.staff.viewmodel.StaffViewModelFactory
import com.rcappstudio.complaintbox.utils.accountConfirmantionDialog
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class StaffActivity : AppCompatActivity() {
    
    private lateinit var binding : ActivityStaffBinding
    @Inject
    lateinit var factory: StaffViewModelFactory
    private lateinit var viewModel: StaffViewModel
    @Inject
    lateinit var sharedPreferences: SharedPreferences
    @Inject
    lateinit var firebaseData: FirebaseData

    private val department by lazy {
        sharedPreferences.getString("department","")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStaffBinding.inflate(layoutInflater)
        supportActionBar!!.hide()
        setContentView(binding.root)
        viewModel = ViewModelProvider(this, factory)[StaffViewModel::class.java]
        viewModel.setNavController(getNavController())
        initBottomNavigation()
        setNotificationToken()
        toolBarClickListener()
    }

    private fun setNotificationToken(){
        FirebaseMessaging.getInstance().token.addOnSuccessListener {
            FirebaseDatabase.getInstance().getReference("Staff/$department/workers/${FirebaseAuth.getInstance().uid}/token")
                .setValue(it)
        }
    }

    private fun initBottomNavigation() {
        binding.staffBottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.staff_pending -> {
                    viewModel.switchToFragment(R.id.staffFragment1)
                }
                R.id.staff_solved -> {
                    viewModel.switchToFragment(R.id.staffFragment2)
                }
            }
            true
        }
        getNavController().addOnDestinationChangedListener{_, dest, _->
            if(dest.id == R.id.viewFragment || dest.id == R.id.mediaViewFragment) {
                binding.staffBottomNavigationView.visibility = View.GONE
            } else if(dest.id == R.id.staffFragment1) {
                binding.staffBottomNavigationView.visibility = View.VISIBLE
                binding.staffBottomNavigationView.selectedItemId = R.id.staff_pending
            } else if(dest.id == R.id.staffFragment2) {
                binding.staffBottomNavigationView.visibility = View.VISIBLE
                binding.staffBottomNavigationView.selectedItemId = R.id.staff_solved
            } else{
                binding.staffBottomNavigationView.visibility = View.VISIBLE
            }
        }
    }

    private fun toolBarClickListener(){
        binding.toolBar.menu.getItem(0).isVisible = false
        binding.toolBar.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.logout -> {
                    accountConfirmantionDialog(this, "logout", sharedPreferences).show()
                }
                R.id.delete_acc -> {
                    accountConfirmantionDialog(this, "delete", sharedPreferences).show()
                }


            }
            true
        }
    }
    private fun getNavController(): NavController {
        return (supportFragmentManager.findFragmentById(R.id.staffFragmentContainerView) as NavHostFragment).navController
    }

}