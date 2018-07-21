package com.moggot.findmycarlocation.presentation.main;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
import com.moggot.findmycarlocation.presentation.common.LocationActivity;
import com.moggot.findmycarlocation.presentation.map.MapActivity;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends LocationActivity implements MainView, View.OnTouchListener {

    @BindView(R.id.iv_gear)
    View ivGear;
    @BindView(R.id.ad_main)
    AdView adView;

    @Inject
    MainPresenter presenter;
    private boolean isAnimated;
    private float startY;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        App.getInstance().getAppComponent().inject(this);

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
    protected void onStart() {
        super.onStart();
        presenter.onAttach(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        checkPlayServicesAvailable();
        adView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        adView.pause();
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
        presenter.onDetach();
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
                //do nothing
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
                .setPositiveButton(R.string.dialog_yes,
                        (dialog, id) -> presenter.reParkCar())
                .setNegativeButton(R.string.dialog_no,
                        (dialog, id) -> dialog.dismiss());

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
        if (!Utils.isOnline(this)) {
            noInternet();
            return;
        }
        Animation animationDown = AnimationUtils.loadAnimation(this, R.anim.middle_down_middle);
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
                //do nothing
            }
        });
        ivGear.startAnimation(animationDown);

    }

    @Override
    public void enableGear(boolean block) {
        ivGear.setEnabled(block);
    }

    public void noInternet() {
        Toast.makeText(this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        v.performClick();
        int action = event.getAction();
        if (isAnimated) {
            return false;
        }

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                startY = event.getY();
                return true;

            case MotionEvent.ACTION_UP:
                float endY = event.getY();
                if (startY > endY) {
                    presenter.parkCar();
                } else {
                    presenter.showMap();
                }
                break;
            default:
                break;
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        adView.destroy();
    }

    @Override
    public void showError() {
        Toast.makeText(this, getString(R.string.no_location), Toast.LENGTH_SHORT).show();
    }
}
