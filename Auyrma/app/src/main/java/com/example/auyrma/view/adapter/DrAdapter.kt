package com.example.auyrma.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.auyrma.databinding.ItemCardDrBinding
import com.bumptech.glide.Glide
import com.example.auyrma.R
import com.example.auyrma.model.datasource.ApiSource
import com.example.auyrma.model.entity.Dr
import com.example.auyrma.model.entity.FavoriteRequest
import com.example.auyrma.model.entity.FavoriteResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DrAdapter(
    private val onSessionClickListener: (Dr) -> Unit,
    private val onChangeFavouriteStateDoctor: (Dr, Boolean) -> Unit,
    private val context: Context,
    private val userId: Int?
    )
    : ListAdapter<Dr, DrAdapter.ViewHolder>(ItemCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemCardDrBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        println("onBindViewHolder: $position")
        holder.bind(getItem(position))
    }
    inner class ViewHolder(
        private val binding: ItemCardDrBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(dr: Dr) {
            with(binding) {
                binding.doctorName.text = (dr.name).split(" ").joinToString("\n")
                val resourceId = context.resources.getIdentifier(
                    (dr.speciality).toLowerCase(), "drawable", context.packageName
                )
                binding.backgroundImage.setImageResource(resourceId)
                binding.doctorSpeciality.text = dr.speciality
                val favoriteRequest = userId?.let {
                    FavoriteRequest(
                        clientId = it,
                        medicId = dr.id
                    )
                }
                favoriteIcon.setOnClickListener {
                    if (dr.isFavorite) {
                        if (favoriteRequest != null) {
                            removeFavoriteList(favoriteRequest)
                        }
                        favoriteIcon.setImageResource(R.drawable.baseline_favorite_border_24)
                        dr.isFavorite = false
                    }
                    else {
                        if (favoriteRequest != null) {
                            addFavoriteList(favoriteRequest)
                        }
                        favoriteIcon.setImageResource(R.drawable.baseline_favorite_24)
                        dr.isFavorite = true
                    }
                    onChangeFavouriteStateDoctor(dr, !dr.isFavorite)
                }
                if (dr.isFavorite) {
                    favoriteIcon.setImageResource(R.drawable.baseline_favorite_24)
                }
                Glide
                    .with(root.context)
                    .load(dr.medicImage)
                    .placeholder(R.drawable.placeholder_image)
                    .into(person);
                createSession.setOnClickListener {
                    onSessionClickListener(dr)
                }
            }
        }
    }
    private fun addFavoriteList(favoriteRequest: FavoriteRequest) : Boolean {
        var isFavorite: Boolean = false
        ApiSource.client.addFavoriteDr(favoriteRequest).enqueue(object : Callback<FavoriteResponse> {
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
        ApiSource.client.removeFavoriteDr(favoriteRequest).enqueue(object : Callback<FavoriteResponse> {
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