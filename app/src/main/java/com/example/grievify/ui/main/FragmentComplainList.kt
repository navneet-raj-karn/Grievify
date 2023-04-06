package com.example.grievify.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.grievify.R
import com.example.grievify.adapters.ComplaintAdapter
import com.example.grievify.data.TicketData
import com.example.grievify.databinding.FragmentComplainListBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase



class FragmentComplainList : Fragment() {

    val itemList=ArrayList<TicketData>()
    lateinit var itemsAdapter:ComplaintAdapter

    private var viewBinding: FragmentComplainListBinding?=null
    private val binding get()= viewBinding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = FragmentComplainListBinding.inflate(inflater, container, false)
        retriveDataFromDatabase()
        return binding.root
    }

    private fun retriveDataFromDatabase() {
        val database: FirebaseDatabase = FirebaseDatabase.getInstance()
        val myReference: DatabaseReference =database.reference.child("tickets")
        myReference.get().addOnSuccessListener {

        }
    }
}