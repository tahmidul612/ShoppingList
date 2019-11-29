package com.lakehead.shoppinglist

import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
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

    data class itemData(
        val itemCost: Double,
        val itemQuantity: Int
    )

    private var itemList = mutableMapOf<String, itemData>()

    private var currentList: String = "Primary List"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(bottom_app_bar)
        createNotificationChannel()
        val userId = checkUserSignIn()
        add_item_button.setOnClickListener {
            addItemDialog("user_$userId", currentList)
        }

    }

    private fun checkUserSignIn(): String? {
        val userId: String? = intent.getStringExtra("userId")
        if (userId == null) {
            //User ID not found, return user to the login activity to re-sign-in.
            finish()
            startActivity(LoginActivity.getLaunchIntent(applicationContext))
        } else {
            //User ID found, activity can continue.
            Toast.makeText(applicationContext, "Signed in with UID $userId", Toast.LENGTH_LONG)
                .show()
        }
        return userId
    }


    private fun addItemDialog(userId: String, listName: String) {

        //Create a new alert dialog
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Please enter item details:")
        val inflater = layoutInflater

        //Get a layout for inputting multiple values:
        val inputLayout       = inflater.inflate(R.layout.input_item_view, null) as LinearLayout
        val inputItemName     = inputLayout.findViewById<EditText>(R.id.inputName)
        val inputItemCost     = inputLayout.findViewById<EditText>(R.id.inputCost)
        val inputItemQuantity = inputLayout.findViewById<EditText>(R.id.inputQuantity)

        builder.setView(inputLayout)

        //Sets the action when "Submit" is pressed:
        builder.setPositiveButton("Submit") { _, _ ->
            val itemTxt:String
            val quanAmt:Int
            val costAmt:Double
            //Add the entry to the list, first to FireStore then the local list in the same format:

            if(inputItemName.text.isEmpty())
                itemTxt = "No Name"
            else
                itemTxt = inputItemName.text.toString()

            if (inputItemCost.text.isEmpty())
                costAmt = 0.00
            else
                costAmt = inputItemCost.text.toString().toDouble()

            if (inputItemQuantity.text.isEmpty())
                quanAmt = 0
            else
                quanAmt = inputItemQuantity.text.toString().toInt()

            addItemToList(userId, listName, itemTxt, costAmt, quanAmt)
            addToRecyclerView(userId, listName, itemTxt)

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

    private fun addItemToList(
        user: String?,
        listName: String,
        itemName: String,
        itemCost: Double,
        itemQuantity: Int
    ) {
        val item = itemData(itemCost, itemQuantity)
        itemList[itemName] = item
        db
            .collection("users").document("user_$user")
            .collection(listName).document(itemName)
            .set(item)
    }

    private fun addToRecyclerView(
        user: String?,
        listName: String,
        itemName: String
    ) {
        viewManager = LinearLayoutManager(this)
        val dividerItemDecoration = DividerItemDecoration(
            my_recycler_view.context,
            1
        )
        viewAdapter = MyAdapter(itemList, listName, user, this)
        recyclerView = my_recycler_view.apply {
            layoutManager = viewManager
            adapter = viewAdapter
        }
        my_recycler_view.addItemDecoration(dividerItemDecoration)
        viewAdapter.notifyItemInserted(viewManager.childCount - 1)
        val swipeHandler = object : SwipeToDeleteCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = recyclerView.adapter as MyAdapter
                adapter.removeAt(viewHolder.adapterPosition)
                removeFromDatabase(user, listName, itemName)
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun removeFromDatabase(
        user: String?,
        listName: String,
        itemName: String
    ) {
        itemList.remove(itemName)
        db
            .collection("users").document("user_$user")
            .collection(listName).document(itemName).delete()

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.bottom_app_bar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.app_bar_settings -> {
                startActivity(SettingsActivity.getLaunchIntent(this))
            }
        }
        return true
    }

    companion object {
        fun getLaunchIntent(from: Context) = Intent(from, MainActivity::class.java)
    }

    private val CHANNEL_ID = "personal_notifications"
    private val NOTIFICATION_ID = 1

    override fun onTrimMemory(level: Int) {

        Thread.sleep(5000)
        notifyCall()
        super.onTrimMemory(level)
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = CHANNEL_ID
            val descriptionText = "Come back to the app"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun notifyCall() {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, 0)
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_sms_notification)
            .setContentTitle("LUList")
            .setContentText("Want to prepare a list?")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        val notify1 = NotificationManagerCompat.from(this)
        notify1.notify(NOTIFICATION_ID, builder.build())
    }

}