package com.rnd.mapbox.ui

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.View
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.card.MaterialCardView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.rnd.mapbox.R
import com.rnd.mapbox.databinding.ActivityMainBinding
import com.rnd.mapbox.ui.bottomsheet.SafetyOptionBottomSheet
import com.rnd.mapbox.ui.home.HomeFragment
import com.rnd.mapbox.utils.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.complete_pickup_view.view.*
import kotlinx.android.synthetic.main.driving_preference.view.*
import kotlinx.android.synthetic.main.pickup_view.view.*
import kotlinx.android.synthetic.main.start_pickup_view.view.*
import kotlinx.android.synthetic.main.trip_view.view.*
import kotlinx.android.synthetic.main.trip_view.view.btnProfile
import kotlinx.coroutines.*

class MainActivity : BaseActivity(), HomeFragment.MapInterationListener,
    SafetyOptionBottomSheet.OnSafetyOptionClickListener,
    NavController.OnDestinationChangedListener {

    public var isStarted: Boolean = false
    var job: Job? = null
    lateinit var binding: ActivityMainBinding
    lateinit var sharedPreference: SharedPreference

    var optionType: OptionType = OptionType.NONE
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        val navController = findNavController(R.id.nav_host_fragment)

        navController.addOnDestinationChangedListener(this)
        sharedPreference = SharedPreference(this)
        val font = Typeface.createFromAsset(assets, "fonts/icomoon.ttf")
        binding.openMenu.typeface = font
        binding.drivingPref.tvClose.typeface = font
        binding.tripView.close.typeface = font
        binding.tripView.btnProfile.typeface = font

        binding.startPickupView.ivStartArrow.typeface = font
        binding.startPickupView.startTaxiOpenMenu.typeface = font
        binding.startPickupView.ivCall.typeface = font
        binding.startPickupView.ivStartUser.typeface = font

        binding.completePickupView.ivCompleteArrow.typeface = font
        binding.completePickupView.completeTaxiOpenMenu.typeface = font
        binding.completePickupView.ivCompleteCall.typeface = font
        binding.completePickupView.ivCompleteUser.typeface = font

        binding.drivingPref.taxi.typeface = font
        binding.drivingPref.food.typeface = font
        binding.drivingPref.both.typeface = font
        binding.ivUser.typeface = font

        binding.pickupView.tvArrowUp.typeface = font

        binding.tvDriverStatus.text = resources.getString(R.string.str_you_are_offline)

        binding.openMenu.setOnClickListener {
            openDriverPreference()
        }

        binding.startPickupView.startTaxiOpenMenu.setOnClickListener {
            /* binding.startPickupView.visibility = View.GONE
             openDriverPreference()*/
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
            binding.tvDriverStatus.text = resources.getString(R.string.str_you_are_offline)
            binding.llPickupDetail.visibility = View.GONE

            showHideSafetyGoAndMyLocationView(true)
            showHideNavigateButton(false)

            binding.pickupView.visibility = View.GONE

            cancelJob()
        }
        binding.tripView.btnAccept.setOnClickListener {
            binding.tripView.visibility = View.GONE
            binding.drivingPref.visibility = View.GONE
            nav_host_fragment.view?.visibility = View.VISIBLE
            binding.driveView.visibility = View.VISIBLE

            binding.pickupView.visibility = View.VISIBLE
            binding.pickupView.tvArrowUp.typeface = font
            binding.tvDriverStatus.text = "Picking up Joe"
            binding.llPickupDetail.visibility = View.VISIBLE
            binding.ivUser.typeface = font
            showHideSafetyGoAndMyLocationView(false)
            showHideNavigateButton(true)
            cancelJob()
        }

        /* binding.pickupView.btnNavigate.setOnClickListener {

             if (isStarted) {
                 toast("Navigate using mapbox")
                 isStarted = false
             } else {
                 binding.llPickupDetail.visibility = View.GONE
                 binding.tvDriverStatus.text = "Droping up Joe"
                 binding.pickupView.visibility = View.GONE
                 startTaxi()
             }
         }*/
        binding.startPickupView.cardStartTaxi.setOnClickListener {
            isStarted = true
            onStartClick()

        }

        binding.completePickupView.cardCompleteTaxi.setOnClickListener {
//            completeTaxi()
            onReset()
        }
        binding.completePickupView.openMenu

        binding.navView.setupWithNavController(navController)
    }

    private fun showHideSafetyGoAndMyLocationView(visibility: Boolean) {
        nav_host_fragment.view?.findViewById<ExtendedFloatingActionButton>(R.id.btnRate)?.visibility =
            if (visibility) View.VISIBLE else View.GONE
        nav_host_fragment.view?.findViewById<ExtendedFloatingActionButton>(R.id.btnMyLocation)?.visibility =
            if (visibility) View.VISIBLE else View.GONE
        nav_host_fragment.view?.findViewById<MaterialCardView>(R.id.mcvGo)?.visibility =
            if (visibility) View.VISIBLE else View.GONE
        nav_host_fragment.view?.findViewById<ExtendedFloatingActionButton>(R.id.btnSafety)?.visibility =
            if (visibility) View.VISIBLE else View.GONE


    }

    private fun showHideNavigateButton(visibility: Boolean) {
        nav_host_fragment.view?.findViewById<ExtendedFloatingActionButton>(R.id.btnNavigate)?.visibility =
            if (visibility) View.VISIBLE else View.GONE
    }

    private fun completeTaxi() {
        binding.pickupView.visibility = View.GONE
        binding.driveView.visibility = View.GONE
        binding.startPickupView.visibility = View.GONE
        binding.completePickupView.visibility = View.VISIBLE
        binding.completePickupView.tvCompleteTaxi.blink(500, 20)
        /*Handler(Looper.getMainLooper()).postDelayed(Runnable {
            toast("Trip completed")
            onReset()
        }, 2000)*/
    }

    private fun onStartClick() {

        binding.startPickupView.visibility = View.GONE

        binding.tripView.visibility = View.GONE
        binding.drivingPref.visibility = View.GONE
        nav_host_fragment.view?.visibility = View.VISIBLE
        binding.driveView.visibility = View.VISIBLE
        binding.pickupView.visibility = View.VISIBLE
        binding.tvDriverStatus.text = "Dropping off Joe"
        binding.llPickupDetail.visibility = View.VISIBLE

        showHideSafetyGoAndMyLocationView(false)

        Handler(Looper.getMainLooper()).postDelayed(Runnable {
            toast("Trip completed")
            completeTaxi()
        }, 5000)
    }

    private fun openDriverPreference() {

        binding.drivingPref.visibility = View.VISIBLE

        nav_host_fragment.view?.visibility = View.GONE

        binding.driveView.visibility = View.GONE
        binding.pickupView.visibility = View.GONE
        binding.llPickupDetail.visibility = View.GONE
        binding.startPickupView.visibility = View.GONE

        updateOptionType()
    }

    private fun startTaxi() {
        binding.driveView.visibility = View.GONE
        binding.startPickupView.visibility = View.VISIBLE
        binding.startPickupView.tvStartTaxi.blink(500, 20)
        Handler(Looper.getMainLooper()).postDelayed(Runnable {
            binding.startPickupView.startPickupDetail.visibility = View.GONE
            binding.startPickupView.llWaiting.visibility = View.VISIBLE
        }, 5000)


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
        binding.progressIndicator.visibility = View.VISIBLE

        cancelJob()

        job = CoroutineScope(Dispatchers.Main).launch {
            for (i in 0..100 step 1) {
                delay(50)
                /* binding.progressIndicator.progress = i
                 binding.notifyPropertyChanged(R.id.progress_indicator)*/
            }

        }
        job?.apply {
            invokeOnCompletion { cause ->
                if (cause != null && cause is CancellationException && !isActive) {
                    cancel()
                    return@invokeOnCompletion
                }
                CoroutineScope(Dispatchers.Default).launch {
                    withContext(Dispatchers.Main) {
                        binding.progressIndicator.visibility = View.GONE
                        showTrip()
                        showHideSafetyGoAndMyLocationView(false)
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
        binding.driveView.visibility = View.GONE
        nav_host_fragment.view?.findViewById<ExtendedFloatingActionButton>(R.id.btnRate)?.visibility =
            View.GONE
        job = CoroutineScope(Dispatchers.Default).launch {
            for (i in 0..100 step 1) {
                delay(50)
                withContext(Dispatchers.Main) {
                    binding.tripView.timerProgress.progress = i
                    binding.notifyPropertyChanged(R.id.timerProgress)
                }
            }

        }
        job?.apply {
            invokeOnCompletion { cause ->
                if (cause != null && cause is CancellationException && !isActive) {
                    cancel()
                    return@invokeOnCompletion
                }
                CoroutineScope(Dispatchers.Main).launch {
                    binding.tripView.btnAccept.performClick()
                }
            }
        }

    }

    override fun onSafetyClicked() {
        SafetyOptionBottomSheet().show(supportFragmentManager, "")
    }

    override fun onReset() {
        hideHomeWidgetView()
        resetHomeWidget()
        showHideSafetyGoAndMyLocationView(true)
    }

    override fun onNavigate(isStarted: Boolean, currentRoute: DirectionsRoute?) {
        if (isStarted) {
//            toast("Navigate using mapbox")
            this.isStarted = false
            currentRoute?.let {
                val intent = Intent(this, NavigationActivity::class.java)
                intent.putExtra("route", it.toJson())
                startActivity(intent)
            }
        } else {
            binding.llPickupDetail.visibility = View.GONE
            binding.tvDriverStatus.text = "Droping up Joe"
            binding.pickupView.visibility = View.GONE
            startTaxi()
        }
    }

    override fun onSafetyOptionClick(reason: String) {
        toast(reason)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        /*   val font = Typeface.createFromAsset(assets, "fonts/icomoon.ttf")
           menu?.findItem(R.id.navigation_home)?.actionView?.findViewById<TextView>(R.id.ivHome)?.typeface =
               font*/
        return super.onCreateOptionsMenu(menu)
    }


    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        when (destination.id) {
            R.id.navigation_home -> {
                hideHomeWidgetView()
                resetHomeWidget()
            }
            R.id.navigation_queue -> {
                hideHomeWidgetView()
            }
            R.id.navigation_profile -> {
                hideHomeWidgetView()
            }

        }
    }

    private fun resetHomeWidget() {

        nav_host_fragment.view?.findViewById<ExtendedFloatingActionButton>(R.id.btnRate)?.visibility =
            View.VISIBLE
        nav_host_fragment.view?.findViewById<ExtendedFloatingActionButton>(R.id.btnMyLocation)?.visibility =
            View.VISIBLE
        nav_host_fragment.view?.findViewById<MaterialCardView>(R.id.mcvGo)?.visibility =
            View.VISIBLE
        nav_host_fragment.view?.findViewById<ExtendedFloatingActionButton>(R.id.btnSafety)?.visibility =
            View.VISIBLE
        binding.driveView.visibility = View.VISIBLE
        binding.rlPickupDetails.visibility = View.VISIBLE
        binding.llPickupDetail.visibility = View.GONE
        binding.tvDriverStatus.text = resources.getString(R.string.str_you_are_offline)
        nav_host_fragment.view?.visibility = View.VISIBLE
    }

    private fun hideHomeWidgetView() {
        binding.startPickupView.visibility = View.GONE
        binding.completePickupView.visibility = View.GONE
        binding.drivingPref.visibility = View.GONE
        binding.tripView.visibility = View.GONE
        binding.driveView.visibility = View.GONE
        binding.pickupView.visibility = View.GONE
        binding.completePickupView.visibility = View.GONE
        nav_host_fragment.view?.visibility = View.VISIBLE
        nav_host_fragment.view?.findViewById<ExtendedFloatingActionButton>(R.id.btnNavigate)?.visibility =
            View.GONE
        cancelJob()
    }

}