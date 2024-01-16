package com.example.sayyada.activities

import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.example.sayyada.R
import com.example.sayyada.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.example.sayyada.firebase.FirestoreClass

class SignUpActivity : BaseActivity() {

    private lateinit var binding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setMyActionBar()
        //hideSystemBars()



        }

    /*private fun hideSystemBars(){
        val windowInsetsController =
            ViewCompat.getWindowInsetsController(window.decorView) ?: return
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
    }*/
    fun userRegisteredSuccess(){
        Toast.makeText(this, "You have successfully registered", Toast.LENGTH_LONG).show()
        hideProgressDialog()

        FirebaseAuth.getInstance().signOut()
        finish()
    }

    private fun setMyActionBar(){
        setSupportActionBar(binding.toolbarSignUpActivity)

        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)
        }

        binding.toolbarSignUpActivity.setNavigationOnClickListener{onBackPressed()}

        binding.btnSignUp.setOnClickListener{
            registerUser()
        }
    }

    private fun registerUser() {

        val name: String = binding.etName.text.toString().trim{it <= ' '}
        val email: String = binding.etEmail.text.toString().trim{it <= ' ' }
        val password: String = binding.etPassword.text.toString().trim{it <= ' '}

        if (validateForm(name, email, password)){
            showProgressDialog(resources.getString(R.string.please_wait))
            FirebaseAuth.getInstance()
                .createUserWithEmailAndPassword(email,password).addOnCompleteListener {

                    task->
                    if (task.isSuccessful){
                        val firebaseUser : FirebaseUser = task.result!!.user!!
                        val registeredEmail = firebaseUser.email!!
                        val user = com.example.sayyada.models.User(firebaseUser.uid, name, registeredEmail)

                        FirestoreClass().registerUser(this, user)
                    }else{
                        Toast.makeText(this,
                        "Registration has Failed", Toast.LENGTH_SHORT).show()
                    }


            }
        }
    }

    private fun validateForm(name: String,
                             email: String, password: String) : Boolean{
        return when{
            TextUtils.isEmpty(name)->{
                showErrorSnackBar("Please enter a name")
                false
            }
            TextUtils.isEmpty(email)->{
                showErrorSnackBar("Please enter an email address")
                false
            }
            TextUtils.isEmpty(password)->{
                showErrorSnackBar("Please input your password")

                   false
            }else ->{
                true
            }
        }
    }
    }

