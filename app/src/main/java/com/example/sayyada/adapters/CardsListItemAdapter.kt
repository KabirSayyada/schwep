package com.example.sayyada.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sayyada.activities.TaskListActivity
import com.example.sayyada.databinding.ItemCardBinding
import com.example.sayyada.models.Card
import com.example.sayyada.models.SelectedMembers

class CardsListItemAdapter(
    private val context: Context,
    private var list: ArrayList<Card>,
 ):

    RecyclerView.Adapter<CardsListItemAdapter.ViewBindingCL>() {

    private var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewBindingCL {
        val binding = ItemCardBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewBindingCL(binding)
    }

    override fun onBindViewHolder(holder: ViewBindingCL, position: Int) {
        val model = list[position]

                if (model.labelColor?.isNotEmpty() == true) {
                    holder.binding.viewLabelColor.visibility = View.VISIBLE
                    holder.binding.viewLabelColor.setBackgroundColor(Color.parseColor(model.labelColor))
            }else{
                holder.binding.viewLabelColor.visibility = View.GONE
            }
        holder.binding.tvCardName.text = model.name
        if((context as TaskListActivity).mAssignedMemberDetailsList.size > 0){
            val selectedMembersList: ArrayList<SelectedMembers> = ArrayList()
            for (i in context.mAssignedMemberDetailsList.indices){
                for (j in model.assignedTo){
                    if (context.mAssignedMemberDetailsList[i].id == j){
                        val selectedMember = SelectedMembers(
                            context.mAssignedMemberDetailsList[i].id,
                            context.mAssignedMemberDetailsList[i].image
                        )
                        selectedMembersList.add(selectedMember)
                    }
                }
            }
            if (selectedMembersList.size > 0){
                if(selectedMembersList.size == 1 && selectedMembersList[0].id == model.createdBy){
                    holder.binding.rvCardSelectedMembersList.visibility = View.GONE
                }else{
                    holder.binding.rvCardSelectedMembersList.visibility = View.VISIBLE
                    holder.binding.rvCardSelectedMembersList.layoutManager = GridLayoutManager(context, 4)
                    val adapter = CardMemberListItemsAdapter(context, selectedMembersList, false)
                    holder.binding.rvCardSelectedMembersList.adapter = adapter
                    adapter.setOnClickListener(object: CardMemberListItemsAdapter.OnClickListener{
                        override fun onClick() {
                            if (onClickListener != null){
                                onClickListener!!.onClick(holder.adapterPosition)
                            }
                        }
                    })

                }
            }else{
                holder.binding.rvCardSelectedMembersList.visibility = View.GONE
            }
        }
        holder.itemView.setOnClickListener{
            if (onClickListener != null){
                onClickListener!!.onClick(position)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
    class ViewBindingCL( var binding: ItemCardBinding):RecyclerView.ViewHolder(binding.root)

    interface OnClickListener{
        fun onClick(cardPosition: Int)
    }

    fun setOnclickListener(onClickListener: OnClickListener){
         this.onClickListener = onClickListener
         }


 }