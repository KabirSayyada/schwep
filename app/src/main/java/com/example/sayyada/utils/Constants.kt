package com.example.sayyada.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import androidx.core.app.ActivityCompat.startActivityForResult
import com.example.sayyada.R
import com.example.sayyada.activities.MyProfileActivity
import com.example.sayyada.activities.BaseActivity


object Constants {
    const val USERS: String = "users"

    const val BOARDS: String = "boards"

    const val IMAGE: String = "image"
    const val NAMES: String = "name"
    const val MOBILE: String = "mobile"
    const val ASSIGNED_TO: String = "assignedTo"
    const val READ_STORAGE_PERMISSION_CODE = 1
    const val PICK_IMAGE_REQUEST_CODE = 2
    const val Document_ID : String = "document Id"
    const val TASK_LIST: String ="taskList"
    const val ID = "id"
    const val TASK_LIST_ITEM_POSITION = "task_list_item_position"
    const val CARD_LIST_ITEM_POSITION = "card_list_item_position"
    const val BOARD_MEMBERS_LIST = "board_members_list"
    const val BOARD_DETAIL ="board_detail"
    const val UN_SELECT = "unselect"
    const val SELECT = "select"
    const val FCM_BASE_URL :String = "https://fcm.googleapis.com/fcm/send"
    const val FCM_AUTHORIZATION: String = "authorization"
    const val FCM_KEY: String = "key"
    const val FCM_SERVER_KEY: String = R.string.fcm_key.toString()
    const val FCM_KEY_TITLE: String = "title"
    const val FCM_KEY_MESSAGE: String = "message"
    const val FCM_KEY_DATA: String = "data"
    const val FCM_KEY_TO: String = "to"



    /*fun showImageChooser(activity: Activity){

        var galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        beginActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
    }*/

    //override fun shouldRegisterForActivityResult(): Boolean {
      //  return true
    //}
    fun getFileExtension(activity: Activity, uri: Uri): String?{
        return MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(activity.contentResolver.getType(uri))
    }
}