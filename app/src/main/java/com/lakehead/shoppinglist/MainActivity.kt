package com.lakehead.shoppinglist

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    //Connect to FireStore Database to Retrieve List Items:
    private val db = FirebaseFirestore.getInstance()
    private var currentList: String = "Primary List"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(bottom_app_bar)

        //Ensure user is logged in:

        val userId:String? = intent.getStringExtra("userId")
        if (userId == null){
            //User ID not found, return user to the login activity to re-sign-in.
            finish()
            startActivity(LoginActivity.getLaunchIntent(this))
        }else{
            //User ID found, activity can continue.
            Toast.makeText(applicationContext, "Signed in with UID $userId", Toast.LENGTH_LONG).show()
        }

//        //Ensure the user has a FireStore document:
//        createUserDocument("user_$userId") //Should only create if the user does not already have a document in FireStore
//
//        // Access FireStore and create a list of items
//        createList(userId)

        add_item_button.setOnClickListener {

            addItemDialog("user_$userId", currentList)

        }

    }

    data class itemDataClass(
        val itemCost: Double? = null,
        val itemQuantity: Int? = null
    )

    private fun addItemDialog(userId: String, listName: String) {

        //Create a new alert dialog
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Please enter item details:")
        val inflater = layoutInflater

        //Get a layout for inputting multiple values:
        val inputLayout = inflater.inflate(R.layout.input_item_view, null) as LinearLayout
        val inputItemNameLayout = inputLayout.findViewById<EditText>(R.id.inputName)
        val inputItemCostLayout = inputLayout.findViewById<EditText>(R.id.inputCost)
        val inputItemQuantityLayout = inputLayout.findViewById<EditText>(R.id.inputQuantity)

        builder.setView(inputLayout)

        //Sets the action when "Submit" is pressed:
        builder.setPositiveButton("Submit") { _, _ ->

            val inputItemName = inputItemNameLayout.text.toString()
            val inputItemCost = inputItemCostLayout.text.toString().toDouble()
            val inputItemQuantity = inputItemQuantityLayout.text.toString().toInt()
            val item = itemDataClass(inputItemCost, inputItemQuantity)

            db
                .collection("users").document(userId)
                .collection(listName).document(inputItemName).set(item)
            updateRecyclerView(userId, inputItemName, inputItemCost, inputItemQuantity)
        }
        //Sets the action when "Cancel" is pressed:
        builder.setNeutralButton("Cancel") { _, _ ->
            //Display a message to the user saying they cancelled input.
            Toast.makeText(applicationContext, "Cancelled", Toast.LENGTH_LONG).show()
        }

        //Display the dialog box:
        val dialog = builder.create()
        dialog.show()

    }

    private fun updateRecyclerView(
        userId: String,
        itemName: String,
        itemCost: Double,
        itemQuantity: Int
    ) {
//        Populate the RecyclerView with item list:
        viewManager = LinearLayoutManager(this)
        val dividerItemDecoration = DividerItemDecoration(
            my_recycler_view.context,
            1
        )
        db
            .collection("users").document(userId)
            .collection(currentList).document(itemName).get()
            .addOnSuccessListener { documentSnapshot ->
                val item = documentSnapshot.toObject(itemDataClass::class.java)
                viewAdapter = MyAdapter(itemName, itemCost, itemQuantity, this)
                recyclerView = my_recycler_view.apply {
                    layoutManager = viewManager
                    adapter = viewAdapter
                }
                my_recycler_view.addItemDecoration(dividerItemDecoration)
            }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.bottom_app_bar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.app_bar_settings -> {
                val settingsIntent = Intent(this, SettingsActivity::class.java)
                startActivity(settingsIntent)
            }
        }
        return true
    }

    companion object {
        fun getLaunchIntent(from: Context) = Intent(from, MainActivity::class.java)
    }

//    Internal function to add a new list item to the FireStore database
//    fun addItemToList(user : String, listName: String, itemName: String, itemCost: Double, itemQuantity: Int){
//
//        //Ensure user is logged in (their token is not null):
//        if (user != null){
//
//            val userDoc = db.collection("users").document(user)
//
//            //Update the list with the new entry:
//            val newEntry = object {
//                val itemName = itemName
//                val itemCost = itemCost
//                val itemQuantity = itemQuantity
//            }
//
//            userDoc.update(listName, FieldValue.arrayUnion(newEntry))
//
//            return
//
//        }
//        else{
//            finish()
//        }
//
//    }
}


