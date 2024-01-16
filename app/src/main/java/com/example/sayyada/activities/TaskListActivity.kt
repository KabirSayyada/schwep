package com.example.sayyada.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sayyada.R
import com.example.sayyada.adapters.TaskListItemsAdapter
import com.example.sayyada.databinding.ActivityTaskListBinding
import com.example.sayyada.firebase.FirestoreClass
import com.example.sayyada.models.BoardFunctions
import com.example.sayyada.models.Card
import com.example.sayyada.models.Task
import com.example.sayyada.models.User
import com.example.sayyada.utils.Constants

class TaskListActivity : BaseActivity() {

    private lateinit var binding : ActivityTaskListBinding

    private lateinit var mBoardDetails: BoardFunctions
    private lateinit var mBoardDocumentID: String
    lateinit var mAssignedMemberDetailsList: ArrayList<User>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskListBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        var boardDocumentId = ""
        if (intent.hasExtra(Constants.Document_ID)){
            boardDocumentId = intent.getStringExtra(Constants.Document_ID)!!
        }
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass(). getBoardDetails(this, boardDocumentId)
    }

    override fun onActivityResult(data: Intent?, requestCode: Int, resultCode: Int) {
        super.onActivityResult(data, requestCode, resultCode)
        if(resultCode == Activity.RESULT_OK && requestCode == MEMBERS_REQUEST_CODE
            ||requestCode == CARD_DETAILS_REQUEST_CODE){
            showProgressDialog(resources.getString(R.string.please_wait))
            FirestoreClass().getBoardDetails(this, mBoardDocumentID)
        }else{
            Log.e("Cancelled", "Cancelled")
        }
    }

    fun cardDetails(taskListPosition: Int, cardPosition: Int){
        val intent = Intent(this, CardDetailsActivity:: class.java)
        intent.putExtra(Constants.BOARD_DETAIL,mBoardDetails)
        intent.putExtra(Constants.TASK_LIST_ITEM_POSITION, taskListPosition)
        intent.putExtra(Constants.CARD_LIST_ITEM_POSITION, cardPosition)
        intent.putExtra(Constants.BOARD_MEMBERS_LIST, mAssignedMemberDetailsList)
        beginActivityForResult(intent, CARD_DETAILS_REQUEST_CODE)
    }

    override fun shouldRegisterForActivityResult(): Boolean {
        return true
    }

    /*override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_members, menu)
        return super.onCreateOptionsMenu(menu)
    }*/

    /*override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_members ->{
                val intent = Intent(this, MembersActivity:: class.java)
                intent.putExtra(Constants.BOARD_DETAIL, mBoardDetails)
                beginActivityForResult(intent, MEMBERS_REQUEST_CODE)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }*/

    private fun setupActionBar(){
        setSupportActionBar(binding.tsToolBar)
        val actionBar = supportActionBar
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)
            actionBar.title = mBoardDetails.name
        }
        binding.tsToolBar.setNavigationOnClickListener{onBackPressed()}
    }

    fun boardDetails(board: BoardFunctions){
        mBoardDetails = board
        hideProgressDialog()
        setupActionBar()

        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getAssignedMembersListDetails(this, mBoardDetails.assignedTo)

    }

    fun addUpdateTaskListSuccess(){
        hideProgressDialog()
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getBoardDetails(this, mBoardDetails.documentId)
    }
    fun createTaskList(taskListName: String){
        val task = Task(taskListName, FirestoreClass().getCurrentUserID() )
        mBoardDetails.taskList.add(0, task)
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size -1)

        showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().addUpdateTaskList(this, mBoardDetails)
    }
    fun updateTaskList(position: Int, listName: String, model: Task){
        val task = Task(listName, model.createdBy)
        mBoardDetails.taskList[position] = task
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size -1)
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addUpdateTaskList(this, mBoardDetails)
    }

    fun deleteTaskList(position: Int){
        mBoardDetails.taskList.removeAt(position)
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size -1)
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addUpdateTaskList(this, mBoardDetails)
    }
    fun addCardToTaskList(position: Int, cardName: String){
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size -1)
        val cardAssignedUsersList: ArrayList<String> = ArrayList()
        val card = Card(cardName,FirestoreClass().getCurrentUserID(), cardAssignedUsersList)
        val cardsList = mBoardDetails.taskList[position].cards
        cardsList.add(card)

        val task = Task(
            mBoardDetails.taskList[position].title,
            mBoardDetails.taskList[position].createdBy,
            cardsList
        )
        mBoardDetails.taskList[position] = task
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addUpdateTaskList(this, mBoardDetails)
    }


    fun boardMembersDetailsList(list: ArrayList<User>){
        mAssignedMemberDetailsList = list
        hideProgressDialog()
        val addTaskList = Task(resources.getString(R.string.action_add_list))
        mBoardDetails.taskList.add(addTaskList)

        binding.rvTaskList.layoutManager = LinearLayoutManager(
            this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvTaskList.setHasFixedSize(true)
        val adapter = TaskListItemsAdapter(this, mBoardDetails.taskList)
        binding.rvTaskList.adapter = adapter
    }

    fun updateCardsInTaskList(taskListPosition: Int, cards: ArrayList<Card>){
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size -1)
        mBoardDetails.taskList[taskListPosition].cards = cards
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addUpdateTaskList(this, mBoardDetails)
    }
    companion object {
        const val MEMBERS_REQUEST_CODE = 13
        const val CARD_DETAILS_REQUEST_CODE = 14
    }
}