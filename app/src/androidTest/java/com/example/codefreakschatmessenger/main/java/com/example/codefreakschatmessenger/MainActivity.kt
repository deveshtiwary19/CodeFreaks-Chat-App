package com.example.codefreakschatmessenger

import android.content.Intent
import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.codefreakschatmessenger.Fragments.ChatsFragment
import com.example.codefreakschatmessenger.Fragments.SearchFragment
import com.example.codefreakschatmessenger.Fragments.SettingsFragment
import com.example.codefreakschatmessenger.ModeClasses.Chat
import com.example.codefreakschatmessenger.ModeClasses.Users
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    //Reference to the usersNod
    var refUser:DatabaseReference?=null
    var fireBaseUser:FirebaseUser?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar_main))

        fireBaseUser=FirebaseAuth.getInstance().currentUser
        refUser=FirebaseDatabase.getInstance().reference.child("Users").child(fireBaseUser!!.uid)

        val toolbar:Toolbar=findViewById(R.id.toolbar_main)
        setSupportActionBar(toolbar)
        supportActionBar!!.title=""

        val imageProfile:CircleImageView=findViewById(R.id.profile_image)

        val tabLayout:TabLayout=findViewById(R.id.tab_layout)
       val viewPager:ViewPager=findViewById(R.id.view_pager)

        //val viewPagerAdapter=ViewPagerAdapter(supportFragmentManager)
        //viewPagerAdapter.addFragment(ChatsFragment(),"Chats")
       // viewPagerAdapter.addFragment(SearchFragment(),"Search")
        //viewPagerAdapter.addFragment(SettingsFragment(),"Settings")
        //Add fragments here;;

       // viewPager.adapter=viewPagerAdapter
       // tabLayout.setupWithViewPager(viewPager)


        val ref=FirebaseDatabase.getInstance().reference.child("Chats")
        ref!!.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {

                val viewPagerAdapter=ViewPagerAdapter(supportFragmentManager)

                var countUnreadMessages=0

                for (datasnapshot in p0.children)
                {
                    val chat=datasnapshot.getValue(Chat::class.java)
                    if (chat!!.getReceiver().equals(fireBaseUser!!.uid) && !chat.getIsseen())
                    {
                        countUnreadMessages += 1
                    }
                }

                if (countUnreadMessages==0)
                {
                    viewPagerAdapter.addFragment(ChatsFragment(),"Chats")
                }
                else
                {
                    viewPagerAdapter.addFragment(ChatsFragment(),"Chats ($countUnreadMessages)")
                }
                viewPagerAdapter.addFragment(SearchFragment(),"Search")
                viewPagerAdapter.addFragment(SettingsFragment(),"Settings")
                viewPager.adapter=viewPagerAdapter
                tabLayout.setupWithViewPager(viewPager)
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })







        //Display the usernname and Profile Picture
        refUser!!.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists())
                {
                    val user:Users?=p0.getValue(Users::class.java)
                    user_name.text=user!!.getUsername() //Setting the username on the top
                    Picasso.get().load(user!!.getProfile()).placeholder(R.drawable.profile).into(imageProfile) //Setting the profile pic along with the username on the top
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })




    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
         when (item.itemId) {


            R.id.action_logout ->
            {
                FirebaseAuth.getInstance().signOut()
                val intent= Intent(this@MainActivity,WelcomeActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                //finishing the current
                finish()
                return true
            }

        }
        return false
    }

    internal class ViewPagerAdapter(fragmentManager:FragmentManager):
                FragmentPagerAdapter(fragmentManager)
    {
        private val  fragments:ArrayList<Fragment>
        private val  titles:ArrayList<String>


        init {
            fragments= ArrayList<Fragment>()
            titles= ArrayList<String>()
        }

        override fun getItem(position: Int): Fragment {
            return fragments[position]
        }

        override fun getCount(): Int {

            return fragments.size
        }
        //Method after initizlizing the viewpager to add fragments to View Pager
        fun addFragment(fragment:Fragment,title:String)
        {
            fragments.add(fragment)
            titles.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return titles[position]
        }


    }

    private fun updateStatus(status:String)
    {
        val ref=FirebaseDatabase.getInstance().reference.child("Users").child(fireBaseUser!!.uid)
        val hashMap=HashMap<String,Any>()
        hashMap["status"]=status

        ref.updateChildren(hashMap)

    }

    override fun onResume() {
        super.onResume()



        updateStatus("online")
    }

    override fun onPause() {
        super.onPause()


        updateStatus("offline")
    }


}