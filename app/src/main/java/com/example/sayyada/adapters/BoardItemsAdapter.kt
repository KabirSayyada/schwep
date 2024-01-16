package com.example.sayyada.adapters

import android.content.Context
import android.icu.text.Transliterator
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.example.sayyada.R
import com.example.sayyada.activities.CreateBoardActivity
import com.example.sayyada.models.BoardFunctions
import com.example.sayyada.databinding.ItemBoardBinding
import com.example.sayyada.firebase.FirestoreClass
import java.util.ArrayList


class BoardItemsAdapter(private val context: Context,
                        private var list: ArrayList<BoardFunctions>,
):
      RecyclerView.Adapter<BoardItemsAdapter.ViewBindingBI>() {

    private var onClickListener: OnClickListener? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewBindingBI {

        return ViewBindingBI.create(parent, ItemBoardBinding::inflate)

    }
    override fun onBindViewHolder(holder: ViewBindingBI, position: Int) {
        (holder.binding as ItemBoardBinding).apply{
            val model = list[position]

                Glide
                    .with(context)
                    .load(model.image)
                    .centerCrop()
                    .placeholder(R.drawable.ic_mp_image_place_holder)
                    .into(holder.binding.ivBoardImage)

                holder.binding.itemBoardTextView.text = model.name
                holder.binding.textViewCreatedBy.text = "Created by: ${model.createdBy}"
                holder.itemView.setOnClickListener{
                    if (onClickListener != null){
                        onClickListener!!.onClick(position,model)
                    }

                }

        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

   /* fun notifyEditTeam(activity: Activity, position: Int, requestCode: Int){
        val intent = Intent(context, CreateBoardActivity::class.java)
        intent.putExtra(Constants.EDIT_BOARD, list[position])
        activity.startActivityForResult(intent, requestCode)
        notifyItemChanged(position)
    }
    fun removeAt(position: Int){
        val model = list[position]
        list.removeAt(position)
        FirestoreClass().deleteBoard(model)
        notifyDataSetChanged()
    }*/
    class ViewBindingBI constructor(val binding: ViewBinding) :
        RecyclerView.ViewHolder(binding.root){
        companion object{
            inline fun create(
                parent: ViewGroup,
                crossinline block:(inflater:LayoutInflater,container: ViewGroup, attach: Boolean) -> ViewBinding

            ) = ViewBindingBI(block(LayoutInflater.from(parent.context), parent, false))
        }
    }
    interface OnClickListener{
        fun onClick(position: Int, model: BoardFunctions)
    }
    fun setOnclickEars(onClickListener: OnClickListener){
        this.onClickListener = onClickListener
    }

    //private class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)

}