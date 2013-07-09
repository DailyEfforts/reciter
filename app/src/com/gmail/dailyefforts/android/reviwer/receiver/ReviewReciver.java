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
import com.gmail.dailyefforts.android.reviwer.Launcher;
import com.gmail.dailyefforts.android.reviwer.R;
import com.gmail.dailyefforts.android.reviwer.Sessions;
import com.gmail.dailyefforts.android.reviwer.db.DBA;

public class ReviewReciver extends BroadcastReceiver {

	private static final String TAG = ReviewReciver.class.getSimpleName();

	private boolean shouldNofity(Context context) {
		if (Sessions.RUNNING) {
			return false;
		}

		SharedPreferences mSharedPref = PreferenceManager
				.getDefaultSharedPreferences(context.getApplicationContext());
		if (mSharedPref == null) {
			return false;
		} else {
			boolean allowed = mSharedPref.getBoolean(
					context.getString(R.string.pref_key_review_notification),
					false);
			if (!allowed) {
				return false;
			}
		}
		if (Config.DEBUG) {
			Log.d(TAG, "ReviewReciver: onReceive() notifi");
		}
		Calendar cal = Calendar.getInstance();

		int day = cal.get(Calendar.DAY_OF_WEEK);
		if (day == Calendar.SATURDAY || day == Calendar.SUNDAY) {
			return false;
		}

		int hour = cal.get(Calendar.HOUR_OF_DAY);
		if (hour > 19 || hour < 8) {
			return false;
		}

		return true;
	}

	@Override
	public void onReceive(Context context, Intent intent) {

		if (Config.DEBUG) {
			Log.d(TAG, "ReviewReciver: onReceive() " + Sessions.RUNNING);
		}
		
		if (shouldNofity(context)) {
			nofity(context, intent);
		}

	}

	@SuppressWarnings("deprecation")
	private void nofity(Context context, Intent intent) {
		DBA dba = DBA.getInstance(context);
		String word = dba.getOneWordToReview();

		Notification notification = new Notification();
		notification.icon = R.drawable.ic_launcher;
		if (word == null || word.length() == 0) {
			notification.tickerText = context
					.getString(R.string.time_to_review_words);
		} else {
			notification.tickerText = context.getString(
					R.string.time_to_review, word);
		}
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		notification.defaults = Notification.DEFAULT_ALL;

		Intent willing = new Intent(context.getApplicationContext(),
				Launcher.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
				willing, 0);

		notification.setLatestEventInfo(context, notification.tickerText,
				context.getString(R.string.tap_to_start_reciting_words),
				pendingIntent);

		NotificationManager nm = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		nm.notify(R.string.time_to_review, notification);
	}
}
