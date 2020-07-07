package com.dorukaneskiceri.kotlinmessenger

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        registerButton.setOnClickListener {
            doRegister()
        }

        accountText.setOnClickListener {
            val intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)
        }

        selectPhotoButton.setOnClickListener{

        }
    }

    private fun doRegister(){
        val registerAuth = FirebaseAuth.getInstance()
        val email = emailTextRegister.text.toString()
        val password = passwordTextRegister.text.toString()
        val username = usernameTextRegister.text.toString()
        if(email.isNotEmpty() && password.isNotEmpty() && username.isNotEmpty()){
            registerAuth.createUserWithEmailAndPassword(email,password).addOnSuccessListener {
                println("Registration Successful for uid: ${it.user?.uid}")
            }.addOnFailureListener {
                Toast.makeText(this,"Please check the inputs that you wrote.",Toast.LENGTH_LONG).show()
                println(it.localizedMessage.toString())
            }
        }else{
            Toast.makeText(this,"Please fill the inputs correctly.",Toast.LENGTH_LONG).show()
        }
    }
}

