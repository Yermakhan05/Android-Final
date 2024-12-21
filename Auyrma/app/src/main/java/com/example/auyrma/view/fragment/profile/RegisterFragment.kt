package com.example.auyrma.view.fragment.profile

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.auyrma.R
import com.example.auyrma.databinding.FragmentRegisterBinding
import com.example.auyrma.model.datasource.ApiSource
import com.example.auyrma.model.entity.ClientRequest
import com.example.auyrma.model.entity.FavoriteResponse
import com.example.auyrma.model.entity.User
import com.example.auyrma.model.repository.UserRepository
import com.example.auyrma.viewmodel.AuthViewModel
import com.example.auyrma.viewmodel.ViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private lateinit var authViewModel: AuthViewModel
    private var isPasswordVisible = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val toolbar = requireActivity().findViewById<Toolbar>(R.id.toolbar)
        toolbar.visibility = View.GONE
        val navigationView = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation_view)
        navigationView.visibility = View.GONE

        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        val rootView = binding.root

        // Инициализация ViewModel
        val repository = UserRepository(requireContext())
        val factory = ViewModelFactory(repository)
        authViewModel = ViewModelProvider(this, factory).get(AuthViewModel::class.java)

        // Добавление текстового наблюдателя для проверки пароля
//        binding.passwordToggle.setOnClickListener {
//            togglePasswordVisibility()
//        }

        binding.passwordInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (isValidPassword(s.toString())) {
                    binding.passwordRequirements.setTextColor(resources.getColor(android.R.color.holo_green_dark))
                } else {
                    binding.passwordRequirements.setTextColor(resources.getColor(android.R.color.darker_gray))
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Кнопка регистрации
        binding.registerButton.setOnClickListener {
            val name = binding.nameInput.text.toString().trim()
            val phone = binding.phoneInput.text.toString().trim()
            val password = binding.passwordInput.text.toString().trim()
            val city = binding.cityInput.text.toString().trim()

            if (name.isEmpty() || phone.isEmpty() || password.isEmpty() || city.isEmpty()) {
                Toast.makeText(context, "All fields are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!isValidPhoneNumber(phone)) {
                Toast.makeText(context, "Invalid phone number format", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!isValidPassword(password)) {
                Toast.makeText(context, "Password does not meet requirements", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val user = User(name = name, phone = phone, password = password, city = city)
            lifecycleScope.launch {
                val success = authViewModel.registerUser(user)
                if (success) {
                    addToBack(user)
                    Toast.makeText(context, "Registration Successful!", Toast.LENGTH_SHORT).show()
                    loadFragment(LoginFragment())
                } else {
                    Toast.makeText(context, "User already exists", Toast.LENGTH_SHORT).show()
                }
            }
        }

        return rootView
    }

    private fun addToBack(user: User) {
        val clientRequest = ClientRequest(clientName = user.name)
        ApiSource.client.addUserToBack(clientRequest).enqueue(object :
            Callback<FavoriteResponse> {
            override fun onResponse(call: Call<FavoriteResponse>, response: Response<FavoriteResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        println("Success: ${it.message}")
                    }
                } else {
                    println("Error: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<FavoriteResponse>, t: Throwable) {
                println("Failed: ${t.message}")
            }
        })
    }

//    private fun togglePasswordVisibility() {
//        if (isPasswordVisible) {
//            binding.passwordInput.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
//            binding.passwordToggle.setImageResource(R.drawable.visibility) // Закрытый глаз
//        } else {
//            binding.passwordInput.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
//            binding.passwordToggle.setImageResource(R.drawable.visibility_off_24dp_e8eaed) // Открытый глаз
//        }
//        isPasswordVisible = !isPasswordVisible
//        binding.passwordInput.setSelection(binding.passwordInput.text.length)
//    }

    private fun loadFragment(fragment: Fragment) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_view, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun isValidPhoneNumber(phone: String): Boolean {
        val phoneRegex = "^((\\+7)|8)\\d{10}$".toRegex()
        return phoneRegex.matches(phone)
    }

    private fun isValidPassword(password: String): Boolean {
        val lengthRequirement = password.length >= 8
        val letterRegex = ".*[a-zA-Z].*".toRegex()
        val numberRegex = ".*\\d.*".toRegex()
        val specialCharRegex = ".*[#$%^,()\\*+.:|=?@/\\[\\]{}_!;~`'-].*".toRegex()

        val conditionsMet = listOf(
            letterRegex.containsMatchIn(password),
            numberRegex.containsMatchIn(password),
            specialCharRegex.containsMatchIn(password)
        ).count { it } >= 2

        return lengthRequirement && conditionsMet
    }

    override fun onDestroyView() {
        val navigationView = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation_view)
        navigationView.visibility = View.VISIBLE
        super.onDestroyView()
        _binding = null
    }
}