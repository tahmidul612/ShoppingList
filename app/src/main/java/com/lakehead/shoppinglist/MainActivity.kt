package com.lakehead.shoppinglist

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    //Connect to FireStore Database to Retrieve List Items:
    val items = mutableListOf<String>("Onions", "Bread", "Chicken")
    val db = FirebaseFirestore.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(bottom_app_bar)
        // Access a Cloud Firestore instance from your Activity
        viewManager = LinearLayoutManager(this)
        viewAdapter = MyAdapter(items)
        recyclerView = my_recycler_view.apply {
            layoutManager = viewManager
            adapter = viewAdapter
        }

        //Ensure user is logged in:
        val userId:String? = intent.getStringExtra("userId")
        if (userId == null){
            finish()
            Toast.makeText(applicationContext, "Failed to sign in. Redirecting to Login.", Toast.LENGTH_LONG).show()
            startActivity(LoginActivity.getLaunchIntent(applicationContext))
        }else{
            Toast.makeText(applicationContext, "Signed in with UID $userId", Toast.LENGTH_LONG).show()
        }

        //Testing Firestore:
        createUser("user_" + userId)
        addItemToList("user_$userId", "list1", "Apple", 3.00, 1)

        addNewItemBtnListener()

    }

    private fun addNewItemBtnListener() {
        add_item_button.setOnClickListener {

            //Create a new alert dialog
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Please enter item details:")
            val inflater = layoutInflater

            //Get a layout for inputting multiple values:
            val inputLayout = inflater.inflate(R.layout.input_item_view, null)
            val inputItemName = inputLayout.findViewById<EditText>(R.id.inputName)
            //val inputItemCost = inputLayout.findViewById<EditText>(R.id.inputCost)
            //val inputItemQuantity = inputLayout.findViewById<EditText>(R.id.inputQuantity)

            builder.setView(inputLayout)

            builder.setPositiveButton("Submit") { _, _ ->
                val itemTxt = inputItemName.text
                items.add(itemTxt.toString())
                viewAdapter.notifyItemInserted(items.size - 1)
            }
            builder.setNeutralButton("Cancel") { _, _ ->
                Toast.makeText(applicationContext, "Cancelled", Toast.LENGTH_LONG).show()
            }

            //Display the dialog box:
            val dialog = builder.create()
            dialog.show()
        }
    }

    //Internal function to create a new user for the FireStore database
    private fun createUser(user:String){

        //Check to see if a user already exists by retrieving info from FireStore:
        val userRef = db.collection("users").document(user)
        userRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {

                    // Do nothing, the user already exists

                }
                else //The user does not exist, create them:
                {

                    //Create a temporary entry to define the structure of the list entries:
                    val tempEntry = hashMapOf(
                        "itemName" to "Blah",
                        "itemCost" to 3.00,
                        "itemQuantity" to 1
                    )

                    val data = hashMapOf(

                        "list1" to hashMapOf(
                            "0" to tempEntry
                        )

                    )

                    //Create the user file if it does not exist, and add the starting list
                    db.collection("users").document(user).set(data, SetOptions.merge())

                    //Remove the temporary entry: will convert list from hashmap to an array internally within FireStore.
                    db.collection("users").document(user).update("list1", FieldValue.arrayRemove(tempEntry))

                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(applicationContext, "Error connecting to the database.", Toast.LENGTH_LONG).show()
            }

    }


    //Internal function to add a new list item to the FireStore database
    private fun addItemToList(user : String, listName: String, itemName: String, itemCost: Double, itemQuantity: Int){

        //Ensure user is logged in (their token is not null):
        if (user != null){

            val userDoc = db.collection("users").document(user)

            //Update the list with the new entry:
            val newEntry = object {
                val itemName = itemName
                val itemCost = itemCost
                val itemQuantity = itemQuantity
            }

            userDoc.update(listName, FieldValue.arrayUnion(newEntry))

        }
        else{
            finish()
        }

    }

    //Internal function to remove a list item from the FireStore database
    private fun removeItemFromList(user: String, listName: String, itemName: String, itemCost: Double, itemQuantity: String){

        if (user!=null){

            val userDoc = db.collection("users").document(user)

            val entryToDelete = object {
                val itemName = itemName
                val itemCost = itemCost
                val itemQuantity = itemQuantity
            }

            userDoc.update(listName, FieldValue.arrayRemove(entryToDelete))

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
        fun getLaunchIntent(from: Context) = Intent(from, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
    }
}
