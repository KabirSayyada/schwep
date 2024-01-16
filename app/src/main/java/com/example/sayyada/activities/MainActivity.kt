package com.example.sayyada.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.sayyada.R
import com.example.sayyada.adapters.BoardItemsAdapter
import com.example.sayyada.databinding.ActivityMainBinding
import com.example.sayyada.firebase.FirestoreClass
import com.example.sayyada.models.BoardFunctions
import com.example.sayyada.models.User
import com.example.sayyada.utils.Constants
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    companion object {
        const val MY_PROFILE_REQUESTED_CODE: Int = 10
        const val CREATE_BOARD_REQUEST_CODE: Int = 20
    }

    private lateinit var mUserName: String

    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setupActionBar()



        binding.navView.setNavigationItemSelectedListener(this)

        FirestoreClass().loadUserData(this, true)

        binding.toolbarMain.fabCreateBoard.setOnClickListener{
            val intent = Intent(this,
                CreateBoardActivity::class.java)
            intent.putExtra(Constants.NAMES, mUserName)
            beginActivityForResult(intent, CREATE_BOARD_REQUEST_CODE)
        }


    }
    fun populateBoardsListToUi(boardsList : ArrayList<BoardFunctions>){

        hideProgressDialog()

        if (boardsList.size > 0){
            binding.mainContentXml.recyclerViewBoardsList.visibility = View.VISIBLE
            binding.mainContentXml.tvNoBoardsAvailable.visibility = View.GONE

            binding.mainContentXml.recyclerViewBoardsList.layoutManager = LinearLayoutManager(this)
            binding.mainContentXml.recyclerViewBoardsList.setHasFixedSize(true)

            val adapter = BoardItemsAdapter(this, boardsList)
            binding.mainContentXml.recyclerViewBoardsList.adapter = adapter

            adapter.setOnclickEars(object: BoardItemsAdapter.OnClickListener{
                override fun onClick(position: Int, model: BoardFunctions) {
                    val intention = Intent(this@MainActivity, TaskListActivity::class.java)
                    intention.putExtra(Constants.Document_ID, model.documentId)
                    startActivity(intention)
                }
            })
        }else{
            binding.mainContentXml.recyclerViewBoardsList.visibility = View.GONE
            binding.mainContentXml.tvNoBoardsAvailable.visibility = View.VISIBLE

        }
    }

    private fun setupActionBar() {
        setSupportActionBar(binding.toolbarMain.toolbarMainActivity)
        binding.toolbarMain.toolbarMainActivity.setNavigationIcon(R.drawable.ic_action_navigation_menu)

        binding.toolbarMain.toolbarMainActivity.setNavigationOnClickListener {
            toggleDrawer()

        }
    }

    private fun toggleDrawer() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            doubleBackToExit()
        }
    }

    fun updateNavigationUserDetails(user: User, readBoardList: Boolean) {
        mUserName = user.name

        val imageView = findViewById<View>(R.id.nav_profile_image) as ImageView
        Glide
            .with(this)
            .load(user.image)
            .centerCrop()
            .fallback(R.drawable.ic_nav_user)
            .into(imageView)

            binding.root.findViewById<TextView>(R.id.nav_username).text = user.name

            if (readBoardList){
                showProgressDialog(resources.getString(R.string.please_wait))
                FirestoreClass().getBoardsList(this)
            }


    }

    override fun onActivityResult(data: Intent?, requestCode: Int, resultCode: Int) {
        super.onActivityResult(data, requestCode, resultCode)
        if(resultCode == Activity.RESULT_OK
            && requestCode == MY_PROFILE_REQUESTED_CODE){
            FirestoreClass().loadUserData(this, true)

        }else if (resultCode == Activity.RESULT_OK
            && requestCode == CREATE_BOARD_REQUEST_CODE){
            FirestoreClass().getBoardsList(this)

        } else{
            Log.e("Cancelled", "Cancelled" )
        }
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
          when(item.itemId){
            R.id.nav_my_profile ->{
                beginActivityForResult(
                    Intent(this,
                        MyProfileActivity::class.java ),
                    MY_PROFILE_REQUESTED_CODE)
            }
            R.id.nav_sign_out ->{
                FirebaseAuth.getInstance().signOut()

                val intent = Intent(this, IntroActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
    override fun shouldRegisterForActivityResult(): Boolean {
        return true
    }



}