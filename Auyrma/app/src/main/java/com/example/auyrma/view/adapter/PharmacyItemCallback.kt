package com.example.auyrma.view.adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.auyrma.model.entity.Pharmacy

class PharmacyItemCallback() : DiffUtil.ItemCallback<Pharmacy>(){
    override fun areItemsTheSame(oldItem: Pharmacy, newItem: Pharmacy): Boolean {
        return oldItem.dentistryId == newItem.dentistryId
    }

    override fun areContentsTheSame(oldItem: Pharmacy, newItem: Pharmacy): Boolean {
        return oldItem.dentistryId == newItem.dentistryId
    }

}