package com.example.codefreakschatmessenger.Fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.codefreakschatmessenger.AdapterClasses.UserAdapter
import com.example.codefreakschatmessenger.ModeClasses.Users
import com.example.codefreakschatmessenger.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.fragment_search.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SearchFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SearchFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null


    private var userAdapter:UserAdapter?=null
    private var  mUers:List<Users>?=null
    private var recyclerView:RecyclerView?=null

    //The field too gget the data for search
    private var searchEditText:EditText?=null

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
        val view:View= inflater.inflate(R.layout.fragment_search, container, false)
        searchEditText=view.findViewById(R.id.search_users_ET)

        //initializing the recycler view
        recyclerView=view.findViewById(R.id.search_list)
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.layoutManager=LinearLayoutManager(context)



        mUers=ArrayList()
        rtrieveAllUsers() //Method to retrive all users from database


        searchEditText!!.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                SearchforUsers(s.toString().toLowerCase())

            }

            override fun afterTextChanged(s: Editable?) {

            }


        })







        return view
    }

    private fun rtrieveAllUsers() {

        var firebaseUserID=FirebaseAuth.getInstance().currentUser!!.uid
         val refUser= FirebaseDatabase.getInstance().reference.child("Users")

        refUser.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                (mUers as ArrayList<Users>).clear()
                if (searchEditText!!.text.toString()=="")
                {
                    for (snapshot in p0.children)
                    {
                        val user:Users?=snapshot.getValue(Users::class.java)
                        if (!(user!!.getUid()).equals(firebaseUserID))
                        {
                            (mUers as ArrayList<Users>).add(user)
                        }
                    }
                    userAdapter= UserAdapter(context!!,mUers!!,false)

                    recyclerView!!.adapter=userAdapter
                }

            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })


    }

    private fun SearchforUsers(str:String)
    {
        var firebaseUserID=FirebaseAuth.getInstance().currentUser!!.uid
        val queryUsers= FirebaseDatabase.getInstance().reference.child("Users").orderByChild("search")
            .startAt(str)
            .endAt(str+"\uf8ff")

        queryUsers.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                (mUers as ArrayList<Users>).clear()

                for (snapshot in p0.children)
                {
                    val user:Users?=snapshot.getValue(Users::class.java)
                    if (!(user!!.getUid()).equals(firebaseUserID))
                    {
                        (mUers as ArrayList<Users>).add(user)
                    }
                }

                userAdapter= UserAdapter(context!!,mUers!!,false)

                recyclerView!!.adapter=userAdapter
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
         * @return A new instance of fragment SearchFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SearchFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}