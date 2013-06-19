package com.gmail.dailyefforts.android.reviwer.receiver;

import java.util.Calendar;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.gmail.dailyefforts.android.reviwer.Config;
import com.gmail.dailyefforts.android.reviwer.R;
import com.gmail.dailyefforts.android.reviwer.Sessions;

public class ReviewReciver extends BroadcastReceiver {

	private static final String TAG = ReviewReciver.class.getSimpleName();

	@SuppressWarnings("deprecation")
	@Override
	public void onReceive(Context context, Intent intent) {

		if (Config.DEBUG) {
			Log.d(TAG, "ReviewReciver: onReceive() " + Sessions.RUNNING);
		}

		if (Sessions.RUNNING) {
			return;
		}

		SharedPreferences mSharedPref = PreferenceManager
				.getDefaultSharedPreferences(context.getApplicationContext());
		if (mSharedPref == null) {
			return;
		} else {
			boolean allowed = mSharedPref.getBoolean(
					context.getString(R.string.pref_key_review_notification),
					false);
			if (!allowed) {
				return;
			}
		}
		if (Config.DEBUG) {
			Log.d(TAG, "ReviewReciver: onReceive() notifi");
		}
		Calendar cal = Calendar.getInstance();

		int day = cal.get(Calendar.DAY_OF_WEEK);
		if (day == Calendar.SATURDAY || day == Calendar.SUNDAY) {
			return;
		}

		int hour = cal.get(Calendar.HOUR_OF_DAY);
		if (hour > 16 || hour < 9) {
			return;
		}

		Notification notification = new Notification();
		notification.icon = R.drawable.ic_launcher;
		notification.tickerText = context.getString(R.string.time_to_review);
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		notification.defaults = Notification.DEFAULT_ALL;

		Intent willing = new Intent(context.getApplicationContext(),
				Sessions.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
				willing, 0);

		notification
				.setLatestEventInfo(context, notification.tickerText,
						context.getString(R.string.time_to_review_words),
						pendingIntent);

		NotificationManager nm = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		nm.notify(R.string.time_to_review, notification);

	}
}
