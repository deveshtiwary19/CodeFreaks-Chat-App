package com.example.codefreakschatmessenger

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_welcome.*

class WelcomeActivity : AppCompatActivity() {

    var firebaseUser:FirebaseUser?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)


        //Register activity link
        register_welcome_btn.setOnClickListener {
            val intent=Intent(this@WelcomeActivity,RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }
        //Login Activty link
        login_welcome_btn.setOnClickListener {
            val intent=Intent(this@WelcomeActivity,LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

    }


    override fun onStart() {
        super.onStart()

        firebaseUser=FirebaseAuth.getInstance().currentUser
        //hecking for the user is already logged in or not

        if (firebaseUser!=null)
        {
            val intent=Intent(this@WelcomeActivity,MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}