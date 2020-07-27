package com.example.codefreakschatmessenger

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.*

class LoginActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var refUsers: DatabaseReference



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

       back_login.setOnClickListener {
           //Starting Welcome page again
           val intent= Intent(this@LoginActivity,WelcomeActivity::class.java)
           startActivity(intent)
           //finishing the current
           finish()
       }

        mAuth=FirebaseAuth.getInstance()

        login_btn.setOnClickListener {
            loginUser()//Method to log in the existing user
        }
    }

    private fun loginUser() {
        val email:String=email_login.text.toString()
        val password:String=password_login.text.toString()

        var message:String=""//Var to store message for exceptions
        if (email.equals(""))
        {
            message="Enter the Email"
            Snackbar.make(findViewById(android.R.id.content),message, Snackbar.LENGTH_SHORT).show()
        }
        else if (password.equals(""))
        {
            message="Password can not be Empty"
            Snackbar.make(findViewById(android.R.id.content),message, Snackbar.LENGTH_SHORT).show()
        }
        else
        {
            val progressDialog= ProgressDialog(this@LoginActivity)
            progressDialog.setTitle("Sign In")
            progressDialog.setMessage("Please Wait While Logging You In")
            progressDialog.setCanceledOnTouchOutside(false)
            progressDialog.show()


            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener{task ->
                if (task.isSuccessful)
                {
                    val intent=Intent(this@LoginActivity,MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()


                    Toast.makeText(this,"Logged in",Toast.LENGTH_SHORT).show()
                    progressDialog.dismiss()


                }
                else
                {
                    message=task.exception!!.message.toString()
                    Toast.makeText(this,"Error"+message,Toast.LENGTH_LONG).show()
                    progressDialog.dismiss()
                }
            }


        }
    }
}