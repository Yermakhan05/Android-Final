package com.example.auyrma.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.auyrma.view.adapter.PharmacyAdapter
import com.example.auyrma.R
import com.example.auyrma.databinding.FragmentHospitalListBinding
import com.example.auyrma.model.datasource.ApiSource
import com.example.auyrma.model.entity.Hospital
import com.example.auyrma.model.entity.Pharmacy
import com.example.auyrma.view.adapter.HospitalAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class HospitalListFragment() : Fragment() {

    private var _binding: FragmentHospitalListBinding? = null
    private val binding: FragmentHospitalListBinding get() = _binding!!
    private var selectedCity: String? = null
    private var adapter: RecyclerView.Adapter<*>? = null

    companion object {
        private const val ARG_CITY = "arg_city"

        fun newInstance(city: String): HospitalListFragment {
            val fragment = HospitalListFragment()
            val args = Bundle()
            args.putString(ARG_CITY, city)
            fragment.arguments = args
            return fragment
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            selectedCity = it.getString(ARG_CITY, "Almaty")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHospitalListBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        parentFragmentManager.setFragmentResultListener("requestKey", this) { _, bundle ->
            selectedCity = bundle.getString("selectedCity", "Almaty")
            updateRecyclerView(binding.toggleGroup.checkedButtonId)
        }

        binding.toggleGroup.check(R.id.toggle_hospital)

        binding.toggleGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                updateRecyclerView(checkedId)
            }
        }

        updateRecyclerView(binding.toggleGroup.checkedButtonId)
        search()
    }

    private fun fetchHospitalList(params: Map<String, String>) {
        val mergedParams = mapOf(
            "city" to selectedCity
        ) + params
        ApiSource.client.fetchHospitals(mergedParams).enqueue(object : Callback<List<Hospital>> {
            override fun onResponse(call: Call<List<Hospital>>, response: Response<List<Hospital>>) {
                if (response.isSuccessful) {
                    val hospitalList = response.body()
                    if (hospitalList != null) {
                        adapter = HospitalAdapter(
                            onSessionClickListener = {
                                val toolbar = requireActivity().findViewById<Toolbar>(R.id.toolbar)
                                toolbar.visibility = View.GONE

                                requireActivity().supportFragmentManager
                                    .beginTransaction()
                                    .replace(R.id.fragment_container_view, HospitalDetailFragment.newInstance(it))
                                    .addToBackStack(null)
                                    .commit()
                            },
                            onChangeFavouriteState = { hospital, isFavourite ->
                                changeFavouriteState(hospital.hospitalId, isFavourite)
                            }
                        )
                        hospitalList.forEach {
                            it.isFavorite = it.favorites.contains(2)
                        }
                        binding.recyclerView.adapter = adapter
                        (adapter as HospitalAdapter).submitList(hospitalList)
                    }
                    else {
                        adapter = null
                    }
                }
            }

            override fun onFailure(call: Call<List<Hospital>>, t: Throwable) {
                println("Error: ${t.message}")
            }
        })
    }

    private fun fetchPharmacyList(params: Map<String, String>) {
        val mergedParams = mapOf(
            "city" to selectedCity
        ) + params
        ApiSource.client.fetchPharmacies(mergedParams).enqueue(object : Callback<List<Pharmacy>> {
            override fun onResponse(call: Call<List<Pharmacy>>, response: Response<List<Pharmacy>>) {
                if (response.isSuccessful) {
                    val pharmacyList = response.body()
                    adapter = PharmacyAdapter()
                    binding.recyclerView.adapter = adapter
                    if (pharmacyList != null) {
                        (adapter as PharmacyAdapter).submitList(pharmacyList)
                    }
                    else {
                        (adapter as? PharmacyAdapter)?.submitList(emptyList())
                    }
                }
            }

            override fun onFailure(call: Call<List<Pharmacy>>, t: Throwable) {
                println("Error: ${t.message}")
            }
        })
    }
    private fun changeFavouriteState(drId: Int, isFavourite: Boolean) {
        fetchHospitalList(mapOf())
        val message = if (isFavourite) {
            "Hospital with ID $drId added to favorites"
        } else {
            "Hospital with ID $drId removed from favorites"
        }
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun search() {
        val searchView = requireActivity().findViewById<SearchView>(R.id.search_view)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    filterList(it)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    filterList(it)
                }
                return true
            }
        })
    }

    private fun filterList(query: String) {
        when (adapter) {
            is HospitalAdapter -> {
                val params = mapOf(
                    "search" to query
                )
                fetchHospitalList(params)
            }

            is PharmacyAdapter -> {
                val params = mapOf(
                    "search" to query
                )
                fetchPharmacyList(params)
            }

            else -> {
                (adapter as? HospitalAdapter)?.submitList(emptyList())
                (adapter as? PharmacyAdapter)?.submitList(emptyList())
            }
        }
    }


    private fun updateRecyclerView(checkedId: Int) {
        when (checkedId) {
            binding.toggleHospital.id -> {
                fetchHospitalList(mapOf())
            }

            binding.togglePharmacy.id -> {
                fetchPharmacyList(mapOf())
            }
        }
    }
}