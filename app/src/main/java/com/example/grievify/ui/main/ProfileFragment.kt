package com.example.grievify.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.grievify.CreateComplaint
import com.example.grievify.MainActivity
import com.example.grievify.R
import com.example.grievify.data.UserData
import com.example.grievify.databinding.FragmentComplainListBinding
import com.example.grievify.databinding.FragmentProfileBinding
import com.example.grievify.ui.auth.LoginActivity
import com.google.firebase.auth.FirebaseAuth
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
        binding.buttonLogOut.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }
        return binding.root
    }

    private fun retriveDataFromDatabase() {
        valueEventListener = object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                val user = snapshot.child(Firebase.auth.currentUser?.uid.toString()).getValue(
                    UserData::class.java)
                if (user != null){
                    binding.EMAIL.text=user.email
                    println("Hello ${user.name}")
                    binding.USERNAME.text=user.name
                    binding.dept.text=user.scholarId
                }


            }

            override fun onCancelled(error: DatabaseError) {

            }

        }
        myReference.addValueEventListener(valueEventListener!!)
    }

}