package com.example.auyrma.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.auyrma.R
import com.example.auyrma.databinding.DentistryCardItemBinding
import com.example.auyrma.model.entity.Pharmacy

class PharmacyAdapter() : ListAdapter<Pharmacy, PharmacyAdapter.ViewHolder>(PharmacyItemCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            DentistryCardItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        println("onBindViewHolder: $position")
        holder.bind(getItem(position))
    }

    inner class ViewHolder(
        private val binding: DentistryCardItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Pharmacy) {
            with(binding) {
                tvName.text = (item.name).split(" ").joinToString("\n")
                addressD.text = item.address.replaceFirst("/", "\n")
                ratingD.text = "Rating: ${item.rating}"
                phoneNumber.text = item.phoneNumber
                email.text = item.email

                Glide.with(root.context)
                    .load(item.imageUrl)
                    .placeholder(R.drawable.placeholder_image)
                    .into(pharmacyImage)
            }
        }

    }
}