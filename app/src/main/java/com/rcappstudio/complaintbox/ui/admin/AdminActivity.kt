package com.rcappstudio.complaintbox.ui.admin

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.rcappstudio.complaintbox.R
import com.rcappstudio.complaintbox.databinding.ActivityAdminBinding
import com.rcappstudio.complaintbox.ui.admin.viewmodel.AdminViewModelFactory
import com.rcappstudio.complaintbox.ui.admin.viewmodel.AdminViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AdminActivity : AppCompatActivity() {
    
    private lateinit var binding : ActivityAdminBinding
    @Inject
    lateinit var sharedPreferences: SharedPreferences
    @Inject
    lateinit var factory: AdminViewModelFactory
    private lateinit var viewModel: AdminViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar!!.hide()
        viewModel = ViewModelProvider(this, factory)[AdminViewModel::class.java]
        viewModel.setNavController(getNavController())
        initBottomNavigation()
    }

    private fun initBottomNavigation(){
        binding.adminBottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.pending->{
                    viewModel.switchToFragment(R.id.adminFragment1)
                }
                R.id.assigned->{
                    viewModel.switchToFragment(R.id.adminFragment2)
                }
                R.id.solved->{
                    viewModel.switchToFragment(R.id.adminFragment3)
                }
                R.id.approved->{
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
        binding.adminBottomNavigationView.selectedItemId = R.id.all
    }
}