package com.example.auyrma.view.adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.auyrma.model.entity.Session

class SessionItemCallback(): DiffUtil.ItemCallback<Session>() {
    override fun areItemsTheSame(oldItem: Session, newItem: Session): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Session, newItem: Session): Boolean {
        return oldItem.id == newItem.id
    }
}