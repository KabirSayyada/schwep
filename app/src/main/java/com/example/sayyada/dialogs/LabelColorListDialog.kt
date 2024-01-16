package com.example.sayyada.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sayyada.adapters.LabelColorListItemsAdapter
import com.example.sayyada.databinding.DialogListBinding

abstract class LabelColorListDialog(
    context: Context,
    private var list: ArrayList<String>,
    private val title: String = "",
    private var mSelectedColor: String = ""
        ): Dialog(context){
            private var adapter: LabelColorListItemsAdapter? = null
    private lateinit var minding: DialogListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        minding = DialogListBinding.inflate(layoutInflater)
        val mind = minding.root
        setContentView(mind)
        setCanceledOnTouchOutside(true)
        setCancelable(true)
        setUpRecyclerView(mind)
    }
    private fun setUpRecyclerView(view: View){
        minding.tvTitle.text = title
        minding.rvList.layoutManager = LinearLayoutManager(context)
        adapter = LabelColorListItemsAdapter(list, mSelectedColor)
        minding.rvList.adapter = adapter
        adapter!!.onItemClickListener = object: LabelColorListItemsAdapter.OnItemClickListener{
            override fun onClick(position: Int, color: String) {
                dismiss()
                onItemSelected(color)
            }


        }
    }
    protected abstract fun onItemSelected(color: String)


        }