package com.example.grievify

import android.app.Activity
import android.app.ProgressDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.grievify.data.TicketData
import com.example.grievify.databinding.ActivityCreateComplaintBinding
import com.example.grievify.utils.CheckInternet
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class CreateComplaint : AppCompatActivity() {
    private lateinit var database: DatabaseReference
    private var userUID: String? = ""

    private lateinit var baos: ByteArrayOutputStream
    private lateinit var binding: ActivityCreateComplaintBinding
    private lateinit var categoryDropDown: AutoCompleteTextView
    private lateinit var priorityDropDown: AutoCompleteTextView
    private lateinit var title: TextInputEditText
    private lateinit var description: TextInputEditText
    private lateinit var complain: ExtendedFloatingActionButton
    private lateinit var userName: String
    private val PICK_PDF_REQUEST = 5000
    private val arrayList = ArrayList<String>()
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
        val databaseUser =
            FirebaseDatabase.getInstance()
                .getReference("Users")
        databaseUser.child(userUID.toString()).get().addOnSuccessListener { snapshot ->

            if (snapshot.exists()) {
                userName = snapshot.child("name").value.toString()

            }
        }.addOnFailureListener {
            TODO("Not yet implemented")
        }
        complain = findViewById(R.id.fab)
        complain.setOnClickListener {
            if (checkInternet()) {
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
        findViewById<Button>(R.id.mediaUpload).setOnClickListener {
//            val i=Intent()
//           i.type="pdf/*"
//            i.action=Intent.ACTION_GET_CONTENT
//            //intent.setType("application/pdf");
//            startActivityForResult(Intent.createChooser(i,"Select pdf"),5000)
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "application/pdf"
            startActivityForResult(Intent.createChooser(intent, "Select PDF"), PICK_PDF_REQUEST)


        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_PDF_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val selectedPdfUri = data.data
            selectedPdfUri?.let { uploadPdf(it) }
        }
    }

    //
//    private lateinit var uploadTask:UploadTask
//    private fun upload(fileuri: Uri?) {
//        //val storageRef = Firebase.storage.reference
//        val dNow = Date()
//        val ft = SimpleDateFormat("yyMMddhhmmssMs")
//        val datetime: String = ft.format(dNow)
//        val filename= "file$datetime"
//        val pd = ProgressDialog(this);
//        pd.setMessage("Sit back and relax,we are processing");
//        pd.show()
//        pd.setCancelable(false)
//        val storage = FirebaseStorage.getInstance()
//        val file = Uri.fromFile(File(fileuri.toString()))
//        val storageRef = storage.reference.child("path/to/file.pdf")
//        makeToast(file.toString())
////        storageRef.putFile(file)
////            .addOnSuccessListener { taskSnapshot ->
////                pd.hide()
////                // Handle successful upload
////            }
////            .addOnFailureListener { exception ->
////                // Handle failed upload
////            }
//
////        uploadTask = storageRef.child("Complaint Docs/$filename").putFile(fileuri!!)
////        uploadTask.addOnSuccessListener {
////            pd.hide()
////        }
////        Handler(Looper.getMainLooper()).postDelayed({
////
////            onBackPressed()
////            finish()
////        }, 3000)
//
//    }
    private fun uploadPdf(pdfUri: Uri) {
        val storage = FirebaseStorage.getInstance()
        val pd = ProgressDialog(this);
        val dNow = Date()
        val ft = SimpleDateFormat("yyMMddhhmmssMs")
        val datetime: String = ft.format(dNow)
        val filename= "file$datetime"
        pd.setMessage("Sit back and relax,we are processing");
        pd.show()
        pd.setCancelable(false)
        val storageRef = storage.reference.child("docs/$filename.pdf")
        storageRef.putFile(pdfUri)
            .addOnSuccessListener { taskSnapshot ->

                pd.hide()
                downloadUrl("docs/$filename.pdf")
                // Handle successful upload
            }
            .addOnFailureListener { exception ->
                // Handle failed upload
            }

    }
    private fun downloadUrl(path:String)
    {
        val storageRef = FirebaseStorage.getInstance().reference.child(path)
        storageRef.downloadUrl
            .addOnSuccessListener { uri ->
                val downloadUrl = uri.toString()
                //makeToast(downloadUrl)
                arrayList.add(downloadUrl)
                // Use the download URL here
            }
            .addOnFailureListener { exception ->
                // Handle the failure
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
            binding.sellMainScrollView.visibility = View.GONE
//              binding.successAnimationView.visibility=View.VISIBLE
            binding.fab.visibility = View.INVISIBLE
//              binding.successAnimationView.playAnimation()
            Handler(Looper.getMainLooper()).postDelayed({

                onBackPressed()
                finish()
            }, 1000)
        }.addOnFailureListener {
            makeToast("Listing Failed, Please try again")
            //deleteProgressBar()
        }

    }

    private fun getData(ticketID: String): TicketData? {
        var flag = true
        val titleName = title.text.toString().trim()
        if (titleName == "") {
            binding.inputName.error = "This field is required"

            flag = false
        } else {
            binding.inputName.error = null
        }
        var category = categoryDropDown.text.toString()
        if (category == "Student Welfare") {
            category = "SW"
        } else if (category == "Academic") {
            category = "Acad"
        } else {
            category = "Admin"
        }
        val priority = priorityDropDown.text.toString()


        val desc = description.text.toString().trim()
        if (desc == "") {
            binding.inputDesc.error = "This field is required"
            flag = false
        } else {
            binding.inputDesc.error = null
        }
        val username = Firebase.auth.currentUser?.displayName
        //Creating an empty arraylist
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
                userUID, userName
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