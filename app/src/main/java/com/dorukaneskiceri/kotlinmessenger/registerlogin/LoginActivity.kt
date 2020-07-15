package com.dorukaneskiceri.kotlinmessenger.registerlogin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.dorukaneskiceri.kotlinmessenger.R
import com.dorukaneskiceri.kotlinmessenger.messages.LatestMessagesActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        supportActionBar?.title = "    Messenger"
        supportActionBar?.setDisplayUseLogoEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setIcon(R.drawable.whatsapp)

        loginButton.setOnClickListener {
            doLogin()
        }

        backToRegisterText.setOnClickListener{
            val intent = Intent(this,
                RegisterActivity::class.java)
            startActivity(intent)
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

                val intent = Intent(this,
                    LatestMessagesActivity::class.java)
                //intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
                //finishActivity()

            }.addOnFailureListener {
                Toast.makeText(this,"Username or Password is incorrect.",Toast.LENGTH_LONG).show()
                println(it.localizedMessage.toString())
            }
        }else{
            Toast.makeText(this,"Please fill the inputs correctly.",Toast.LENGTH_LONG).show()
        }
    }
}
