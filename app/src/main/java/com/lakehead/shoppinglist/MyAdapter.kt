package com.lakehead.shoppinglist

import android.app.Activity
import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class MyAdapter(private var dataSet: MutableList<String>, var listName:String, var userId: String, var applicationContext:Activity) : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    class MyViewHolder(val layoutView: LinearLayout) : RecyclerView.ViewHolder(layoutView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val layoutView = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_view_item, parent, false) as LinearLayout

        return MyViewHolder(layoutView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        //TODO: update this adapter to better display the data from each String entry of the MutableList
        val nameView:TextView      = holder.layoutView.getChildAt(0) as TextView
        val quanView:TextView      = holder.layoutView.getChildAt(1) as TextView
        val costView:TextView      = holder.layoutView.getChildAt(2) as TextView
        val editBtn: ImageButton   = holder.layoutView.getChildAt(3) as ImageButton
        val deleteBtn: ImageButton = holder.layoutView.getChildAt(4) as ImageButton

        val currentItem = dataSet[position].split("\t")

        if(currentItem.size == 3){
            nameView.text = currentItem[0]
            quanView.text = currentItem[1]
            costView.text = currentItem[2]

            val itemName     = currentItem[0]
            val itemQuantity = currentItem[1]
            val itemCost     = currentItem[2].replace("\$", "")
            //Removed the $ from cost to make sure toDouble() does not cause a crash

            deleteBtn.setOnClickListener{

                //Update the FireStore database, then the local dataset to reflect those changes:
                removeItemFromList(userId, listName, itemName, itemCost.toDouble(), itemQuantity.toInt())
                dataSet.removeAt(position)

                //Update the view to reflect changes:
                notifyItemRemoved(position)
                notifyItemRangeChanged(position, itemCount - position)

            }

            editBtn.setOnClickListener{

                //TODO: Write the edit function
                removeItemFromList(userId, listName, itemName, itemCost.toDouble(), itemQuantity.toInt())
                dataSet.removeAt(position)

                //Create a new alert dialog
                val builder = AlertDialog.Builder(applicationContext)
                builder.setTitle("Please enter item details:")
                val inflater = applicationContext.layoutInflater

                //Get a layout for inputting multiple values:
                val inputLayout       = inflater.inflate(R.layout.input_item_view, null) as LinearLayout
                val inputItemName     = inputLayout.findViewById<EditText>(R.id.inputName)
                val inputItemCost     = inputLayout.findViewById<EditText>(R.id.inputCost)
                val inputItemQuantity = inputLayout.findViewById<EditText>(R.id.inputQuantity)

                builder.setView(inputLayout)

                //Sets the action when "Submit" is pressed:
                builder.setPositiveButton("Submit") { _, _ ->
                    //Get the raw input values to be added to the database:
                    val itemTxt        = inputItemName.text.toString()
                    val costAmt:Double = inputItemCost.text.toString().toDouble()
                    val quanAmt:Int    = inputItemQuantity.text.toString().toInt()
                    //Add the entry to the list, first to FireStore then the local list in the same format:
                    addItemToList(userId, listName, itemTxt, costAmt, quanAmt)
                    dataSet.add("$itemTxt\t$quanAmt\t\$$costAmt")

                    //Update the view to reflect changes:
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position, itemCount - position)

                    notifyItemInserted(itemCount - 1)
                    notifyItemRangeChanged(position, itemCount - position)

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

        }else if (currentItem.size == 1)
            nameView.text = "Empty Row"
        else
            nameView.text = "Item Listing Error"



    }

    //Internal function to remove a list item from the FireStore database
    fun removeItemFromList(user: String, listName: String, itemName: String, itemCost: Double, itemQuantity: Int){

        if (user != "user_null" && user != "user_"){

            val db = FirebaseFirestore.getInstance()
            val userDoc = db.collection("users").document(user)

            val entryToDelete = object {
                val itemName = itemName
                val itemCost = itemCost
                val itemQuantity = itemQuantity
            }

            userDoc.update(listName, FieldValue.arrayRemove(entryToDelete))

        }

    }

    //Internal function to add a new list item to the FireStore database
    fun addItemToList(user : String, listName: String, itemName: String, itemCost: Double, itemQuantity: Int) {

        //Ensure user is logged in (their token is not null):
        if (user != "user_null" && user != "user_") {

            val db = FirebaseFirestore.getInstance()
            val userDoc = db.collection("users").document(user)

            //Update the list with the new entry:
            val newEntry = object {
                val itemName = itemName
                val itemCost = itemCost
                val itemQuantity = itemQuantity
            }

            userDoc.update(listName, FieldValue.arrayUnion(newEntry))

            return

        }

    }


    override fun getItemCount() = dataSet.size

}