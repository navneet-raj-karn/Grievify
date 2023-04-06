package com.example.grievify.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.grievify.data.TicketData
import com.example.grievify.databinding.ComplaintBoxBinding

class ComplaintAdapter(
    private val context: Context?,
    private var itemList: ArrayList<TicketData>
):
    RecyclerView.Adapter<ComplaintAdapter.ItemsViewHolder>() {

    inner class ItemsViewHolder(val adapterBinding: ComplaintBoxBinding):RecyclerView.ViewHolder(adapterBinding.root){

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ):ItemsViewHolder {

        val binding = ComplaintBoxBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ComplaintAdapter.ItemsViewHolder, position: Int) {
        holder.adapterBinding.Subject.text=itemList[position].title
        holder.adapterBinding.status.text=itemList[position].status
        holder.adapterBinding.ticket.text=itemList[position].ticketID

    }

    override fun getItemCount(): Int {
        return itemList.size
    }
}