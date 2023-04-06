package com.example.grievify.ui.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.grievify.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth

class SignUpActivity : AppCompatActivity() {
    lateinit var signUpBinding: ActivitySignUpBinding
    val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        signUpBinding = ActivitySignUpBinding.inflate(layoutInflater)
        var view = signUpBinding.root
        super.onCreate(savedInstanceState)
        setContentView(view)
        signUpBinding.signUpButton.setOnClickListener {
            val userEmail = signUpBinding.email.text.toString()
            val userPassword = signUpBinding.password.text.toString()
            signUp(userEmail,userPassword)
        }
    }

    fun signUp(userEmail: String, userPassword: String) {
        auth.createUserWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(
                    applicationContext,
                    "Your account has been created",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    applicationContext,
                    task.exception?.toString(),
                    Toast.LENGTH_LONG)
            }
        }
    }
}