package com.moggot.findmycarlocation.presentation.main;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.moggot.findmycarlocation.App;
import com.moggot.findmycarlocation.R;
import com.moggot.findmycarlocation.Utils;
import com.moggot.findmycarlocation.presentation.map.MapActivity;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

import static com.moggot.findmycarlocation.Utils.LOCATION_PERMISSION;

public class MainActivity extends AppCompatActivity implements MainView, View.OnTouchListener {

    @BindView(R.id.iv_gear)
    ImageView ivGear;
    @BindView(R.id.ad_main)
    AdView adView;

    @Inject
    MainPresenter presenter;
    private boolean isAnimated;
    private float yStart, yEnd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        App.getInstance().getAppComponent().inject(this);
        presenter.onAttach(this);

        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        App application = (App) getApplication();
        Tracker t = application.getTracker(App.TrackerName.APP_TRACKER);
        t.enableAdvertisingIdCollection(true);

        presenter.initAd();
        isAnimated = false;
        ivGear.setOnTouchListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        checkPlayServicesAvailable();
    }

    private void checkPlayServicesAvailable() {
        final GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        final int status = apiAvailability.isGooglePlayServicesAvailable(this);

        if (status != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(status)) {
                apiAvailability.getErrorDialog(this, status, 1).show();
            } else {
                Snackbar.make(ivGear, "Google Play Services unavailable. This app will not work", Snackbar.LENGTH_INDEFINITE).show();
            }
        }
    }

    @Override
    public void onStop() {
        // Stop the analytics tracking
        GoogleAnalytics.getInstance(this).reportActivityStop(this);
        super.onStop();
    }

    @Override
    public void showCarIsParking() {
        animateParking();
        Toast.makeText(this, getString(R.string.save_car_location_success), Toast.LENGTH_SHORT).show();
    }

    private void animateParking() {
        Animation animationUp = AnimationUtils.loadAnimation(this, R.anim.middle_up_middle);
        animationUp.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                isAnimated = true;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                isAnimated = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        ivGear.startAnimation(animationUp);
    }

    @Override
    public void showCarIsNotParking() {
        Toast.makeText(this, getString(R.string.you_should_save_car_location), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showCantSaveParking() {
        createDialog();
    }

    private void createDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dialog_you_not_find_car)
                .setMessage(R.string.dialog_title_save_car)
                .setPositiveButton(R.string.dialog_yes, (dialog, id) -> {
                    presenter.reParkCar();
                }).setNegativeButton(R.string.dialog_no, (dialog, id) -> {
            dialog.dismiss();
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void showAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    @Override
    public void openMap() {
        Animation animationDown = AnimationUtils.loadAnimation(this, R.anim.middle_down);
        animationDown.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                isAnimated = true;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                isAnimated = false;
                startActivity(new Intent(MainActivity.this, MapActivity.class));
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        animationDown.setFillAfter(true);
        ivGear.startAnimation(animationDown);

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        if (isAnimated) {
            return false;
        }
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                yStart = event.getY();
                return true;

            case MotionEvent.ACTION_UP:
                yEnd = event.getY();
                if (yStart > yEnd) {
                    Timber.d("upda");
                    Utils.checkLocationPermissions(this);
                    presenter.parkCar();
                    break;
                } else {
                    presenter.showMap();
                    Timber.d("down");
                    break;
                }
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    presenter.parkCar();
                } else {
                }
                return;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onDetach();
    }

    @Override
    public void showError() {
        Toast.makeText(this, getString(R.string.no_location), Toast.LENGTH_SHORT).show();
    }
}
