package com.lakehead.shoppinglist

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*

//Comment to Test Commits

class MainActivity : AppCompatActivity() {
    val items = mutableListOf<String>("Onions", "Bread", "Chicken")
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

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
        addNewItem()
    }

    private fun addNewItem() {
        add_item_button.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Please enter item name")
            val input = EditText(this)
            input.inputType = InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE
            builder.setView(input)
            builder.setPositiveButton("OK") { _, _ ->
                val itemTxt = input.text
                items.add(itemTxt.toString())
                viewAdapter.notifyItemInserted(items.size - 1)
            }
            builder.setNeutralButton("Cancel") { _, _ ->
                Toast.makeText(applicationContext, "Cancelled", Toast.LENGTH_LONG).show()
            }
            val dialog = builder.create()
            dialog.show()
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
