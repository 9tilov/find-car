package com.moggot.findmycarlocation;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.location.LocationManager;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ProximityIntentReceiver extends BroadcastReceiver {
    final static String LOG_TAG = "myLogs";

    @SuppressLint("SimpleDateFormat")
    @SuppressWarnings("deprecation")
    @Override
    public void onReceive(Context context, Intent intent) {
        String key = LocationManager.KEY_PROXIMITY_ENTERING;
        Boolean entering = intent.getBooleanExtra(key, false);
        String difference = "";
        if (entering) {
            Log.d(LOG_TAG, "ENTER");
            car_found(context);

            SimpleDateFormat format_time = new SimpleDateFormat("dd HH:mm");
            String real_time = format_time.format(new Date());
            String park_time = SharedPreference.LoadTime(context);
            difference = timeDifference(context, park_time, real_time);
            SharedPreference.clearPref(context);
            SharedPreference.SaveIsLocationSavedState(context, false);
        }
        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT < 16) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
            Notification notification = builder.setSmallIcon(R.mipmap.car)
                    .setAutoCancel(true)
                    .setContentTitle(context.getResources().getString(R.string.car_found))
                    .setDefaults(Notification.DEFAULT_SOUND)
                    .setContentText(context.getResources().getString(R.string.parking_time) + " " + difference).build();
            notificationManager.notify(1000, notification);
            Log.d(LOG_TAG, "<16");

        } else {
            Notification notification = new Notification.Builder(context)
                    .setContentTitle(context.getResources().getString(R.string.car_found))
                    .setContentText(context.getResources().getString(R.string.parking_time) + " " + difference)
                    .setSmallIcon(R.drawable.notif)
                    .setDefaults(Notification.DEFAULT_SOUND)
                    .setAutoCancel(true).build();
            notificationManager.notify(1000, notification);
            Log.d(LOG_TAG, ">=16");
        }
    }

    @SuppressLint("SimpleDateFormat")
    String timeDifference(Context ctx, String park_time, String real_time) {
        String difference = null;
        SimpleDateFormat format = new SimpleDateFormat("dd HH:mm");
        Date d1, d2;
        Configuration c = new Configuration(ctx.getResources()
                .getConfiguration());
        String sDefSystemLanguage = c.locale.getLanguage();

        try {
            d1 = format.parse(real_time);
            d2 = format.parse(park_time);

            long diff = d1.getTime() - d2.getTime();

            long diffMinutes = diff / (60 * 1000) % 60;
            long diffHours = diff / (60 * 60 * 1000) % 24;

            String min_str;
            String hour_str;
            if (sDefSystemLanguage.equals("ru")) {
                min_str = " мин ";
                hour_str = " ч ";
                if (diffHours == 0) {
                    difference = diffMinutes + min_str;
                } else {
                    difference = diffHours + hour_str + diffMinutes + min_str;
                }
            } else {
                min_str = " min ";
                hour_str = " h ";
                if (diffHours == 0) {
                    difference = diffMinutes + min_str;
                } else {
                    difference = diffHours + hour_str;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return difference;
    }

    private void car_found(Context ctx) {
        Toast.makeText(ctx, R.string.car_found, Toast.LENGTH_LONG).show();
    }
}