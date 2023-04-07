package com.example.grievify

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.AutoText
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.getSystemService
import com.example.grievify.data.TicketData
import com.example.grievify.databinding.ActivityCreateComplaintBinding
import com.example.grievify.databinding.ActivityMainBinding
import com.example.grievify.databinding.ComplaintBoxBinding
import com.example.grievify.databinding.DropdownMenuCategoryItemBinding
import com.example.grievify.utils.CheckInternet
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

class CreateComplaint : AppCompatActivity() {
    private lateinit var database: DatabaseReference
    private var userUID: String? = ""

    private lateinit var baos: ByteArrayOutputStream
    private lateinit var binding: ActivityCreateComplaintBinding
    private lateinit var categoryDropDown: AutoCompleteTextView
    private lateinit var priorityDropDown: AutoCompleteTextView
    private lateinit var title:TextInputEditText
    private lateinit var description: TextInputEditText
    private lateinit var complain: ExtendedFloatingActionButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_complaint)
        binding = ActivityCreateComplaintBinding.inflate(layoutInflater)
        categoryDropDown = findViewById(R.id.categoryDropDown)
        priorityDropDown = findViewById(R.id.priorityDropDown)
        title = findViewById(R.id.textFieldTitle)
        description = findViewById(R.id.textFieldDesc)
        populateDropDown()
        val user = Firebase.auth.currentUser
        userUID = user?.uid
        complain = findViewById(R.id.fab)
        complain.setOnClickListener {if (checkInternet()) {
            //setProgressBar()
            val dtb =
                FirebaseDatabase.getInstance("https://grievify-default-rtdb.firebaseio.com/").reference
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Are you sure?")
            builder.setMessage("Your complaint will be posted for reviewing")
            builder.setPositiveButton("Yes") { _, _ ->
                setData()
            }
            builder.setNegativeButton("No") { _, _ ->
            }
            builder.show()
        }
    }

    }
    private fun populateDropDown() {
        //Populate dropDown category list
        val categories = resources.getStringArray(R.array.Categories)
        val categoriesAdapter = ArrayAdapter(this, R.layout.dropdown_menu_category_item, categories)
        categoryDropDown.setAdapter(categoriesAdapter)
        val conditions = resources.getStringArray(R.array.Priorities)
        val conditionsAdapter = ArrayAdapter(this, R.layout.dropdown_menu_category_item, conditions)
        priorityDropDown.setAdapter(conditionsAdapter)
    }

    private fun setData() {

        val dNow = Date()
        val ft = SimpleDateFormat("yyMMddhhmmssMs")
        val datetime: String = ft.format(dNow)

        var ticketID = "ticket" + datetime
//        setProgressBar()
        val dataObject = getData(ticketID)
        if (dataObject == null) {
//            deleteProgressBar()
            return
        }
        database =
            FirebaseDatabase.getInstance("https://grievify-default-rtdb.firebaseio.com/")
                .getReference("tickets")
        val uploadData = database.child(ticketID).setValue(dataObject)
//        Handler(Looper.getMainLooper()).postDelayed({
//            if (progressCircular?.visibility == View.VISIBLE) {
//                Toast.makeText(this, "Listing Failed, Please try again", Toast.LENGTH_LONG).show()
//                deleteProgressBar()
//                FirebaseDatabase.getInstance().purgeOutstandingWrites()
//            }
//
//        }, 180000)
        uploadData.addOnSuccessListener {

            makeToast("Successfully Listed")
//                deleteProgressBar()
            closeKeyboard()
            binding.sellMainScrollView.visibility=View.GONE
//              binding.successAnimationView.visibility=View.VISIBLE
            binding.fab.visibility=View.INVISIBLE
            supportActionBar?.hide()
//              binding.successAnimationView.playAnimation()
            Handler(Looper.getMainLooper()).postDelayed({

                onBackPressed()
               finish()
            }, 2000)
        }.addOnFailureListener {
            makeToast("Listing Failed, Please try again")
            //deleteProgressBar()
        }

    }

    private fun getData(ticketID: String): TicketData? {
        var flag = true
        val titleName =  title.text.toString().trim()
        if (titleName == "") {
            binding.inputName.error = "This field is required"

            flag = false
        } else {
            binding.inputName.error = null
        }
        var category =  categoryDropDown.text.toString()
        if(category=="Student Welfare")
        {
            category="SW"
        }
        else if(category=="Academic")
        {
            category="Acad"
        }
        else
        {
            category="Admin"
        }
        val priority =  priorityDropDown.text.toString()


        val desc = description.text.toString().trim()
        if (desc == "") {
            binding.inputDesc.error = "This field is required"
            flag = false
        } else {
            binding.inputDesc.error = null
        }
        val username = Firebase.auth.currentUser?.displayName
        val arrayList = ArrayList<String>()//Creating an empty arraylist
        arrayList.add("null")
        if (flag) {
            val currentDate = SimpleDateFormat("dd-MM-yyyy").format(Date())


            return TicketData(
                titleName,
                category,
                desc,
                arrayList,
                null,
                priority,
                null,
                "Assigned",
                ticketID,
                userUID, username
            )
        } else {
            return null
        }
    }
    private fun makeToast(value: String) {

        Toast.makeText(applicationContext, value, Toast.LENGTH_LONG).show()
    }
    private fun checkInternet(): Boolean {
        if (CheckInternet.isConnectedToInternet(applicationContext)) {
            Toast.makeText(
                applicationContext, "Something went wrong! Check your network...",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }
        return true
    }
    private fun closeKeyboard() {
        // this will give us the view
        // which is currently focus
        // in this layout
        val view = this.currentFocus

        // if nothing is currently
        // focus then this will protect
        // the app from crash
        if (view != null) {

            // now assign the system
            // service to InputMethodManager
            val manager: InputMethodManager = getSystemService(
                Context.INPUT_METHOD_SERVICE
            ) as InputMethodManager
            manager
                .hideSoftInputFromWindow(
                    view.windowToken, 0
                )
        }
    }
}