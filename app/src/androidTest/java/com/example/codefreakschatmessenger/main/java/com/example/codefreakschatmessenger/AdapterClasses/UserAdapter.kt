package com.example.codefreakschatmessenger.AdapterClasses

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.codefreakschatmessenger.MainActivity
import com.example.codefreakschatmessenger.MessageCharActivity
import com.example.codefreakschatmessenger.ModeClasses.Chat
import com.example.codefreakschatmessenger.ModeClasses.Users
import com.example.codefreakschatmessenger.R
import com.example.codefreakschatmessenger.VisitUserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.user_search_item_layout.view.*

class UserAdapter(mContext:Context,mUsers:List<Users>,isChat:Boolean):RecyclerView.Adapter<UserAdapter.ViewHolder?>()
{
private val mContext:Context
    private val mUsers:List<Users>
    private var isChat:Boolean

    var lastMsg:String=""

init {
    this.mUsers=mUsers
    this.mContext=mContext
    this.isChat=isChat
}


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view:View=LayoutInflater.from(mContext).inflate(R.layout.user_search_item_layout,parent,false)
        return UserAdapter.ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mUsers.size

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val user:Users?=mUsers[position]
        holder.usernameTxt.text=user!!.getUsername()
        Picasso.get().load(user!!.getProfile()).placeholder(R.drawable.profile).into(holder.profileImageView)

        if (isChat)
        {
            retrievelastMessage(user.getUid(),holder.lastMessageTxt)
        }
        else
        {
            holder.lastMessageTxt.visibility=View.GONE
        }




        if (isChat)
        {
            if (user.getStatus()=="online")
            {
                holder.onlineImageView.visibility=View.VISIBLE
                holder.offlineImageView.visibility=View.GONE
            }
            else
            {
                holder.onlineImageView.visibility=View.GONE
                holder.offlineImageView.visibility=View.VISIBLE
            }
        }
        else
        {
            holder.onlineImageView.visibility=View.GONE
            holder.offlineImageView.visibility=View.GONE
        }



        holder.itemView.setOnClickListener{
            val options= arrayOf<CharSequence>(
                "Send Message",
                "Visit Profile"
            )

            val  builder:AlertDialog.Builder=AlertDialog.Builder(mContext)
            builder.setTitle("Options")
            builder.setItems(options,DialogInterface.OnClickListener { dialog, which ->
                if(which==0) {
                    val intent = Intent(mContext, MessageCharActivity::class.java)
                    intent.putExtra("visit_id", user.getUid())
                    mContext.startActivity(intent)
                }
                if(which==1)
                {
                    val intent = Intent(mContext, VisitUserProfile::class.java)
                    intent.putExtra("visit_id", user.getUid())
                    mContext.startActivity(intent)


                }
            })
            builder.show()

        }



    }
//the function to retrieve the last message oon the chat list
    private fun retrievelastMessage(chatUserId: String?, lastMessageTxt: TextView) {

    lastMsg="defaultMsg"

    val firebaseUsers=FirebaseAuth.getInstance().currentUser
    val reference=FirebaseDatabase.getInstance().reference.child("Chats")

    reference.addValueEventListener(object:ValueEventListener{
        override fun onDataChange(p0: DataSnapshot) {

            for (datasnapshot in p0.children)
            {
                val chat: Chat?=datasnapshot.getValue(Chat::class.java)

                if (firebaseUsers !=null && chat !=null)
                {
                    if (chat.getReceiver()==firebaseUsers!!.uid && chat.getSender()==chatUserId || chat.getReceiver()==chatUserId && chat.getSender()==firebaseUsers!!.uid)
                    {
                        lastMsg=chat.getMessage()!!

                    }
                }
            }
            when(lastMsg)
            {
                "defaultMsg" ->  lastMessageTxt.text="No message"
                "sent you an image."->lastMessageTxt.text="Image"
                else -> lastMessageTxt.text=lastMsg
            }
            lastMsg="defaultMsg"

        }

        override fun onCancelled(p0: DatabaseError) {

        }
    })




    }


    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView)
    {
        var usernameTxt:TextView
        var profileImageView:CircleImageView
        var onlineImageView:CircleImageView
        var offlineImageView:CircleImageView
        var lastMessageTxt:TextView

        init {
            usernameTxt=itemView.findViewById(R.id.user_name_search)
            profileImageView=itemView.findViewById(R.id.profile_image_search)
            onlineImageView=itemView.findViewById(R.id.image_online_search)
            offlineImageView=itemView.findViewById(R.id.image_offline_search)
            lastMessageTxt=itemView.findViewById(R.id.message_last_search)

        }
    }



}