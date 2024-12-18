package com.example.auyrma.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.RecyclerView
import com.example.auyrma.view.adapter.HospitalAdapter
import com.example.auyrma.R
import com.example.auyrma.view.adapter.SessionAdapter
import com.example.auyrma.databinding.FragmentFavoriteListBinding
import com.example.auyrma.model.datasource.ApiSource
import com.example.auyrma.model.entity.Dr
import com.example.auyrma.model.entity.Hospital
import com.example.auyrma.model.entity.Session
import com.example.auyrma.view.adapter.FavoriteAdapter
import com.google.android.material.tabs.TabLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class FavoriteListFragment : Fragment() {

    private var _binding: FragmentFavoriteListBinding? = null
    private val binding: FragmentFavoriteListBinding get() = _binding!!
    private var adapter: RecyclerView.Adapter<*>? = null


    companion object {
        fun newInstance() = FavoriteListFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        _binding = FragmentFavoriteListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupTabs()
        fetchSessionList(mapOf("user" to "2"))
    }
    private fun setupTabs() {
        val tabLayout = binding.tabLayout

        tabLayout.addTab(tabLayout.newTab().setText("Sessions"))
        tabLayout.addTab(tabLayout.newTab().setText("Hospitals"))
        tabLayout.addTab(tabLayout.newTab().setText("Doctors"))

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.position) {
                    0 -> fetchSessionList(mapOf("user" to "2"))
                    1 -> fetchHospitalFavouriteList()
                    2 -> fetchDoctorFavouriteList()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

    }
    private fun fetchHospitalFavouriteList() {
        ApiSource.client.fetchFavoriteHospitals().enqueue(object : Callback<List<Hospital>> {
            override fun onResponse(call: Call<List<Hospital>>, response: Response<List<Hospital>>) {
                if (response.isSuccessful) {
                    val hospitalList = response.body()
                    if (hospitalList != null) {
                        adapter = HospitalAdapter(
                            onSessionClickListener = {
                                val sessionDetailFragment = SessionDetailFragment.newInstance(it.name)

                                val toolbar = requireActivity().findViewById<Toolbar>(R.id.toolbar)
                                toolbar.visibility = View.GONE

                                requireActivity().supportFragmentManager
                                    .beginTransaction()
                                    .replace(R.id.fragment_container_view, SessionDetailFragment.newInstance(it.name))
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
                        binding.recyclerViewSessions.adapter = adapter
                        (adapter as HospitalAdapter).submitList(hospitalList)
                    }
                    else {
                        binding.recyclerViewSessions.adapter = adapter
                        (adapter as HospitalAdapter).submitList(emptyList())
                    }
                }
            }

            override fun onFailure(call: Call<List<Hospital>>, t: Throwable) {
                println("Error: ${t.message}")
            }
        })
    }
    private fun fetchDoctorFavouriteList() {
        ApiSource.client.fetchFavoriteDoctor().enqueue(object : Callback<List<Dr>> {
            override fun onResponse(call: Call<List<Dr>>, response: Response<List<Dr>>) {
                if (response.isSuccessful) {
                    val drList = response.body()
                    if (drList != null) {
                        adapter = FavoriteAdapter()
                        drList.forEach {
                            it.isFavorite = it.favorites.contains(2)
                        }
                        binding.recyclerViewSessions.adapter = adapter
                        (adapter as FavoriteAdapter).submitList(drList)
                    }
                    else {
                        binding.recyclerViewSessions.adapter = adapter
                        (adapter as FavoriteAdapter).submitList(emptyList())
                    }
                }
            }

            override fun onFailure(call: Call<List<Dr>>, t: Throwable) {
                println("Error: ${t.message}")
            }
        })
    }
    private fun fetchSessionList(params: Map<String, String>) {
        var sessionList: List<Session>? = null
        ApiSource.client.fetchSessions(params).enqueue(object : Callback<List<Session>> {
            override fun onResponse(call: Call<List<Session>>, response: Response<List<Session>>) {
                if (response.isSuccessful && response.body() != null) {
                    sessionList = response.body()
                    adapter = SessionAdapter(
                        removeSessionState = {

                        }
                    )
                    binding.recyclerViewSessions.adapter = adapter
                    (adapter as SessionAdapter).submitList(sessionList)
                } else {
                    println("Error: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<List<Session>>, t: Throwable) {
                println("Failed: ${t.message}")
            }
        })
    }

    private fun changeFavouriteState(hospitalId: Int, isFavourite: Boolean) {
        fetchHospitalFavouriteList()
        val message = if (isFavourite) {
            "Hospital with ID $hospitalId added to favorites"
        } else {
            "Hospital with ID $hospitalId removed from favorites"
        }
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
    private fun changeFavouriteStateDoctor(drId: Int, isFavourite: Boolean) {
        fetchDoctorFavouriteList()
        val message = if (isFavourite) {
            "Doctor with ID $drId added to favorites"
        } else {
            "Doctor with ID $drId removed from favorites"
        }
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}