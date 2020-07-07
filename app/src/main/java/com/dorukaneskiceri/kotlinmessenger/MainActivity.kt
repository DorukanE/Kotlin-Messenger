package com.dorukaneskiceri.kotlinmessenger

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        registerButton.setOnClickListener {
            val email = emailTextRegister.text.toString()
            val password = passwordTextRegister.text.toString()
            println("Email: $email")
            println("Password: $password")
        }

        accountText.setOnClickListener {
            val intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)
        }
    }
}
