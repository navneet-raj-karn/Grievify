package com.example.grievify.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.grievify.data.TicketData
import com.example.grievify.databinding.ComplaintBoxBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase

class ComplaintAdapter(private val context: Context?,
                      private var ArrayList: ArrayList<TicketData>
) :
    RecyclerView.Adapter<ComplaintAdapter.ItemsViewHolder>() {

    inner class ItemsViewHolder(val adapterBinding: ComplaintBoxBinding) :
        RecyclerView.ViewHolder(adapterBinding.root)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemsViewHolder {
        val binding=ComplaintBoxBinding.inflate(LayoutInflater.from(parent.context),parent,false)

        return ItemsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemsViewHolder, position: Int) {
        holder.adapterBinding.ticket.text=ArrayList[position].ticketID
        holder.adapterBinding.status.text=ArrayList[position].status
        holder.adapterBinding.Subject.text=ArrayList[position].title
        holder.itemView.setOnClickListener {
            Toast.makeText(context,"clicked",Toast.LENGTH_SHORT).show()
        }
        holder.adapterBinding.deleteButton.setOnClickListener {
            val builder = AlertDialog.Builder(context!!)
            builder.setTitle("Are you sure?")
            builder.setMessage("Your item will be deleted permanently from the database")
            builder.setPositiveButton("Yes") { _, _ ->
                deleteModel(ArrayList[position].ticketID.toString())

            }
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
            Toast.makeText(context, "Item deleted from database", Toast.LENGTH_LONG).show()

        }.addOnFailureListener {
            Toast.makeText(context, "Error", Toast.LENGTH_LONG).show()
        }

    }
    override fun getItemCount(): Int {
        return ArrayList.size
    }

}



