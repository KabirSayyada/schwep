package com.example.sayyada.firebase

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.example.sayyada.activities.*
import com.example.sayyada.models.BoardFunctions
import com.example.sayyada.models.Task
import com.example.sayyada.models.User
import com.example.sayyada.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions


class FirestoreClass {

    private val mFireStore = FirebaseFirestore.getInstance()

    fun registerUser(activity: SignUpActivity, userInfo: com.example.sayyada.models.User){
        mFireStore.collection(com.example.sayyada.utils.Constants.USERS)
            .document(getCurrentUserID()).set(userInfo, com.google.firebase.firestore.SetOptions.merge())
            .addOnSuccessListener {
                activity.userRegisteredSuccess()
            }.addOnFailureListener{
                e->
                Log.e(activity.javaClass.simpleName, "Error", e)
            }

    }

    fun getBoardDetails(activity: TaskListActivity, documentId : String){
        mFireStore.collection(Constants.BOARDS)
            .document(documentId)
            .get()
            .addOnSuccessListener {
                    document ->
                Log.i(activity.javaClass.simpleName, document.toString())
                val board = document.toObject(BoardFunctions::class.java)!!
                board.documentId = document.id
                activity.boardDetails(board)

            }.addOnFailureListener{e ->

                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while creating a board.", e)
            }
    }

    fun getAssignedMembersListDetails(activity: TaskListActivity, assignedTo: ArrayList<String>){
        mFireStore
            .collection(Constants.USERS)
            .whereIn(Constants.ID, assignedTo)
            .get()
            .addOnSuccessListener {
                document ->
                Log.i(activity.javaClass.simpleName, document.documents.toString())
                val users: ArrayList<User> = ArrayList()
                for (i in document.documents){
                    val user = i.toObject(User::class.java)!!
                    users.add(user)
               }
                //if (activity is MembersActivity)
                  //  activity.setUpMembersList(users)
                //else if (activity is TaskListActivity)
                    activity.boardMembersDetailsList(users)
            }
            .addOnFailureListener{
                //if (activity is MembersActivity)
                 //   activity.hideProgressDialog()
               // else if (activity is TaskListActivity)
                    activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, it.message.toString())
            }

    }

    fun createBoard(activity: CreateBoardActivity, board: BoardFunctions){
        mFireStore.collection(Constants.BOARDS)
            .document()
            .set(board, SetOptions.merge())
            .addOnSuccessListener {
                Log.e(activity.javaClass.simpleName, "Board created successfully.")
                Toast.makeText(activity, "Board creation Success.", Toast.LENGTH_LONG).show()
                activity.boardCreatedSuccessfully()
            }.addOnFailureListener{
                exception ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error occurred while creating board.",
                    exception
                )
            }
    }
    fun updateBoard(activity: CreateBoardActivity, board: BoardFunctions){
        val boardHashMap = HashMap<String,Any>()
        boardHashMap[Constants.NAMES] = board.name
        boardHashMap[Constants.IMAGE] = board.image
        mFireStore
            .collection(Constants.BOARDS)
            .document(board.documentId)
            .update(boardHashMap)
            .addOnSuccessListener {
                Log.i(activity.javaClass.simpleName, "Board Updated")
                Toast.makeText(activity, "Board Updated successfully", Toast.LENGTH_SHORT).show()
                activity.boardCreatedSuccessfully()
            }
            .addOnFailureListener {
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, it.message.toString())
            }
    }

    fun deleteBoard(board: BoardFunctions){
        mFireStore
            .collection(Constants.BOARDS)
            .document(board.documentId)
            .delete()
            .addOnSuccessListener {
                Log.i("Board_Del", "Board deleted")
            }
            .addOnFailureListener {
                Log.i("Board_Del","Some error occurred")
            }
    }


    fun getBoardsList(activity: MainActivity){
        mFireStore.collection(Constants.BOARDS)
            .whereArrayContains(Constants.ASSIGNED_TO, getCurrentUserID())
            .get()
            .addOnSuccessListener {
                document ->
                Log.i(activity.javaClass.simpleName, document.documents.toString())
                val boardList : ArrayList<BoardFunctions> = ArrayList()
                for(i in document.documents){
                    val board = i.toObject(BoardFunctions::class.java)!!
                        board.documentId = i.id
                        boardList.add(board)

            }
                activity.populateBoardsListToUi(boardList)
            }.addOnFailureListener{e ->

                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while creating a board.", e)
            }
    }
    fun addUpdateTaskList(activity: Activity, board: BoardFunctions){
        val taskListHashMap = HashMap<String, Any>()
        taskListHashMap[Constants.TASK_LIST] = board.taskList

        mFireStore.collection(Constants.BOARDS)
            .document(board.documentId)
            .update(taskListHashMap)
            .addOnSuccessListener {
                Log.i(activity.javaClass.simpleName, "TaskList updated successfully")
                if (activity is TaskListActivity)
                    activity.addUpdateTaskListSuccess()
                else if (activity is CardDetailsActivity)
                    activity.addUpdateTaskListSuccess()
            }.addOnFailureListener{
                exception ->
                if (activity is TaskListActivity)
                    activity.hideProgressDialog()
                else if (activity is CardDetailsActivity)
                    activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while updating your TaskList", exception)
            }
    }

    fun updateUserProfileData(activity: MyProfileActivity,
                              userHashMap: HashMap<String, Any>){
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserID())
            .update(userHashMap)
            .addOnSuccessListener {
                Log.i(activity.javaClass.simpleName, "Profile Data updated successfully")
                Toast.makeText(activity, "Profile updated successfully", Toast.LENGTH_LONG).show()
                activity.profileUpdateSuccess()

            }.addOnFailureListener{
                e ->
                activity.hideProgressDialog()
                Log.e(
                activity.javaClass.simpleName,
                "Error while creating a board.",
                    e
                )
                Toast.makeText(activity, "Unexpected error while updating Profile!", Toast.LENGTH_SHORT).show()
            }
    }


    fun loadUserData(activity: Activity, readBoardList: Boolean = false){
                mFireStore.collection(com.example.sayyada.utils.Constants.USERS)
                    .document(getCurrentUserID())
                    .get()
            .addOnSuccessListener { document ->
                val loggedInUser = document.toObject(com.example.sayyada.models.User::class.java)
                if (loggedInUser != null)

                    when(activity){
                        is SignInActivity ->{
                            activity.signInSuccess(loggedInUser)
                        }
                        is MainActivity ->{
                            activity.updateNavigationUserDetails(loggedInUser, readBoardList)
                        }
                        is MyProfileActivity ->{
                            activity.setUserDataInUI(loggedInUser)
                        }

                    }



            }.addOnFailureListener{
                    e->

                when(activity){
                    is SignInActivity ->{
                        activity.hideProgressDialog()
                    }
                    is MainActivity ->{
                        activity.hideProgressDialog()
                    }
                }
                Log.e("signInUser", "Error writing document", e)
            }

    }
    fun getCurrentUserID(): String {
        val currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserID = ""
        if (currentUser != null) {
            currentUserID = currentUser.uid
        }
        return currentUserID
    }
}