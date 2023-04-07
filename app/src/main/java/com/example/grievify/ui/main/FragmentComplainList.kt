package com.example.grievify.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
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
    private fun fragmentload(fragment : Fragment)
    {

        val fragmentTransaction = parentFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.mainFrameLayout, fragment)
        fragmentTransaction.commit()

    }
    private fun retriveDataFromDatabase() {
        val database: FirebaseDatabase = FirebaseDatabase.getInstance()
        val myReference: DatabaseReference =database.reference.child("tickets")
        myReference.get().addOnSuccessListener {
            val user=Firebase.auth.currentUser?.uid.toString()
            itemList.clear()
            for(eachData in it.children){
                val item=eachData.getValue(TicketData::class.java)

                if(item!=null && item.userID==user){
                    itemList.add(item)
                }
                itemsAdapter= ComplaintAdapter(requireContext(),itemList)
                binding.recyclerView.layoutManager= LinearLayoutManager(context)
                binding.recyclerView.adapter=itemsAdapter
            }
            binding.animationView.cancelAnimation()
            binding.animationView.visibility=View.GONE
        }.addOnFailureListener {
            Toast.makeText(context, "Please try again!", Toast.LENGTH_SHORT).show()

        }
    }
}