package com.example.grievify.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.grievify.ChatBot
import com.example.grievify.CreateComplaint
import com.example.grievify.R
import com.example.grievify.databinding.FragmentHomeBinding
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentHomeBinding.inflate(layoutInflater)
        val chatBotActivate: ExtendedFloatingActionButton = binding.sellButton
        chatBotActivate.setOnClickListener {
            val intent = Intent(requireContext(),ChatBot::class.java)
            startActivity(intent)
            activity?.finish()
        }
        binding.register.setOnClickListener {
            //Toast.makeText(applicationContext,"clicked", Toast.LENGTH_SHORT).show()
        }

        binding.list.setOnClickListener {
            //Toast.makeText(applicationContext,"clicked", Toast.LENGTH_SHORT).show()
        }
        binding.complain.setOnClickListener {
            val intent = Intent(requireContext(), CreateComplaint::class.java)
            startActivity(intent)
            activity?.finish()
        }
        return binding.root

    }


}