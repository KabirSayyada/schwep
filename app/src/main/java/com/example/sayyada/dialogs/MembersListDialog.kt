package com.example.sayyada.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sayyada.adapters.MemberListItemsAdapter
import com.example.sayyada.databinding.DialogListBinding
import com.example.sayyada.models.User

abstract class MembersListDialog(
    context: Context,
    private var list: ArrayList<User>?,
    private val title: String = ""
) : Dialog(context) {

    private var adapter: MemberListItemsAdapter? = null
    private var binding: DialogListBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState ?: Bundle())

        val binding = DialogListBinding.inflate(layoutInflater)
        val bind = binding.root

        setContentView(binding.root)
        setCanceledOnTouchOutside(true)
        setCancelable(true)
        setUpRecyclerView(bind)
    }

    private fun setUpRecyclerView(view: View) {
        binding?.tvTitle?.text  = title

        if (list?.size != 0) {

            binding?.rvList?.layoutManager = LinearLayoutManager(context)
            adapter = list?.let { MemberListItemsAdapter(context, it) }
            binding?.rvList?.adapter = adapter

            adapter?.setOnClickListener(object :
                MemberListItemsAdapter.OnClickListener {
                override fun onClick(position: Int, user: User, action:String) {
                    dismiss()
                    onItemSelected(user, action)
                }
            })
        }
    }

    protected abstract fun onItemSelected(user: User, action:String)
}