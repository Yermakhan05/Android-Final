package com.example.auyrma.view.activity

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.auyrma.R
import com.example.auyrma.databinding.ActivityMainBinding
import com.example.auyrma.view.fragment.DrListFragment
import com.example.auyrma.view.fragment.FavoriteListFragment
import com.example.auyrma.view.fragment.HospitalListFragment
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.example.auyrma.view.fragment.profile.ProfileFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    var selectedCityOfUser: String = "Almaty"
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            println("POST_NOTIFICATIONS permission granted")
        } else {
            println("POST_NOTIFICATIONS permission denied")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        supportFragmentManager.addOnBackStackChangedListener {
            if (supportFragmentManager.backStackEntryCount == 0) {
                binding.toolbar.visibility = View.VISIBLE
            }
        }

        setContentView(binding.root)

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val cities = arrayOf("Almaty", "Astana", "Shymkent", "Semey", "Aktau")

        binding.selectCity.setOnClickListener {
            showCitySelectionDialog(cities)
        }

        supportFragmentManager
            .beginTransaction()
            .add(R.id.fragment_container_view, DrListFragment.newInstance(selectedCityOfUser))
            .commit()

        binding.bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.home_page -> {
                    showHomeToolbar()
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.fragment_container_view, DrListFragment.newInstance(selectedCityOfUser))
                        .commit()
                }

                R.id.session_page -> {
                    navigateToFavoriteFragment()
                }

                R.id.hospital -> {
                    navigateToHospitalFragment()
                }
                R.id.profile -> {
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.fragment_container_view, ProfileFragment())
                        .commit()

                }
            }

            true
        }
    }
    override fun onStart() {
        super.onStart()
        checkNotificationPermission()
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+
            when {
                ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // Permission is already granted
                    println("POST_NOTIFICATIONS permission already granted")
                }
                else -> {
                    // Request the permission
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
    }
    private fun showHomeToolbar() {
        binding.toolbar.visibility = View.VISIBLE
        binding.toolbarMainLayout.visibility = View.VISIBLE
        binding.toolbarHospitalLayout.visibility = View.GONE
        binding.selectCity.visibility = View.VISIBLE
    }

    private fun showHospitalToolbar() {
        binding.toolbar.visibility = View.VISIBLE
        binding.toolbar.setPadding(
            binding.toolbar.paddingLeft,
            binding.toolbar.paddingTop,
            0,
            binding.toolbar.paddingBottom
        )
        binding.toolbarMainLayout.visibility = View.GONE
        binding.toolbarHospitalLayout.visibility = View.VISIBLE
        binding.selectCity.visibility = View.VISIBLE
    }
    private fun showSessionToolbar() {
        binding.toolbar.visibility = View.VISIBLE
        binding.toolbarMainLayout.visibility = View.VISIBLE
        binding.selectCity.visibility = View.GONE
        binding.toolbarHospitalLayout.visibility = View.GONE
    }

    fun navigateToFavoriteFragment() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container_view)
        if (currentFragment is FavoriteListFragment) return

        showSessionToolbar()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container_view, FavoriteListFragment.newInstance())
            .commit()
        binding.bottomNavigationView.menu.findItem(R.id.session_page)?.isChecked = true
    }

    fun navigateToHospitalFragment() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container_view)
        if (currentFragment is HospitalListFragment) return

        showHospitalToolbar()
        val hospitalFragment = HospitalListFragment.newInstance(selectedCityOfUser)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container_view, hospitalFragment)
            .commit()

        binding.bottomNavigationView.menu.findItem(R.id.hospital)?.isChecked = true
    }


    private fun showCitySelectionDialog(cities: Array<String>) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select city")
        builder.setItems(cities) { _, which ->
            val selectedCity = cities[which]
            binding.selectCityName.text = selectedCity
            selectedCityOfUser = selectedCity

            supportFragmentManager.setFragmentResult(
                "requestKey",
                Bundle().apply {
                    putString("selectedCity", selectedCity)
                }
            )
        }
        builder.show()
    }
}