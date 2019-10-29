package com.moggot.findmycarlocation.home

import android.app.AlertDialog
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.material.snackbar.Snackbar
import com.moggot.findmycarlocation.MainActivity
import com.moggot.findmycarlocation.R
import com.moggot.findmycarlocation.Utils
import com.moggot.findmycarlocation.base.BaseFragment
import com.moggot.findmycarlocation.di.ComponentHolder
import com.moggot.findmycarlocation.di.component.MainComponent
import javax.inject.Inject

class HomeFragment : BaseFragment(), HomeView, View.OnTouchListener, MainActivity.AdsCallback {

    private lateinit var ivGear: View
    private lateinit var adView: AdView
    private lateinit var adRequest: AdRequest

    @Inject
    lateinit var homePresenter: HomePresenter

    private var isAnimated: Boolean = false
    private var startY: Float = 0f


    override val fragmentTag: String
        get() = TAG

    override val layoutRes: Int
        get() = R.layout.fragment_home

    override val componentName: String
        get() = MainComponent::class.java.name

    override val isComponentDestroyable: Boolean
        get() = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adRequest = AdRequest.Builder().build()
        ComponentHolder.provideComponent(MainComponent::class.java.name) {
            MainComponent.Initializer.init()
        }.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homePresenter.attachView(this)
        ivGear = view.findViewById(R.id.iv_gear)
        adView = view.findViewById(R.id.ad_main)
        val activity = activity as MainActivity
        if (!activity.isPremiumPurchased) {
            adView.loadAd(adRequest)
        }
        isAnimated = false
        ivGear.setOnTouchListener(this)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as MainActivity).setCallback(this)
    }

    override fun onResume() {
        super.onResume()
        adView.resume()
    }

    override fun onPause() {
        super.onPause()
        adView.pause()
    }

    override fun animateParking() {
        ivGear.isEnabled = true
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
        ivGear.startAnimation(animationUp)
    }

    override fun showConfirmDialog() {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle(R.string.dialog_you_not_find_car)
                .setMessage(R.string.dialog_title_save_car)
                .setPositiveButton(R.string.dialog_yes
                ) { dialog, id -> homePresenter.forceParkCar() }
                .setNegativeButton(R.string.dialog_no
                ) { dialog, id -> dialog.dismiss() }

        val dialog = builder.create()
        dialog.show()
    }

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
                    homePresenter.parkCarIfNeeded()
                    ivGear.isEnabled = false
                } else {
                    val isShowMap = homePresenter.tryToShowMap()
                    if (isShowMap) {
                        openMap()
                    } else {
                        Toast.makeText(context, getString(R.string.you_should_save_car_location), Toast.LENGTH_SHORT).show()
                    }
                }
            }
            else -> {
            }
        }
        return false
    }

    private fun openMap() {
        if (!Utils.isOnline(context)) {
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
        ivGear.startAnimation(animationDown)
    }

    private fun noInternet() {
        Toast.makeText(context, getString(R.string.no_internet), Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        adView.destroy()
        super.onDestroyView()
    }

    override fun showAds(show: Boolean) {
        adView.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun onError(throwable: Throwable) {
        Snackbar.make(ivGear, throwable.message.toString(), Snackbar.LENGTH_SHORT)
                .show()
        ivGear.isEnabled = true
    }

    companion object {

        private const val TAG = "HomeFragment"

        fun newInstance(): HomeFragment {
            return HomeFragment()
        }
    }
}
