package com.example.sayyada.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sayyada.R
import com.example.sayyada.databinding.ItemCardSelectedMemberBinding
import com.example.sayyada.models.SelectedMembers

class CardMemberListItemsAdapter(
    private val context: Context,
    private val list: ArrayList<SelectedMembers>,
    private val assignMembers: Boolean
): RecyclerView.Adapter<CardMemberListItemsAdapter.ViewBindingCML>(){

    private var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewBindingCML {
        val binding = ItemCardSelectedMemberBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewBindingCML(binding)
    }

    override fun onBindViewHolder(holder: ViewBindingCML, position: Int) {
        val model = list[position]
        if (position == list.size -1 && assignMembers){
            holder.binding.ivAddMember.visibility = View.VISIBLE
            holder.binding.ivSelectedMemberImage.visibility = View.GONE
        }else{
            holder.binding.ivAddMember.visibility = View.GONE
            holder.binding.ivSelectedMemberImage.visibility = View.VISIBLE

            Glide
                .with(context)
                .load(model.image)
                .centerCrop()
                .placeholder(R.drawable.ic_user_place_holder_from)
                .into(holder.binding.ivSelectedMemberImage)
        }
        holder.itemView.setOnClickListener{
            if(onClickListener != null){
                onClickListener!!.onClick()
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewBindingCML(var binding: ItemCardSelectedMemberBinding):RecyclerView.ViewHolder(binding.root)

    fun setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener = onClickListener
    }
    interface OnClickListener{
        fun onClick()
    }
}