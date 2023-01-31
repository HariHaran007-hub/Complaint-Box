package com.rcappstudio.complaintbox.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.google.gson.Gson
import com.rcappstudio.complaintbox.R
import com.rcappstudio.complaintbox.model.Complaint

class ViewFragment : Fragment() {

    val args : ViewFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val complaint = Gson().fromJson(args.complaint,Complaint::class.java)
        Log.d("TAGData", "onViewCreated: ${complaint.complaintId}")
    }
}