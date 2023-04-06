package com.example.grievify.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.grievify.R
import com.example.grievify.data.UserData
import com.example.grievify.databinding.FragmentComplainListBinding
import com.example.grievify.databinding.FragmentProfileBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase

class ProfileFragment : Fragment() {

    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    val myReference: DatabaseReference =database.reference.child("Users")

    private var viewBinding: FragmentProfileBinding?=null
    private val binding get()= viewBinding!!
    private var valueEventListener: ValueEventListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = FragmentProfileBinding.inflate(inflater, container, false)
        retriveDataFromDatabase()
        return binding.root
    }

    private fun retriveDataFromDatabase() {
        valueEventListener = object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                val user = snapshot.child(Firebase.auth.currentUser?.uid.toString()).getValue(
                    UserData::class.java)
                if (user != null){
                    binding.email.text=user.email
                    println("Hello ${user.name}")
                    binding.name.text=user.name
                    binding.scholarId.text=user.scholarId
                }


            }

            override fun onCancelled(error: DatabaseError) {

            }

        }
        myReference.addValueEventListener(valueEventListener!!)
    }

}