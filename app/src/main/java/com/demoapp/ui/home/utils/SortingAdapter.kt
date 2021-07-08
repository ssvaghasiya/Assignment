package com.demoapp.ui.home.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.demoapp.R
import com.demoapp.databinding.ItemSideMenuBinding

class SortingAdapter() : RecyclerView.Adapter<SortingAdapter.MyViewHolder>() {

    private lateinit var mEventListener: EventListener

    private var data = mutableListOf<String>()
    lateinit var context: Context
    private var selectedPos = 0

    constructor(context: Context) : this() {
        this.context = context
    }

    fun setEventListener(eventListener: EventListener) {
        mEventListener = eventListener
    }

    interface EventListener {
        fun onItemClick(pos: Int, item: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(context)
        val itemBinding = DataBindingUtil.inflate<ItemSideMenuBinding>(
            inflater,
            R.layout.item_side_menu, parent, false
        )
        return MyViewHolder(itemBinding)
    }


    override fun getItemCount(): Int {
        return data.size
    }

    fun getItem(p: Int): String {
        return data[p]

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = getItem(position)
        try {

        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

        holder.itemBinding.root.setOnClickListener {

        }
    }

    fun addAll(mData: List<String>) {
        data.clear()
        data.addAll(mData)
        notifyDataSetChanged()
    }

    fun clear() {
        data.clear()
        notifyDataSetChanged()
    }

    fun updateSelectedPos(pos: Int) {
        selectedPos = pos
        notifyDataSetChanged()
    }

    inner class MyViewHolder(internal var itemBinding: ItemSideMenuBinding) :
        RecyclerView.ViewHolder(itemBinding.root)
}