package com.example.grievify.ui.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.grievify.MainActivity
import com.example.grievify.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class SignUpActivity : AppCompatActivity() {
    lateinit var signUpBinding: ActivitySignUpBinding
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    val database:FirebaseDatabase=FirebaseDatabase.getInstance()
    val reference:DatabaseReference=database.reference.child("Users")

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
                val user= Firebase.auth.currentUser?.uid.toString()
                var name=signUpBinding.userName.text.toString()
                var scholarId=signUpBinding.scholarId.text.toString()
                val userEmail = signUpBinding.email.text.toString()
                reference.child(user).child("name").setValue(name)
                reference.child(user).child("scholarId").setValue(scholarId)
                reference.child(user).child("email").setValue(userEmail)
                val intent= Intent(this@SignUpActivity, LoginActivity::class.java)
                startActivity(intent)





            } else {
                Toast.makeText(
                    applicationContext,
                    task.exception?.toString(),
                    Toast.LENGTH_LONG)
            }
        }
    }
}