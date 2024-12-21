package com.example.auyrma.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.auyrma.R
import com.example.auyrma.databinding.FragmentHospitalListBinding
import com.example.auyrma.model.repository.UserRepository
import com.example.auyrma.view.adapter.HospitalAdapter
import com.example.auyrma.view.adapter.PharmacyAdapter
import com.example.auyrma.viewmodel.AuthViewModel
import com.example.auyrma.viewmodel.HospitalListViewModel
import com.example.auyrma.viewmodel.ViewModelFactory

class HospitalListFragment : Fragment() {

    private var _binding: FragmentHospitalListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HospitalListViewModel by viewModels()

    private lateinit var hospitalAdapter: HospitalAdapter
    private lateinit var pharmacyAdapter: PharmacyAdapter

    private lateinit var userRepository: UserRepository
    private lateinit var authViewModel: AuthViewModel

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHospitalListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toggleGroup.check(R.id.toggle_hospital)
        val selectedCity = arguments?.getString(ARG_CITY, "Almaty") ?: "Almaty"
        viewModel.setCity(selectedCity)
        viewModel.fetchHospitals(mapOf())
        val repository = UserRepository(requireContext())
        val factory = ViewModelFactory(repository)
        authViewModel = ViewModelProvider(this, factory).get(AuthViewModel::class.java)
        userRepository = UserRepository(requireContext())
        var userId: Int? = null
        if (isUserAuthenticated()) {
            userId = userRepository.getAuthenticatedUserId()
        } else {
            userId = null
        }

        setupAdapters(userId)
        observeViewModel()
        setupUI()
        setupSearch()
        selectedCityUpdate()
    }

    private fun selectedCityUpdate(){
        parentFragmentManager.setFragmentResultListener("requestKey", this) { _, bundle ->
            val newCity = bundle.getString("selectedCity", "Almaty")
            viewModel.setCity(newCity)
            observeViewModel()
            updateRecyclerView(binding.toggleGroup.checkedButtonId)
        }
    }

    private fun updateRecyclerView(checkedId: Int) {
        when (checkedId) {
            binding.toggleHospital.id -> {
                viewModel.fetchHospitals(mapOf())
            }

            binding.togglePharmacy.id -> {
                viewModel.fetchPharmacies(mapOf())
            }
        }
    }

    private fun setupAdapters(userId: Int?) {
        hospitalAdapter = HospitalAdapter(
            onSessionClickListener = {
                val toolbar = requireActivity().findViewById<Toolbar>(R.id.toolbar)
                toolbar.visibility = View.GONE

                requireActivity().supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fragment_container_view, HospitalDetailFragment.newInstance(it))
                    .addToBackStack(null)
                    .commit()
            },
            userId
        )

        pharmacyAdapter = PharmacyAdapter()
        binding.recyclerView.adapter = hospitalAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
    }

    private fun observeViewModel() {
        viewModel.hospitalList.observe(viewLifecycleOwner) { hospitals ->
            hospitalAdapter.submitList(hospitals)
            binding.recyclerView.adapter = hospitalAdapter
        }

        viewModel.pharmacyList.observe(viewLifecycleOwner) { pharmacies ->
            pharmacyAdapter.submitList(pharmacies)
            binding.recyclerView.adapter = pharmacyAdapter
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupUI() {
        binding.toggleGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    R.id.toggle_hospital -> viewModel.fetchHospitals(mapOf())
                    R.id.toggle_pharmacy -> viewModel.fetchPharmacies(mapOf())
                }
            }
        }
    }
    private fun isUserAuthenticated(): Boolean {
        return userRepository.isUserAuthenticated()
    }

    private fun setupSearch() {
        val searchView = requireActivity().findViewById<SearchView>(R.id.search_view)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { filterList(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { filterList(it) }
                return true
            }
        })
    }

    private fun filterList(query: String) {
        val params = mapOf("search" to query)
        when (binding.toggleGroup.checkedButtonId) {
            R.id.toggle_hospital -> viewModel.fetchHospitals(params)
            R.id.toggle_pharmacy -> viewModel.fetchPharmacies(params)
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}