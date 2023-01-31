package com.rcappstudio.complaintbox.ui.user.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.rcappstudio.complaintbox.R
import com.rcappstudio.complaintbox.databinding.FragmentUser1Binding
import com.rcappstudio.complaintbox.ui.user.UserActivity
import com.rcappstudio.complaintbox.ui.user.viewmodel.UserViewModel
import com.rcappstudio.complaintbox.ui.user.viewmodel.UserViewModelFactory
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class UserFragment1 : Fragment() {

    private lateinit var binding: FragmentUser1Binding
    @Inject
    lateinit var factory: UserViewModelFactory
    private lateinit var viewModel : UserViewModel


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, factory)[UserViewModel::class.java]
        viewModel.setNavController(getNavController())
        clickListener()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentUser1Binding.inflate(layoutInflater,container,false)
        return binding.root
    }

    private fun clickListener(){
        binding.addComplaintFab.setOnClickListener {
            viewModel.switchToFragment(R.id.addComplaintFragment)
        }
    }
    private fun getNavController(): NavController {
        return (requireActivity().supportFragmentManager.findFragmentById(R.id.userFragmentContainerView) as NavHostFragment).navController
    }


}