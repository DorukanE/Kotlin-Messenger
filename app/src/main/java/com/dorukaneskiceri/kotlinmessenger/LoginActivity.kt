package com.dorukaneskiceri.kotlinmessenger

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loginButton.setOnClickListener {
            doLogin()
        }

        backToRegisterText.setOnClickListener{
            finish()
        }
    }

    private fun doLogin(){
        val loginAuth = FirebaseAuth.getInstance()
        val email = emailLogin.text.toString()
        val password = passwordLogin.text.toString()
        if(email.isNotEmpty() && password.isNotEmpty()){
            loginAuth.signInWithEmailAndPassword(email,password).addOnSuccessListener {
                println("Sign In completed.")
                Toast.makeText(this,"Sign in successful.",Toast.LENGTH_LONG).show()
            }.addOnFailureListener {
                Toast.makeText(this,"Username or Password is incorrect.",Toast.LENGTH_LONG).show()
                println(it.localizedMessage.toString())
            }
        }else{
            Toast.makeText(this,"Please fill the inputs correctly.",Toast.LENGTH_LONG).show()
        }
    }
}
