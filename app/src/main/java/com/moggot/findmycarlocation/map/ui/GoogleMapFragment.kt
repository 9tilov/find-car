package com.moggot.findmycarlocation.map.ui

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.admanager.AdManagerInterstitialAd
import com.google.android.gms.ads.admanager.AdManagerInterstitialAdLoadCallback
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.Dash
import com.google.android.gms.maps.model.Gap
import com.google.android.gms.maps.model.JointType
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.gms.maps.model.RoundCap
import com.google.android.material.snackbar.Snackbar
import com.google.maps.android.PolyUtil
import com.moggot.findmycarlocation.MainActivity
import com.moggot.findmycarlocation.R
import com.moggot.findmycarlocation.base.data.Result
import com.moggot.findmycarlocation.base.data.Status
import com.moggot.findmycarlocation.base.viewBinding
import com.moggot.findmycarlocation.data.model.route.Path
import com.moggot.findmycarlocation.databinding.FragmentMapBinding
import com.moggot.findmycarlocation.extensions.showToast
import com.moggot.findmycarlocation.location.LocationFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@AndroidEntryPoint
class GoogleMapFragment : LocationFragment(R.layout.fragment_map), OnMapReadyCallback {

    override val viewModel by viewModels<MapViewModel>()
    override val fragmentTag: String = "GoogleMapFragment"
    private val viewBinding by viewBinding(FragmentMapBinding::bind)
    private var adManagerInterstitial: AdManagerInterstitialAd? = null
    private var map: GoogleMap? = null
    private val mapView: MapView by lazy { requireActivity().findViewById(R.id.map) }

    override fun locationUpdated(location: Location) {
        viewModel.buildRoute(location.latitude, location.longitude)
        map?.uiSettings?.isMyLocationButtonEnabled = true
        stopLocationUpdates()
    }

    override fun locationPermissionRejected() {
    }

    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
        startLocationUpdatesAfterCheck()
        viewModel.getRouteData().observe(viewLifecycleOwner, { result: Result<Path> ->
            when (result.status) {
                Status.SUCCESS -> {
                    val activity = activity as MainActivity? ?: return@observe
                    if (!activity.isPremiumPurchased) {
                        showInterstitial()
                    }
                    val path = result.data ?: return@observe
                    enableSearchMode(true)
                    showDistance(path.routes[0].legs[0].distance?.text ?: "")
                    showDuration(path.routes[0].legs[0].duration?.text ?: "")
                    val pointsStr = path.routes[0].overviewPolyline?.points ?: ""
                    val points = PolyUtil.decode(pointsStr)
                    viewBinding.mapTvStartAddress.text = path.routes[0].legs[0].startAddress
                    viewBinding.mapTvEndAddress.text = path.routes[0].legs[0].endAddress
                    showRoute(points)
                }
                Status.ERROR -> Snackbar.make(
                    view,
                    getString(R.string.no_path),
                    Snackbar.LENGTH_INDEFINITE
                )
                    .setAction(getString(R.string.retry)) { viewModel.retryCall() }
                    .show()
                else -> {
                }
            }

        })
        viewModel.parkingData.observe(viewLifecycleOwner, { data ->
            if (data != null) {
                decoratePoint(data.coords)
            }
        })
        viewBinding.mapBtnFound.setOnClickListener { v ->
            viewModel.foundCar()
            if (activity != null) {
                enableSearchMode(false)
                showToast(getString(R.string.car_is_found), Toast.LENGTH_SHORT)
            }
        }
    }

    private fun enableSearchMode(enable: Boolean) {
        viewBinding.mapHeader.visibility = if (enable) VISIBLE else GONE
        if (enable) {
            val pulse = AnimationUtils.loadAnimation(context, R.anim.pulse_scale)
            viewBinding.mapIvLocation.startAnimation(pulse)
        } else {
            viewBinding.mapIvLocation.clearAnimation()
            map?.clear()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        map?.run {
            isMyLocationEnabled = true
            uiSettings.isZoomControlsEnabled = true
            uiSettings.isCompassEnabled = true
        }
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
        val adRequest = AdManagerAdRequest.Builder().build()
        AdManagerInterstitialAd.load(
            requireContext(),
            getString(R.string.banner_ad_unit_id_map_interstitial),
            adRequest,
            object : AdManagerInterstitialAdLoadCallback() {
                override fun onAdLoaded(interstitialAd: AdManagerInterstitialAd) {
                    adManagerInterstitial = interstitialAd
                    adManagerInterstitial?.show(requireActivity())
                }

                override fun onAdFailedToLoad(p0: LoadAdError) {
                    adManagerInterstitial = null
                }
            })
    }

    private fun showRoute(points: List<LatLng>) {
        val polylineOptions = PolylineOptions()
        polylineOptions.width(4f).color(R.color.line)
        val latLngBuilder = LatLngBounds.Builder()
        for (i in points.indices) {
            polylineOptions.add(points[i])
            latLngBuilder.include(points[i])
        }
        val line: Polyline? = map?.addPolyline(polylineOptions)
        if (line != null) {
            stylePolyline(line)
        }
        val size = resources.displayMetrics.widthPixels
        val latLngBounds = latLngBuilder.build()
        val track = CameraUpdateFactory.newLatLngBounds(latLngBounds, size, size, 25)
        map?.animateCamera(track)
    }

    private fun stylePolyline(polyline: Polyline) {
        polyline.startCap = RoundCap()
        polyline.endCap = RoundCap()
        polyline.width = POLYLINE_STROKE_WIDTH_PX
        polyline.color = ContextCompat.getColor(requireContext(), R.color.polyline_color)
        polyline.jointType = JointType.ROUND
        val pattern = listOf(
            Gap(GAP_LENGTH), Dash(DASH_LENGTH), Gap(GAP_LENGTH)
        )
        polyline.pattern = pattern
    }

    private fun showDistance(distance: String) {
        viewBinding.tvDistanceValue.text = getString(R.string.distance, distance)
    }

    private fun showDuration(duration: String) {
        viewBinding.tvDurationValue.text = getString(R.string.duration, duration)
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    companion object {

        private const val POLYLINE_STROKE_WIDTH_PX = 8f
        private const val GAP_LENGTH = 5f
        private const val DASH_LENGTH = 30f

        fun newInstance(): GoogleMapFragment {
            return GoogleMapFragment()
        }
    }
}