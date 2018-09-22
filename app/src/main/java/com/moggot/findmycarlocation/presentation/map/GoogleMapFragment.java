package com.moggot.findmycarlocation.presentation.map;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

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
import com.moggot.findmycarlocation.ErrorStatus;
import com.moggot.findmycarlocation.MapViewModel;
import com.moggot.findmycarlocation.R;
import com.moggot.findmycarlocation.presentation.common.BaseFragment;

import java.util.List;

import butterknife.BindView;

public class GoogleMapFragment extends BaseFragment<MapViewModel> implements OnMapReadyCallback {

    @BindView(R.id.tv_distance_value)
    TextView tvDistance;
    @BindView(R.id.tv_duration_value)
    TextView tvDuration;
    @BindView(R.id.map)
    MapView googleMapView;

    @Nullable
    private GoogleMap map;
    private MapViewModel viewModel;

    private static final String TAG = "GoogleMapFragment";

    public static GoogleMapFragment newInstance() {
        return new GoogleMapFragment();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        googleMapView.onCreate(savedInstanceState);
        googleMapView.getMapAsync(this);
        viewModel.getRouteData().observe(this, path -> {
            showInterstitial();
            if (path == null) {
                return;
            }
            showDistance(path.getRoutes().get(0).getLegs().get(0).getDistance().getText());
            showDuration(path.getRoutes().get(0).getLegs().get(0).getDuration().getText());
            String pointsStr = path.getRoutes().get(0).getOverviewPolyline().getPoints();
            List<LatLng> points = PolyUtil.decode(pointsStr);
            showRoute(points);
        });
        viewModel.getErrorStatus().observe(this, errorStatus -> {
            switch (errorStatus.getStatus()) {
                case ErrorStatus.BUILD_PATH_ERROR:
                    Snackbar.make(view, getString(R.string.no_path), Snackbar.LENGTH_INDEFINITE)
                            .setAction(getString(R.string.retry), action -> viewModel.retryCall())
                            .show();
                    break;
                default:
                    throw new IllegalArgumentException("Unknown parking state = " + errorStatus.getStatus());
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
    public void onDestroy() {
        googleMapView.onDestroy();
        super.onDestroy();
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
            decoratePoint(viewModel.drawCircle());
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
        tvDistance.setText(distance);
    }

    private void showDuration(String duration) {
        tvDuration.setText(duration);
    }

    public void onClickFound(View view) {
        viewModel.foundCar();
    }
}
