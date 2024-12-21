package com.example.auyrma.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.auyrma.databinding.ItemCardHospitalBinding
import com.bumptech.glide.Glide
import com.example.auyrma.R
import com.example.auyrma.model.datasource.ApiSource
import com.example.auyrma.model.entity.FavoriteRequestToHospital
import com.example.auyrma.model.entity.FavoriteResponse
import com.example.auyrma.model.entity.Hospital
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HospitalAdapter(
    private val onSessionClickListener: ((Hospital) -> Unit)? = null,
    private val userId: Int?
): ListAdapter<Hospital, HospitalAdapter.ViewHolder>(HospitalItemCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemCardHospitalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        println("onBindViewHolder: $position")
        holder.bind(getItem(position))
    }

    inner class ViewHolder(
        private val binding: ItemCardHospitalBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Hospital) {
            with(binding) {
                tvHospitalName.text = item.name

                tvHospitalAddress.text = "${item.streetAddress}, ${item.city}"

                tvBedCount.text = "Beds: ${item.hospitalBedCount}"

                tvRating.text = item.rating.toString()
                val favoriteRequest = userId?.let {
                    FavoriteRequestToHospital(
                        clientId = it,
                        hospitalId = item.hospitalId
                    )
                }
                favoriteHospital.setOnClickListener {
                    if (item.isFavorite) {
                        if (favoriteRequest != null) {
                            removeFavoriteList(favoriteRequest)
                        }
                        favoriteHospital.setImageResource(R.drawable.baseline_favorite_border_24)
                        item.isFavorite = false
                    }
                    else {
                        if (favoriteRequest != null) {
                            addFavoriteList(favoriteRequest)
                        }
                        favoriteHospital.setImageResource(R.drawable.baseline_favorite_24)
                        item.isFavorite = true
                    }
                }
                if (item.isFavorite) {
                    favoriteHospital.setImageResource(R.drawable.baseline_favorite_24)
                }

                hospitalCard.setOnClickListener{
                    onSessionClickListener?.let { it1 -> it1(item) }
                }

                Glide.with(root.context)
                    .load(item.imageUrl)
                    .placeholder(R.drawable.placeholder_image)
                    .into(imageHospital)
            }
        }

    }
    private fun addFavoriteList(favoriteRequest: FavoriteRequestToHospital) : Boolean {
        var isFavorite: Boolean = false
        ApiSource.client.addFavoriteHospital(favoriteRequest).enqueue(object :
            Callback<FavoriteResponse> {
            override fun onResponse(call: Call<FavoriteResponse>, response: Response<FavoriteResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        println("Success: ${it.message}")
                        isFavorite = true
                    }
                } else {
                    println("Error: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<FavoriteResponse>, t: Throwable) {
                println("Failed: ${t.message}")
            }
        })
        return isFavorite
    }
    private fun removeFavoriteList(favoriteRequest: FavoriteRequestToHospital) : Boolean {
        var isFavorite: Boolean = false
        ApiSource.client.removeFavoriteHospital(favoriteRequest).enqueue(object :
            Callback<FavoriteResponse> {
            override fun onResponse(call: Call<FavoriteResponse>, response: Response<FavoriteResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        println("Success: ${it.message}")
                        isFavorite = true
                    }
                } else {
                    println("Error: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<FavoriteResponse>, t: Throwable) {
                println("Failed: ${t.message}")
            }
        })
        return isFavorite
    }
}