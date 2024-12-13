package com.example.auyrma.view.adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.auyrma.model.entity.Hospital

class HospitalItemCallback() : DiffUtil.ItemCallback<Hospital>() {
    override fun areItemsTheSame(oldItem: Hospital, newItem: Hospital): Boolean {
        return oldItem.hospitalId == newItem.hospitalId
    }

    override fun areContentsTheSame(oldItem: Hospital, newItem: Hospital): Boolean {
        return oldItem.hospitalId == newItem.hospitalId
    }

}