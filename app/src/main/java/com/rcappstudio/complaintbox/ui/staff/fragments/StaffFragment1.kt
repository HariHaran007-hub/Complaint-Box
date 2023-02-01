package com.rcappstudio.complaintbox.ui.staff.fragments

import android.os.Bundle
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
import com.rcappstudio.complaintbox.databinding.FragmentStaff1Binding
import com.rcappstudio.complaintbox.model.Complaint
import com.rcappstudio.complaintbox.ui.FirebaseData
import com.rcappstudio.complaintbox.ui.adapter.CompRVAdapter
import com.rcappstudio.complaintbox.ui.staff.viewmodel.StaffViewModel
import com.rcappstudio.complaintbox.ui.staff.viewmodel.StaffViewModelFactory
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class StaffFragment1 : Fragment(), CompRVAdapter.CardClickListener {

    private lateinit var binding: FragmentStaff1Binding
    @Inject
    lateinit var factory: StaffViewModelFactory
    private lateinit var viewModel: StaffViewModel
    private lateinit var rvAdapter: CompRVAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentStaff1Binding.inflate(layoutInflater, container, false)
        binding.staffFrag1RV.layoutManager = LinearLayoutManager(requireContext())
        viewModel = ViewModelProvider(this, factory)[StaffViewModel::class.java]
        viewModel.setNavController(getNavController())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getPendingData().observe(viewLifecycleOwner) {
            rvAdapter = CompRVAdapter(requireContext(), it, this)
            binding.staffFrag1RV.adapter = rvAdapter
        }
    }

    private fun getNavController(): NavController {
        return (requireActivity().supportFragmentManager.findFragmentById(R.id.staffFragmentContainerView) as NavHostFragment).navController
    }

    override fun onCardClick(comp: Complaint) {
        val directions =
            StaffFragment1Directions.actionStaffFragment1ToViewFragment(Gson().toJson(comp))
        viewModel.switchToViewFragment(directions, R.id.viewFragment)
        requireActivity()
            .findViewById<BottomNavigationView>(R.id.staffBottomNavigationView)
            ?.visibility = View.GONE
    }

}