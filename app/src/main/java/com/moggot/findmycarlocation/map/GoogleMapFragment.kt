package com.moggot.findmycarlocation.map

import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import com.google.android.material.snackbar.Snackbar
import com.google.maps.android.PolyUtil
import com.moggot.findmycarlocation.MainActivity
import com.moggot.findmycarlocation.R
import com.moggot.findmycarlocation.base.BaseFragment
import com.moggot.findmycarlocation.data.model.route.Path
import com.moggot.findmycarlocation.di.ComponentHolder
import com.moggot.findmycarlocation.di.component.AppComponent
import com.moggot.findmycarlocation.di.component.MainComponent
import javax.inject.Inject

class GoogleMapFragment : BaseFragment(), OnMapReadyCallback, GoogleMapView {

    lateinit var tvDistance: TextView
    lateinit var tvDuration: TextView
    lateinit var tvLat: TextView
    lateinit var tvLng: TextView
    lateinit var btnFound: TextView
    lateinit var ivLocation: AppCompatImageView
    lateinit var viewDot: View
    lateinit var googleMapView: MapView
    private lateinit var adView: AdView
    private lateinit var adRequest: AdRequest

    private val handler = Handler()
    private var map: GoogleMap? = null
    private val runnableCode = object : Runnable {
        override fun run() {
            handler.postDelayed(this, LOCATION_UPDATE_PERIOD.toLong())
        }
    }
    @Inject
    lateinit var presenter: MapPresenter

    override val fragmentTag: String
        get() = TAG

    override val componentName: String
        get() = AppComponent::class.java.name

    override val isComponentDestroyable: Boolean
        get() = false

    override val layoutRes: Int
        get() = R.layout.fragment_map

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adRequest = AdRequest.Builder().build()
        ComponentHolder.provideComponent(MainComponent::class.java.name) {
            MainComponent.Initializer.init()
        }.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.attachView(this)
        tvDistance = view.findViewById(R.id.tv_distance_value)
        tvDuration = view.findViewById(R.id.tv_duration_value)
        tvLat = view.findViewById(R.id.map_tv_lat)
        tvLng = view.findViewById(R.id.map_tv_lng)
        btnFound = view.findViewById(R.id.map_btn_found)
        ivLocation = view.findViewById(R.id.map_iv_location)
        viewDot = view.findViewById(R.id.map_status_dot)
        googleMapView = view.findViewById(R.id.map)
        adView = view.findViewById(R.id.ad_map)
        val activity = activity as MainActivity
        if (!activity.isPremiumPurchased) {
            adView.loadAd(adRequest)
        }

        googleMapView.onCreate(savedInstanceState)
        googleMapView.getMapAsync(this)
        enableSearchMode(false)
        presenter.getCurrentLocation()
        btnFound.setOnClickListener {
            presenter.foundCar()
            enableSearchMode(false)
        }
    }

    override fun drawRoute(path: Path?) {
        if (!(activity as MainActivity).isPremiumPurchased) {
            showInterstitial()
        }
        if (path == null) {
            return
        }
        enableSearchMode(true)
        showDistance(path.routes[0].legs[0].distance.text)
        showDuration(path.routes[0].legs[0].duration.text)
        val pointsStr = path.routes[0].overviewPolyline.points
        val points = PolyUtil.decode(pointsStr)
        showRoute(points)
    }

    private fun enableSearchMode(enable: Boolean) {
        btnFound.visibility = if (enable) View.VISIBLE else View.INVISIBLE
        btnFound.isEnabled = enable
        viewDot.setBackgroundResource(if (enable) R.drawable.status_green_dot else R.drawable.status_red_dot)
        tvDistance.visibility = if (enable) View.VISIBLE else View.GONE
        tvDuration.visibility = if (enable) View.VISIBLE else View.GONE
        if (enable) {
            val pulse = AnimationUtils.loadAnimation(context, R.anim.pulse_scale)
            ivLocation.startAnimation(pulse)
            decoratePoint(presenter.drawCircle())
        } else {
            ivLocation.clearAnimation()
            map?.clear()
        }
    }

    override fun onStart() {
        super.onStart()
        googleMapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        googleMapView.onResume()
        adView.resume()
    }

    override fun onPause() {
        googleMapView.onPause()
        adView.pause()
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
        googleMapView.onStop()
    }

    override fun onDestroyView() {
        googleMapView.onDestroy()
        ivLocation.clearAnimation()
        presenter.detachView()
        adView.destroy()
        super.onDestroyView()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(runnableCode)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        googleMapView.onLowMemory()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        if (ContextCompat.checkSelfPermission(requireContext(),
                        android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        map?.isMyLocationEnabled = true
        map?.uiSettings?.isZoomControlsEnabled = true
        map?.uiSettings?.isCompassEnabled = true
        presenter.buildRoute()
    }

    private fun decoratePoint(point: LatLng) {
        drawCircle(point)
        addMarker(point)
    }

    private fun drawCircle(point: LatLng) {
        val circleOptions = CircleOptions()
        circleOptions.center(point)
        circleOptions.radius(10.0)
        circleOptions.strokeColor(Color.BLACK)
        circleOptions.fillColor(0x30ff0000)
        circleOptions.strokeWidth(2f)
        map?.addCircle(circleOptions)
    }

    private fun addMarker(carPosition: LatLng) {
        val endMarkerOptions = MarkerOptions()
                .position(carPosition)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.car))
        map?.addMarker(endMarkerOptions)
    }

    private fun showInterstitial() {
        val interstitialAd = InterstitialAd(context)
        interstitialAd.adUnitId = getString(R.string.banner_ad_unit_id_map_interstitial)
        val adRequest = AdRequest.Builder().build()
        interstitialAd.loadAd(adRequest)
        interstitialAd.adListener = object : AdListener() {
            override fun onAdLoaded() {
                if (interstitialAd.isLoaded) {
                    interstitialAd.show()
                }
            }

            override fun onAdOpened() {
                //do nothing
            }

            override fun onAdFailedToLoad(errorCode: Int) {
                //do nothing
            }
        }
    }

    private fun showRoute(points: List<LatLng>) {
        val line = PolylineOptions()
        line.width(4f).color(R.color.line)
        val latLngBuilder = LatLngBounds.Builder()
        for (i in points.indices) {
            line.add(points[i])
            latLngBuilder.include(points[i])
        }
        map?.addPolyline(line)
        val size = resources.displayMetrics.widthPixels
        val latLngBounds = latLngBuilder.build()
        val track = CameraUpdateFactory.newLatLngBounds(latLngBounds, size, size, 25)
        map?.animateCamera(track)
    }

    private fun showDistance(distance: String) {
        tvDistance.text = getString(R.string.distance, distance)
    }

    private fun showDuration(duration: String) {
        tvDuration.text = getString(R.string.duration, duration)
    }

    override fun updateLocation(location: Location?) {
        handler.post(runnableCode);
        tvLat.text = location?.latitude.toString()
        tvLng.text = location?.longitude.toString()
    }

    override fun onError(throwable: Throwable) {
        Snackbar.make(googleMapView, getString(R.string.no_path), Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(R.string.retry)) { presenter.retryCall() }
                .show()
    }

    override fun onCarFound() {
        Toast.makeText(context, getString(R.string.car_is_found), Toast.LENGTH_SHORT).show()
    }

    companion object {

        private const val LOCATION_UPDATE_PERIOD = 10000
        private const val TAG = "GoogleMapFragment"

        fun newInstance(): GoogleMapFragment {
            return GoogleMapFragment()
        }
    }
}
