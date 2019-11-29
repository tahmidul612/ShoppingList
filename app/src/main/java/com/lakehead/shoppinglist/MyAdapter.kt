package com.lakehead.shoppinglist

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MyAdapter(
    private var dataSet: MutableMap<String, MainActivity.itemData>,
    private var listName: String,
    private var userId: String?,
    private var applicationContext: Activity
) : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    class MyViewHolder(val layoutView: LinearLayout) : RecyclerView.ViewHolder(layoutView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val layoutView = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_view_item, parent, false) as LinearLayout
        return MyViewHolder(layoutView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        //TODO: update this adapter to better display the data from each String entry of the MutableList
        val nameView: TextView = holder.layoutView.getChildAt(0) as TextView
        val quanView: TextView = holder.layoutView.getChildAt(1) as TextView
        val costView: TextView = holder.layoutView.getChildAt(2) as TextView

        val currentItem = dataSet.toList()[position]

        nameView.text = currentItem.first
        quanView.text = currentItem.second.itemQuantity.toString()
        costView.text = currentItem.second.itemCost.toString()
        //Removed the $ from cost to make sure toDouble() does not cause a crash

//            deleteBtn.setOnClickListener{
//
//                //Update the FireStore database, then the local dataset to reflect those changes:
//                removeItemFromList(userId, listName, itemName, itemCost.toDouble(), itemQuantity.toInt())
//                dataSet.removeAt(position)
//
//                //Update the view to reflect changes:
//                notifyItemRemoved(position)
//                notifyItemRangeChanged(position, itemCount - position)
//
//            }


    }

    override fun getItemCount(): Int {
        return dataSet.count()
    }

    fun removeAt(adapterPosition: Int) {
        notifyItemRemoved(adapterPosition)
        notifyItemRangeChanged(adapterPosition, itemCount - adapterPosition)
    }
}
