package com.example.sayyada.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.sayyada.R
import com.example.sayyada.databinding.ActivityCreateBoardBinding
import com.example.sayyada.firebase.FirestoreClass
import com.example.sayyada.models.BoardFunctions
import com.example.sayyada.utils.Constants
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.IOException

class CreateBoardActivity : BaseActivity() {

    private lateinit var binding : ActivityCreateBoardBinding

    private var mSelectedImageFileUri : Uri? = null

    private lateinit var mUserName: String
    private var  mBoardImageURL: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateBoardBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setupActionBar()

        if (intent.hasExtra(Constants.NAMES)){
            mUserName = intent.getStringExtra(Constants.NAMES).toString()
        }

        binding.cbBoardImage.setOnClickListener{

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
        binding.cbButtonCreate.setOnClickListener{
            if (mSelectedImageFileUri != null){
                uploadBoardImage()
            }else{
                showProgressDialog(resources.getString(R.string.please_wait))
                createBoard()
            }
        }

    }

    private fun createBoard(){
        val assignedUserArrayList: ArrayList<String> = ArrayList()
        assignedUserArrayList.add(getCurrentUserID())

        var board = BoardFunctions(
            binding.cbBoardName.text.toString(),
            mBoardImageURL,
            mUserName,
            assignedUserArrayList
        )
        FirestoreClass().createBoard(this, board)
    }
    private fun uploadBoardImage(){
        showProgressDialog(resources.getString(R.string.please_wait))

        val sRef : StorageReference =
            FirebaseStorage.getInstance().reference.child(
                "Board_IMAGE" +System.currentTimeMillis()
                        + "." + Constants.getFileExtension(this, mSelectedImageFileUri!!))
        sRef.putFile(mSelectedImageFileUri!!).addOnSuccessListener {
                taskSnapshot ->
            Log.i(
                "Board Image URL",
                taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
            )
            taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                    uri->
                Log.i("Downloadable Image URL", uri.toString())
                mBoardImageURL = uri.toString()

                createBoard()

            }
        }.addOnFailureListener{
                exception ->
            Toast.makeText(
                this@CreateBoardActivity,
                exception.message,
                Toast.LENGTH_LONG
            ).show()
            hideProgressDialog()
        }
    }

    fun boardCreatedSuccessfully(){
        hideProgressDialog()

        setResult(Activity.RESULT_OK)
        finish()
    }
    private fun setupActionBar(){
        setSupportActionBar(binding.boardActivityToolbar)
        val actionBar = supportActionBar
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)
            actionBar.title = resources.getString(R.string.create_board_title)
        }
        binding.boardActivityToolbar.setNavigationOnClickListener{onBackPressed()}
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
                showImageChooser()
            }
        }else{
            Toast.makeText(
                this,
                "Unfortunately you just denied the permission for storage.But don't worry you can grant it on settings"
                , Toast.LENGTH_LONG
            ).show()
        }
    }
    private fun showImageChooser(){

        var intent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        beginActivityForResult(intent, Constants.PICK_IMAGE_REQUEST_CODE)
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
                    .with(this)
                    .load(mSelectedImageFileUri)
                    .centerCrop()
                    .placeholder(R.drawable.cb_image_place_holder)
                    .into(binding.cbBoardImage)
            }catch (e: IOException){
                e.printStackTrace()
            }
        }
    }
}