package com.moggot.findmycarlocation;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.widget.RemoteViews;

public class MyWidget extends AppWidgetProvider {

    final static String ACTION_CHANGE = "com.moggot.widgetbutton.change_count";
    final static String LOG_TAG = "myLogs";

    public final static String WIDGET_PREF = "widget_pref";

    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        // обновляем все экземпляры
        for (int i : appWidgetIds) {
            updateMyWidget(context, appWidgetManager, i);
        }
    }

    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        // Удаляем Preferences
        Editor editor = context.getSharedPreferences(WIDGET_PREF, Context.MODE_PRIVATE).edit();
        final String WIDGET_IS_CAR_PARKED = "is_car_parked";
        for (int widgetID : appWidgetIds) {
            editor.remove(WIDGET_IS_CAR_PARKED + widgetID);
        }
        editor.commit();
        SharedPreference.SaveInstallWidgetState(context, false);
    }

    static void updateMyWidget(Context ctx, AppWidgetManager appWidgetManager,
                             int widgetID) {
        SharedPreferences sp = ctx.getSharedPreferences(
                WIDGET_PREF, Context.MODE_PRIVATE);

        // Читаем счетчик
        boolean isCarParked = sp.getBoolean(SharedPreference.s_state_location_save, false);

        // Помещаем данные в текстовые поля
        RemoteViews widgetView = new RemoteViews(ctx.getPackageName(),
                R.layout.widget);

        Intent configIntent = new Intent(ctx, MainActivity.class);
        configIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE);
        configIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID);
        PendingIntent pIntent = PendingIntent.getActivity(ctx, widgetID,
                configIntent, 0);
        widgetView.setOnClickPendingIntent(R.id.widget_save_location, pIntent);

        if (isCarParked)
            widgetView.setImageViewResource(R.id.widget_save_location, R.mipmap.find_car1);
        else
            widgetView.setImageViewResource(R.id.widget_save_location, R.mipmap.park_car1);
        // Счетчик нажатий (третья зона)
        Intent countIntent = new Intent(ctx, MyWidget.class);
        countIntent.setAction(ACTION_CHANGE);
        countIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID);
        widgetView.setOnClickPendingIntent(R.id.widget_save_location, pIntent);

        // Обновляем виджет
        appWidgetManager.updateAppWidget(widgetID, widgetView);
    }

    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        // Проверяем, что это intent от нажатия на третью зону
        if (intent.getAction().equalsIgnoreCase(ACTION_CHANGE)) {

            // извлекаем ID экземпляра
            int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
            Bundle extras = intent.getExtras();
            if (extras != null) {
                mAppWidgetId = extras.getInt(
                        AppWidgetManager.EXTRA_APPWIDGET_ID,
                        AppWidgetManager.INVALID_APPWIDGET_ID);

            }
            if (mAppWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
                // Читаем значение счетчика, увеличиваем на 1 и записываем
                SharedPreferences sp = context.getSharedPreferences(
                        WIDGET_PREF, Context.MODE_PRIVATE);
                boolean isCarParked = sp.getBoolean(SharedPreference.s_state_location_save, false);
                sp.edit().putBoolean(SharedPreference.s_state_location_save,
                        isCarParked).apply();

                // Обновляем виджет
                updateMyWidget(context, AppWidgetManager.getInstance(context),
                        mAppWidgetId);
            }
        }
    }

}