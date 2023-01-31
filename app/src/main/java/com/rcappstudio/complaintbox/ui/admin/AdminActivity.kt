package com.rcappstudio.complaintbox.ui.admin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.rcappstudio.complaintbox.R
import com.rcappstudio.complaintbox.databinding.ActivityAdminBinding

class AdminActivity : AppCompatActivity() {
    private lateinit var binding : ActivityAdminBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        binding.toolBar.setOnMenuItemClickListener {
//
//        }
    }
}