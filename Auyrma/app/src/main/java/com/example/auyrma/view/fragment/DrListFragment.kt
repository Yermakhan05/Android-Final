package com.example.auyrma.view.fragment

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ImageSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.auyrma.view.activity.MainActivity
import com.example.auyrma.R
import com.example.auyrma.databinding.FragmentDrListBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.auyrma.model.datasource.ApiSource
import com.example.auyrma.model.entity.Dr
import com.example.auyrma.model.entity.DrResponse
import com.example.auyrma.view.adapter.DrAdapter

class DrListFragment : Fragment() {

    private var _binding: FragmentDrListBinding? = null
    private val binding: FragmentDrListBinding get() = _binding!!

    private var adapter: DrAdapter? = null

    private val categoryList = listOf("All", "Dentist", "Cardiologist", "Therapist", "Psychiatrist")
    private var selectedCategory: String = "All"

    private var selectedCity: String? = null
    private var lastList: List<Dr>? = null

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
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
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
                        SessionDetailFragment.newInstance(it.name)
                    )
                    .addToBackStack(null)
                    .commit()

            },
            onChangeFavouriteStateDoctor = { dr, isFavourite ->
                changeFavouriteState(dr.id, isFavourite)
            },
            requireContext()
        )

        binding.recyclerViewDoctors.adapter = adapter

        parentFragmentManager.setFragmentResultListener("requestKey", this) { _, bundle ->
            selectedCity = bundle.getString("selectedCity", "Almaty")
            if (selectedCategory != "All") {
                fetchDrList(mapOf("search" to selectedCategory))
            }
            else {
                fetchDrList(mapOf())
            }
        }

        val searchView = binding.searchView
        searchView.queryHint = "Search here"

        setupCategoryButtons()
        fetchDrList(mapOf())
        search()

        binding.toHospitalList.setOnClickListener {
            (requireActivity() as? MainActivity)?.navigateToHospitalFragment()
        }
    }

    private fun fetchDrList(params: Map<String, String>) {
        val mergedParams = mapOf(
            "city" to selectedCity
        ) + params
        ApiSource.client.fetchMedicsWithParams(mergedParams).enqueue(object : Callback<DrResponse> {
            override fun onResponse(call: Call<DrResponse>, response: Response<DrResponse>) {
                if (response.isSuccessful) {
                    val drList = response.body()?.results
                    if (!drList.isNullOrEmpty()) {
                        drList.forEach {
                            it.isFavorite = it.favorites.contains(2)
                        }
                        adapter?.submitList(drList)
                        lastList = drList
                        drList?.let {
                            val averagePrice = calculateAveragePrice(it);
                            binding.averagePrice.text = averagePrice.toString() + "₸"
                            val container = binding.priceRangeContainer
                            val averageLine = binding.averagePriceLine

                            val maxPrice = it.maxOf { it.price }
                            val minPrice = 0

                            container.post {
                                val containerWidth = container.width
                                val position = ((averagePrice - minPrice).toFloat() / (maxPrice - minPrice) * containerWidth).toInt()

                                val layoutParams = averageLine.layoutParams as FrameLayout.LayoutParams
                                layoutParams.marginStart = position
                                averageLine.layoutParams = layoutParams
                            }
                        }
                    }
                }
            }

            override fun onFailure(call: Call<DrResponse>, t: Throwable) {
                println("Error: ${t.message}")
            }
        })
    }
    private fun changeFavouriteState(drId: Int, isFavourite: Boolean) {
        fetchDrList(mapOf())
        val message = if (isFavourite) {
            "Doctor with ID $drId added to favorites"
        } else {
            "Doctor with ID $drId removed from favorites"
        }
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }


    private fun search() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
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
        val params = mapOf(
            "search" to query
        )
        fetchDrList(params)
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

    private fun handleCategorySelection(selected: String, button: Button) {
        selectedCategory = selected

        for (i in 0 until binding.linearLayoutCategories.childCount) {
            val childButton = binding.linearLayoutCategories.getChildAt(i) as Button
            childButton.setBackgroundResource(R.drawable.category_button_background)
        }

        button.setBackgroundResource(R.drawable.selected_category_background)
        button.setTextColor(Color.BLACK)

        if (selected == "All") {
            fetchDrList(
                mapOf(
                    "search" to ""
                )
            )
            return
        }
        fetchDrList(mapOf("search" to selected))
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
    private fun calculateAveragePrice(drList: List<Dr>): Int {
        return if (drList.isNotEmpty()) {
            drList.sumOf { it.price } / drList.size
        } else {
            0
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}