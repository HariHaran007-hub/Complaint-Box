package com.rcappstudio.complaintbox.ui.staff

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.rcappstudio.complaintbox.R
import com.rcappstudio.complaintbox.ui.staff.viewmodel.StaffViewModel
import com.rcappstudio.complaintbox.ui.staff.viewmodel.StaffViewModelFactory
import com.rcappstudio.complaintbox.ui.user.viewmodel.UserViewModel
import com.rcappstudio.complaintbox.ui.user.viewmodel.UserViewModelFactory
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class StaffActivity : AppCompatActivity() {
    @Inject
    lateinit var factory: StaffViewModelFactory

    private lateinit var viewModel: StaffViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_staff)


        viewModel = ViewModelProvider(this, factory)[StaffViewModel::class.java]

    }
}