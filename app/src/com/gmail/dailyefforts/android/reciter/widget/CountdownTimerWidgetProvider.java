package com.gmail.dailyefforts.android.reciter.widget;

import java.util.Calendar;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.util.Log;
import android.widget.RemoteViews;

import com.gmail.dailyefforts.android.reviwer.R;

public class CountdownTimerWidgetProvider extends AppWidgetProvider {

	private static final String TAG = CountdownTimerWidgetProvider.class
			.getSimpleName();

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
				R.layout.countdown_timer_widget);

		for (int i = 0; i < appWidgetIds.length; i++) {
			int widgetId = appWidgetIds[i];
			Log.i(TAG, "widgetId: " + widgetId);

			Calendar cal = Calendar.getInstance();
			cal.set(2014, Calendar.JANUARY, 4);

			long targetTime = cal.getTimeInMillis();

			Log.i(TAG, "targetTime: " + targetTime);
			Log.i(TAG, "curent time: "
					+ Calendar.getInstance().getTimeInMillis());

			updateKaoyan(appWidgetManager, remoteViews, widgetId, targetTime,
					context.getResources().getString(R.string.kaoyan));
		}
	}

	private void updateKaoyan(AppWidgetManager appWidgetManager,
			RemoteViews remoteViews, int widgetId, long targetTime, String name) {
		int days = Math.round((targetTime - Calendar.getInstance()
				.getTimeInMillis()) / WidgetConfig.MillSecondsOfDay);

		remoteViews.setTextViewText(R.id.tv_count_down_label, name);
		remoteViews.setTextViewText(R.id.tv_countdown_days,
				String.valueOf(days));

		appWidgetManager.updateAppWidget(widgetId, remoteViews);
	}
}
