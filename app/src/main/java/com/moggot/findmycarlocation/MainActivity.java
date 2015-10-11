package com.moggot.findmycarlocation;

import android.Manifest;
import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.GoogleAnalytics;

import java.util.Calendar;

public class MainActivity extends Activity {

    float y1, y2;
    ImageView img_animation;
    int height;

    int trigger = 0;


    final static String LOG_TAG = "myLogs";
    boolean isLocationSaved;

    private static boolean isAnimation = false;
    private static boolean show_map = false;

    int widgetID = AppWidgetManager.INVALID_APPWIDGET_ID;
    Intent resultValue;
    SharedPreferences sp;
    NetworkManager nwM;
    Location location;
    boolean isWidgetInstalled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        nwM = new NetworkManager(this);
        isLocationSaved = SharedPreference.LoadIsLocationSavedState(this);
        img_animation = (ImageView) findViewById(R.id.ivTrigger);
        installWidget();

        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        ((AnalyticsApplication) getApplication())
                .getTracker(AnalyticsApplication.TrackerName.APP_TRACKER);


        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        height = displaymetrics.heightPixels;
    }


    public boolean onTouchEvent(MotionEvent touchevent) {
        isLocationSaved = SharedPreference.LoadIsLocationSavedState(this);
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

                if (y1 < y2) {
                    updateWidget(isLocationSaved);
                    // if UP to DOWN sweep event on screen
                    if (trigger == 0) {
                        if (!isLocationSaved) {
                            save_car_location();
                            break;
                        }
                        showMap();
                        break;
                    }
                }

                // if Down to UP sweep event on screen
                if (y1 > y2) {

                    if (trigger == -1) {
                        animationDown(height / 9, 0);
                        trigger = 0;
                        break;
                    }
                    if (trigger == 0) {
                        if (isLocationSaved) {
                            find_your_car();
                            break;
                        }
                        saveLocation();
                        break;
                    }
                }
            }
            break;

        }
        return false;
    }


    private void updateWidget(boolean isLocationSavedValue) {
        if (widgetID == -1)
            return;
        sp = getSharedPreferences(MyWidget.WIDGET_PREF, MODE_PRIVATE);
        sp.edit().putBoolean(SharedPreference.s_state_location_save, isLocationSavedValue).apply();
        Log.d(LOG_TAG,
                "widgetIDValue = " + widgetID);
        MyWidget.updateMyWidget(this, AppWidgetManager.getInstance(this), widgetID);
        setResult(RESULT_OK, resultValue);
    }

    private void installWidget() {
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        widgetID = SharedPreference.LoadWidgetID(this);

        Log.d(LOG_TAG, "widgetID = " + widgetID);
        if (extras != null) {
            widgetID = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
            Log.d(LOG_TAG, "widgetID_extras = " + widgetID);
            SharedPreference.SaveWidgetID(this, widgetID);

            if (isLocationSaved) {
                Intent intent_screen = new Intent(MainActivity.this,
                        ScreenMap.class);
                startActivityForResult(intent_screen, SharedPreference.ACTIVITY_RESULT_CODE.MAP_SCREEN);
                updateWidget(isLocationSaved);
            }
            isWidgetInstalled = SharedPreference.LoadInstallWidgetState(this);
            if (isWidgetInstalled == false) {
                isWidgetInstalled = true;
                SharedPreference.SaveInstallWidgetState(this, isWidgetInstalled);
                finish();
            }

        }
        // и проверяем его корректность
        if (widgetID != AppWidgetManager.INVALID_APPWIDGET_ID) {
            // формируем intent ответа
            resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID);
            // отрицательный ответ
            isLocationSaved = SharedPreference
                    .LoadIsLocationSavedState(this);
            updateWidget(isLocationSaved);
        }

    }

    void animationUP() {
        TranslateAnimation animation = new TranslateAnimation(0.0f, 0.0f,
                0, -height / 9);

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
                        -height / 9, 0);
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

    void animationDown(float start, float end) {
        TranslateAnimation animation = new TranslateAnimation(0.0f, 0.0f,
                start, end);
        isLocationSaved = SharedPreference.LoadIsLocationSavedState(this);
        updateWidget(isLocationSaved);
        animation.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                isAnimation = true;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (isLocationSaved && show_map) {
                    Intent intent = new Intent(MainActivity.this,
                            ScreenMap.class);
                    startActivityForResult(intent, SharedPreference.ACTIVITY_RESULT_CODE.MAP_SCREEN);
                    isAnimation = false;
                }
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

    private void saveLocation() {

        Calendar time = Calendar.getInstance();
        int cur_day = time.get(Calendar.DAY_OF_MONTH);
        int cur_hour = time.get(Calendar.HOUR_OF_DAY);
        int cur_minute = time.get(Calendar.MINUTE);
        SharedPreference.SaveTime(this, cur_day, cur_hour,
                cur_minute);

//                                 SharedPreference.SaveLocation(this, 55.928,
//                                 37.520);
        nwM.checkLocationSettings();
        String provider = nwM.locationManager.NETWORK_PROVIDER;
        location = nwM.getLocation();
        if (location == null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                location = nwM.locationManager.getLastKnownLocation(provider);
            }
        }
        if (location != null) {
            animationUP();
            isLocationSaved = true;
            updateWidget(isLocationSaved);
            SharedPreference.SaveIsLocationSavedState(this, isLocationSaved);
            SharedPreference.SaveLocation(this,
                    location.getLatitude(),
                    location.getLongitude());
            car_loc_save_success();

        }
        trigger = 0;
    }

    public void showMap() {
        isLocationSaved = SharedPreference.LoadIsLocationSavedState(this);
        show_map = true;
        animationDown(0, height / 9);
        trigger = -1;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.info) {
            Intent intent = new Intent(MainActivity.this, ScreenInfo.class);
            startActivityForResult(intent, SharedPreference.ACTIVITY_RESULT_CODE.INFO_SCREEN);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        isLocationSaved = SharedPreference.LoadIsLocationSavedState(this);
        final int REQUEST_CHECK_SETTINGS = 199;
        switch (requestCode) {
            case SharedPreference.ACTIVITY_RESULT_CODE.MAP_SCREEN:
                animationDown(height / 9, 0);
                trigger = 0;
                show_map = false;
                updateWidget(isLocationSaved);
                break;
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.i(LOG_TAG, "User agreed to make required location settings changes.");
                        nwM.startLocationUpdates();
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.i(LOG_TAG, "User chose not to make required location settings changes.");
                        break;
                }
                break;

        }

    }

    private void find_your_car() {
        Toast.makeText(getBaseContext(), R.string.find_your_car,
                Toast.LENGTH_SHORT).show();
    }

    private void save_car_location() {
        Toast.makeText(getBaseContext(), R.string.you_should_save_car_location,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        GoogleAnalytics.getInstance(this).reportActivityStart(this);
    }

    @Override
    protected void onStop() {
        // Stop the analytics tracking
        GoogleAnalytics.getInstance(this).reportActivityStop(this);
        super.onStop();
        setResult(RESULT_OK, resultValue);
    }

    private void car_loc_save_success() {
        Toast.makeText(this, R.string.save_car_location_success,
                Toast.LENGTH_SHORT).show();
    }

}
