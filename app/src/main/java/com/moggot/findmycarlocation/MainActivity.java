package com.moggot.findmycarlocation;

import android.app.Activity;
import android.app.AlertDialog;
import android.appwidget.AppWidgetManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
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

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.util.Calendar;

public class MainActivity extends Activity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener,
        ResultCallback<LocationSettingsResult> {

    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    /**
     * The fastest rate for active location updates. Exact. Updates will never be more frequent
     * than this value.
     */
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    /**
     * Provides the entry point to Google Play services.
     */
    protected GoogleApiClient mGoogleApiClient = null;

    /**
     * Stores parameters for requests to the FusedLocationProviderApi.
     */
    protected LocationRequest mLocationRequest = null;

    /**
     * Stores the types of location services the client is interested in using. Used for checking
     * settings to determine if the device has optimal location settings.
     */
    protected LocationSettingsRequest mLocationSettingsRequest = null;

    /**
     * Represents a geographical location.
     */
    protected Location mCurrentLocation = null;

    ImageView img_animation = null;
    int height;

    float y1, y2;

    final static String LOG_TAG = "myLogs";
    boolean isLocationSaved;

    private static boolean isAnimation = false;

    int widgetID = AppWidgetManager.INVALID_APPWIDGET_ID;
    Intent resultValue;

    static final int NUM_LAUNCH_TO_RATE_APP = 11;

    public class ERROR_CODE {
        final static int OK = 0;
        final static int LOCATION_NOT_RETRIEVED = 1;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        buildGoogleApiClient();
        createLocationRequest();
        buildLocationSettingsRequest();
        installWidget();
        setContentView(R.layout.activity_main);

        img_animation = (ImageView) findViewById(R.id.ivTrigger);

        Ad advertisment = new Ad(this);
        advertisment.ShowBanner(R.id.adViewMain);

        Tracker t = ((AnalyticsApplication) getApplication())
                .getTracker(AnalyticsApplication.TrackerName.APP_TRACKER);
        t.enableAdvertisingIdCollection(true);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        height = displaymetrics.heightPixels;
        if (AppWidgetManager.getInstance(this).getAppWidgetIds(new ComponentName(this, MyWidget.class)).length == 0)
            SharedPreference.SaveInstallWidgetState(this, false);

        int rate_count = SharedPreference.LoadRatingCount(this);
        if (rate_count >= NUM_LAUNCH_TO_RATE_APP) {
            SharedPreference.SaveRatingCount(this, 0);
            showRatingDialog();
        }

        if (SharedPreference.LoadTutorialStatus(this)) {
            Intent onboarding = new Intent(this, OnboardingActivity.class);
            startActivity(onboarding);
        }
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

    void showSaveDialog() {
        AlertDialog.Builder ad;
        ad = new AlertDialog.Builder(MainActivity.this);

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

    public void showRatingDialog() {

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
                    if (!MyStartActivity(intent))
                        return;
                } else {
                    improve_app();
                }
                ad.dismiss();
            }
        });
    }

    private boolean MyStartActivity(Intent aIntent) {
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
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        if (extras != null) {
            widgetID = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }
        Log.i(LOG_TAG, "widgetID = " + widgetID);

        // и проверяем его корректность
        if (widgetID != AppWidgetManager.INVALID_APPWIDGET_ID) {
            SharedPreference.SaveWidgetID(this, widgetID);
            resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID);

            setResult(RESULT_CANCELED, resultValue);
            boolean isWidgetInstalled = SharedPreference.LoadInstallWidgetState(this);
            boolean isLocationSaved = SharedPreference
                    .LoadIsLocationSavedState(this);
            if (isWidgetInstalled == false) {
                updateWidget(isLocationSaved);
                isWidgetInstalled = true;
                SharedPreference.SaveInstallWidgetState(this, isWidgetInstalled);
                finish();
                return;
            }

            if (isLocationSaved) {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        animationFromMiddleToDown();
                    }
                }, 500);
            } else {
                saveLocation();
            }
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

    void animationFromDownToMiddle() {
        TranslateAnimation animation = new TranslateAnimation(0.0f, 0.0f,
                height / 9, 0);
        animation.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                Log.i(LOG_TAG, "onAnimationStart");
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

    void animationFromMiddleToDown() {
        TranslateAnimation animation = new TranslateAnimation(0.0f, 0.0f,
                0, height / 9);
        animation.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                Log.i(LOG_TAG, "onAnimationStart");
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

    public int saveLocation() {
        Log.i(LOG_TAG, "saveLocation");
        int res = ERROR_CODE.OK;
        if (mCurrentLocation == null) {
            checkLocationSettings();
            res = ERROR_CODE.LOCATION_NOT_RETRIEVED;
            return res;
        }

        saveLocationToSharedMemory(mCurrentLocation);

        Log.i(LOG_TAG, "lat = " + mCurrentLocation.getLatitude());
        Log.i(LOG_TAG, "lng = " + mCurrentLocation.getLongitude());
        saveRatingCountToSharedMemory();
        animationUP();
        car_loc_save_success();
        if (widgetID != 0) {
            finish();
            return res;
        }
        return res;
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
                    ScreenMap.class);
            startActivityForResult(intent, SharedPreference.ACTIVITY_RESULT_CODE.MAP_SCREEN);
        }
    }

    /**
     * Builds a GoogleApiClient. Uses the {@code #addApi} method to request the
     * LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        Log.i(LOG_TAG, "Building GoogleApiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    /**
     * Sets up the location request. Android has two location request settings:
     * {@code ACCESS_COARSE_LOCATION} and {@code ACCESS_FINE_LOCATION}. These settings control
     * the accuracy of the current location. This sample uses ACCESS_FINE_LOCATION, as defined in
     * the AndroidManifest.xml.
     * <p/>
     * When the ACCESS_FINE_LOCATION setting is specified, combined with a fast update
     * interval (5 seconds), the Fused Location Provider API returns location updates that are
     * accurate to within a few feet.
     * <p/>
     * These settings are appropriate for mapping applications that show real-time location
     * updates.
     */
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * Uses a {@link com.google.android.gms.location.LocationSettingsRequest.Builder} to build
     * a {@link com.google.android.gms.location.LocationSettingsRequest} that is used for checking
     * if a device has the needed location settings.
     */
    protected void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }

    /**
     * Check if the device's location settings are adequate for the app's needs using the
     * {@link com.google.android.gms.location.SettingsApi#checkLocationSettings(GoogleApiClient,
     * LocationSettingsRequest)} method, with the results provided through a {@code PendingResult}.
     */
    protected void checkLocationSettings() {
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(
                        mGoogleApiClient,
                        mLocationSettingsRequest
                );
        result.setResultCallback(this);
    }

    /**
     * The callback invoked when
     * {@link com.google.android.gms.location.SettingsApi#checkLocationSettings(GoogleApiClient,
     * LocationSettingsRequest)} is called. Examines the
     * {@link com.google.android.gms.location.LocationSettingsResult} object and determines if
     * location settings are adequate. If they are not, begins the process of presenting a location
     * settings dialog to the user.
     */
    @Override
    public void onResult(LocationSettingsResult locationSettingsResult) {
        final Status status = locationSettingsResult.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                Log.i(LOG_TAG, "All location settings are satisfied.");
                startLocationUpdates();
                if (mCurrentLocation == null)
                    no_location();
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                Log.i(LOG_TAG, "Location settings are not satisfied. Show the user a dialog to" +
                        "upgrade location settings ");

                try {
                    // Show the dialog by calling startResolutionForResult(), and check the result
                    // in onActivityResult().
                    status.startResolutionForResult(MainActivity.this,
                            SharedPreference.ACTIVITY_RESULT_CODE.REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException e) {
                    Log.i(LOG_TAG, "PendingIntent unable to execute request.");
                }
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                Log.i(LOG_TAG, "Location settings are inadequate, and cannot be fixed here. Dialog " +
                        "not created.");
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        boolean isLocationSaved = SharedPreference.LoadIsLocationSavedState(this);
        final Handler handler = new Handler();
        switch (requestCode) {
            case SharedPreference.ACTIVITY_RESULT_CODE.MAP_SCREEN:
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        animationFromDownToMiddle();
                    }
                }, 500);
                updateWidget(isLocationSaved);
                break;
            case SharedPreference.ACTIVITY_RESULT_CODE.REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.i(LOG_TAG, "User agreed to make required location settings changes.");
                        startLocationUpdates();
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.i(LOG_TAG, "User chose not to make required location settings changes.");
                        break;
                }
                break;

        }

    }

    /**
     * Requests location updates from the FusedLocationApi.
     */
    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient,
                    mLocationRequest,
                    this
            ).setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(Status status) {
                }
            });
        }

    }

    /**
     * Removes location updates from the FusedLocationApi.
     */
    protected void stopLocationUpdates() {
        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient,
                this
        ).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {
            }
        });
    }

    private void save_car_location() {
        Toast.makeText(getBaseContext(), R.string.you_should_save_car_location,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
        GoogleAnalytics.getInstance(this).reportActivityStart(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Within {@code onPause()}, we pause location updates, but leave the
        // connection to GoogleApiClient intact.  Here, we resume receiving
        // location updates if the user has requested them.
        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Stop location updates to save battery, but don't disconnect the GoogleApiClient object.
        if (mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
        }
    }

    @Override
    protected void onStop() {
        // Stop the analytics tracking
        GoogleAnalytics.getInstance(this).reportActivityStop(this);
        super.onStop();
        mGoogleApiClient.disconnect();
        setResult(RESULT_OK, resultValue);
    }

    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(LOG_TAG, "Connected to GoogleApiClient");

        // If the initial location was never previously requested, we use
        // FusedLocationApi.getLastLocation() to get it. If it was previously requested, we store
        // its value in the Bundle and check for it in onCreate(). We
        // do not request it again unless the user specifically requests location updates by pressing
        // the Start Updates button.
        //
        // Because we cache the value of the initial location in the Bundle, it means that if the
        // user launches the activity,
        // moves to a new location, and then changes the device orientation, the original location
        // is displayed as the activity is re-created.
        if (mCurrentLocation == null) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            }
            Log.i(LOG_TAG, "mCurrentLocation1 = " + mCurrentLocation);
        }
    }

    /**
     * Callback that fires when the location changes.
     */
    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        Log.i(LOG_TAG, "mCurrentLocation2 = " + mCurrentLocation);
        if (mCurrentLocation != null) {
            saveLocation();
            stopLocationUpdates();
            return;
        }
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(LOG_TAG, "Connection suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(LOG_TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    private void car_loc_save_success() {
        Toast.makeText(this, R.string.save_car_location_success,
                Toast.LENGTH_SHORT).show();
    }

    private void improve_app() {
        Toast.makeText(this, R.string.improve_app, Toast.LENGTH_SHORT).show();
    }

    private void no_location() {
        Toast.makeText(this, R.string.no_location, Toast.LENGTH_SHORT).show();
    }

}
