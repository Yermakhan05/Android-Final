package com.example.auyrma.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.example.auyrma.R
import com.example.auyrma.databinding.FragmentSessionDetailBinding
import com.google.android.material.datepicker.MaterialDatePicker
import org.threeten.bp.Instant
import org.threeten.bp.LocalDate
import org.threeten.bp.ZoneId

class SessionDetailFragment : Fragment() {
    private var _binding: FragmentSessionDetailBinding? = null
    private val binding get() = _binding!!

    private var selectedDate: LocalDate? = null
    private var selectedTime: String? = null
    private val availableTimes = listOf("10:00", "11:00", "14:00", "16:00")

    companion object {
        private const val KEY_MOVIE_TITLE = "movie_title"

        fun newInstance(movieTitle: String) = SessionDetailFragment().apply {
            arguments = bundleOf(KEY_MOVIE_TITLE to movieTitle)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSessionDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.doctorName.text = "Dr. John Doe"
        binding.doctorSpeciality.text = "Cardiologist"
        binding.doctorPrice.text = "Price: 5000 ₸"

        setupSpinner()

        binding.selectDateButton.setOnClickListener {
            showDatePickerDialog()
        }
        binding.backButton.setOnClickListener {
            val toolbar = requireActivity().findViewById<Toolbar>(R.id.toolbar)
            toolbar.visibility = View.VISIBLE
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.saveButton.setOnClickListener {
            if (selectedDate != null && selectedTime != null) {
                Toast.makeText(
                    requireContext(),
                    "Дата: $selectedDate, Время: $selectedTime",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(requireContext(), "Выберите дату и время!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupSpinner() {
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            availableTimes
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.timeSpinner.adapter = adapter

        binding.timeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedTime = parent?.getItemAtPosition(position) as String
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedTime = null
            }
        }

    }

    private fun showDatePickerDialog() {
        val today = LocalDate.now(ZoneId.systemDefault())
        val maxDate = today.plusDays(40)

        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Выберите дату")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()

        datePicker.addOnPositiveButtonClickListener { selection ->
            val selected = Instant.ofEpochMilli(selection)
                .atZone(ZoneId.systemDefault()).toLocalDate()

            if (selected.isAfter(today.minusDays(1)) && selected.isBefore(maxDate.plusDays(1))) {
                selectedDate = selected
                binding.selectDateButton.text = "Дата: $selected"
            } else {
                Toast.makeText(requireContext(), "Дата вне диапазона 40 дней", Toast.LENGTH_SHORT).show()
            }
        }

        datePicker.show(parentFragmentManager, "DatePicker")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}