package com.example.auyrma.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.auyrma.R
import com.example.auyrma.databinding.DrItemCardBinding
import com.example.auyrma.model.datasource.ApiSource
import com.example.auyrma.model.entity.Dr
import com.example.auyrma.model.entity.FavoriteRequest
import com.example.auyrma.model.entity.FavoriteResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FavoriteAdapter(
    private val onSessionClickListener: (Dr) -> Unit,
): ListAdapter<Dr, FavoriteAdapter.ViewHolder>(ItemCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            DrItemCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        println("onBindViewHolder: $position")
        holder.bind(getItem(position))
    }
    inner class ViewHolder(
        private val binding: DrItemCardBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(dr: Dr) {
            with(binding) {
                binding.tvDoctorName.text = (dr.name)
                binding.tvDoctorPrice.text = "₸" + dr.price.toString()
                binding.tvDoctorDetails.text = dr.speciality
                val favoriteRequest = FavoriteRequest(
                    clientId = 2,
                    medicId = dr.id
                )
                if (dr.isFavorite) {
                    favoriteIc.setImageResource(R.drawable.baseline_favorite_24)
                }
                favoriteIc.setOnClickListener {
                    if (dr.isFavorite) {
                        removeFavoriteList(favoriteRequest)
                        favoriteIc.setImageResource(R.drawable.baseline_favorite_border_24)
                        dr.isFavorite = false
                    }
                    else {
                        addFavoriteList(favoriteRequest)
                        favoriteIc.setImageResource(R.drawable.baseline_favorite_24)
                        dr.isFavorite = true
                    }
                }
                btnOpen.setOnClickListener {
                    onSessionClickListener(dr)
                }
                Glide
                    .with(root.context)
                    .load(dr.medicImage)
                    .placeholder(R.drawable.placeholder_image)
                    .into(ivDoctorPhoto)
            }
        }
    }
    private fun addFavoriteList(favoriteRequest: FavoriteRequest) : Boolean {
        var isFavorite: Boolean = false
        ApiSource.client.addFavoriteDr(favoriteRequest).enqueue(object :
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
    private fun removeFavoriteList(favoriteRequest: FavoriteRequest) : Boolean {
        var isFavorite: Boolean = false
        ApiSource.client.removeFavoriteDr(favoriteRequest).enqueue(object :
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