package com.moggot.findmycarlocation;

import android.Manifest;
import android.app.AlertDialog;
import android.appwidget.AppWidgetManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.Calendar;

public class MainActivity extends NetworkActivity implements NetworkActivity.LocationObserver {

    private ImageView img_animation;
    private int heightScreen = 0;

    private float y1 = 0, y2 = 0;

    private final static String LOG_TAG = "myLogs";

    private static boolean isAnimation = false;

    private int widgetID = AppWidgetManager.INVALID_APPWIDGET_ID;
    private Intent resultValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        registerLocationObserver(this);
        initLocationServices();
        installWidget();
        setContentView(R.layout.activity_main);

        img_animation = (ImageView) findViewById(R.id.ivTrigger);

        AdView mAdView = (AdView) findViewById(R.id.adViewMain);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        Tracker t = ((AnalyticsApplication) getApplication())
                .getTracker(AnalyticsApplication.TrackerName.APP_TRACKER);
        t.enableAdvertisingIdCollection(true);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        heightScreen = displaymetrics.heightPixels;
        if (AppWidgetManager.getInstance(this).getAppWidgetIds(new ComponentName(this, MyWidget.class)).length == 0)
            SharedPreference.SaveInstallWidgetState(this, false);

        int rate_count = SharedPreference.LoadRatingCount(this);
        if (rate_count >= Consts.NUM_LAUNCH_TO_RATE_APP) {
            SharedPreference.SaveRatingCount(this, 0);
            showRatingDialog();
        }

        if (SharedPreference.LoadTutorialStatus(this)) {
            Intent onboarding = new Intent(this, OnboardingActivity.class);
            startActivityForResult(onboarding, Consts.ONBOARDING_SCREEN);
        }

        FirebaseAnalytics mFirebaseAnalytics = null;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WAKE_LOCK) == PackageManager.PERMISSION_GRANTED)
            mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, Consts.FIREBASE_ITEM_ID);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, Consts.FIREBASE_ITEM_NAME);
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, Consts.FIREBASE_CONTENT_TYPE);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

    }

    public boolean onTouchEvent(MotionEvent touchevent) {
        if (isAnimation)
            return false;
        switch (touchevent.getAction()) {
            // when user first touches the screen we get x and y coordinate
            case MotionEvent.ACTION_DOWN: {
                y1 = touchevent.getY();
                break;
            }
            case MotionEvent.ACTION_UP: {
                y2 = touchevent.getY();

                boolean isLocationSaved = SharedPreference.LoadIsLocationSavedState(this);
                if (y1 < y2) {
                    if (!isLocationSaved) {
                        save_car_location();
                        break;
                    }
                    animationFromMiddleToDown();
                    break;
                }

                // if Down to UP sweep event on screen
                if (y1 > y2) {
                    if (isLocationSaved) {
                        showSaveDialog();
                        break;
                    }
                    saveLocation();
                    break;
                }
            }
            break;

        }
        return false;
    }

    private void showSaveDialog() {
        AlertDialog.Builder ad = new AlertDialog.Builder(MainActivity.this);

        ad.setTitle(getResources().getString(R.string.dialog_title_save_car));

        ad.setMessage(getResources().getString(R.string.dialog_you_not_find_car));
        ad.setPositiveButton(getResources().getString(R.string.dialog_yes), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                saveLocation();
            }
        });
        ad.setNegativeButton(getResources().getString(R.string.dialog_no), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
            }
        });
        ad.setCancelable(true);
        ad.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
            }
        });
        ad.create();
        ad.show();
    }

    private void showRatingDialog() {

        final AlertDialog.Builder ratingdialog = new AlertDialog.Builder(this);

        ratingdialog.setIcon(android.R.drawable.btn_star_big_on);
        ratingdialog.setTitle(getResources().getString(R.string.rating_title));
        ratingdialog.setMessage(getResources().getString(R.string.rating_text));

        View linearlayout = getLayoutInflater().inflate(R.layout.rating_dialog, null);
        ratingdialog.setView(linearlayout);

        final RatingBar rating = (RatingBar) linearlayout.findViewById(R.id.ratingbar);

        ratingdialog.create();
        final AlertDialog ad = ratingdialog.show();
        rating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {

            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (rating >= 4) {
                    Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.moggot.findmycarlocation");
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    if (!openGooglePlayPage(intent))
                        return;
                } else {
                    improve_app();
                }
                ad.dismiss();
            }
        });
    }

    private boolean openGooglePlayPage(Intent aIntent) {
        try {
            startActivity(aIntent);
            return true;
        } catch (ActivityNotFoundException e) {
            return false;
        }
    }

    private void updateWidget(boolean isLocationSavedValue) {
        if (widgetID == AppWidgetManager.INVALID_APPWIDGET_ID)
            return;
        SharedPreferences sp = getSharedPreferences(MyWidget.WIDGET_PREF, MODE_PRIVATE);
        sp.edit().putBoolean(SharedPreference.s_state_location_save, isLocationSavedValue).apply();
        MyWidget.updateMyWidget(this, AppWidgetManager.getInstance(this), widgetID);
        setResult(RESULT_OK, resultValue);
    }

    private void installWidget() {
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            widgetID = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }
        Log.i(LOG_TAG, "widgetID = " + widgetID);

        // и проверяем его корректность
        if (widgetID != AppWidgetManager.INVALID_APPWIDGET_ID) {
            Log.i(LOG_TAG, "widgetID correct");
            SharedPreference.SaveWidgetID(this, widgetID);
            resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID);

            setResult(RESULT_CANCELED, resultValue);
            boolean isWidgetInstalled = SharedPreference.LoadInstallWidgetState(this);
            boolean isLocationSaved = SharedPreference
                    .LoadIsLocationSavedState(this);
            if (!isWidgetInstalled) {
                updateWidget(isLocationSaved);
                isWidgetInstalled = true;
                SharedPreference.SaveInstallWidgetState(this, isWidgetInstalled);
                finish();
                return;
            }

            if (isLocationSaved) {
                Log.i(LOG_TAG, "widgetID isLocationSaved");
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        animationFromMiddleToDown();
                    }
                }, 500);
            } else {
                checkLocationSettings();
                Log.i(LOG_TAG, "widgetID !isLocationSaved");
                saveLocation();
            }
        }
    }

    private void animationUP() {
        TranslateAnimation animation = new TranslateAnimation(0.0f, 0.0f,
                0, -heightScreen / 9);

        final int[] count_anim = {0};

        animation.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                isAnimation = true;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                ++count_anim[0];
                if (count_anim[0] == 2)
                    return;
                TranslateAnimation animation_repeate = new TranslateAnimation(0.0f, 0.0f,
                        -heightScreen / 9, 0);
                animation_repeate.setDuration(500);
                animation_repeate.setFillAfter(true);
                img_animation.startAnimation(animation_repeate);
                isAnimation = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        animation.setDuration(500);
        animation.setFillAfter(true);
        img_animation.startAnimation(animation);
    }

    private void animationFromDownToMiddle() {
        TranslateAnimation animation = new TranslateAnimation(0.0f, 0.0f,
                heightScreen / 9, 0);
        animation.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                isAnimation = true;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                isAnimation = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        animation.setDuration(500);
        animation.setFillAfter(true);
        img_animation.startAnimation(animation);
    }

    private void animationFromMiddleToDown() {
        TranslateAnimation animation = new TranslateAnimation(0.0f, 0.0f,
                0, heightScreen / 9);
        animation.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                isAnimation = true;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                isAnimation = false;
                showMap();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        animation.setDuration(500);
        animation.setFillAfter(true);
        img_animation.startAnimation(animation);
    }

    private void saveLocation() {
        Location location = getLocation();
        Log.v(LOG_TAG, "location = " + location);
        if (location == null) {
            checkLocationSettings();
            return;
        }

        saveLocationToSharedMemory(location);
        saveRatingCountToSharedMemory();

        if (widgetID != 0) {
            finish();
        }

        animationUP();
        car_loc_save_success();
    }

    private void saveLocationToSharedMemory(Location location) {
        Calendar time = Calendar.getInstance();
        int cur_day = time.get(Calendar.DAY_OF_MONTH);
        int cur_hour = time.get(Calendar.HOUR_OF_DAY);
        int cur_minute = time.get(Calendar.MINUTE);
        SharedPreference.SaveTime(this, cur_day, cur_hour,
                cur_minute);

        boolean isLocationSaved = true;
        updateWidget(isLocationSaved);
        SharedPreference.SaveIsLocationSavedState(this, isLocationSaved);
        SharedPreference.SaveLocation(this,
                location.getLatitude(),
                location.getLongitude());
    }

    private void saveRatingCountToSharedMemory() {
        int rate_count = SharedPreference.LoadRatingCount(this);
        ++rate_count;
        SharedPreference.SaveRatingCount(this, rate_count);
    }

    private void showMap() {
        boolean isLocationSaved = SharedPreference.LoadIsLocationSavedState(this);
        if (isLocationSaved) {
            isAnimation = false;
            Intent intent = new Intent(MainActivity.this,
                    MapActivity.class);
            startActivityForResult(intent, Consts.MAP_SCREEN);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        boolean isLocationSaved = SharedPreference.LoadIsLocationSavedState(this);
        final Handler handler = new Handler();

        switch (requestCode) {
            case Consts.MAP_SCREEN:
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        animationFromDownToMiddle();
                    }
                }, 500);
                updateWidget(isLocationSaved);
                break;
        }
    }

    @Override
    public void onScanLocationStarted(final NetworkActivity activity) {
        Log.i(LOG_TAG, "MaponScanLocationStarted");
    }

    @Override
    public void onScanLocationFinished(final NetworkActivity activity) {
        Log.i(LOG_TAG, "MaponScanLocationFinished");
        saveLocation();
    }

    @Override
    public void onStop() {
        // Stop the analytics tracking
        GoogleAnalytics.getInstance(this).reportActivityStop(this);
        super.onStop();
        setResult(RESULT_OK, resultValue);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterLocationObserver(this);
    }

    private void save_car_location() {
        Toast.makeText(getBaseContext(), R.string.you_should_save_car_location,
                Toast.LENGTH_SHORT).show();
    }

    private void car_loc_save_success() {
        Toast.makeText(this, R.string.save_car_location_success,
                Toast.LENGTH_SHORT).show();
    }

    private void improve_app() {
        Toast.makeText(this, R.string.improve_app, Toast.LENGTH_SHORT).show();
    }
}
