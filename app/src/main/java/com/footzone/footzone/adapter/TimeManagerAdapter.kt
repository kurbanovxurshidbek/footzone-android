package com.footzone.footzone.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.footzone.footzone.databinding.ItemChooseTimeViewBlueBinding
import com.footzone.footzone.databinding.ItemChooseTimeViewGreyBinding
import com.footzone.footzone.databinding.ItemChooseTimeViewWhiteBinding
import com.footzone.footzone.model.TimeManager

class TimeManagerAdapter:
    ListAdapter<TimeManager, RecyclerView.ViewHolder>(DiffUtil()) {
    private val TYPE_ITEM_WHITE = 1001
    private val TYPE_ITEM_GREY = 1002
    private val TYPE_ITEM_BLUE = 1003

    override fun getItemViewType(position: Int): Int {
        return if (position % 3 == 0){
            TYPE_ITEM_WHITE
        }else if (position %3 == 1){
            TYPE_ITEM_BLUE
        }else{
            TYPE_ITEM_GREY
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if (viewType == TYPE_ITEM_WHITE){
            val view = ItemChooseTimeViewWhiteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ViewHolder.TimeManagerViewHolderWhite(view)
        }else if (viewType == TYPE_ITEM_GREY){
            val view = ItemChooseTimeViewGreyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ViewHolder.TimeManagerViewHolderGrey(view)
        }else{
            val view = ItemChooseTimeViewBlueBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ViewHolder.TimeManagerViewHolderBlue(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is ViewHolder.TimeManagerViewHolderWhite -> {

            }

            is ViewHolder.TimeManagerViewHolderGrey -> {

            }

            is ViewHolder.TimeManagerViewHolderBlue -> {

            }
        }
    }


    class DiffUtil: androidx.recyclerview.widget.DiffUtil.ItemCallback<TimeManager>(){
        override fun areItemsTheSame(oldItem: TimeManager, newItem: TimeManager): Boolean {
            return oldItem.startTime == newItem.startTime
        }

        override fun areContentsTheSame(oldItem: TimeManager, newItem: TimeManager): Boolean {
            return oldItem == newItem
        }

    }

    sealed class ViewHolder(binding: ViewBinding): RecyclerView.ViewHolder(binding.root) {

        class TimeManagerViewHolderWhite(val view: ItemChooseTimeViewWhiteBinding): ViewHolder(view)
        class TimeManagerViewHolderGrey(val view: ItemChooseTimeViewGreyBinding): ViewHolder(view)
        class TimeManagerViewHolderBlue(val view: ItemChooseTimeViewBlueBinding): ViewHolder(view)

    }
}
