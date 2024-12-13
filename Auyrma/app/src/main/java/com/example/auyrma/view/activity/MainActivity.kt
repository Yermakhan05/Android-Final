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

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    var selectedCityOfUser: String = "Almaty"

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
                    showSessionToolbar()
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.fragment_container_view, FavoriteListFragment.newInstance())
                        .commit()
                }

                R.id.hospital -> {
                    navigateToHospitalFragment()
                }


//                R.id.profile -> {
//                    supportFragmentManager
//                        .beginTransaction()
//                        .replace(R.id.fragment_container_view, profileFragment)
//                        .commit()
//
//                }
            }

            true
        }
    }
    private fun showHomeToolbar() {
        binding.toolbarMainLayout.visibility = View.VISIBLE
        binding.toolbarHospitalLayout.visibility = View.GONE
        binding.selectCity.visibility = View.VISIBLE
    }

    private fun showHospitalToolbar() {
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
        binding.toolbarMainLayout.visibility = View.VISIBLE
        binding.selectCity.visibility = View.GONE
        binding.toolbarHospitalLayout.visibility = View.GONE
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