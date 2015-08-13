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

	final static String LOG_TAG = "myLogs";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Intent intent = new Intent(MainActivity.this, ScreenMap.class);
		// startActivityForResult(intent, 1);
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnSaveLocation:
			Calendar time = Calendar.getInstance();
			int cur_day = time.get(Calendar.DAY_OF_MONTH);
			int cur_hour = time.get(Calendar.HOUR_OF_DAY);
			int cur_minute = time.get(Calendar.MINUTE);
			SharedPreference.SaveTime(this, cur_day, cur_hour, cur_minute);
			LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			Criteria criteria = new Criteria();
			String provider = locationManager.getBestProvider(criteria, false);
			if (provider != null && !provider.equals("")) {
				Location location = locationManager
						.getLastKnownLocation(provider);
				if (location != null) {

					Log.d(LOG_TAG, "location_lng = " + location.getLongitude());
					Log.d(LOG_TAG, "location_lat = " + location.getLatitude());
					SharedPreference.SaveLocation(this, 55.93115, 37.522269);
					
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
			break;

		case R.id.btnLoadLocation:
			LatLng location = SharedPreference.LoadState(this);
			Log.d(LOG_TAG, "location_lat = " + location.latitude);
			Log.d(LOG_TAG, "location_lng = " + location.longitude);
			Intent intent = new Intent(MainActivity.this, ScreenMap.class)
					.putExtra(SharedPreference.EXTRA_ARRIVAL_LATITUDE,
							location.latitude).putExtra(
							SharedPreference.EXTRA_ARRIVAL_LONGITUDE,
							location.longitude);
			startActivityForResult(intent, 1);
			break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
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

}
