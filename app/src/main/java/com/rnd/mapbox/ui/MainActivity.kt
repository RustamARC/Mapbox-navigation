package com.rnd.mapbox.ui

import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.rnd.mapbox.R
import com.rnd.mapbox.databinding.ActivityMainBinding
import com.rnd.mapbox.ui.home.HomeFragment
import com.rnd.mapbox.utils.OptionType
import com.rnd.mapbox.utils.SharedPreference
import com.rnd.mapbox.utils.toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.driving_preference.view.*
import kotlinx.android.synthetic.main.trip_view.view.*
import kotlinx.coroutines.*

class MainActivity : BaseActivity(), HomeFragment.MapInterationListener {

    var job: Job? = null
    lateinit var binding: ActivityMainBinding
    lateinit var sharedPreference: SharedPreference
    var optionType: OptionType = OptionType.NONE
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        val navController = findNavController(R.id.nav_host_fragment)
        sharedPreference = SharedPreference(this)
        val font = Typeface.createFromAsset(assets, "fonts/icomoon.ttf")
        binding.openMenu.typeface = font
        binding.drivingPref.tvClose.typeface = font
        binding.tripView.close.typeface = font
        binding.tripView.btnProfile.typeface = font

        binding.openMenu.setOnClickListener {
            binding.drivingPref.visibility = View.VISIBLE
            nav_host_fragment.view?.visibility = View.GONE
            binding.driveView.visibility = View.GONE
            binding.drivingPref.taxi.typeface = font
            binding.drivingPref.food.typeface = font
            binding.drivingPref.both.typeface = font

            updateOptionType()
        }
        binding.drivingPref.tvClose.setOnClickListener {
            binding.drivingPref.visibility = View.GONE
            nav_host_fragment.view?.visibility = View.VISIBLE
            binding.driveView.visibility = View.VISIBLE
        }
        binding.drivingPref.btnSave.setOnClickListener {
            binding.drivingPref.visibility = View.GONE
            nav_host_fragment.view?.visibility = View.VISIBLE
            binding.driveView.visibility = View.VISIBLE
            sharedPreference.save("MODE", optionType.name)
        }
        binding.drivingPref.btnReset.setOnClickListener {
            binding.drivingPref.visibility = View.GONE
            nav_host_fragment.view?.visibility = View.VISIBLE
            binding.driveView.visibility = View.VISIBLE
            sharedPreference.save("MODE", OptionType.NONE.name)
        }


        binding.drivingPref.mcv_taxi.setOnClickListener {
            binding.drivingPref.mcv_taxi.isChecked = !binding.drivingPref.mcv_taxi.isChecked
            if (binding.drivingPref.mcv_taxi.isChecked) {
                binding.drivingPref.mcv_food.isChecked = false
                binding.drivingPref.mcv_both.isChecked = false
                optionType = OptionType.TAXI
            }

        }
        binding.drivingPref.mcv_food.setOnClickListener {
            binding.drivingPref.mcv_food.isChecked = !binding.drivingPref.mcv_food.isChecked
            if (binding.drivingPref.mcv_food.isChecked) {

                binding.drivingPref.mcv_taxi.isChecked = false
                binding.drivingPref.mcv_both.isChecked = false
                optionType = OptionType.FOOD
            }

        }
        binding.drivingPref.mcv_both.setOnClickListener {
            binding.drivingPref.mcv_both.isChecked = !binding.drivingPref.mcv_both.isChecked
            if (binding.drivingPref.mcv_both.isChecked) {
                binding.drivingPref.mcv_taxi.isChecked = false
                binding.drivingPref.mcv_food.isChecked = false
                optionType = OptionType.BOTH
            }
        }

        binding.tripView.btnDecline.setOnClickListener {
            binding.tripView.visibility = View.GONE
            binding.drivingPref.visibility = View.GONE
            nav_host_fragment.view?.visibility = View.VISIBLE
            binding.driveView.visibility = View.VISIBLE
        }
        binding.tripView.btnAccept.setOnClickListener {
            binding.tripView.visibility = View.GONE
            binding.drivingPref.visibility = View.GONE
            nav_host_fragment.view?.visibility = View.VISIBLE
            binding.driveView.visibility = View.VISIBLE
        }
        binding.navView.setupWithNavController(navController)
    }

    private fun updateOptionType() {
        when (sharedPreference.getValueString("MODE")) {
            OptionType.TAXI.name -> {
                binding.drivingPref.mcv_taxi.isChecked = true
                binding.drivingPref.mcv_food.isChecked = false
                binding.drivingPref.mcv_both.isChecked = false
            }
            OptionType.FOOD.name -> {
                binding.drivingPref.mcv_taxi.isChecked = false
                binding.drivingPref.mcv_both.isChecked = false
                binding.drivingPref.mcv_food.isChecked = true
            }
            OptionType.BOTH.name -> {
                binding.drivingPref.mcv_both.isChecked = true
                binding.drivingPref.mcv_taxi.isChecked = false
                binding.drivingPref.mcv_food.isChecked = false
            }
            OptionType.NONE.name -> {
                binding.drivingPref.mcv_taxi.isChecked = false
                binding.drivingPref.mcv_food.isChecked = false
                binding.drivingPref.mcv_both.isChecked = false
            }
        }
    }

    override fun onGoClicked() {
        binding.tvDriverStatus.text = resources.getString(R.string.str_finding_trips)

        cancelJob()

        job = CoroutineScope(Dispatchers.IO).launch {
            for (i in 0..100 step 1) {
                delay(50)
                withContext(Dispatchers.Main) {
                    binding.progressIndicator.progress = i
                    binding.notifyPropertyChanged(R.id.progress_indicator)
                }
            }
        }
        job?.apply {
            invokeOnCompletion { cause ->
                if (cause != null && cause is CancellationException && !job!!.isActive) {
                    cancel()
                }
                CoroutineScope(Dispatchers.Default).launch {
                    withContext(Dispatchers.Main) {
                        showTrip()
                    }
                }
            }
        }
    }

    fun cancelJob() {
        job?.cancel()
    }

    private fun showTrip() {
        binding.tripView.visibility = View.VISIBLE
        binding.drivingPref.visibility = View.GONE
        nav_host_fragment.view?.visibility = View.GONE
        binding.driveView.visibility = View.GONE
    }

    override fun onSafetyClicked() {
        toast("Stop the trip")
    }

}