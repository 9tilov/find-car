package com.moggot.findmycarlocation;

/**
 * Created by dmitry on 02.09.15.
 */

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
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

    NotificationCompat.Builder mBuilder;

    @SuppressLint("SimpleDateFormat")
    @SuppressWarnings("deprecation")
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(LOG_TAG, "CLICK!!!");
        String key = LocationManager.KEY_PROXIMITY_ENTERING;
        Boolean entering = intent.getBooleanExtra(key, false);
        String difference = "";
        if (entering) {
            Log.d(LOG_TAG, "ENTER");
            car_found(context);

            SimpleDateFormat format_time = new SimpleDateFormat("dd HH:mm");
            String real_time = format_time.format(new Date());
            String park_time = SharedPreference.LoadTime(context);
            Log.d(LOG_TAG, "real_time = " + real_time);
            Log.d(LOG_TAG, "park_time = " + park_time);
            difference = timeDifference(context, park_time, real_time);
            SharedPreference.clearPref(context);
            SharedPreference.SaveIsLocationSavedState(context, false);
            Log.d(LOG_TAG, "difference = " + difference);
        } else {
            Log.d(LOG_TAG, "EXIT");
        }
        if (Build.VERSION.SDK_INT < 16) {
            Intent notificationIntent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                    notificationIntent, 0);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
            NotificationManager notificationManager = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            Notification notification = builder.setContentIntent(pendingIntent)
                    .setSmallIcon(R.mipmap.car)
                    .setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_SOUND)
                    .setContentText(context.getResources().getString(R.string.parking_time) + " " + difference).build();
            notificationManager.notify(199, notification);
            Log.d(LOG_TAG, "<16");

        } else {
            NotificationManager notificationManager = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);

            Intent notificationIntent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                    notificationIntent, 0);


            Notification notification = new Notification.Builder(context)
                    .setContentTitle(context.getResources().getString(R.string.car_found))
                    .setContentText(context.getResources().getString(R.string.parking_time) + " " + difference)
                    .setSmallIcon(R.drawable.notif)
                    .setDefaults(Notification.DEFAULT_SOUND)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent).build();
            notificationManager.notify(1000, notification);
            Log.d(LOG_TAG, ">=16");
        }
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
                min_str = " минута ";
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
            else if (diffHours == 2 || diffHours == 3
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

                if (diffHours == 0) {
                    difference = diffMinutes + "";
                    if (diffMinutes == 1)
                        difference += " minute ";
                    else
                        difference += " minutes ";
                } else {
                    difference = diffHours + " hours ";
                    if (diffMinutes == 1)
                        difference += diffMinutes + " minute ";
                    else
                        difference += diffMinutes + " minutes ";
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