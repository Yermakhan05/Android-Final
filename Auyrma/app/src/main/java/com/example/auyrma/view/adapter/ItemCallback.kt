package com.example.auyrma.view.adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.auyrma.model.entity.Dr

class ItemCallback() : DiffUtil.ItemCallback<Dr>() {
    override fun areItemsTheSame(oldItem: Dr, newItem: Dr): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Dr, newItem: Dr): Boolean {
        return oldItem.id == newItem.id
    }

}