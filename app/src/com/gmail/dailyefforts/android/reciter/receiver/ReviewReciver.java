package com.gmail.dailyefforts.android.reciter.receiver;

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

import com.gmail.dailyefforts.android.reciter.Config;
import com.gmail.dailyefforts.android.reciter.Launcher;
import com.gmail.dailyefforts.android.reviwer.R;
import com.gmail.dailyefforts.android.reciter.Sessions;
import com.gmail.dailyefforts.android.reciter.db.DBA;

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

	private void nofity(Context context, Intent intent) {
		DBA dba = DBA.getInstance(context);
		String word = dba.getOneWordToReview();

		String title = null;

		if (word == null || word.length() == 0) {
			title = context.getString(R.string.time_to_review_words);
		} else {
			title = context.getString(R.string.time_to_review, word);
		}

		Intent willing = new Intent(context.getApplicationContext(),
				Launcher.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
				willing, 0);

		Notification.Builder builder = new Notification.Builder(context);
		builder.setSmallIcon(R.drawable.ic_launcher);
		builder.setTicker(title);
		builder.setContentTitle(title);
		builder.setContentText(context
				.getString(R.string.tap_to_start_reciting_words));

		builder.setContentIntent(pendingIntent);

		NotificationManager nm = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		nm.notify(R.string.time_to_review, builder.build());
	}
}
