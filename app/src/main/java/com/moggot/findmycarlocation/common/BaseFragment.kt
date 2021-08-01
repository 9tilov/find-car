package com.moggot.findmycarlocation.common

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.moggot.findmycarlocation.AppAnalytics
import com.moggot.findmycarlocation.base.BaseViewModel

abstract class BaseFragment(@LayoutRes layoutId: Int) : Fragment(layoutId) {

    private lateinit var locationCallback: LocationCallback

    protected lateinit var analytics: AppAnalytics
    protected abstract val viewModel: BaseViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        analytics = AppAnalytics(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations) {
                    // Update UI with location data
                    // ...
                }
            }
        }
    }

    abstract val fragmentTag: String
}