package com.rcappstudio.complaintbox.ui.user

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.navigation.NavigationBarView
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

    private lateinit var binding: ActivityUserBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this, factory)[UserViewModel::class.java]
        initBottomNavigation()
    }

    private fun initBottomNavigation(){
        binding.userBottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){

                R.id.all->{
                    switchToFragment(R.id.userFragment1)
                }
                R.id.pending->{
                    switchToFragment(R.id.userFragment2)
                }
                R.id.completed->{
                    switchToFragment(R.id.userFragment3)
                }
            }
            true
        }
    }


    private fun getNavController(): NavController {
        return (supportFragmentManager.findFragmentById(R.id.userFragmentContainerView) as NavHostFragment).navController
    }

    private fun switchToFragment(destinationId: Int) {
        if (isFragmentInBackStack(destinationId)) {
            getNavController().popBackStack(destinationId, false)
        } else {
            getNavController().navigate(destinationId)
        }
    }

    private fun isFragmentInBackStack(destinationId: Int) =
        try {
            getNavController().getBackStackEntry(destinationId)
            true
        } catch (e: Exception) {
            false
        }

    override fun onBackPressed() {
        super.onBackPressed()
        binding.userBottomNavigationView.selectedItemId = R.id.all
    }
}