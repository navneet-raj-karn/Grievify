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
        fetchData()
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
    private fun fetchData() {
        binding.recyclerView.adapter =
            context?.let { it1 -> ComplaintAdapter(it1, itemList) }
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        val user=Firebase.auth.currentUser?.uid.toString()
        val database = FirebaseDatabase.getInstance()
        val myReference: DatabaseReference = database.reference.child("tickets")
        myReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                binding.animationView.visibility=View.GONE
                itemList.clear()
                if (snapshot.exists()) {
                    for (cartItemIDs in snapshot.children) {

                        println(cartItemIDs.value.toString())
                        val item = cartItemIDs.getValue(TicketData::class.java)
                        if (item != null) {
                            if(item.userID==user)
                            {
                                itemList.add(item)
                            }

                        }
                        binding.recyclerView.adapter?.notifyDataSetChanged()
                    }
                }

            }

            override fun onCancelled(error: DatabaseError) {

            }

        }
        )
    }
}