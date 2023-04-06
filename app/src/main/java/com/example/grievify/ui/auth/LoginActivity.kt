package com.example.grievify.ui.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.grievify.MainActivity
import com.example.grievify.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {


    lateinit var loginBinding:ActivityLoginBinding
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginBinding= ActivityLoginBinding.inflate(layoutInflater)
        val view=loginBinding.root
        setContentView(view)

        loginBinding.register.setOnClickListener {
            val intent= Intent(this@LoginActivity, SignUpActivity::class.java)
            startActivity(intent)
        }

        loginBinding.loginButton.setOnClickListener {
            val userEmail=loginBinding.email.text.toString()
            val userPassword=loginBinding.password.text.toString()
            logIn(userEmail, userPassword )
        }
    }

    fun logIn(userEmail:String,userPassword:String){
        auth.signInWithEmailAndPassword(userEmail, userPassword)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Toast.makeText(
                        applicationContext,
                        "Login is successful",
                        Toast.LENGTH_SHORT
                    ).show()
                    val intent=Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(intent)

                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(
                        applicationContext,
                        task.exception?.toString(),
                        Toast.LENGTH_LONG)

                }
            }
    }
}