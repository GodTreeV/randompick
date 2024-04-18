package com.app.randompick

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.app.randompick.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var seedViewModel: SeedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setupViews()
        seedViewModel =  ViewModelProvider(
            this,
            SeeViewModleFactory(
                application,
                Navigation.findNavController(
                    this,
                    androidx.navigation.fragment.R.id.nav_host_fragment_container
                )
            )
        )[SeedViewModel::class.java]
    }

    private fun setupViews() {
        with(binding) {

            toolbar.setOnLongClickListener {
                debug = !debug
                toast { "start debug : $debug" }
                false
            }

            tablayout.setOnItemSelectedListener {
                when (it.itemId) {
                    R.id.page_1 -> {
                        seedViewModel.navigateTo(R.id.numbersFragment)
                    }
                    R.id.page_2 -> {
                        seedViewModel.navigateTo(R.id.listFragment)
                    }
                }
                true
            }
        }
    }

    fun openHistory(item: MenuItem) {
        seedViewModel.navigateTo(R.id.historyFragment)
    }
}