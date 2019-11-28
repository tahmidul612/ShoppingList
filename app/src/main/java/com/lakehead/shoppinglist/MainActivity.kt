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

    //Internal function to create a new user for the FireStore database
//    private fun createUserDocument(user:String){
//
//        //Check to see if a user already exists by retrieving info from FireStore:
//        val userRef = db.collection("users").document(user)
//        userRef.get()
//            .addOnSuccessListener { document ->
//                if (document.exists()) {
//
//                    //Welcomes back an existing user
//                    Toast.makeText(applicationContext, "Welcome Back!", Toast.LENGTH_LONG).show();
//
//                }
//                else //The user does not exist, create them:
//                {
//
//                    //Create a temporary entry to define the structure of the list entries:
//                    val tempEntry = hashMapOf(
//                        "itemName" to "Blah",
//                        "itemCost" to 3.00,
//                        "itemQuantity" to 1
//                    )
//
//                    val data = hashMapOf(
//
//                        "list1" to hashMapOf(
//                            "0" to tempEntry
//                        )
//
//                    )
//
//                    //Create the user file if it does not exist, and add the starting list
//                    db.collection("users").document(user).set(data, SetOptions.merge())
//
//                    //Remove the temporary entry: will convert list from hashmap to an array internally within FireStore.
//                    db.collection("users").document(user).update("list1", FieldValue.arrayRemove(tempEntry))
//
//                    Toast.makeText(applicationContext, "Created a starting list for you!",Toast.LENGTH_LONG).show()
//
//                }
//            }
//            .addOnFailureListener { exception ->
//                Toast.makeText(applicationContext, "Error connecting to the database.", Toast.LENGTH_LONG).show()
//            }
//
//    }
//
//    private fun createList(userId: String?) {
//        db.collection("users").document("user_$userId")
//            .get()
//            .addOnSuccessListener { document ->
//                if (document.data != null) {
//
//                    //get the list data
//                    var data = document.data!![currentList].toString()
//
//                    //TODO: Create a regex to replace this ugly shit:
//                    //Remove unwanted tokens:
//                    data = data.replace("{", "")
//                    data = data.replace("}", "")
//                    data = data.replace("[", "")
//                    data = data.replace("]", "")
//                    data = data.replace(",", "")
//                    data = data.replace(" ", "")
//                    //Replace itemQuantity and itemCost labels:
//                    data = data.replace("itemQuantity=", "\t")
//                    data = data.replace("itemCost=", "\t\$")
//
//                    //TODO: parse the new items into mutable list of strings more efficiently
//                    //Split the string into a mutable list of strings, separated by entry
//                    val newItems = data.split("itemName=")
//                    items = newItems.toMutableList();
//
//                    //Remove the first item in the list, which for some reason is always empty.
//                    if (items[0].isBlank())
//                        items.removeAt(0)
//
//                    //Populate the RecyclerView with item list:
//                    viewManager = LinearLayoutManager(this)
//                    val dividerItemDecoration = DividerItemDecoration(
//                        my_recycler_view.context,
//                        1
//                    )
//                    viewAdapter = MyAdapter(items, currentList,"user_$userId", this)
//                    recyclerView = my_recycler_view.apply {
//                        layoutManager = viewManager
//                        adapter = viewAdapter
//                    }
//                    my_recycler_view.addItemDecoration(dividerItemDecoration)
//
//                }
//            }.addOnFailureListener{
//
//                //Populate the RecyclerView with item list:
//                viewManager = LinearLayoutManager(this)
//                viewAdapter = MyAdapter(items, currentList,"user_$userId", this)
//                recyclerView = my_recycler_view.apply {
//                    layoutManager = viewManager
//                    adapter = viewAdapter
//                }
//
//            }
//    }

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
        val inputItemName     = inputLayout.findViewById<EditText>(R.id.inputName)
        val inputItemCost     = inputLayout.findViewById<EditText>(R.id.inputCost)
        val inputItemQuantity = inputLayout.findViewById<EditText>(R.id.inputQuantity)

        builder.setView(inputLayout)

        //Sets the action when "Submit" is pressed:
        builder.setPositiveButton("Submit") { _, _ ->
            //Get the raw input values to be added to the database:
//            val itemTxt  = inputItemName.text.toString()
//            val costAmt:Double = inputItemCost.text.toString().toDouble()
//            val quanAmt:Int    = inputItemQuantity.text.toString().toInt()

//            //Add the entry to the list, first to FireStore then the local list in the same format:
//            addItemToList(userId, listName, itemTxt, costAmt, quanAmt)
//            items.add("$itemTxt\t$quanAmt\t\$$costAmt")

            //Using a data class to store data
            val item = hashMapOf(
                inputItemName.text.toString() to itemDataClass(
                inputItemCost.text.toString().toDouble(),
                inputItemQuantity.text.toString().toInt()
                )
            )
            db.collection(userId).document(listName).set(item)
            updateRecyclerView(userId)
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

    private fun updateRecyclerView(userId: String) {
//        Populate the RecyclerView with item list:
        viewManager = LinearLayoutManager(this)
        val dividerItemDecoration = DividerItemDecoration(
            my_recycler_view.context,
            1
        )
        val items = db.collection(userId).document(currentList).get()
        viewAdapter = MyAdapter(items, currentList, "user_$userId", this)
        recyclerView = my_recycler_view.apply {
            layoutManager = viewManager
            adapter = viewAdapter
        }
        my_recycler_view.addItemDecoration(dividerItemDecoration)
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


