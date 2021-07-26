package com.moggot.findmycarlocation.location

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Location
import android.util.Log
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.LayoutRes
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.LocationRequest
import com.moggot.findmycarlocation.R
import com.moggot.findmycarlocation.common.BaseFragment
import com.moggot.findmycarlocation.extensions.showToast
import com.patloew.colocation.CoGeocoder
import com.patloew.colocation.CoLocation
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

abstract class LocationFragment(@LayoutRes layoutId: Int) : BaseFragment(layoutId) {

    protected abstract fun locationUpdated(location: Location)
    protected abstract fun locationPermissionRejected()

    private var curLocation: Location? = null

    @ExperimentalCoroutinesApi
    private val permissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                startLocationUpdatesAfterCheck()
            } else {
                locationPermissionRejected()
            }
        }

    @ExperimentalCoroutinesApi
    private val locationResult =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { activityResult ->
            if (activityResult.resultCode == RESULT_OK) {
                startLocationUpdates()
            } else {
                locationPermissionRejected()
            }
        }
    private lateinit var coLocation: CoLocation
    private lateinit var coGeocoder: CoGeocoder
    private val locationRequest: LocationRequest = LocationRequest.create()
        .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        .setInterval(UPDATED_INTERVAL)
        .setFastestInterval(FASTEST_UPDATED_INTERVAL)
    private var locationUpdatesJob: Job? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        coLocation = CoLocation.from(context)
        coGeocoder = CoGeocoder.from(context)
    }

    @ExperimentalCoroutinesApi
    protected fun startLocationUpdatesAfterCheck() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            viewLifecycleOwner.lifecycleScope.launch {
                when (val status = coLocation.checkLocationSettings(locationRequest)) {
                    CoLocation.SettingsResult.Satisfied -> {
                        val location: Location? = coLocation.getLastLocation()
                        if (location != null) {
                            curLocation = location
                            locationUpdated(location)
                        } else {
                            startLocationUpdates()
                        }
                    }
                    is CoLocation.SettingsResult.Resolvable -> {
                        val intentSenderRequest =
                            IntentSenderRequest.Builder(status.exception.resolution).build()
                        locationResult.launch(intentSenderRequest)
                    }
                    else -> { /* Ignore for now, we can't resolve this anyway */
                    }
                }
            }
        } else {
            permissionsLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    @ExperimentalCoroutinesApi
    private fun startLocationUpdates() {
        showToast(getString(R.string.gsp_turning_on), Toast.LENGTH_LONG)
        locationUpdatesJob?.cancel()
        locationUpdatesJob = viewLifecycleOwner.lifecycleScope.launch {
            try {
                if (ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    coLocation.getLocationUpdates(locationRequest).collect { location ->
                        curLocation = location
                        locationUpdated(location)
                    }
                }
            } catch (e: CancellationException) {
                Log.e("MainViewModel", "Location updates cancelled", e)
            }
        }
    }

    fun stopLocationUpdates() {
        locationUpdatesJob?.cancel()
        locationUpdatesJob = null
    }

    override fun onStop() {
        super.onStop()
        stopLocationUpdates()
    }

    companion object {
        private const val UPDATED_INTERVAL = 5000L
        private const val FASTEST_UPDATED_INTERVAL = 2500L
    }
}
