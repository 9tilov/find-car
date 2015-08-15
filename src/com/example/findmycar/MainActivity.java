package com.example.findmycar;

import java.util.Calendar;

import com.google.android.gms.maps.model.LatLng;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends Activity {

	float y1, y2;
	ImageView img_animation;
	int height;

	int trigger = 0;
	final static String LOG_TAG = "myLogs";
	boolean state;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		state = SharedPreference.LoadState(this);

		img_animation = (ImageView) findViewById(R.id.ivTrigger);
		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		height = displaymetrics.heightPixels;

		// Intent intent = new Intent(MainActivity.this, ScreenMap.class);
		// startActivityForResult(intent, 1);
	}

	public boolean onTouchEvent(MotionEvent touchevent) {
		switch (touchevent.getAction()) {
		// when user first touches the screen we get x and y coordinate
		case MotionEvent.ACTION_DOWN: {
			y1 = touchevent.getY();
			break;
		}
		case MotionEvent.ACTION_UP: {
			y2 = touchevent.getY();

			if (y1 < y2) {
				state = SharedPreference.LoadState(this);
				// if UP to DOWN sweep event on screen
				if (trigger == 0 && state) {
					animation(0, height / 8);
					final LatLng location = SharedPreference.LoadLocation(this);
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {

							Log.d(LOG_TAG, "location_lat = "
									+ location.latitude);
							Log.d(LOG_TAG, "location_lng = "
									+ location.longitude);
							Intent intent = new Intent(MainActivity.this,
									ScreenMap.class).putExtra(
									SharedPreference.EXTRA_ARRIVAL_LATITUDE,
									location.latitude).putExtra(
									SharedPreference.EXTRA_ARRIVAL_LONGITUDE,
									location.longitude);
							startActivityForResult(intent, 1);
						}
					}, 1000);

					trigger = -1;
					break;
				}
				if (trigger == 1) {
					animation(-height / 8, 0);
					trigger = 0;
					break;
				}
			}

			// if Down to UP sweep event on screen
			if (y1 > y2) {
				if (trigger == -1) {
					animation(height / 8, 0);
					trigger = 0;
					break;
				}
				if (trigger == 0) {
					animation(0, -height / 8);
					Calendar time = Calendar.getInstance();
					int cur_day = time.get(Calendar.DAY_OF_MONTH);
					int cur_hour = time.get(Calendar.HOUR_OF_DAY);
					int cur_minute = time.get(Calendar.MINUTE);
					SharedPreference.SaveTime(this, cur_day, cur_hour,
							cur_minute);
					SharedPreference.SaveState(this, true);
					LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
					Criteria criteria = new Criteria();
					String provider = locationManager.getBestProvider(criteria,
							false);
					if (provider != null && !provider.equals("")) {
						Location location = locationManager
								.getLastKnownLocation(provider);
						if (location != null) {

							Log.d(LOG_TAG,
									"location_lng = " + location.getLongitude());
							Log.d(LOG_TAG,
									"location_lat = " + location.getLatitude());
							SharedPreference.SaveLocation(this, 55.93115,
									37.522269);

							Toast.makeText(getBaseContext(),
									R.string.save_car_loc, Toast.LENGTH_SHORT)
									.show();
							// SharedPreference.SaveLocation(this,
							// location.getLatitude(), location.getLongitude());
						} else
							Toast.makeText(getBaseContext(),
									R.string.no_location, Toast.LENGTH_SHORT)
									.show();
					} else {
						Toast.makeText(getBaseContext(), R.string.no_provider,
								Toast.LENGTH_SHORT).show();
					}

					trigger = 1;
					break;
				}
			}
			break;

		}
		}
		return false;
	}

	void animation(float start, float end) {
		TranslateAnimation animation = new TranslateAnimation(0.0f, 0.0f,
				start, end);
		animation.setDuration(1000);
		animation.setFillAfter(true);
		img_animation.startAnimation(animation);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			startActivity(new Intent(
					android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		animation(height / 8, 0);
		trigger = 0;
	}

}
