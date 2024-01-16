package com.example.sayyada.adapters

import android.app.AlertDialog
import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sayyada.activities.TaskListActivity
import com.example.sayyada.databinding.ItemTaskBinding
import com.example.sayyada.models.Task
import java.util.*
import kotlin.collections.ArrayList


class TaskListItemsAdapter(
    private val context: Context,
    private var list: ArrayList<Task>,

): RecyclerView.Adapter<TaskListItemsAdapter.ViewBindTL>(){

    private var mPositionDraggedFrom = -1
    private var mPositionDraggedTo = -1


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewBindTL {
        val binding = ItemTaskBinding.inflate(LayoutInflater.from(context), parent, false)

        val layoutParams = LinearLayout.LayoutParams(
            (parent.width * 0.7).toInt() , LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins((15.toDP().toPX()), 0, (40.toDP()).toPX(),0)
        binding.root.layoutParams = layoutParams
        return ViewBindTL(binding)
    }

    override fun onBindViewHolder(holder: ViewBindTL, positions: Int){

        val model = list[positions]
        if (positions == list.size - 1) {
            holder.binding.addTaskList.visibility = View.VISIBLE
            holder.binding.llItemTask.visibility = View.GONE
        } else {
            holder.binding.addTaskList.visibility = View.GONE
            holder.binding.llItemTask.visibility = View.VISIBLE
        }

        holder.binding.tvTaskListTitle.text = model.title
        holder.binding.addTaskList.setOnClickListener {
            holder.binding.addTaskList.visibility = View.GONE
            holder.binding.cvAddTaskListName.visibility = View.VISIBLE
        }
        holder.binding.imageButtonCloseListName.setOnClickListener {
            holder.binding.addTaskList.visibility = View.VISIBLE
            holder.binding.cvAddTaskListName.visibility = View.GONE
        }
        holder.binding.ibDoneListName.setOnClickListener {
            val listName = holder.binding.etTaskListName.text.toString()

            if (listName.isNotEmpty()) {
                if (context is TaskListActivity) {
                    context.createTaskList(listName)
                }
            } else {
                Toast.makeText(
                    context, "Please Enter List Name",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        holder.binding.ibEditListName.setOnClickListener {
            val updateCards = holder.binding.etEditTaskListName.setText(model.title).toString()
                if (context is TaskListActivity) {
                    context.addCardToTaskList(positions, updateCards)
                }

            holder.binding.llTitleView.visibility = View.GONE
            holder.binding.cvEditTaskListName.visibility = View.VISIBLE
        }
        holder.binding.ibCloseEditableView.setOnClickListener {
            holder.binding.llTitleView.visibility = View.VISIBLE
            holder.binding.cvEditTaskListName.visibility = View.GONE
        }
        holder.binding.ibDoneEditListName.setOnClickListener {
            val listNames = holder.binding.etEditTaskListName.text.toString()
            if (listNames.isNotEmpty()) {
                if (context is TaskListActivity) {
                    context.updateTaskList(positions, listNames, model)
                }
            } else {
                Toast.makeText(context, "Please Enter List Name", Toast.LENGTH_LONG).show()
            }
        }
        holder.binding.ibDeleteList.setOnClickListener {
            alertDialogForDeleteList(positions, model.title)
        }
        holder.binding.tvAddCard.setOnClickListener {
            holder.binding.tvAddCard.visibility = View.GONE
            holder.binding.cvAddCard.visibility = View.VISIBLE
        }
        holder.binding.ibCloseCardName.setOnClickListener {
            holder.binding.tvAddCard.visibility = View.VISIBLE
            holder.binding.cvAddCard.visibility = View.GONE
        }
        holder.binding.ibDoneCardName.setOnClickListener {
            val cardName = holder.binding.etCardName.text.toString()
            if (cardName.isNotEmpty()) {
                if (context is TaskListActivity) {
                    context.addCardToTaskList(positions, cardName)
                }
            } else {
                Toast.makeText(context, "Please Enter List Name", Toast.LENGTH_SHORT).show()
            }
        }
        holder.binding.rvCardList.layoutManager = LinearLayoutManager(context)
        holder.binding.rvCardList.setHasFixedSize(true)
        val adapter = CardsListItemAdapter(context, model.cards)
        holder.binding.rvCardList.adapter = adapter
        adapter.setOnclickListener(object : CardsListItemAdapter.OnClickListener{
            override fun onClick(cardPosition: Int) {
                if (context is TaskListActivity) {
                    context.cardDetails(holder.adapterPosition, cardPosition)
                }
            }
        })
        val dividerItemDecoration = DividerItemDecoration(
            context,
            DividerItemDecoration.VERTICAL
        )
        holder.binding.rvCardList.addItemDecoration(dividerItemDecoration)
        val helper = ItemTouchHelper(
            object: ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0
            ){
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    val draggedPosition = viewHolder.adapterPosition
                    val targetPosition = target.adapterPosition
                    if (mPositionDraggedFrom == -1){
                        mPositionDraggedFrom = draggedPosition
                    }
                    mPositionDraggedTo = targetPosition
                    Collections.swap(list[holder.adapterPosition].cards, draggedPosition, targetPosition)
                    adapter.notifyItemMoved(draggedPosition, targetPosition)
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                }

                override fun clearView(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder
                ) {
                    super.clearView(recyclerView, viewHolder)
                    if(mPositionDraggedFrom != -1 && mPositionDraggedTo != -1 && mPositionDraggedFrom != mPositionDraggedTo){
                        (context as TaskListActivity). updateCardsInTaskList(holder.adapterPosition, list[holder.adapterPosition].cards)
                    }
                    mPositionDraggedTo = -1
                    mPositionDraggedFrom = -1
                }
            }
        )
        helper.attachToRecyclerView(holder.binding.rvCardList)
    }

    class ViewBindTL( var binding: ItemTaskBinding):RecyclerView.ViewHolder(binding.root)

    override fun getItemCount(): Int {
        return list.size
    }

    private fun Int.toDP(): Int = (this / Resources.getSystem().displayMetrics.density).toInt()

    private fun Int.toPX(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()





    private fun alertDialogForDeleteList(position: Int, title: String) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Incoming Alert")
        builder.setMessage("Please confirm that You want to delete $title...")
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        builder.setPositiveButton("Yes") { dialogInterface, _ ->
            dialogInterface.dismiss()
            if (context is TaskListActivity){
                context.deleteTaskList(position)
            }
        }
        builder.setNegativeButton("NO") { dialogInterface, _ ->
            dialogInterface.dismiss()
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    //class ViewBindingTL constructor( val binding: ViewBinding) :
      //   RecyclerView.ViewHolder(binding.root){
        //companion object{
          //  inline fun create(
            //    parent: ViewGroup,
              //  crossinline block:(inflater: LayoutInflater, container: ViewGroup, attach: Boolean) -> ViewBinding

            //) = ViewBindingTL(block(LayoutInflater.from(parent.context), parent, false))
        }


