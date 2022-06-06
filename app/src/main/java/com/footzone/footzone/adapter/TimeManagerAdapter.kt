package com.footzone.footzone.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.footzone.footzone.R
import com.footzone.footzone.databinding.ItemChooseTimeViewGreyBinding
import com.footzone.footzone.databinding.ItemChooseTimeViewWhiteBinding
import com.footzone.footzone.model.TimeManager

class TimeManagerAdapter(private var onItemClicked: ((Int, ItemChooseTimeViewWhiteBinding, TimeManager) -> Unit)) :
    ListAdapter<TimeManager, RecyclerView.ViewHolder>(DiffUtil()) {
    private val TYPE_ITEM_WHITE = 1001
    private val TYPE_ITEM_GREY = 1002

    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)
        return if (item.status == "ACCEPTED" || item.status == "PENDING") {
            TYPE_ITEM_GREY
        } else {
            TYPE_ITEM_WHITE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (viewType == TYPE_ITEM_WHITE) {
            val view = ItemChooseTimeViewWhiteBinding.inflate(LayoutInflater.from(parent.context),
                parent,
                false)
            ViewHolder.TimeManagerViewHolderWhite(view)
        } else {
            val view = ItemChooseTimeViewGreyBinding.inflate(LayoutInflater.from(parent.context),
                parent,
                false)
            ViewHolder.TimeManagerViewHolderGrey(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (holder) {
            is ViewHolder.TimeManagerViewHolderWhite -> {
                holder.view.tvStartTime.text = item.startTime.toString()
                holder.view.tvFinishTime.text = item.finishTime.toString()

                holder.view.linearFreeToBook.setOnClickListener {
                    onItemClicked.invoke(position, holder.view, item)
                }
            }

            is ViewHolder.TimeManagerViewHolderGrey -> {
                holder.view.tvStartTime.text = item.startTime.toString()
                holder.view.tvFinishTime.text = item.finishTime.toString()
            }
        }
    }


    class DiffUtil : androidx.recyclerview.widget.DiffUtil.ItemCallback<TimeManager>() {
        override fun areItemsTheSame(oldItem: TimeManager, newItem: TimeManager): Boolean {
            return oldItem.startTime == newItem.startTime
        }

        override fun areContentsTheSame(oldItem: TimeManager, newItem: TimeManager): Boolean {
            return oldItem == newItem
        }
    }

    sealed class ViewHolder(binding: ViewBinding) : RecyclerView.ViewHolder(binding.root) {
        class TimeManagerViewHolderWhite(val view: ItemChooseTimeViewWhiteBinding) :
            ViewHolder(view)

        class TimeManagerViewHolderGrey(val view: ItemChooseTimeViewGreyBinding) : ViewHolder(view)
    }
}
