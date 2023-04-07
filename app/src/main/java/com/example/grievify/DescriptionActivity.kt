package com.example.grievify

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.grievify.databinding.ActivityDescriptionBinding
import com.google.firebase.database.*
import java.util.*
import kotlin.collections.ArrayList

class DescriptionActivity : AppCompatActivity() {
    private lateinit var itemID:String
    private lateinit var binding: ActivityDescriptionBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDescriptionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        try {
            this.supportActionBar!!.hide()
        } // catch block to handle NullPointerException
        catch (_: NullPointerException) {
        }
        val extras = intent.extras
        if (extras != null) {
            itemID = extras.getString("key").toString()
            fetchDataFromDataBase(itemID)
        }
        binding.button2.setOnClickListener {
           // showDialog(itemID)

            val builder = AlertDialog.Builder(this)
            builder.setTitle("Are you sure?")
            builder.setMessage("Your complaint will be deleted")
            builder.setPositiveButton("Yes") { _, _ ->
                deleteModel(itemID)
                onBackPressed()
            }
            builder.setNegativeButton("No") { _, _ ->
            }
            builder.show()
        }

        binding.button3.setOnClickListener {
            showDialog(itemID)
        }
    }
    private fun deleteModel(model: String) {
        val databaseProd =
            FirebaseDatabase.getInstance().getReference("tickets")
        databaseProd.child(model).removeValue().addOnSuccessListener {
            val ref = FirebaseDatabase.getInstance().getReference("tickets")
            val query: Query = ref.orderByValue().equalTo(model)
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (snapshot in dataSnapshot.children) {
                        snapshot.ref.removeValue()
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                }
            })
            Toast.makeText(applicationContext, "Item deleted from database", Toast.LENGTH_LONG).show()

        }.addOnFailureListener {
            Toast.makeText(applicationContext, "Error", Toast.LENGTH_LONG).show()
        }

    }



    private fun fetchDataFromDataBase(items: String) {
        val databaseItem =
            FirebaseDatabase.getInstance()
                .getReference("tickets")

        databaseItem.child(items).get().addOnSuccessListener { dataSnapshot ->
            //Toast.makeText(applicationContext,dataSnapshot.child("userID").value.toString(),Toast.LENGTH_SHORT).show()
            println(dataSnapshot)
            if (dataSnapshot.exists()) {
                binding.ticketID.text=dataSnapshot.child("ticketID").value.toString()
                binding.title.text=dataSnapshot.child("title").value.toString()
                binding.username.text = dataSnapshot.child("userName").value.toString()
                    .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }

                binding.severity.text = dataSnapshot.child("priority").value.toString()
                binding.status.text = dataSnapshot.child("status").value.toString()
                binding.descriptionHeading.text = dataSnapshot.child("description").value.toString()
                    .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
                if(binding.status.text=="Resolved")
                {
                    binding.resolvedLayout.visibility= View.VISIBLE
                    binding.resolved.text = dataSnapshot.child("resolvedMsg").value.toString()
                }

                val uid = dataSnapshot.child("userUID").value
                fillUser(uid)
                if(binding.status.text=="Resolved")
                {
                    binding.button3.isEnabled=true
                }
                else if(binding.status.text=="Assigned")
                {
                    binding.button2.isEnabled=true
                }
                println(dataSnapshot.child("docsList").value)
                val imageArray = ArrayList<String>()
                val docsList =ArrayList<String>()
                imageArray.add(dataSnapshot.child("docsList").child("0").value.toString())
                imageArray.add(dataSnapshot.child("docsList").child("1").value.toString())
                imageArray.add(dataSnapshot.child("docsList").child("2").value.toString())
                imageArray.add(dataSnapshot.child("docsList").child("3").value.toString())
                var res ="\n"
                for (i in imageArray) {
                    if(i!="null")
                    {
                        res+=i
                        res+="\n"
                    }
                }

                binding.links.text=res


            }
        }.addOnFailureListener {
            TODO("Not yet implemented")
        }


    }
    private fun showDialog(itemID:String) {

        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.send_message)
        dialog.setCancelable(true)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        val etPost = dialog.findViewById<EditText>(R.id.et_post)
        dialog.findViewById<View>(R.id.bt_cancel)
            .setOnClickListener { dialog.dismiss() }
        dialog.findViewById<View>(R.id.btn_submit).setOnClickListener { _: View? ->
            val customCat = etPost.text.toString().trim { it <= ' ' }
            FirebaseDatabase.getInstance().reference.child("tickets").child(itemID).child("feedback")
                .setValue(customCat).addOnSuccessListener {
                    Toast.makeText(applicationContext,"Message Posted", Toast.LENGTH_SHORT).show()
                }
            dialog.dismiss()
        }
        dialog.show()
        dialog.window!!.attributes = lp

    }

    private fun fillUser(uid: Any?) {
        binding.scholarID.text ="2112129"
        val databaseUser =
            FirebaseDatabase.getInstance()
                .getReference("Users")
        databaseUser.child(uid.toString()).get().addOnSuccessListener { snapshot ->

            if (snapshot.exists()) {
                binding.scholarID.text = snapshot.child("scholarID").value.toString()

            }
        }.addOnFailureListener {
            TODO("Not yet implemented")
        }

    }
}