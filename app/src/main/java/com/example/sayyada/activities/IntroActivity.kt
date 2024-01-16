package com.example.sayyada.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.sayyada.databinding.ActivityIntroBinding


class IntroActivity : BaseActivity() {

    private lateinit var binding: ActivityIntroBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.btnSignInIntro.setOnClickListener{

            val intent = Intent (this, SignInActivity::class.java)
            startActivity(intent)
        }

            binding.btnSignUpIntro.setOnClickListener{

                val intent = Intent (this, SignUpActivity::class.java)
                startActivity(intent)
            }

        }

}
