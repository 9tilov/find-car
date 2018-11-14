package com.moggot.findmycarlocation.map;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;
import com.moggot.findmycarlocation.MainActivity;
import com.moggot.findmycarlocation.R;
import com.moggot.findmycarlocation.common.BaseFragment;
import com.moggot.findmycarlocation.common.ErrorStatus;

import java.util.Calendar;
import java.util.List;

import butterknife.BindView;

public class GoogleMapFragment extends BaseFragment<MapViewModel> implements OnMapReadyCallback {

    private static final int LOCATION_UPDATE_PERIOD = 10000;
    private static final int SUGGEST_PURCHASE_FREQUENCY = 3;

    private static final String TAG = "GoogleMapFragment";
    @BindView(R.id.tv_distance_value)
    TextView tvDistance;
    @BindView(R.id.tv_duration_value)
    TextView tvDuration;
    @BindView(R.id.map_tv_lat)
    TextView tvLat;
    @BindView(R.id.map_tv_lng)
    TextView tvLng;
    @BindView(R.id.map_btn_found)
    TextView btnFound;
    @BindView(R.id.map_iv_location)
    AppCompatImageView ivLocation;
    @BindView(R.id.map_status_dot)
    View viewDot;
    @BindView(R.id.map)
    MapView googleMapView;
    Handler handler = new Handler();
    @Nullable
    private GoogleMap map;
    private MapViewModel viewModel;
    private final Runnable runnableCode = new Runnable() {
        @Override
        public void run() {
            viewModel.getCurrentLocation();
            handler.postDelayed(this, LOCATION_UPDATE_PERIOD);
        }
    };

    public static GoogleMapFragment newInstance() {
        return new GoogleMapFragment();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        googleMapView.onCreate(savedInstanceState);
        googleMapView.getMapAsync(this);
        enableSearchMode(false);
        viewModel.getRouteData().observe(this, path -> {
            MainActivity activity = ((MainActivity) getActivity());
            if (activity == null) {
                return;
            }
            if (!activity.isPremiumPurchased()) {
                showInterstitial();
            }
            if (path == null) {
                return;
            }
            enableSearchMode(true);
            showDistance(path.getRoutes().get(0).getLegs().get(0).getDistance().getText());
            showDuration(path.getRoutes().get(0).getLegs().get(0).getDuration().getText());
            String pointsStr = path.getRoutes().get(0).getOverviewPolyline().getPoints();
            List<LatLng> points = PolyUtil.decode(pointsStr);
            showRoute(points);
        });
        viewModel.getErrorStatus().observe(this, errorStatus -> {
            if (errorStatus.getStatus() == ErrorStatus.BUILD_PATH_ERROR) {
                Snackbar.make(view, getString(R.string.no_path), Snackbar.LENGTH_INDEFINITE)
                        .setAction(getString(R.string.retry), action -> viewModel.retryCall())
                        .show();
            }
        });
        viewModel.getLocationData().observe(this, location -> {
            if (location == null) {
                return;
            }
            handler.post(runnableCode);
            tvLat.setText(String.valueOf(location.getLatitude()));
            tvLng.setText(String.valueOf(location.getLongitude()));
        });
        btnFound.setOnClickListener(v -> {
            viewModel.foundCar();
            if (getActivity() != null) {
                enableSearchMode(false);
                Toast.makeText(getContext(), getString(R.string.car_is_found), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState, MapViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    protected Class<MapViewModel> getViewModel() {
        return MapViewModel.class;
    }

    @Override
    public String getFragmentTag() {
        return TAG;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_map;
    }

    private void enableSearchMode(boolean enable) {
        btnFound.setVisibility(enable ? View.VISIBLE : View.INVISIBLE);
        btnFound.setEnabled(enable);
        viewDot.setBackgroundResource(enable ? R.drawable.status_green_dot : R.drawable.status_red_dot);
        tvDistance.setVisibility(enable ? View.VISIBLE : View.GONE);
        tvDuration.setVisibility(enable ? View.VISIBLE : View.GONE);
        if (enable) {
            Animation pulse = AnimationUtils.loadAnimation(getContext(), R.anim.pulse_scale);
            ivLocation.startAnimation(pulse);
            decoratePoint(viewModel.drawCircle());
        } else {
            ivLocation.clearAnimation();
            if (map != null) {
                map.clear();
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        googleMapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        googleMapView.onResume();
    }

    @Override
    public void onPause() {
        googleMapView.onPause();
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        googleMapView.onStop();
    }

    @Override
    public void onDestroyView() {
        googleMapView.onDestroy();
        ivLocation.clearAnimation();
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        viewModel.getRouteData().removeObservers(this);
        handler.removeCallbacks(runnableCode);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        googleMapView.onLowMemory();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        if (ContextCompat.checkSelfPermission(getContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (map != null) {
            map.setMyLocationEnabled(true);
            map.getUiSettings().setZoomControlsEnabled(true);
            map.getUiSettings().setCompassEnabled(true);
            viewModel.buildRoute();
        }
    }

    private void decoratePoint(LatLng point) {
        drawCircle(point);
        addMarker(point);
    }

    private void drawCircle(LatLng point) {
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(point);
        circleOptions.radius(10);
        circleOptions.strokeColor(Color.BLACK);
        circleOptions.fillColor(0x30ff0000);
        circleOptions.strokeWidth(2);
        if (map != null) {
            map.addCircle(circleOptions);
        }
    }

    private void addMarker(LatLng carPosition) {
        MarkerOptions endMarkerOptions = new MarkerOptions()
                .position(carPosition)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.car));
        if (map != null) {
            map.addMarker(endMarkerOptions);
        }
    }

    private void showInterstitial() {
        InterstitialAd interstitialAd = new InterstitialAd(getContext());
        interstitialAd.setAdUnitId(getString(R.string.banner_ad_unit_id_map_interstitial));
        AdRequest adRequest = new AdRequest.Builder().build();
        interstitialAd.loadAd(adRequest);
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                if (interstitialAd.isLoaded()) {
                    interstitialAd.show();
                }
            }

            @Override
            public void onAdOpened() {
                //do nothing
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                //do nothing
            }
        });
    }

    private void showRoute(List<LatLng> points) {
        if (map == null) {
            return;
        }
        PolylineOptions line = new PolylineOptions();
        line.width(4f).color(R.color.line);
        LatLngBounds.Builder latLngBuilder = new LatLngBounds.Builder();
        for (int i = 0; i < points.size(); i++) {
            line.add(points.get(i));
            latLngBuilder.include(points.get(i));
        }
        map.addPolyline(line);
        int size = getResources().getDisplayMetrics().widthPixels;
        LatLngBounds latLngBounds = latLngBuilder.build();
        CameraUpdate track = CameraUpdateFactory.newLatLngBounds(latLngBounds, size, size, 25);
        map.animateCamera(track);
    }

    private void showDistance(String distance) {
        tvDistance.setText(getString(R.string.distance, distance));
    }

    private void showDuration(String duration) {
        tvDuration.setText(getString(R.string.duration, duration));
    }
}
