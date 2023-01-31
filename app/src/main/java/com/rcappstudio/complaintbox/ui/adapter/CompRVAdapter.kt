package com.rcappstudio.complaintbox.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.rcappstudio.complaintbox.R
import com.rcappstudio.complaintbox.model.Complaint
import com.rcappstudio.complaintbox.utils.getDateTime

class CompRVAdapter(
    private val context: Context,
    private val compList: List<Complaint?>?,
    private val clickListener: CardClickListener
): RecyclerView.Adapter<CompRVAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val titleView: TextView = itemView.findViewById(R.id.card_title)
        val timeView: TextView = itemView.findViewById(R.id.card_time)
        val compStatus: TextView = itemView.findViewById(R.id.card_status)
    }

    interface CardClickListener {
        fun onCardClick(comp: Complaint)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.comp_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val comp = compList?.get(position)
        holder.titleView.text = comp?.title
        if (comp?.solved!! == 0) {
            holder.compStatus.text = "Pending"
            holder.compStatus.setTextColor(context.getColor(R.color.tomato_200))
        } else if (comp?.solved!! == 1){
            holder.compStatus.text = "Assigned"
            holder.compStatus.setTextColor(context.getColor(R.color.yellow_500))
        } else if (comp?.solved!! == 2){
            holder.compStatus.text = "Waiting for approval"
            holder.compStatus.setTextColor(context.getColor(R.color.yellow_500))
        } else {
            holder.compStatus.text = "Waiting for approval"
            holder.compStatus.setTextColor(context.getColor(R.color.lawn_green))
        }
        holder.itemView.setOnClickListener {
            clickListener.onCardClick(comp!!)
        }
        holder.timeView.text = getDateTime(comp?.timeStamp!!)
    }

    override fun getItemCount(): Int = compList?.size!!
}