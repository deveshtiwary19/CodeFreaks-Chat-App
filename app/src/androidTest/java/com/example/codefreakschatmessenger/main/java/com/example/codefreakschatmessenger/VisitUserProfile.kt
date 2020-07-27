package com.example.codefreakschatmessenger

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.example.codefreakschatmessenger.ModeClasses.Users
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_visit_user_profile.*

class VisitUserProfile : AppCompatActivity() {

    private var userVisitId:String=""
    private var cover:ImageView?=null
    private var profile:CircleImageView?=null
    private var user:Users?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_visit_user_profile)

        cover=findViewById(R.id.cover_display)
        profile=findViewById(R.id.profile_display)



        userVisitId=intent.getStringExtra("visit_id")

        val ref=FirebaseDatabase.getInstance().reference.child("Users").child(userVisitId)

        ref.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {

                if (p0.exists())
                {
                     user=p0.getValue(Users::class.java)

                    username_display.text=user!!.getUsername()
                    Picasso.get().load(user!!.getProfile()).into(profile)
                    Picasso.get().load(user!!.getCover()).into(cover)

                }

            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })


        facebook_display.setOnClickListener {
            val uri= Uri.parse(user!!.getFacebook())

            val intent=Intent(Intent.ACTION_VIEW,uri)

            startActivity(intent)
        }
        instagram_display.setOnClickListener {
            val uri= Uri.parse(user!!.getInstagram())

            val intent=Intent(Intent.ACTION_VIEW,uri)

            startActivity(intent)
        }
        website_display.setOnClickListener {
            val uri= Uri.parse(user!!.getWebsite())

            val intent=Intent(Intent.ACTION_VIEW,uri)

            startActivity(intent)
        }


        send_msg_btn_dispaly.setOnClickListener {
            val intent = Intent(this, MessageCharActivity::class.java)
            intent.putExtra("visit_id", user!!.getUid())
            startActivity(intent)
            finish()
        }





    }
}