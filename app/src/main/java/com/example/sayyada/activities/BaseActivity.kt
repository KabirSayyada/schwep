package com.example.sayyada.activities

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.sayyada.R
import com.example.sayyada.databinding.ActivityBaseBinding
import com.example.sayyada.databinding.DialogProgressBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

open class BaseActivity : AppCompatActivity() {

    private var requestCode: Int = -2
    private var resultHandler: ActivityResultLauncher<Intent>? =null


    private lateinit var binding: ActivityBaseBinding


    private var doubleBackToExitPressedOnce = false
    private lateinit var mProgressDialog: Dialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBaseBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        registerForActivityResult()


    }
    private fun registerForActivityResult(){
        if (shouldRegisterForActivityResult()){
            resultHandler = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
                result ->
                onActivityResult(result.data, requestCode, result.resultCode)
                this.requestCode = -1
            }
        }
    }
    fun beginActivityForResult(intent: Intent, requestCode: Int){
        resultHandler!!.launch(intent)
        this.requestCode = requestCode
    }
    protected open fun onActivityResult(data: Intent?, requestCode: Int, resultCode: Int){

    }
    protected open fun shouldRegisterForActivityResult(): Boolean{
        return false
    }

    fun showProgressDialog(text: String) {
        mProgressDialog = Dialog(this)
        val dialogProgressBinding = DialogProgressBinding.inflate(layoutInflater, binding.root, false)

        /*set the screen content from a layout resource.
        The resource will be inflated, adding all top-level views to the screen
         */
        mProgressDialog.setContentView(dialogProgressBinding.root)

        dialogProgressBinding.TvProgressText.text = text

            //start the dialog and display it on screen.
        mProgressDialog.show()
    }
    fun hideProgressDialog(){
        mProgressDialog.dismiss()
    }
    fun getCurrentUserID(): String{
        return FirebaseAuth.getInstance().currentUser!!.uid
    }
    fun doubleBackToExit(){
        if (doubleBackToExitPressedOnce){
            super.onBackPressed()
            return
        }
        this.doubleBackToExitPressedOnce = true
        Toast.makeText(
            this,
            resources.getString(R.string.please_clk_bk_agn_to_ext),
            Toast.LENGTH_SHORT
        ).show()

        Handler(Looper.myLooper()!!).postDelayed({
            doubleBackToExitPressedOnce = false
        },2000)
    }
    fun showErrorSnackBar(message: String){
        val snackBar = Snackbar.make(findViewById(android.R.id.content),
        message, Snackbar.LENGTH_LONG)
        val snackBarView = snackBar.view
        snackBarView.setBackgroundColor(ContextCompat.getColor(this,
            R.color.snack_bar_error_color))
        snackBar.show()
    }



}