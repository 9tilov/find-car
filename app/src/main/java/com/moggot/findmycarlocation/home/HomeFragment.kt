package com.moggot.findmycarlocation.home

import android.app.AlertDialog
import android.content.DialogInterface
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.google.android.gms.ads.AdRequest
import com.moggot.findmycarlocation.MainActivity
import com.moggot.findmycarlocation.MainActivity.AdsCallback
import com.moggot.findmycarlocation.R
import com.moggot.findmycarlocation.base.viewBinding
import com.moggot.findmycarlocation.databinding.FragmentHomeBinding
import com.moggot.findmycarlocation.extensions.isInternetAvailable
import com.moggot.findmycarlocation.extensions.showToast
import com.moggot.findmycarlocation.location.LocationFragment
import com.moggot.findmycarlocation.parking.ui.ParkingViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@AndroidEntryPoint
class HomeFragment : LocationFragment(R.layout.fragment_home), OnTouchListener, AdsCallback {

    override val viewModel by viewModels<ParkingViewModel>()
    override val fragmentTag: String = "HomeFragment"
    private val viewBinding by viewBinding(FragmentHomeBinding::bind)

    private var isAnimated = false
    private var startY = 0f
    private var adRequest: AdRequest = AdRequest.Builder().build()

    @ExperimentalCoroutinesApi
    override fun locationUpdated(location: Location) {
        stopLocationUpdates()
        viewBinding.ivGear.isEnabled = true
        if (viewModel.isCarParked()) {
            createDialog()
        } else {
            viewModel.parkCar(location)
            animateParking()
            showToast(getString(R.string.save_car_location_success), Toast.LENGTH_SHORT)
        }
    }

    override fun locationPermissionRejected() {
        viewBinding.ivGear.isEnabled = true
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity).setCallback(this)
        viewBinding.ivGear.isEnabled = false
        viewModel.parkingData.observe(viewLifecycleOwner, {
            viewBinding.ivGear.isEnabled = true
        })
        val activity = activity as MainActivity
        if (!activity.isPremiumPurchased) {
            viewBinding.adMain.loadAd(adRequest)
        }
        isAnimated = false
        viewBinding.ivGear.setOnTouchListener(this)
    }

    override fun onResume() {
        super.onResume()
        viewBinding.adMain.resume()
    }

    override fun onPause() {
        super.onPause()
        viewBinding.adMain.pause()
    }

    private fun animateParking() {
        val animationUp = AnimationUtils.loadAnimation(context, R.anim.middle_up_middle)
        animationUp.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {
                isAnimated = true
            }

            override fun onAnimationEnd(animation: Animation) {
                isAnimated = false
            }

            override fun onAnimationRepeat(animation: Animation) {
                //do nothing
            }
        })
        viewBinding.ivGear.startAnimation(animationUp)
    }

    @ExperimentalCoroutinesApi
    private fun createDialog() {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle(R.string.dialog_you_not_find_car)
            .setMessage(R.string.dialog_title_save_car)
            .setPositiveButton(
                R.string.dialog_yes
            ) { dialog: DialogInterface?, id: Int ->
                viewModel.markCarIsFound()
                startLocationUpdatesAfterCheck()
            }
            .setNegativeButton(
                R.string.dialog_no
            ) { dialog: DialogInterface, id: Int -> dialog.dismiss() }
        val dialog = builder.create()
        dialog.show()
    }

    @ExperimentalCoroutinesApi
    override fun onTouch(v: View, event: MotionEvent): Boolean {
        v.performClick()
        val action = event.action
        if (isAnimated) {
            return false
        }
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                startY = event.y
                return true
            }
            MotionEvent.ACTION_UP -> {
                val endY = event.y
                if (startY > endY) {
                    startLocationUpdatesAfterCheck()
                    viewBinding.ivGear.isEnabled = false
                } else {
                    val isParked = viewModel.isCarParked()
                    if (isParked) {
                        openMap()
                    } else {
                        showToast(
                            getString(R.string.you_should_save_car_location),
                            Toast.LENGTH_SHORT
                        )
                    }
                }
            }
            else -> {
            }
        }
        return false
    }

    private fun openMap() {
        if (!isInternetAvailable()) {
            noInternet()
            return
        }
        val animationDown = AnimationUtils.loadAnimation(context, R.anim.middle_down_middle)
        animationDown.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {
                isAnimated = true
            }

            override fun onAnimationEnd(animation: Animation) {
                isAnimated = false
                if (activity != null) {
                    (activity as MainActivity).switchToMap()
                }
            }

            override fun onAnimationRepeat(animation: Animation) {
                //do nothing
            }
        })
        viewBinding.ivGear.startAnimation(animationDown)
    }

    private fun noInternet() {
        showToast(getString(R.string.no_internet), Toast.LENGTH_SHORT)
    }

    override fun showAds(show: Boolean) {
        viewBinding.adMain.visibility = if (show) View.VISIBLE else View.GONE
    }

    companion object {

        fun newInstance(): HomeFragment {
            return HomeFragment()
        }
    }
}