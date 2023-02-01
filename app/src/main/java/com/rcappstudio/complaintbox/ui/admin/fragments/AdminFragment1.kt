package com.rcappstudio.complaintbox.ui.admin.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.rcappstudio.complaintbox.R
import com.rcappstudio.complaintbox.databinding.FragmentAdmin1Binding
import com.rcappstudio.complaintbox.databinding.FragmentUser1Binding
import com.rcappstudio.complaintbox.model.Complaint
import com.rcappstudio.complaintbox.ui.FirebaseData
import com.rcappstudio.complaintbox.ui.adapter.CompRVAdapter
import com.rcappstudio.complaintbox.ui.admin.viewmodel.AdminViewModel
import com.rcappstudio.complaintbox.ui.admin.viewmodel.AdminViewModelFactory
import com.rcappstudio.complaintbox.ui.user.fragments.UserFragment1Directions
import com.rcappstudio.complaintbox.ui.user.viewmodel.UserViewModel
import com.rcappstudio.complaintbox.ui.user.viewmodel.UserViewModelFactory
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AdminFragment1 : Fragment(),CompRVAdapter.CardClickListener {

    private lateinit var binding: FragmentAdmin1Binding
    @Inject
    lateinit var factory: AdminViewModelFactory
    private lateinit var viewModel : AdminViewModel
    private lateinit var rvAdapter: CompRVAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentAdmin1Binding.inflate(layoutInflater,container,false)
        binding.adminFrag1RV.layoutManager = LinearLayoutManager(requireContext())
        viewModel = ViewModelProvider(this, factory)[AdminViewModel::class.java]
        viewModel.setNavController(getNavController())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        FirebaseData.liveData.observe(viewLifecycleOwner){
            Log.d("TAGData", "onViewCreated: ${it.size}")

        }
        viewModel.getPendingData().observe(viewLifecycleOwner) {
            rvAdapter = CompRVAdapter(requireContext(),it,this)
            binding.adminFrag1RV.adapter = rvAdapter
        }
    }

    private fun getNavController(): NavController {
        return (requireActivity().supportFragmentManager.findFragmentById(R.id.adminFragmentContainerView) as NavHostFragment).navController
    }

    override fun onCardClick(comp: Complaint) {
        val directions = AdminFragment1Directions.actionAdminFragment1ToViewFragment(Gson().toJson(comp))
        viewModel.switchToViewFragment(directions, R.id.viewFragment)
        requireActivity()
            .findViewById<BottomNavigationView>(R.id.adminBottomNavigationView)
            ?.visibility = View.GONE
    }

}