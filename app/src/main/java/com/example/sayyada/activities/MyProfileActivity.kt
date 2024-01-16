package com.example.sayyada.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.sayyada.R
import com.example.sayyada.databinding.ActivityMyProfileBinding
import com.example.sayyada.models.User
import com.example.sayyada.firebase.FirestoreClass
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.IOException
import androidx.activity.result.ActivityResultLauncher
import com.example.sayyada.utils.Constants

class MyProfileActivity : BaseActivity() {

    /*companion object{
        private const val READ_STORAGE_PERMISSION_CODE = 1
        private const val PICK_IMAGE_REQUEST_CODE = 2
    }*/
    private var mSelectedImageFileUri: Uri? = null
    private var mProfileImageURL : String = ""
    private lateinit var mUserDetails: User

    private lateinit var binding : ActivityMyProfileBinding




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyProfileBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setupActionBar()
        showImageChooser()


        FirestoreClass().loadUserData(this, true)

        binding.mpUserImage.setOnClickListener{

            if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            ==PackageManager.PERMISSION_GRANTED){
               showImageChooser()

            }else{
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    Constants.READ_STORAGE_PERMISSION_CODE
                )
            }
        }
        binding.btnUpdate.setOnClickListener{
            if (mSelectedImageFileUri != null){
                uploadUserImage()
            }else{
                showProgressDialog(resources.getString(R.string.please_wait))

                updateUserProfileData()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == Constants.READ_STORAGE_PERMISSION_CODE){
            if(grantResults.isNotEmpty()
                && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                /*Constants.*/showImageChooser()//(this)
            }
        }else{
            Toast.makeText(
                this,
                "Unfortunately you just denied the permission for storage.But don't worry you can grant it on settings"
            ,Toast.LENGTH_LONG
            ).show()
        }
    }
    private fun showImageChooser(){

        var galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        beginActivityForResult(galleryIntent, Constants.PICK_IMAGE_REQUEST_CODE)
    }

    override fun shouldRegisterForActivityResult(): Boolean {
        return true
    }


    override fun onActivityResult(data: Intent?, requestCode: Int, resultCode: Int) {
       // super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == Constants.PICK_IMAGE_REQUEST_CODE && data!!.data != null){
            mSelectedImageFileUri = data.data

            try {


                Glide
                    .with(this@MyProfileActivity)
                    .load(mSelectedImageFileUri)
                    .centerCrop()
                    .placeholder(R.drawable.ic_mp_image_place_holder)
                    .into(binding.mpUserImage)
            }catch (e:IOException){
                e.printStackTrace()
            }
        }
    }

    private fun setupActionBar(){
        setSupportActionBar(binding.myProfileToolbar)
        val actionBar = supportActionBar
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)
            actionBar.title = resources.getString(R.string.my_profile)
        }
        binding.myProfileToolbar.setNavigationOnClickListener{onBackPressed()}
    }
    fun setUserDataInUI(user: User){

        mUserDetails = user
        Glide
            .with(this@MyProfileActivity)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_nav_user)
            .into(binding.mpUserImage)

        binding.mpEtName.setText(user.name)
        binding.mpEtEmail.setText(user.email)
        if (user.mobile != 0L) {
            binding.mpEtMobile.setText(user.mobile.toString())

        }
    }

    private fun updateUserProfileData(){
        val userHashMap = HashMap<String, Any>()

        var anyChangesMade = false

        if(mProfileImageURL.isNotEmpty() && mProfileImageURL != mUserDetails.image){
            userHashMap[Constants.IMAGE] = mProfileImageURL
            anyChangesMade = true
        }

        if(binding.mpEtName.text.toString() != mUserDetails.name){
            userHashMap[Constants.NAMES] = binding.mpEtName.text.toString()
            anyChangesMade = true
        }

        if (binding.mpEtMobile.text.toString() != mUserDetails.mobile.toString()){
            userHashMap[Constants.MOBILE] = binding.mpEtMobile.text.toString().toLong()
            anyChangesMade = true
        }

        if (anyChangesMade)
        FirestoreClass().updateUserProfileData(this, userHashMap)
    }

    private fun uploadUserImage(){
        showProgressDialog(resources.getString(R.string.please_wait))
        if (mSelectedImageFileUri != null){
            val sRef : StorageReference =
                FirebaseStorage.getInstance().reference.child(
                    "USER_IMAGE" +System.currentTimeMillis()
                            + "." + Constants.getFileExtension(this, mSelectedImageFileUri!!))
            sRef.putFile(mSelectedImageFileUri!!).addOnSuccessListener {
                taskSnapshot ->
                Log.i(
                    "Firebase Image URL",
                    taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
                )
                taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                    uri->
                    Log.i("Downloadable Image URL", uri.toString())
                    mProfileImageURL = uri.toString()

                    updateUserProfileData()

                }
            }.addOnFailureListener{
                exception ->
                Toast.makeText(
                    this@MyProfileActivity,
                    exception.message,
                    Toast.LENGTH_LONG
                ).show()
                hideProgressDialog()
            }
        }
    }

    //private fun getFileExtension(uri: Uri): String?{
      //  return MimeTypeMap.getSingleton().getExtensionFromMimeType(contentResolver.getType(uri))
    //}
    fun profileUpdateSuccess(){
        hideProgressDialog()

        setResult(Activity.RESULT_OK)
        finish()
    }

}

