package com.gmail.dailyefforts.android.reviwer.widget;

import java.util.Calendar;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.RemoteViews;

import com.gmail.dailyefforts.android.reviwer.R;

public class CountdownTimerWidgetProvider extends AppWidgetProvider {

	private static final String TAG = CountdownTimerWidgetProvider.class
			.getSimpleName();

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		RemoteViews rv = new RemoteViews(context.getPackageName(),
				R.layout.countdown_timer_widget);

		SharedPreferences prefs = context.getSharedPreferences(
				WidgetConfig.PrefFileName, Context.MODE_PRIVATE);

		for (int i = 0; i < appWidgetIds.length; i++) {
			int widgetId = appWidgetIds[i];
			Log.i(TAG, "widgetId: " + widgetId);

			long targetTime = prefs.getLong(String.format(
					WidgetConfig.CountdownTimerTargetTimeFormat, widgetId), 0);
			String name = prefs.getString(String.format(
					WidgetConfig.CountdownTimerNameFormat, widgetId), null);

			if (targetTime == 0) {
				Log.e(TAG, "onUpdate() target time is 0");
				return;
			}

			if (name == null) {
				Log.e(TAG, "onUpdate() name is null");
				return;
			}

			Log.i(TAG, "timer name: " + name);
			Log.i(TAG, "targetTime: " + targetTime);
			Log.i(TAG, "curent time: "
					+ Calendar.getInstance().getTimeInMillis());

			int days = Math.round((targetTime - Calendar.getInstance()
					.getTimeInMillis()) / WidgetConfig.MillSecondsOfDay + 0.5f);

			rv.setTextViewText(R.id.tv_count_down_label, name);
			rv.setTextViewText(R.id.tv_countdown_days, String.valueOf(days));

			appWidgetManager.updateAppWidget(widgetId, rv);
		}
	}
}
