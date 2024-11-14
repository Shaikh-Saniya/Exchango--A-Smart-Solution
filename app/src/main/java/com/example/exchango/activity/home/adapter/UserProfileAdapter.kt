package com.example.exchango.activity.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.exchango.R

data class Profile(val name: String)

class UserProfileAdapter(private var userList: List<Profile>) : RecyclerView.Adapter<UserProfileAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userName: TextView = itemView.findViewById(R.id.profile_name)
    }
    interface OnItemClickListener {
        fun onItemClick(product: Profile)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_home_user_profile, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.userName.text = userList[position].name
    }

    override fun getItemCount(): Int = userList.size

    fun updateList(newList: List<Profile>) {
        userList = newList
        notifyDataSetChanged()
    }
}
