package com.example.codefreakschatmessenger.Fragments

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.PixelCopy
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.example.codefreakschatmessenger.ModeClasses.Users
import com.example.codefreakschatmessenger.R
import com.example.codefreakschatmessenger.R.drawable.logo
import com.example.codefreakschatmessenger.R.drawable.profile
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.database.core.Context
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.android.synthetic.main.fragment_settings.view.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SettingsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SettingsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    var usersReference:DatabaseReference?=null
    var firebaseUser:FirebaseUser?=null

    var profileImage:CircleImageView?=null
    var coverImage:ImageView?=null

    private val RequestCode=438
    private var imageUri: Uri?=null
    private var storageRef:StorageReference?=null

    //to diff between cover and profile image
     private  var coverChecker:String?=""

    //to diff between facebook,insta and website links
    private  var socialChecker:String?=""




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
         var view:View= inflater.inflate(R.layout.fragment_settings, container, false)


        firebaseUser=FirebaseAuth.getInstance().currentUser
        usersReference=FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)

        storageRef=FirebaseStorage.getInstance().reference.child("User Images")

        profileImage=view.findViewById(R.id.profile_image_settings)
        coverImage=view.findViewById(R.id.cover_image_settings)


        usersReference!!.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {

                if (p0.exists())
                {
                    val user:Users?= p0.getValue(Users::class.java)

                   if (context!=null)
                   {
                       view.username_settings.text=user!!.getUsername()
                     Picasso.get().load(user!!.getProfile()).placeholder(profile).into(profileImage)
                       Picasso.get().load(user!!.getCover()).placeholder(logo).into(coverImage)
                   }
                }

            }

            override fun onCancelled(p0: DatabaseError) {

            }

        })

        view.profile_image_settings.setOnClickListener{
            pickImage() //function to piick a image from gallery
        }

        view.cover_image_settings.setOnClickListener{
            coverChecker="cover"
            pickImage() //function to piick a image from gallery

        }




        view.set_facebook.setOnClickListener{
            socialChecker="facebook"
           setSocialLinks()//function created to add links oof users to social accounts

        }

        view.set_instagram.setOnClickListener{
            socialChecker="instagram"
            setSocialLinks()//function created to add links oof users to social accounts

        }

        view.set_website.setOnClickListener{
            socialChecker="website"
            setSocialLinks()//function created to add links oof users to social accounts

        }



        return view
    }

    private fun setSocialLinks() {

        val builder:AlertDialog.Builder=
            AlertDialog.Builder(context,R.style.Theme_AppCompat_DayNight_Dialog_Alert)

        if (socialChecker=="website")
        {
            builder.setTitle("Enter the URL:")
        }
         else
        {
            builder.setTitle("Enter the username:")
        }
        val editText=EditText(context)

        if (socialChecker=="website")
        {
            editText.hint="www.example.com"
        }
        else
        {
            editText.hint="Your username here"
        }

        //Setting the designes edit Text to AlertDialog
        builder.setView(editText)
        builder.setPositiveButton("Create",DialogInterface.OnClickListener{
            dialog, which ->

            val str=editText.text.toString()

            if (str=="")
            {
                Toast.makeText(context,"Please Enter Something",Toast.LENGTH_SHORT).show()
            }
            else
            {
                //After all the validations we wil allow the user to upload a social link
                createSocialLink(str) //following is the method to upload the link to the database
            }
        })
        builder.setNegativeButton("Cancel",DialogInterface.OnClickListener { dialog, which ->
            dialog.cancel()

        })
        builder.show()

        }

    private fun createSocialLink(str:String) {
        val mapSocial=HashMap<String,Any>()




        when(socialChecker)
        {
            "facebook"->
            {
                 mapSocial["facebook"]="https://m.facebook.com/$str"
            }
            "instagram"->
            {
                mapSocial["instagram"]="https://www.instagram.com/$str"
            }
            "website"->
            {
                mapSocial["website"]="https://$str"
            }
        }

        usersReference!!.updateChildren(mapSocial).addOnCompleteListener {
            task ->
            if (task.isSuccessful)
            {
                Toast.makeText(context,"Link Saved",Toast.LENGTH_SHORT).show()
            }
            else
            {
                Toast.makeText(context,"Some Error Occured!! Try Again",Toast.LENGTH_SHORT).show()
            }
        }


    }

    private fun pickImage() {
        val intent= Intent()
        intent.type="image/*"
        intent.action=Intent.ACTION_GET_CONTENT
        startActivityForResult(intent,RequestCode)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode==RequestCode && resultCode==Activity.RESULT_OK && data!!.data!=null)
        {

            imageUri=data.data
            Toast.makeText(context,"Image Selected",Toast.LENGTH_SHORT).show()
            uploadImageToDatabase() //function to upload the image to firebase


        }
    }

    private fun uploadImageToDatabase() {
        val progressBar=ProgressDialog(context)
        progressBar.setMessage("Image is Uploading,Please wait!!")
        progressBar.setTitle("Profile Settings")
        progressBar.show()

        if (imageUri!=null)
        {
            val fileRef=storageRef!!.child(System.currentTimeMillis().toString()+".jpg")//Using the uploading time as the basis of key for the uploaded images

            var uploadTask:StorageTask<*>
            uploadTask=fileRef.putFile(imageUri!!)
            uploadTask.continueWithTask(Continuation <UploadTask.TaskSnapshot, Task<Uri>>{task ->

                if (!task.isSuccessful)
                {
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation fileRef.downloadUrl

            }).addOnCompleteListener { task ->
                if (task.isSuccessful)
                {
                    val downloadUrl=task.result
                    val url=downloadUrl.toString()

                    if (coverChecker=="cover") //for this conditon we will change the cover image
                    {
                        val mapCoverImg=HashMap<String,Any>()
                        mapCoverImg["cover"]=url

                        usersReference!!.updateChildren(mapCoverImg)
                        coverChecker=""

                    }
                    else //for this conditon we will change the profile image
                    {
                        val mapProfileImg=HashMap<String,Any>()
                        mapProfileImg["profile"]=url

                        usersReference!!.updateChildren(mapProfileImg)
                        coverChecker=""

                    }

                    progressBar.dismiss()

                }
            }

        }


    }


































    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SettingsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SettingsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}