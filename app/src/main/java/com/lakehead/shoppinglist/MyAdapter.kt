package com.lakehead.shoppinglist

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.RecyclerView

class MyAdapter(private var dataSet: MutableList<String>) : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

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

        val currentItem = dataSet[position].split("\t")

        if(currentItem.size == 3){
            nameView.text = currentItem[0]
            quanView.text = currentItem[1]
            costView.text = currentItem[2]
        }else if (currentItem.size == 1)
            nameView.text = "Empty Row"
        else
            nameView.text = "Item Listing Error"

    }

    override fun getItemCount() = dataSet.size

}