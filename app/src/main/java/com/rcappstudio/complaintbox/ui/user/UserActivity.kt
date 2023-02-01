package com.rcappstudio.complaintbox.ui.user

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.rcappstudio.complaintbox.R
import com.rcappstudio.complaintbox.databinding.ActivityUserBinding
import com.rcappstudio.complaintbox.ui.FirebaseData
import com.rcappstudio.complaintbox.ui.user.viewmodel.UserViewModel
import com.rcappstudio.complaintbox.ui.user.viewmodel.UserViewModelFactory
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class UserActivity : AppCompatActivity() {

    @Inject
    lateinit var factory: UserViewModelFactory

    private lateinit var viewModel: UserViewModel

    @Inject
    lateinit var firebaseData: FirebaseData

    private lateinit var binding: ActivityUserBinding

    private val onBackPressedCallback: OnBackPressedCallback by lazy {
        object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if(binding.userBottomNavigationView.selectedItemId == R.id.user_all ){
                finish()
            } else {
                binding.userBottomNavigationView.selectedItemId = R.id.user_all
            }
        }
    }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserBinding.inflate(layoutInflater)
        supportActionBar!!.hide()
        setContentView(binding.root)
        viewModel = ViewModelProvider(this, factory)[UserViewModel::class.java]
        viewModel.setNavController(getNavController())
        initBottomNavigation()
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
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
            Log.d("TAGDataNavigation", "initBottomNavigation: $destination")
            if(destination.id == R.id.addComplaintFragment || destination.id == R.id.viewFragment) {
                binding.userBottomNavigationView.visibility = View.GONE
            } else {
                binding.userBottomNavigationView.visibility  = View.VISIBLE
            }
        }
    }


    private fun getNavController(): NavController {
        return (supportFragmentManager.findFragmentById(R.id.userFragmentContainerView) as NavHostFragment).navController
    }
}