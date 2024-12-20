package com.example.auyrma.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import com.bumptech.glide.Glide
import com.example.auyrma.R
import com.example.auyrma.databinding.FragmentHospitalDetailBinding
import com.example.auyrma.model.datasource.ApiSource
import com.example.auyrma.model.entity.DrResponse
import com.example.auyrma.model.entity.Hospital
import com.example.auyrma.view.adapter.FavoriteAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class HospitalDetailFragment : Fragment() {
    private var _binding: FragmentHospitalDetailBinding? = null
    private val binding get() = _binding!!
    private var adapter: FavoriteAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    companion object {
        private const val KEY_HOSPITAL = "hospital_key"
        fun newInstance(hospital: Hospital) = HospitalDetailFragment().apply {
            arguments = bundleOf(KEY_HOSPITAL to hospital)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHospitalDetailBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = FavoriteAdapter(
            onSessionClickListener = {
                val toolbar = requireActivity().findViewById<Toolbar>(R.id.toolbar)
                toolbar.visibility = View.GONE

                requireActivity().supportFragmentManager
                    .beginTransaction()
                    .replace(
                        R.id.fragment_container_view,
                        SessionDetailFragment.newInstance(it, true)
                    )
                    .addToBackStack(null)
                    .commit()
            },
        )
        binding.recyclerViewDr.adapter = adapter
        val hospital = arguments?.getSerializable(KEY_HOSPITAL) as? Hospital
        viewHospital(hospital)
        fetchDrList(mapOf("hospital" to hospital?.hospitalId.toString()))

    }

    private fun viewHospital(hospital: Hospital?){
        binding.hospitalName.text = hospital?.name
        binding.bedCount.text = "Bed count: " + hospital?.hospitalBedCount.toString()
        binding.rating.text = "Rating: " + hospital?.rating.toString()
        binding.streetAddress.text = hospital?.streetAddress
        binding.city.text = hospital?.city

        binding.backButton.setOnClickListener {
            val toolbar = requireActivity().findViewById<Toolbar>(R.id.toolbar)
            toolbar.visibility = View.VISIBLE
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        Glide.
        with(binding.root.context)
            .load(hospital?.imageUrl)
            .placeholder(R.drawable.placeholder_image)
            .into(binding.srcImage)
    }


    private fun fetchDrList(params: Map<String, String>) {
        var minPrice: Int = 5000
        var maxPrice: Int = 100000
        ApiSource.client.fetchMedicsWithParams2(params).enqueue(object : Callback<DrResponse> {
            override fun onResponse(call: Call<DrResponse>, response: Response<DrResponse>) {
                if (response.isSuccessful) {
                    val drList = response.body()?.results
                    if (!drList.isNullOrEmpty()) {
                        drList.forEach {
                            it.isFavorite = it.favorites.contains(2)
                        }
                        maxPrice = drList.maxOf { it.price }
                        minPrice = drList.minOf { it.price }
                        binding.drSalary.text = minPrice.toString() + " - " + maxPrice.toString() + " tenge"
                        adapter?.submitList(drList)
                    }
                }
            }
            override fun onFailure(call: Call<DrResponse>, t: Throwable) {
                println("Error: ${t.message}")
            }
        })
    }
}