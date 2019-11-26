package com.lakehead.shoppinglist

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
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
    val db = FirebaseFirestore.getInstance()
    var items:MutableList<String> = mutableListOf("Add a new Entry!")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(bottom_app_bar)

        //Ensure user is logged in:
        val userId:String? = intent.getStringExtra("userId")
        if (userId == null){
            finish()
            Toast.makeText(applicationContext, "Sign-in Failure. Redirecting to Login.", Toast.LENGTH_LONG).show()
            startActivity(LoginActivity.getLaunchIntent(applicationContext))
        }else{
            Toast.makeText(applicationContext, "Signed in with UID $userId", Toast.LENGTH_LONG).show()
        }

        // Access FireStore and create a list of items
        val userDoc = db.collection("users").document("user_$userId")
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    //TODO: list the items
                    var data = document.data!!["list1"].toString()
                    //TODO: Create a regex to replace this ugly shit:
                    data = data.replace("{", "")
                    data = data.replace("}", "")
                    data = data.replace("[", "")
                    data = data.replace("]", "")
                    data = data.replace(",", "")
                    data = data.replace("itemQuantity=", ", ")
                    data = data.replace("itemCost=", ", \$")

                    val newItems = data.split("itemName=")

                    //TODO: parse the new items into mutable list more efficiently and add to database

                    viewManager = LinearLayoutManager(this)
                    viewAdapter = MyAdapter(newItems.toMutableList())
                    recyclerView = my_recycler_view.apply {
                        layoutManager = viewManager
                        adapter = viewAdapter
                    }
                } else {
                    //If for some reason there are no entries, put some placeholder data there
                    viewManager = LinearLayoutManager(this)
                    viewAdapter = MyAdapter(items)
                    recyclerView = my_recycler_view.apply {
                        layoutManager = viewManager
                        adapter = viewAdapter
                    }
                }
            }.addOnFailureListener{
                //to do
            }

        //Testing FireStore:
        createUser("user_$userId") //Only creates if the user does not already exist in FireStore
        addItemToList("user_$userId", "list1", "Apple", 3.00, 1) //Example

        val currentList:String = "list1"
        addNewItemBtnListener("user_$userId", currentList)

    }

    private fun addNewItemBtnListener(userId:String, listName:String) {
        add_item_button.setOnClickListener {

            //Create a new alert dialog
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Please enter item details:")
            val inflater = layoutInflater

            //Get a layout for inputting multiple values:
            val inputLayout = inflater.inflate(R.layout.input_item_view, null)
            val inputItemName = inputLayout.findViewById<EditText>(R.id.inputName)
            val inputItemCost = inputLayout.findViewById<EditText>(R.id.inputCost)
            val inputItemQuantity = inputLayout.findViewById<EditText>(R.id.inputQuantity)

            builder.setView(inputLayout)

            builder.setPositiveButton("Submit") { _, _ ->
                val itemTxt        = inputItemName.text.toString()
                val costAmt:Double = inputItemCost.text.toString().toDouble()
                val quanAmt:Int    = inputItemQuantity.text.toString().toInt()
                addItemToList(userId, listName, itemTxt, costAmt, quanAmt)
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

                    Toast.makeText(applicationContext, "Welcome Back!", Toast.LENGTH_LONG).show();

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

                    Toast.makeText(applicationContext, "Created a starting list for you!",Toast.LENGTH_LONG).show()

                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(applicationContext, "Error connecting to the database.", Toast.LENGTH_LONG).show()
            }

    }


    //Internal function to add a new list item to the FireStore database
    private fun addItemToList(user : String?, listName: String, itemName: String, itemCost: Double, itemQuantity: Int){

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
    private fun removeItemFromList(user: String?, listName: String, itemName: String, itemCost: Double, itemQuantity: String){

        if (user != null){

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
