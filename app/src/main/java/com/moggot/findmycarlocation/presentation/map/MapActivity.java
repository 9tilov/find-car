package com.moggot.findmycarlocation.presentation.map;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.moggot.findmycarlocation.App;
import com.moggot.findmycarlocation.R;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback, MapView {

    @BindView(R.id.tv_distance_value)
    TextView tvDistance;
    @BindView(R.id.tv_duration_value)
    TextView tvDuration;
    @Inject
    MapPresenter presenter;
    @Nullable
    private GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        ButterKnife.bind(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        App.getInstance().getAppComponent().inject(this);
        presenter.onAttach(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (map != null) {
            map.setMyLocationEnabled(true);
            map.getUiSettings().setZoomControlsEnabled(true);
            map.getUiSettings().setCompassEnabled(true);
            presenter.buildRoute();
            presenter.drawCircle();
        }
    }

    @Override
    public void decoratePoint(LatLng point) {
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

    @Override
    public void showAd() {
        showInterstitial();
    }

    public void showInterstitial() {
        InterstitialAd interstitialAd = new InterstitialAd(getApplicationContext());
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
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {

            }
        });
    }

    @Override
    public void showError() {
        Toast.makeText(this, getString(R.string.no_path), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showRoute(List<LatLng> points) {
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

    @Override
    public void showDistance(String distance) {
        tvDistance.setText(distance);
    }

    @Override
    public void showDuration(String duration) {
        tvDuration.setText(duration);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onDetach();
    }
}
