package com.gmail.dailyefforts.android.reviwer.receiver;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.gmail.dailyefforts.android.reviwer.Config;

public class BootReciver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		AlarmManager am = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		PendingIntent sender = PendingIntent.getBroadcast(context, 0,
				new Intent(Config.ACTION_REVIEW),
				PendingIntent.FLAG_CANCEL_CURRENT);
		am.setRepeating(AlarmManager.RTC, Calendar.getInstance()
				.getTimeInMillis(), Config.INTERVAL_TIME_TO_TIP_REVIEW, sender);
	}

}
