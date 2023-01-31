package com.rcappstudio.complaintbox.ui.user

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.rcappstudio.complaintbox.R
import com.rcappstudio.complaintbox.databinding.ActivityUserBinding
import com.rcappstudio.complaintbox.ui.user.viewmodel.UserViewModel
import com.rcappstudio.complaintbox.ui.user.viewmodel.UserViewModelFactory
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class UserActivity : AppCompatActivity() {

    @Inject
    lateinit var factory: UserViewModelFactory

    private lateinit var viewModel: UserViewModel

    lateinit var binding: ActivityUserBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserBinding.inflate(layoutInflater)
        supportActionBar!!.hide()
        setContentView(binding.root)
        viewModel = ViewModelProvider(this, factory)[UserViewModel::class.java]
        viewModel.setNavController(getNavController())
        initBottomNavigation()
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
    }


    private fun getNavController(): NavController {
        return (supportFragmentManager.findFragmentById(R.id.userFragmentContainerView) as NavHostFragment).navController
    }


    override fun onBackPressed() {
        super.onBackPressed()
        binding.userBottomNavigationView.selectedItemId = R.id.user_all
    }
}