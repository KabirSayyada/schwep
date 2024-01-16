package com.example.sayyada.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sayyada.R
import com.example.sayyada.databinding.ItemMemberBinding
import com.example.sayyada.databinding.ItemTaskBinding
import com.example.sayyada.models.User
import com.example.sayyada.utils.Constants

 class MemberListItemsAdapter (
    private val context: Context,
    private var list: ArrayList<User>
        ): RecyclerView.Adapter<MemberListItemsAdapter.ViewBindingMemberList>() {
    private var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewBindingMemberList {
        val binding = ItemMemberBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewBindingMemberList(binding)
    }

    override fun onBindViewHolder(
        holder: ViewBindingMemberList,
        position: Int
    ) {
        val model = list[position]

        Glide
            .with(context)
            .load(model.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder_from)
            .into(holder.binding.ivMemberImage)

        holder.binding.tvMemberName.text = model.name
        holder.binding.tvMemberEmail.text = model.email
        if (model.selected) {
            holder.binding.ivSelectedMember.visibility = View.VISIBLE
        } else {
            holder.binding.ivSelectedMember.visibility = View.GONE
        }
        holder.itemView.setOnClickListener {
            if (onClickListener != null) {
                if (model.selected) {
                    onClickListener!!.onClick(position, model, Constants.UN_SELECT)
                } else {
                    onClickListener!!.onClick(position, model, Constants.SELECT)
                }
            }
        }
    }

     override fun getItemCount(): Int {
         return list.size
     }

    class ViewBindingMemberList( var binding: ItemMemberBinding):RecyclerView.ViewHolder(binding.root)

    fun setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener = onClickListener
    }
    interface  OnClickListener{
        fun onClick(position: Int, user: User, action: String)
    }

 }

