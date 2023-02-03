package com.rcappstudio.complaintbox.ui.user

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import com.google.firebase.auth.FirebaseAuth
import com.rcappstudio.complaintbox.R
import com.rcappstudio.complaintbox.databinding.ActivityUserBinding
import com.rcappstudio.complaintbox.ui.FirebaseData
import com.rcappstudio.complaintbox.ui.login.LoginActivity
import com.rcappstudio.complaintbox.ui.user.viewmodel.UserViewModel
import com.rcappstudio.complaintbox.ui.user.viewmodel.UserViewModelFactory
import com.rcappstudio.complaintbox.utils.accountConfirmantionDialog
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class UserActivity : AppCompatActivity() {

    @Inject
    lateinit var factory: UserViewModelFactory
    private lateinit var viewModel: UserViewModel
    @Inject
    lateinit var sharedPreferences: SharedPreferences
    @Inject
    lateinit var firebaseData: FirebaseData
    lateinit var binding: ActivityUserBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserBinding.inflate(layoutInflater)
        supportActionBar!!.hide()
        setContentView(binding.root)
        viewModel = ViewModelProvider(this, factory)[UserViewModel::class.java]
        viewModel.setNavController(getNavController())
        initBottomNavigation()
        toolBarClickListener()
    }

    private fun initBottomNavigation() {
        binding.userBottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.user_all -> {
                    viewModel.switchToFragment(R.id.userFragment1)
                }
                R.id.user_pending -> {
                    viewModel.switchToFragment(R.id.userFragment2)
                }
                R.id.user_solved -> {
                    viewModel.switchToFragment(R.id.userFragment3)
                }
            }
            true
        }

        getNavController().addOnDestinationChangedListener { _, destination, _ ->
            if(destination.id == R.id.viewFragment
                || destination.id == R.id.mediaViewFragment
                || destination.id == R.id.addComplaintFragment){
                binding.userBottomNavigationView.visibility = View.GONE
            } else if (destination.id == R.id.userFragment3){
                binding.userBottomNavigationView.visibility = View.VISIBLE
                binding.userBottomNavigationView.selectedItemId = R.id.user_solved
            }else if (destination.id == R.id.userFragment2){
                binding.userBottomNavigationView.visibility = View.VISIBLE
                binding.userBottomNavigationView.selectedItemId = R.id.user_pending
            } else {
                binding.userBottomNavigationView.visibility = View.VISIBLE
                binding.userBottomNavigationView.selectedItemId = R.id.user_all
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
        return (supportFragmentManager.findFragmentById(R.id.userFragmentContainerView) as NavHostFragment).navController
    }

}