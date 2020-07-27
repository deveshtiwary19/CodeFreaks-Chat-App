package com.example.codefreakschatmessenger

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.media.session.MediaSession
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.renderscript.Sampler
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import android.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.codefreakschatmessenger.AdapterClasses.ChatAdapter
import com.example.codefreakschatmessenger.Fragments.APIService
import com.example.codefreakschatmessenger.ModeClasses.Chat
import com.example.codefreakschatmessenger.ModeClasses.Users
import com.example.codefreakschatmessenger.Notifications.*
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_message_char.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.StringBuilder

class MessageCharActivity : AppCompatActivity() {

    var userIdVisit:String=""
    var firebaseUser:FirebaseUser?=null

    var chatAndapter:ChatAdapter?=null
    var mChatList:List<Chat>?=null

    var reference:DatabaseReference?=null
    var apiService: APIService?=null

    var notify=false

        lateinit var recyclerViewChat:RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message_char)


        //customizing the toolbar for a back button
        val toolbar:androidx.appcompat.widget.Toolbar=findViewById(R.id.toolbar_message_chat)
        setSupportActionBar(toolbar)
        supportActionBar!!.title=""
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }


        apiService=Client.Client.getClient("https://fcm.googleapis.com/")!!.create(APIService::class.java)


        intent=intent
        userIdVisit=intent.getStringExtra("visit_id")
        firebaseUser=FirebaseAuth.getInstance().currentUser

        recyclerViewChat=findViewById(R.id.recycler_view_chats)
        recyclerViewChat.setHasFixedSize(true)
        var linearLayoutManager=LinearLayoutManager(applicationContext)
        linearLayoutManager.stackFromEnd=true
        recyclerViewChat.layoutManager=linearLayoutManager





        var msgChatProfileImage:CircleImageView =findViewById(R.id.profile_image_msgchat)

         reference=FirebaseDatabase.getInstance().reference.child("Users").child(userIdVisit)


        reference!!.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {

                val users:Users?=p0.getValue(Users::class.java)
                username_msgchat.text=users!!.getUsername()
                Picasso.get().load(users!!.getProfile()).into(msgChatProfileImage)

                retrieveMessages(firebaseUser!!.uid,userIdVisit,users.getProfile())

            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })








        send_msg_btn.setOnClickListener{

            notify=true

            val message=text_message.text.toString()

            if (message=="")
            {
                Toast.makeText(this,"Message is Empty",Toast.LENGTH_SHORT).show()
            }
            else
            {
                sendMessageToUser(firebaseUser!!.uid,userIdVisit,message) //Method  to send the message
            }
            text_message.setText("")

        }


        attach_image_file_btn.setOnClickListener {

            notify=true
            val intent= Intent()
            intent.action=Intent.ACTION_GET_CONTENT
            intent.type="image/*"
            startActivityForResult(Intent.createChooser(intent,"Select Image"),438)



        }

        seenMessage(userIdVisit)

    }



    private fun sendMessageToUser(senderId: String, recieverId: String?, message: String) { //Function to send the messges




        val reference=FirebaseDatabase.getInstance().reference
        val messageKey=reference.push().key


        val messageHashMap=HashMap<String,Any?>()
        messageHashMap["sender"]=senderId
        messageHashMap["message"]=message
        messageHashMap["receiver"]=recieverId
        messageHashMap["isseen"]=false
        messageHashMap["url"]=""
        messageHashMap["messageId"]=messageKey

        reference.child("Chats").child(messageKey!!).setValue(messageHashMap)
            .addOnCompleteListener {
                task ->

                if (task.isSuccessful)
                {

                    val chatsListReference=FirebaseDatabase.getInstance().reference.child("ChatList").child(firebaseUser!!.uid).child(userIdVisit)

                    chatsListReference.addListenerForSingleValueEvent(object :ValueEventListener{
                        override fun onDataChange(p0: DataSnapshot) {

                            if (!p0.exists())
                            {
                                chatsListReference.child("id").setValue(userIdVisit)

                            }


                            val chatsListReceiverRef=FirebaseDatabase.getInstance().reference.child("ChatList").child(userIdVisit).child(firebaseUser!!.uid)
                            chatsListReceiverRef.child("id").setValue(firebaseUser!!.uid)
                        }

                        override fun onCancelled(p0: DatabaseError) {
                        }
                    })








                    //Pushing Notificatins now if message aaded to chat List here









                }
                else
                {
                    Toast.makeText(this,"Unable to send message!!",Toast.LENGTH_SHORT).show()
                }
            }

        val userReference=FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)

        userReference.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {

                val user=p0.getValue(Users::class.java)
                if (notify)
                {
                    sendNotification(recieverId,user!!.getUsername(),message)
                }
                notify=false
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })

    }

    private fun sendNotification(recieverId: String?, username: String?, message: String) {

        val ref=FirebaseDatabase.getInstance().reference.child("Tokens")

        val query=ref.orderByKey().equalTo(recieverId)

        query.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {

                for (datasnapshot in p0.children)
                {
                    val token:Token?=datasnapshot.getValue(Token::class.java)
                    //change the notification icon here
                    val data=Data(firebaseUser!!.uid,R.mipmap.ic_launcher_round,"$username: $message","New Message",userIdVisit)

                    val sender=Sender(data!!,token!!.getToken().toString())

                    apiService!!.sendNotification(sender)
                        .enqueue(object :Callback<MyResponse>{
                            override fun onResponse(
                                call: Call<MyResponse>,
                                response: Response<MyResponse>
                            ) {

                                if (response.code()==200)
                                {
                                    if (response.body()!!.sucess !== 1)
                                    {
                                            Toast.makeText(this@MessageCharActivity,"",Toast.LENGTH_SHORT).show()
                                    }
                                }


                            }

                            override fun onFailure(call: Call<MyResponse>, t: Throwable) {

                            }

                        })
                }


            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })





    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode==438 && resultCode==RESULT_OK && data!=null && data!!.data!=null)
        {
            val progressBar=ProgressDialog(this)
            progressBar.setMessage("Please wait....")
            progressBar.show()


            val fileUri=data.data
            val storageRef=FirebaseStorage.getInstance().reference.child("Chat Images")
            val ref=FirebaseDatabase.getInstance().reference
             val messageId=ref.push().key

            val  filePath=storageRef.child("$messageId.jpg")

            var uploadTask: StorageTask<*>
            uploadTask=filePath.putFile(fileUri!!)
            uploadTask.continueWithTask(Continuation <UploadTask.TaskSnapshot, Task<Uri>>{ task ->

                if (!task.isSuccessful)
                {
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation filePath.downloadUrl

            }).addOnCompleteListener {
                task ->
                if (task.isSuccessful)
                {
                    val downloadUrl=task.result
                    val url=downloadUrl.toString()


                    val messageHashMap=HashMap<String,Any?>()
                    messageHashMap["sender"]=firebaseUser!!.uid
                    messageHashMap["message"]="sent you an image."
                    messageHashMap["receiver"]=userIdVisit
                    messageHashMap["isseen"]=false
                    messageHashMap["url"]=url
                    messageHashMap["messageId"]=messageId

                    ref.child("Chats").child(messageId!!).setValue(messageHashMap)
                        .addOnCompleteListener { task ->
                            if(task.isSuccessful)
                            {
                                progressBar.dismiss()
                                val reference=FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)

                                reference.addValueEventListener(object :ValueEventListener{
                                    override fun onDataChange(p0: DataSnapshot) {

                                        val user=p0.getValue(Users::class.java)
                                        if (notify)
                                        {
                                            sendNotification(userIdVisit,user!!.getUsername(),"sent you an image.")
                                        }
                                        notify=false
                                    }

                                    override fun onCancelled(p0: DatabaseError) {

                                    }
                                })
                            }
                        }





                }
            }
        }
    }


    private fun retrieveMessages(senderId: String, recieverId: String?, recieverImageUrl: String?) {

        mChatList=ArrayList()
        val reference=FirebaseDatabase.getInstance().reference.child("Chats")

        reference.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                (mChatList as ArrayList<Chat>).clear()

                for(snapshot in p0.children)
                {
                    val chat=snapshot.getValue(Chat::class.java)
                    if (chat!!.getReceiver().equals(senderId) && chat!!.getSender().equals(recieverId)
                        ||chat.getReceiver().equals(recieverId) && chat.getSender().equals(senderId)  )
                    {
                        (mChatList as ArrayList<Chat>).add(chat)
                    }
                    chatAndapter= ChatAdapter(this@MessageCharActivity,(mChatList as ArrayList<Chat>),recieverImageUrl!!)
                    recyclerViewChat.adapter=chatAndapter
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })

    }



    var seenListener:ValueEventListener?=null

    private fun seenMessage(userId:String)
    {
        val reference=FirebaseDatabase.getInstance().reference.child("Chats")

        seenListener=reference.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {


                for (dataSnapshot in p0.children)
                {
                    val chat=dataSnapshot.getValue(Chat::class.java)

                    if (chat!!.getReceiver().equals(firebaseUser!!.uid) && chat.getSender().equals(userId))
                    {
                        val hashMap=HashMap<String,Any>()
                        hashMap["isseen"]=true
                        dataSnapshot.ref.updateChildren(hashMap)


                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })

    }

    override fun onPause() {
        super.onPause()

        reference!!.removeEventListener(seenListener!!)
    }

}