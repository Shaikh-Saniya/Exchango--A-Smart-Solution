package com.example.exchango.activity.invite.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.paging.PagingDataAdapter
import com.example.exchango.R

data class Invite(val name: String, val number: String)

class InviteList : PagingDataAdapter<Invite, InviteList.InviteViewHolder>(InviteDiffCallback()) {

    class InviteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.contactName)
        private val numberTextView: TextView = itemView.findViewById(R.id.contactNumber)

        fun bind(invite: Invite) {
            nameTextView.text = invite.name
            numberTextView.text = invite.number
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InviteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_invite_list, parent, false)
        return InviteViewHolder(view)
    }

    override fun onBindViewHolder(holder: InviteViewHolder, position: Int) {
        val invite = getItem(position)
        if (invite != null) {
            holder.bind(invite)
        }
    }

    class InviteDiffCallback : DiffUtil.ItemCallback<Invite>() {
        override fun areItemsTheSame(oldItem: Invite, newItem: Invite): Boolean {
            return oldItem.name == newItem.name // Use a unique identifier for better performance
        }

        override fun areContentsTheSame(oldItem: Invite, newItem: Invite): Boolean {
            return oldItem == newItem
        }
    }
}
