package com.example.findmycar;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

public class ProximityActivity extends Activity {

	String notificationTitle;
	String notificationContent;
	String tickerMessage;
	final static String LOG_TAG = "myLogs";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Log.d(LOG_TAG, "ENTER");
		boolean proximity_entering = getIntent().getBooleanExtra(
				LocationManager.KEY_PROXIMITY_ENTERING, true);

		if (proximity_entering) {
//			Intent intent = new Intent(this, ScreenMap.class);
//			PendingIntent pendingIntent = PendingIntent.getActivity(getBaseContext(),
//					0, intent, Intent.FLAG_ACTIVITY_NEW_TASK);
//			LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//			locationManager.removeProximityAlert(pendingIntent);
//			SimpleDateFormat format_time = new SimpleDateFormat("dd HH:mm");
//			String real_time = format_time.format(new Date());
//			String park_time = SharedPreference.LoadTime(this);
//			Log.d(LOG_TAG, "real_time = " + real_time);
//			Log.d(LOG_TAG, "park_time = " + park_time);
//			String difference = timeDifference(park_time, real_time);
//			Log.d(LOG_TAG, "difference = " + difference);
			Toast.makeText(getBaseContext(), "Entering the region",
					Toast.LENGTH_LONG).show();
//			NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
//					context).setSmallIcon(R.drawable.ic_launcher)
//					.setContentTitle("appname").setContentText("msg")
//					.setAutoCancel(true);
//
//			NotificationManager mNotificationManager = (NotificationManager) context
//					.getSystemService(Context.NOTIFICATION_SERVICE);
//
//			Notification n = mBuilder.getNotification();
//			n.flags |= Notification.FLAG_AUTO_CANCEL;
//			n.defaults = Notification.DEFAULT_SOUND
//					| Notification.DEFAULT_VIBRATE;
//			mNotificationManager.notify(1, n);
		} else {
			Toast.makeText(getBaseContext(), "Exiting the region",
					Toast.LENGTH_LONG).show();

		}

		// Intent notificationIntent = new Intent(getApplicationContext(),
		// NotificationView.class);
		// notificationIntent.putExtra("content", notificationContent);
		//
		// /**
		// * This is needed to make this intent different from its previous
		// * intents
		// */
		// notificationIntent.setData(Uri.parse("tel:/"
		// + (int) System.currentTimeMillis()));
		//
		// /**
		// * Creating different tasks for each notification. See the flag
		// * Intent.FLAG_ACTIVITY_NEW_TASK
		// */
		// PendingIntent pendingIntent = PendingIntent.getActivity(
		// getApplicationContext(), 0, notificationIntent,
		// Intent.FLAG_ACTIVITY_NEW_TASK);
		//
		// /** Getting the System service NotificationManager */
		// NotificationManager nManager = (NotificationManager)
		// getApplicationContext()
		// .getSystemService(Context.NOTIFICATION_SERVICE);
		//
		// /** Configuring notification builder to create a notification */
		// NotificationCompat.Builder notificationBuilder = new
		// NotificationCompat.Builder(
		// getApplicationContext()).setWhen(System.currentTimeMillis())
		// .setContentText(notificationContent)
		// .setContentTitle(notificationTitle)
		// .setSmallIcon(R.drawable.ic_launcher).setAutoCancel(true)
		// .setTicker(tickerMessage).setContentIntent(pendingIntent);
		//
		// /** Creating a notification from the notification builder */
		// Notification notification = notificationBuilder.build();
		//
		// /**
		// * Sending the notification to system. The first argument ensures that
		// * each notification is having a unique id If two notifications share
		// * same notification id, then the last notification replaces the first
		// * notification
		// * */
		// nManager.notify((int) System.currentTimeMillis(), notification);
		//
		// /** Finishes the execution of this activity */
		finish();

	}

	String timeDifference(String park_time, String real_time) {
		String difference = null;
		SimpleDateFormat format = new SimpleDateFormat("dd HH:mm");
		Date d1 = null;
		Date d2 = null;
		Configuration c = new Configuration(getResources().getConfiguration());
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
