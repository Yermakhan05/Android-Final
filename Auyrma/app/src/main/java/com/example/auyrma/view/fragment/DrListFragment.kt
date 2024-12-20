package com.example.auyrma.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.auyrma.databinding.FragmentDrListBinding
import com.example.auyrma.model.datasource.ApiSource
import com.example.auyrma.view.adapter.DrAdapter
import com.example.auyrma.viewmodel.DrListViewModel
import com.example.auyrma.viewmodel.DrListViewModelFactory
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ImageSpan
import android.widget.LinearLayout
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.example.auyrma.R
import android.widget.FrameLayout
import androidx.annotation.StringRes
import androidx.appcompat.widget.SearchView
import com.example.auyrma.model.entity.Dr
import com.example.auyrma.view.activity.MainActivity

class DrListFragment : Fragment() {
    private var _binding: FragmentDrListBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: DrListViewModel
    private var adapter: DrAdapter? = null
    private var selectedCity: String? = null
    private val categoryList = listOf("All", "Dentist", "Cardiologist", "Therapist", "Psychiatrist")
    private var selectedCategory: String = "All"

    companion object {
        private const val ARG_CITY = "arg_city"

        fun newInstance(city: String): DrListFragment {
            val fragment = DrListFragment()
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
        val factory = DrListViewModelFactory(ApiSource)
        viewModel = ViewModelProvider(this, factory)[DrListViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDrListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = DrAdapter(
            onSessionClickListener = {
                val toolbar = requireActivity().findViewById<Toolbar>(R.id.toolbar)
                toolbar.visibility = View.GONE

                requireActivity().supportFragmentManager
                    .beginTransaction()
                    .replace(
                        R.id.fragment_container_view,
                        SessionDetailFragment.newInstance(dr = it, isFromHospitalDetailPage = false)
                    )
                    .addToBackStack(null)
                    .commit()
            },
            onChangeFavouriteStateDoctor = { dr, isFavourite -> handleFavoriteState(dr.id, isFavourite) },
            requireContext()
        )
        binding.recyclerViewDoctors.adapter = adapter
        viewModel.fetchDoctorList(selectedCity ?: "Almaty", "")

        setupObservers()
        setupCategoryButtons()
        search()
        toOtherPages()
        parentFragmentManager.setFragmentResultListener("requestKey", this) { _, bundle ->
            selectedCity = bundle.getString("selectedCity", "Almaty")
            viewModel.fetchDoctorList(selectedCity ?: "Almaty", selectedCategory)
        }
    }

    private fun toOtherPages() {
        binding.toHospitalList.setOnClickListener {
            (requireActivity() as? MainActivity)?.navigateToHospitalFragment()
        }
        binding.toFavoriteList.setOnClickListener {
            (requireActivity() as? MainActivity)?.navigateToFavoriteFragment()
        }
    }
    private fun search() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    viewModel.fetchDoctorList(selectedCity ?: "Almaty", query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    viewModel.fetchDoctorList(selectedCity ?: "Almaty", newText)
                }
                return true
            }
        })
    }
    private fun setupObservers() {
        viewModel.drListUI.observe(viewLifecycleOwner) { state ->
            when (state) {
                is DrListUI.Loading -> showLoading(state.isLoading)
                is DrListUI.Success -> showDoctors(state.doctorList)
                is DrListUI.Empty -> showEmptyState()
                is DrListUI.Error -> showError(state.errorMessage)
                is DrListUI.DrInserted -> showInsertionMessage(state.doctor)
                else -> {}
            }
        }
    }
    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showDoctors(doctors: List<Dr>) {
        val averagePrice = if (doctors.isNotEmpty()) doctors.sumOf { it.price } / doctors.size else 0
        binding.averagePrice.text = averagePrice.toString()
        val container = binding.priceRangeContainer
        val averageLine = binding.averagePriceLine

        val maxPrice = doctors.maxOf { it.price }
        val minPrice = 0
        container.post {
            val containerWidth = container.width
            val position = ((averagePrice - minPrice).toFloat() / (maxPrice - minPrice) * containerWidth).toInt()

            val layoutParams = averageLine.layoutParams as FrameLayout.LayoutParams
            layoutParams.marginStart = position
            averageLine.layoutParams = layoutParams
        }
        adapter?.submitList(doctors)
    }

    private fun showEmptyState() {
        Toast.makeText(requireContext(), getString(R.string.no_doctors_found), Toast.LENGTH_SHORT).show()
    }
    private fun showError(@StringRes errorMessage: Int) {
        Toast.makeText(context, getString(errorMessage), Toast.LENGTH_SHORT).show()
    }
    private fun showInsertionMessage(doctor: Dr) {
        Toast.makeText(context, "Added ${doctor.name} to favourites", Toast.LENGTH_SHORT).show()
    }

    private fun handleFavoriteState(drId: Int, isFavourite: Boolean) {
        Toast.makeText(
            context,
            if (!isFavourite) "Doctor added to favorites" else "Doctor removed from favorites",
            Toast.LENGTH_SHORT
        ).show()
    }
    private fun setupCategoryButtons() {
        for (category in categoryList) {
            val button = Button(requireContext()).apply {
                text = category
                textSize = 16f
                setPadding(60, 50, 50, 60)
                setTextColor(Color.BLACK)

                if (category != "All") {
                    val categoryImage = getCategoryImage(category)
                    categoryImage?.let {
                        val drawable = it.mutate()
                        val width = 50
                        val height = 50
                        drawable.setBounds(0, 0, width, height)
                        val imageSpan = ImageSpan(drawable)

                        val spannableString = SpannableString("  $category")
                        spannableString.setSpan(imageSpan, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

                        text = spannableString
                    }
                    compoundDrawablePadding = 12
                }
                isAllCaps = false

                setBackgroundResource(
                    if (category == selectedCategory)
                        R.drawable.selected_category_background
                    else
                        R.drawable.category_button_background
                )

                setOnClickListener {
                    handleCategorySelection(category, this)
                }
            }

            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 0, 30, 0)

            button.layoutParams = params

            binding.linearLayoutCategories.addView(button)
        }
    }
    private fun getCategoryImage(category: String): Drawable? {
        val imageName = category.toLowerCase()
        val resId = resources.getIdentifier(imageName, "drawable", requireContext().packageName)
        return if (resId != 0) {
            ContextCompat.getDrawable(requireContext(), resId)
        } else {
            null
        }
    }
    private fun handleCategorySelection(selected: String, button: Button) {
        selectedCategory = selected

        for (i in 0 until binding.linearLayoutCategories.childCount) {
            val childButton = binding.linearLayoutCategories.getChildAt(i) as Button
            childButton.setBackgroundResource(R.drawable.category_button_background)
        }

        button.setBackgroundResource(R.drawable.selected_category_background)
        button.setTextColor(Color.BLACK)
        if (selected == "All") {
            viewModel.fetchDoctorList(selectedCity ?: "Almaty", "")
            return
        }
        viewModel.fetchDoctorList(selectedCity ?: "Almaty", selectedCategory)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
