package com.lakehead.shoppinglist

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MyAdapter(
    private val itemName: String,
    private val itemCost: Double,
    private val itemQuantity: Int,
    var applicationContext: Activity
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

        nameView.text = itemName
        quanView.text = itemCost.toString()
        costView.text = itemQuantity.toString()

    }

    override fun getItemCount(): Int {
        return 1
    }


}