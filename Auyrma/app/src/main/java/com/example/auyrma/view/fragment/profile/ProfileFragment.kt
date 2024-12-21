package com.example.auyrma.view.fragment.profile

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.auyrma.R
import com.example.auyrma.databinding.FragmentProfileBinding
import com.example.auyrma.model.repository.UserRepository
import com.example.auyrma.view.fragment.FavoriteListFragment
import com.example.auyrma.viewmodel.AuthViewModel
import com.example.auyrma.viewmodel.ViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var userRepository: UserRepository
    private lateinit var authViewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val toolbar = requireActivity().findViewById<Toolbar>(R.id.toolbar)
        toolbar.visibility = View.GONE
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val rootView = binding.root

        val repository = UserRepository(requireContext())
        val factory = ViewModelFactory(repository)
        authViewModel = ViewModelProvider(this, factory).get(AuthViewModel::class.java)

        // Инициализация репозитория
        userRepository = UserRepository(requireContext())

        // Проверка авторизации пользователя
        if (isUserAuthenticated()) {
            // Если пользователь авторизован
            binding.isAuth.visibility = View.VISIBLE
            binding.notAuth.visibility = View.GONE

            binding.profileSettings.setOnClickListener {
                loadFragment(ProfileSettingsFragment())
            }
            binding.paymentMethods.setOnClickListener {
//                loadFragment(Payment())
            }
            binding.favourite.setOnClickListener {
                val toolbar = requireActivity().findViewById<Toolbar>(R.id.toolbar)
                toolbar.visibility = View.VISIBLE
                val navigationView = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation_view)
                navigationView.menu.findItem(R.id.session_page)?.isChecked = true
                loadFragment(FavoriteListFragment())
            }
            binding.settings.setOnClickListener {
//                loadFragment(Settings())
            }
            binding.helpInfo.setOnClickListener {
//                loadFragment(Help_info())
            }

            // Пример: получаем ID пользователя и отображаем его имя
            val userId = userRepository.getAuthenticatedUserId()

            lifecycleScope.launch {
                val authUser = authViewModel.getUserById(userId = userId)
//                binding.city.text = "City: ${authUser?.city}"
                binding.userName.text = "${authUser?.name}"
//                binding.phone.text = "Phone: ${authUser?.phone}"
//                binding.balance.text = "Balance: ${authUser?.balance}"
            }

            binding.logoutButton.setOnClickListener {
                AuthViewModel(userRepository).logout() // Очистка данных авторизации
                navigateToLogin()
            }
        } else {
            // Если пользователь не авторизован
            binding.isAuth.visibility = View.GONE
            binding.notAuth.visibility = View.VISIBLE

            binding.login.setOnClickListener {
                loadFragment(LoginFragment())
            }
            binding.registerButton.setOnClickListener {
                loadFragment(RegisterFragment())
            }
        }

        return rootView
    }
    private fun navigateToLogin() {
        loadFragment(ProfileFragment())
    }
    private fun loadFragment(fragment: Fragment) {
//        val navigationView = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation_view)
//        navigationView.id = R.id.session_page
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_view, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun isUserAuthenticated(): Boolean {
        return userRepository.isUserAuthenticated()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}