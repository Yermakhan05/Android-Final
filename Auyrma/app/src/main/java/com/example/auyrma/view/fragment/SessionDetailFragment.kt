package com.example.auyrma.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.example.auyrma.databinding.FragmentSessionDetailBinding
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.example.auyrma.R
import com.google.android.material.datepicker.MaterialDatePicker
import org.threeten.bp.Instant
import org.threeten.bp.LocalDate
import org.threeten.bp.ZoneId
import android.widget.TextView
import android.app.AlertDialog
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.bumptech.glide.Glide
import com.example.auyrma.MyApp.Companion.database
import com.example.auyrma.model.datasource.ApiSource
import com.example.auyrma.model.db.Converters
import com.example.auyrma.model.entity.Dr
import com.example.auyrma.model.entity.FavoriteResponse
import com.example.auyrma.model.entity.SessionRequest
import com.example.auyrma.model.entity.SessionRoom
import com.example.auyrma.view.activity.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.TimeUnit
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Date


class SessionDetailFragment: Fragment() {
    private var _binding: FragmentSessionDetailBinding? = null
    private val binding get() = _binding!!

    private var selectedDate: LocalDate? = null
    private var selectedTime: String? = null
    private val availableTimes = listOf("10:00", "11:00", "21:45", "22:00")

    companion object {
        private const val KEY_DR = "dr_key"
        private const val KEY_IS = "is_key"
        fun newInstance(dr: Dr, isFromHospitalDetailPage: Boolean) = SessionDetailFragment().apply {
            arguments = bundleOf(KEY_DR to dr, KEY_IS to isFromHospitalDetailPage)
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
        val dr = arguments?.getSerializable(KEY_DR) as? Dr

        binding.drName.text = dr?.name
        binding.drPrice.text = dr?.price.toString() + "₸ 1 item\ninc. of all taxes"
        binding.drSp.text = dr?.speciality
        Glide
            .with(binding.root.context)
            .load(dr?.medicImage)
            .placeholder(R.drawable.placeholder_image)
            .into(binding.drImage);


        setupSpinner()
        setClickButtons()
    }



    private fun setClickButtons() {
        binding.selectDateButton.setOnClickListener {
            showDatePickerDialog()
        }
        binding.backButton.setOnClickListener {
            val isFromHospitalDetailPage = arguments?.getSerializable(KEY_IS) as? Boolean
            if (isFromHospitalDetailPage != true) {
                val toolbar = requireActivity().findViewById<Toolbar>(R.id.toolbar)
                toolbar.visibility = View.VISIBLE
            }
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.btnProceed.setOnClickListener {
            if (selectedDate != null && selectedTime != null) {
                showConfirmationDialog(selectedDate.toString(), selectedTime.toString())
                Toast.makeText(
                    requireContext(),
                    "Дата: $selectedDate, Время: $selectedTime",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(requireContext(), "Select the date and time!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showConfirmationDialog(date: String, time: String) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_confirmation, null)

        val confirmationDetails = dialogView.findViewById<TextView>(R.id.confirmationDetails)
        confirmationDetails.text = "Appointment is confirmed on\n$date at $time"

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(true)
            .create()

        dialogView.findViewById<View>(R.id.btnContinue).setOnClickListener {
            val dr = arguments?.getSerializable(KEY_DR) as? Dr
            val sessionRequest = dr?.let { it1 ->
                SessionRequest(
                    medicsId = it1.id,
                    clientId = 2,
                    appointment = selectedDate.toString() + "T" + selectedTime
                )
            }
            print("Session: " + sessionRequest?.appointment)
            postSession(sessionRequest, selectedDate.toString() + selectedTime, dr)
            dialog.dismiss()
        }

        dialogView.findViewById<View>(R.id.btnCancel).setOnClickListener {
            Toast.makeText(requireContext(), "Appointment Cancelled!", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
        dialog.show()
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
            .setTitleText("Select a date")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()

        datePicker.addOnPositiveButtonClickListener { selection ->
            val selected = Instant.ofEpochMilli(selection)
                .atZone(ZoneId.systemDefault()).toLocalDate()

            if (selected.isAfter(today.minusDays(1)) && selected.isBefore(maxDate.plusDays(1))) {
                selectedDate = selected
                binding.selectDateButton.text = "Date: $selected"
            } else {
                Toast.makeText(requireContext(), "Date outside the 40 day range", Toast.LENGTH_SHORT).show()
            }
        }

        datePicker.show(parentFragmentManager, "DatePicker")
    }

    private fun postSession(sessionRequest: SessionRequest?, time: String, dr: Dr?) {
        if (sessionRequest != null) {
            ApiSource.client.addSession(sessionRequest).enqueue(object : Callback<FavoriteResponse> {
                override fun onResponse(call: Call<FavoriteResponse>, response: Response<FavoriteResponse>) {
                    if (response.isSuccessful) {
                        val toolbar = requireActivity().findViewById<Toolbar>(R.id.toolbar)
                        toolbar.visibility = View.VISIBLE
                        CoroutineScope(Dispatchers.IO).launch {
                            val sessionDao = database.sessionDao()
                            val sessionRoom = SessionRoom(dr = dr!!, time = time)
                            sessionDao.insertSession(sessionRoom)

                            val upcomingSessions = sessionDao.getUpcomingSessions()
                            print("Session $upcomingSessions")
                            scheduleSessionReminders(upcomingSessions)
                        }
                        response.body()?.let {
                            Toast.makeText(requireContext(), "Appointment Saved!", Toast.LENGTH_SHORT).show()
                            (requireActivity() as? MainActivity)?.navigateToFavoriteFragment()
                            onDestroyView()
                            return
                        }
                    } else {
                        println("Error: ${response.errorBody()?.string()}")
                    }
                }
                override fun onFailure(call: Call<FavoriteResponse>, t: Throwable) {
                }
            })
        }
    }
    private fun scheduleSessionReminders(sessions: List<SessionRoom>) {
        val workManager = WorkManager.getInstance(requireContext())

        sessions.forEach { session ->
            val delay = calculateDelayInMillis(session.time)

            val workRequest = OneTimeWorkRequestBuilder<SessionReminderWorker>()
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setInputData(
                    workDataOf(
                        "session_dr" to (session.dr).name,
                        "session_time" to session.time
                    )
                )
                .build()

            workManager.enqueue(workRequest)
        }
    }

    private fun calculateDelayInMillis(sessionTime: String): Long {
        try {
            val fixedDate = fixDateString(sessionTime)
            val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault())
            val sessionDate = formatter.parse(fixedDate) ?: Date()
            return sessionDate.time - System.currentTimeMillis()
        } catch (e: Exception) {
            e.printStackTrace()
            return 0L
        }
    }
    private fun fixDateString(dateString: String): String {
        return if (dateString.contains("T")) {
            dateString
        } else {
            val fixedDate = dateString.substring(0, 10) + "T" + dateString.substring(10)
            fixedDate
        }
    }





    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}