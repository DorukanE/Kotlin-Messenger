package com.dorukaneskiceri.kotlinmessenger

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_register.*
import java.net.URI
import java.util.*

class RegisterActivity : AppCompatActivity() {

    private var selectedPhotoUri: Uri? = null
    private val registerAuth = FirebaseAuth.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val database = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        registerButton.setOnClickListener {
            doRegister()
        }

        accountText.setOnClickListener {
            val intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)
        }

        selectPhotoButton.setOnClickListener{
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent,0)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 0 && resultCode == Activity.RESULT_OK && data != null){
            selectedPhotoUri = data.data

            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver,selectedPhotoUri)
            circleProfileImage.setImageBitmap(bitmap)
            selectPhotoButton.alpha = 0f

//            selectPhotoButton.background = BitmapDrawable(bitmap)
        }
    }

    private fun doRegister(){
        val email = emailTextRegister.text.toString()
        val password = passwordTextRegister.text.toString()
        val username = usernameTextRegister.text.toString()
        if(email.isNotEmpty() && password.isNotEmpty() && username.isNotEmpty() && selectedPhotoUri != null){
            registerAuth.createUserWithEmailAndPassword(email,password).addOnSuccessListener {
                println("Registration Successful for uid: ${it.user?.uid}")
                uploadImageToFirebase()
                Toast.makeText(this,"Registration successful",Toast.LENGTH_LONG).show()

            }.addOnFailureListener {
                Toast.makeText(this,"Registration failed, ${it.localizedMessage}",Toast.LENGTH_LONG).show()
                println(it.localizedMessage.toString())
            }
        }else{
            Toast.makeText(this,"Please fill the inputs correctly.",Toast.LENGTH_LONG).show()
        }
    }

    private fun uploadImageToFirebase(){
        if (selectedPhotoUri == null) return

        val filename = UUID.randomUUID().toString()
        val reference = storage.getReference("/images/$filename")
        reference.putFile(selectedPhotoUri!!).addOnSuccessListener {
            println("Successfully uploaded image ${it.metadata?.path}")
            reference.downloadUrl.addOnSuccessListener {
                saveUserToDatabase(it.toString())

            }
        }.addOnFailureListener{
            println(it.localizedMessage.toString())
        }

    }

    private fun saveUserToDatabase(imageUrl: String){
        val userUid = registerAuth.uid

        if(userUid != null){
            val user = User(userUid, usernameTextRegister.text.toString(), imageUrl)
            database.collection("Users").add(user).addOnSuccessListener {
                println("User successfully added to Firebase Firestore")

            }.addOnFailureListener {
                println(it.localizedMessage.toString())
            }
        }
    }

    class User(val uid: String, val username: String, val imageUrl: String)
}

