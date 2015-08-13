package com.example.findmycar;

import java.text.SimpleDateFormat;
import java.util.Date;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.location.LocationManager;
import android.util.Log;
import android.widget.Toast;

public class ProximityIntentReceiver extends BroadcastReceiver {
	final static String LOG_TAG = "myLogs";

	@SuppressLint("SimpleDateFormat")
	@SuppressWarnings("deprecation")
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(LOG_TAG, "CLICK!!!");
		String key = LocationManager.KEY_PROXIMITY_ENTERING;
		Boolean entering = intent.getBooleanExtra(key, false);
		if (entering) {
			Log.d(LOG_TAG, "ENTER");
			Toast.makeText(context, R.string.car_found, Toast.LENGTH_LONG)
					.show();

			SimpleDateFormat format_time = new SimpleDateFormat("dd HH:mm");
			String real_time = format_time.format(new Date());
			String park_time = SharedPreference.LoadTime(context);
			Log.d(LOG_TAG, "real_time = " + real_time);
			Log.d(LOG_TAG, "park_time = " + park_time);
			String difference = timeDifference(context, park_time, real_time);
			Log.d(LOG_TAG, "difference = " + difference);
		} else {
			Log.d(LOG_TAG, "EXIT");
		}
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);

		Intent notificationIntent = new Intent(context, ScreenMap.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
				notificationIntent, 0);
		Notification notification = createNotification();
		notification.setLatestEventInfo(context, "Proximity Alert!",
				"You are near your point of interest.", pendingIntent);

		notificationManager.notify(1000, notification);
	}

	@SuppressLint("SimpleDateFormat")
	private Notification createNotification() {
		Notification notification = new Notification();
		notification.icon = R.drawable.ic_launcher;
		notification.when = System.currentTimeMillis();
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notification.flags |= Notification.FLAG_SHOW_LIGHTS;
		notification.defaults |= Notification.DEFAULT_VIBRATE;
		notification.defaults |= Notification.DEFAULT_LIGHTS;
		notification.ledARGB = Color.WHITE;
		notification.ledOnMS = 1500;
		notification.ledOffMS = 1500;
		return notification;
	}

	@SuppressLint("SimpleDateFormat")
	String timeDifference(Context ctx, String park_time, String real_time) {
		String difference = null;
		SimpleDateFormat format = new SimpleDateFormat("dd HH:mm");
		Date d1 = null;
		Date d2 = null;
		Configuration c = new Configuration(ctx.getResources()
				.getConfiguration());
		String sDefSystemLanguage = c.locale.getLanguage();

		try {
			d1 = format.parse(real_time);
			d2 = format.parse(park_time);

			long diff = d1.getTime() - d2.getTime();

			long diffMinutes = diff / (60 * 1000) % 60;
			long diffHours = diff / (60 * 60 * 1000) % 24;

			String min_str = "";
			if (diffMinutes == 1 || diffMinutes == 21 || diffMinutes == 31
					|| diffMinutes == 41 || diffMinutes == 51)
				min_str = " минуту ";
			else if (diffMinutes == 2 || diffMinutes == 3 || diffMinutes == 4
					|| diffMinutes == 22 || diffMinutes == 23
					|| diffMinutes == 24 || diffMinutes == 32
					|| diffMinutes == 33 || diffMinutes == 34
					|| diffMinutes == 42 || diffMinutes == 43
					|| diffMinutes == 44 || diffMinutes == 52
					|| diffMinutes == 53 || diffMinutes == 54)
				min_str = " минуты ";
			else
				min_str = " минут ";
			String hour_str = "";
			if (diffHours == 1 || diffHours == 21)
				hour_str = " час ";
			else if (diffHours == 1 || diffHours == 2 || diffHours == 3
					|| diffHours == 4 || diffHours == 22 || diffHours == 23
					|| diffHours == 24)
				hour_str = " часа ";
			else
				hour_str = " часов ";

			if (sDefSystemLanguage.equals("ru")) {
				if (diffHours == 0) {

					difference = diffMinutes + min_str;

				} else {
					difference = diffHours + hour_str + diffMinutes + min_str;
				}
			} else {

				if (diffHours == 0)
					difference = diffMinutes + " minutes ";
				else
					difference = diffHours + " hours " + diffMinutes
							+ " minutes ";
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return difference;
	}
}
