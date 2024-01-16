package com.example.sayyada.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.sayyada.databinding.ItemLabelColorBinding

class LabelColorListItemsAdapter(
    private var list: ArrayList<String>,
    private val mSelectedColor: String

) : RecyclerView.Adapter<LabelColorListItemsAdapter.ViewBindLColor>() {

    var onItemClickListener: OnItemClickListener? = null

    class ViewBindLColor constructor(val binding: ViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        companion object {
            inline fun create(
                parent: ViewGroup,
                crossinline block: (inflater: LayoutInflater, container: ViewGroup, attach: Boolean) -> ViewBinding

            ) = ViewBindLColor(block(LayoutInflater.from(parent.context), parent, false))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewBindLColor {
        return ViewBindLColor.create(parent, ItemLabelColorBinding::inflate)
    }

    override fun onBindViewHolder(holder: ViewBindLColor, position: Int) {
        (holder.binding as ItemLabelColorBinding).apply {
            val item = list[position]
            holder.binding.viewMain.setBackgroundColor(Color.parseColor(item))
            if (item == mSelectedColor){
                holder.binding.ivSelectedColor.visibility = View.VISIBLE
            }else{
                holder.binding.ivSelectedColor.visibility = View.GONE
            }
            holder.itemView.setOnClickListener{
                if (onItemClickListener != null){
                    onItemClickListener!!.onClick(position, item)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
    interface OnItemClickListener{
        fun onClick(position: Int, color: String)
    }
}