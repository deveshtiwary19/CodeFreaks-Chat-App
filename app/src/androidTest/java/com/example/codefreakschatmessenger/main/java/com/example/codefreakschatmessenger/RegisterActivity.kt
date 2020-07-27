package com.example.codefreakschatmessenger

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    private lateinit var mAuth:FirebaseAuth
    private lateinit var refUsers:DatabaseReference
    private var firebaseuserID:String=""







    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

       back_register.setOnClickListener{
           //Starting Welcome page again
           val intent= Intent(this@RegisterActivity,WelcomeActivity::class.java)
           startActivity(intent)
           //finishing the current
           finish()
       }

        mAuth=FirebaseAuth.getInstance()

        register_btn.setOnClickListener {
            registerUser()//Method to register a User
        }



    }

    private fun registerUser() {
        val userName:String=username_register.text.toString()
        val email:String=email_register.text.toString()
        val password:String=password_register.text.toString()
        var message:String=""//Var to store message for exceptions
        if (userName.equals(""))
        {
            message="Name can not be Empty"
            Snackbar.make(findViewById(android.R.id.content),message,Snackbar.LENGTH_SHORT).show()
        }
        else if (email.equals(""))
        {
           message="Email can not be Empty"
            Snackbar.make(findViewById(android.R.id.content),message,Snackbar.LENGTH_SHORT).show()
        }
        else if (password.equals(""))
        {
            message="Password can not be Empty"
            Snackbar.make(findViewById(android.R.id.content),message,Snackbar.LENGTH_SHORT).show()
        }
        //If all the fields are appropriate,we will createa user account
        else
        {
            val progressDialog= ProgressDialog(this@RegisterActivity)
            progressDialog.setTitle("Sign Up")
            progressDialog.setMessage("Please Wait While Creating Account")
            progressDialog.setCanceledOnTouchOutside(false)
            progressDialog.show()




            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener{
              task ->

              if (task.isSuccessful)
              {
                  firebaseuserID=mAuth.currentUser!!.uid
                  refUsers=FirebaseDatabase.getInstance().reference.child("Users").child(firebaseuserID)

                  val userHashMap=HashMap<String,Any>()
                  userHashMap["uid"]=firebaseuserID
                  userHashMap["username"]=userName
                  userHashMap["profile"]="https://firebasestorage.googleapis.com/v0/b/codefreaks-chat.appspot.com/o/profile.PNG?alt=media&token=529e95e4-f638-4b2d-92fd-c9c9c31ea160"
                  userHashMap["cover"]="https://firebasestorage.googleapis.com/v0/b/codefreaks-chat.appspot.com/o/CF3.png?alt=media&token=63c56131-4eaa-44e7-afc7-3cd6b82b8f5b"
                  userHashMap["status"]="offline"
                  userHashMap["search"]=userName.toLowerCase()
                  userHashMap["facebook"]="https://www.facebook.com/Codefreaks-102734661518429"
                  userHashMap["instagram"]="https://www.instagram.com/codefreaksdevelopers/"
                  userHashMap["website"]="https://www.google.com"

                  refUsers.updateChildren(userHashMap).addOnCompleteListener{task ->
                      if (task.isSuccessful)
                      {
                          val intent=Intent(this@RegisterActivity,MainActivity::class.java)
                          intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                          startActivity(intent)
                          finish()


                          Toast.makeText(this,"Account Created Sucessfully",Toast.LENGTH_SHORT).show()
                          progressDialog.dismiss()
                      }
                  }


              }
              else
              {
                  val messageEr:String=task.exception!!.message.toString()
                  Toast.makeText(this,"Error: "+messageEr,Toast.LENGTH_LONG).show()
                  progressDialog.dismiss()
              }
          }
        }
    }
}