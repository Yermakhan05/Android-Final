package com.example.auyrma.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.auyrma.R
import com.example.auyrma.databinding.DrSessionCardBinding
import com.example.auyrma.model.entity.Dr
import com.example.auyrma.model.entity.Session


class SessionAdapter(
    private val removeSessionState: (session: Session) -> Unit,
) : ListAdapter<Session, SessionAdapter.ViewHolder>(SessionItemCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            DrSessionCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        println("onBindViewHolder: $position")
        holder.bind(getItem(position))
    }
    inner class ViewHolder(
        private val binding: DrSessionCardBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Session) {
            with(binding) {
                doctorName.text = (item.medics.name).split(" ").joinToString("\n")
                doctorSpeciality.text = item.medics.speciality
                drPrice.text = item.medics.price.toString() + "₸"
                removeSession.setOnClickListener {
                    removeSessionState(item)
                }
                sessionDate.text = item.appointment

                Glide
                    .with(root.context)
                    .load(item.medics.medicImage)
                    .placeholder(R.drawable.placeholder_image)
                    .into(person);

//                Glide.with(this) // Pass the context
//                    .load("https://example.com/image.jpg") // URL of the image
//                    .placeholder(R.drawable.placeholder) // Placeholder image while loading
//                    .error(R.drawable.error_image) // Error image if loading fails
//                    .into(binding.imageView)
            }
        }
    }
}