package com.example.auyrma.view.fragment.profile

import com.example.auyrma.databinding.FragmentProfileSettingsBinding
import com.example.auyrma.model.repository.UserRepository
import com.example.auyrma.viewmodel.AuthViewModel
import com.example.auyrma.viewmodel.ViewModelFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class ProfileSettingsFragment : Fragment() {

    private var _binding: FragmentProfileSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var authViewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileSettingsBinding.inflate(inflater, container, false)
        val rootView = binding.root

        // Инициализация ViewModel
        val repository = UserRepository(requireContext())
        val factory = ViewModelFactory(repository)
        authViewModel = ViewModelProvider(this, factory).get(AuthViewModel::class.java)

        // Загрузка данных пользователя
        lifecycleScope.launch {
            val userId = authViewModel.getAuthenticatedUserId()
            val user = authViewModel.getUserById(userId)

            // Отображаем данные
            binding.profileName.text = user?.name ?: "N/A"
            binding.profilePhone.text = user?.phone ?: "N/A"
            binding.profileCity.text = user?.city ?: "N/A"
        }

        return rootView
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}