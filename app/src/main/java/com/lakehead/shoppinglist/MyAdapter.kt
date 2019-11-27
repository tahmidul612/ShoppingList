package com.lakehead.shoppinglist

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class MyAdapter(private var dataSet: MutableList<String>, var listName:String, var userId: String) : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    class MyViewHolder(val layoutView: LinearLayout) : RecyclerView.ViewHolder(layoutView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val layoutView = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_view_item, parent, false) as LinearLayout

        return MyViewHolder(layoutView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        //TODO: update this adapter to better display the data from each String entry of the MutableList
        val nameView:TextView = holder.layoutView.getChildAt(0) as TextView
        val quanView:TextView = holder.layoutView.getChildAt(1) as TextView
        val costView:TextView = holder.layoutView.getChildAt(2) as TextView
        val editBtn: ImageButton = holder.layoutView.getChildAt(3) as ImageButton
        val deleteBtn: ImageButton = holder.layoutView.getChildAt(4) as ImageButton

        val currentItem = dataSet[position].split("\t")

        if(currentItem.size == 3){
            nameView.text = currentItem[0]
            quanView.text = currentItem[1]
            costView.text = currentItem[2]

            deleteBtn.setOnClickListener{

                //removeItemFromList(userId, listName, currentItem[0], currentItem[2].toDouble(), currentItem[1].toInt())

            }

            editBtn.setOnClickListener{

                //TODO: Write the edit function

            }

        }else if (currentItem.size == 1)
            nameView.text = "Empty Row"
        else
            nameView.text = "Item Listing Error"



    }

    //Internal function to remove a list item from the FireStore database
    fun removeItemFromList(user: String? = "user_null", listName: String, itemName: String, itemCost: Double, itemQuantity: Int){

        if (user != null){

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


    override fun getItemCount() = dataSet.size

}