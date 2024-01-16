package com.example.sayyada.activities

import android.app.Activity
import android.app.Dialog
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sayyada.R
import com.example.sayyada.adapters.MemberListItemsAdapter
import com.example.sayyada.databinding.ActivityMembersBinding
import com.example.sayyada.databinding.DialogSearchMemberBinding
import com.example.sayyada.firebase.FirestoreClass
import com.example.sayyada.models.BoardFunctions
import com.example.sayyada.models.User
import com.example.sayyada.utils.Constants
import org.json.JSONObject
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL
import java.util.concurrent.Executors

class MembersActivity : BaseActivity() {

    private lateinit var binding: ActivityMembersBinding
    private lateinit var mBoardDetails: BoardFunctions
    private lateinit var mAssignedMembersList: ArrayList<User>
    private var anyChangesMade : Boolean = false
    private val myExecutor = Executors.newSingleThreadExecutor()
    private val myHandler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMembersBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        if (intent.hasExtra(Constants.BOARD_DETAIL)){
            mBoardDetails = intent.getParcelableExtra<BoardFunctions>(Constants.BOARD_DETAIL)!!
            showProgressDialog(resources.getString(R.string.please_wait))
           // FirestoreClass().getAssignedMembersListDetails(this, mBoardDetails.assignedTo)
        }

    }

    fun memberDetails(user: User){
        mBoardDetails.assignedTo.add(user.id)
        //FirestoreClass().assignMemberToBoard(this, mBoardDetails, user)
    }

    fun setUpMembersList(list: ArrayList<User>){
        mAssignedMembersList = list
        hideProgressDialog()

        binding.membersListRv.layoutManager = LinearLayoutManager(this,
            LinearLayoutManager.VERTICAL, false)
        binding.membersListRv.setHasFixedSize(true)

        val adapter = MemberListItemsAdapter(this,list)
        binding.membersListRv.adapter = adapter
    }

    private fun setActionBar() {
        setSupportActionBar(binding.membersActivityToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back_ios)
        supportActionBar?.title = mBoardDetails.name + "Members"
        binding.membersActivityToolbar.setNavigationOnClickListener{onBackPressed()}
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add_member, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_add_member ->{
                dialogSearchMember()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun dialogSearchMember(){
        var dialog = Dialog(this)
        var binding = DialogSearchMemberBinding.inflate(layoutInflater)
        dialog.setContentView(binding.root)
        binding.addTv.setOnClickListener{
            val email = binding.emailSearchMemberEt.text.toString()
            if (email.isNotEmpty()){
                showProgressDialog(resources.getString(R.string.please_wait))
                //FirestoreClass().getMemberDetails(this, email)
                dialog.dismiss()
            }else{
                Toast.makeText(this, "Please enter email address", Toast.LENGTH_SHORT).show()
            }
        }
        binding.cancelTv.setOnClickListener{
            dialog.dismiss()
        }
        dialog.show()
    }

    override fun onBackPressed() {
        if (anyChangesMade){
            setResult(Activity.RESULT_OK)
        }
        super.onBackPressed()
    }

    fun memberAssignSuccess(user: User){
        hideProgressDialog()
        mAssignedMembersList.add(user)
        anyChangesMade = true
        setUpMembersList(mAssignedMembersList)
        sendNotificationToUserAsyncTask(mBoardDetails.name, user.FcmToken)
    }

    private fun sendNotificationToUserAsyncTask(boardName: String, token: String){
        var result : Any
        myExecutor.execute{
           // var result: Any
            var connection: HttpURLConnection? = null
            try {
                val url = URL(Constants.FCM_BASE_URL)
                connection = url.openConnection() as HttpURLConnection
                connection.doOutput = true
                connection.doInput = true
                connection.instanceFollowRedirects = false
                connection.requestMethod = "POST"

                connection.setRequestProperty("Content_Type", "application/json")
                connection.setRequestProperty("charset", "utf-8")
                connection.setRequestProperty("Accept", "application/json")
                connection.setRequestProperty(Constants.FCM_AUTHORIZATION, "${Constants.FCM_KEY}=${Constants.FCM_SERVER_KEY}")
                connection.useCaches = false

                val wr = DataOutputStream(connection.outputStream)
                val jsonRequest = JSONObject()
                val dataObject = JSONObject()

                dataObject.put(Constants.FCM_KEY_TITLE, "Assigned to the $boardName")
                dataObject.put(Constants.FCM_KEY_MESSAGE, "You have been assigned to the board by ${mAssignedMembersList[0].name}")
                jsonRequest.put(Constants.FCM_KEY_TO, token)
                jsonRequest.put(Constants.FCM_KEY_DATA, dataObject)

                wr.writeBytes(jsonRequest.toString())
                wr.flush()
                wr.close()

                val httpResult: Int = connection.responseCode
                if (httpResult == HttpURLConnection.HTTP_OK){
                    val inputStream = connection.inputStream
                    val reader = BufferedReader(InputStreamReader(inputStream))

                    val stringBuilder = StringBuilder()
                    var line: String?
                    try {
                        while (reader.readLine().also { line = it } != null){
                            stringBuilder.append(line+"\n")
                        }
                    }catch (e: IOException){
                        e.printStackTrace()
                    }finally {
                        try {
                            inputStream.close()
                        }catch (e: IOException){
                            e.printStackTrace()
                        }
                    }
                    result = stringBuilder.toString()
                }else{
                    result = connection.responseMessage
                }
            }catch (e: SocketTimeoutException){
                result = "Connection Timeout"
            }catch (e: Exception){
                result = "Error : " + e.message
            }finally {
                connection?.disconnect()
            }
            return@execute result as Unit
        }

        myHandler.post{
            result = myExecutor
        }

    }
}
