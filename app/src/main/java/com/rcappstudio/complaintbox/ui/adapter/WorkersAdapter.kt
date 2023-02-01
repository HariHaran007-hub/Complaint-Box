package com.rcappstudio.complaintbox.ui.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rcappstudio.complaintbox.databinding.AssignItemBinding
import com.rcappstudio.complaintbox.databinding.RvImageViewBinding
import com.rcappstudio.complaintbox.model.WorkerData
import com.squareup.picasso.Picasso

class WorkersAdapter (
    private val context: Context,
    private val workerDataMap: HashMap<String,WorkerData>,
    private val onClick: (String, String) -> Unit
    ) : RecyclerView.Adapter<WorkersAdapter.ViewHolder>(){

    private lateinit var binding: AssignItemBinding

    class ViewHolder(view: View) :RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = AssignItemBinding.inflate(LayoutInflater.from(context), parent , false)
        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val workerData = workerDataMap.values.toMutableList()[position]

        binding.workerName.text = workerData.name
        binding.root.setOnClickListener {
            onClick.invoke(workerDataMap.keys.toMutableList()[position], workerData.token.toString())
        }
    }

    override fun getItemCount(): Int {
        return workerDataMap.values.size
    }



}