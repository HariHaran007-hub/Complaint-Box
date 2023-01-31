package com.rcappstudio.complaintbox.ui.user.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.rcappstudio.complaintbox.R
import com.rcappstudio.complaintbox.databinding.FragmentUser2Binding
import com.rcappstudio.complaintbox.model.Complaint
import com.rcappstudio.complaintbox.ui.adapter.CompRVAdapter
import com.rcappstudio.complaintbox.ui.user.viewmodel.UserViewModel
import com.rcappstudio.complaintbox.ui.user.viewmodel.UserViewModelFactory
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class UserFragment2 : Fragment(), CompRVAdapter.CardClickListener {

    private lateinit var binding: FragmentUser2Binding
    @Inject
    lateinit var factory: UserViewModelFactory
    private lateinit var viewModel : UserViewModel
    private lateinit var rvAdapter: CompRVAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentUser2Binding.inflate(layoutInflater, container, false)
        binding.userFrag2RV.layoutManager = LinearLayoutManager(requireContext())
        viewModel = ViewModelProvider(this, factory)[UserViewModel::class.java]
        viewModel.setNavController(getNavController())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getPendingData().observe(viewLifecycleOwner) {
            rvAdapter = CompRVAdapter(requireContext(),it,this)
            binding.userFrag2RV.adapter = rvAdapter
        }
    }

    private fun getNavController(): NavController {
        return (requireActivity().supportFragmentManager.findFragmentById(R.id.userFragmentContainerView) as NavHostFragment).navController
    }

    override fun onCardClick(comp: Complaint) {
        val directions = UserFragment2Directions.actionUserFragment2ToViewFragment(Gson().toJson(comp))
        viewModel.switchToViewFragment(directions,R.id.viewFragment)
    }
}