package com.example.sawitprotest.main

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.sawitprotest.R
import com.example.sawitprotest.databinding.TicketListItemBinding
import com.example.sawitprotest.model.Ticket

class MainAdapter(val hook: AdapterHook) :
    ListAdapter<Ticket, MainAdapter.ViewHolder>(DIFF_CALLBACK) {

    inner class ViewHolder(private val binding: TicketListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(ticket: Ticket) {
            Log.d("adsf", ticket.toString())
            binding.nameTv.text =
                hook.getContext().getString(R.string.card_title, ticket.driverName, ticket.licenseNumber)
            binding.timestampTv.text = ticket.datetime
            binding.weightTv.text = if (ticket.outboundWeight == 0) "Empty Load" else "Net weight: ${ticket.outboundWeight - ticket.inboundWeight}"
            itemView.setOnClickListener {
                hook.onClick(ticket)
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Ticket>() {
            override fun areItemsTheSame(oldItem: Ticket, newItem: Ticket): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Ticket, newItem: Ticket): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            TicketListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    interface AdapterHook {
        fun onClick(ticket: Ticket)
        fun getContext(): Context
    }

}