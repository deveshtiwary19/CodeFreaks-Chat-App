package com.example.codefreakschatmessenger.Fragments

import android.os.Bundle
import android.renderscript.Sampler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.codefreakschatmessenger.AdapterClasses.UserAdapter
import com.example.codefreakschatmessenger.ModeClasses.Chat
import com.example.codefreakschatmessenger.ModeClasses.Chatlist
import com.example.codefreakschatmessenger.ModeClasses.Users
import com.example.codefreakschatmessenger.Notifications.Token
import com.example.codefreakschatmessenger.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.ktx.Firebase

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ChatsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ChatsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var userAdapter: UserAdapter?=null
    private var  mUers:List<Users>?=null
    private var  uersChatList:List<Chatlist>?=null
    lateinit var recyclerViewChatList:RecyclerView
    private var firebaseUser:FirebaseUser?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_chats, container, false)

        recyclerViewChatList=view.findViewById(R.id.recycler_view_chatList)
        recyclerViewChatList.setHasFixedSize(true)
        recyclerViewChatList.layoutManager=LinearLayoutManager(context)

        firebaseUser=FirebaseAuth.getInstance().currentUser


        uersChatList=ArrayList()
        val ref=FirebaseDatabase.getInstance().reference.child("ChatList").child(firebaseUser!!.uid)
        ref!!.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                (uersChatList as ArrayList).clear()

                for (datasnapshot in p0.children)
                {
                    val chatlist=datasnapshot.getValue(Chatlist::class.java)
                    (uersChatList as ArrayList).add(chatlist!!)
                }
                retrieveChatList()


            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })


        updateToken(FirebaseInstanceId.getInstance().token)


        return view
    }

    private fun updateToken(token: String?) {

        val ref=FirebaseDatabase.getInstance().reference.child("Tokens")
        val token1=Token(token!!)
        ref.child(firebaseUser!!.uid).setValue(token1)


    }

    private fun retrieveChatList()
    {
        mUers=ArrayList()
        val ref=FirebaseDatabase.getInstance().reference.child("Users")
        ref.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {

                (mUers as ArrayList).clear()

                for (dataSnapshot in p0.children)
                {
                    val user=dataSnapshot.getValue(Users::class.java)

                    for (eachChatList in uersChatList!!)
                    {
                        if (user!!.getUid().equals(eachChatList.getId()))
                        {
                            (mUers as ArrayList).add(user!!)
                        }
                    }
                }

                userAdapter= UserAdapter(context!!,(mUers as ArrayList<Users>),true)

                recyclerViewChatList.adapter=userAdapter
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }





































































    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ChatsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ChatsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}