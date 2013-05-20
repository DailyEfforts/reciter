package com.gmail.dailyefforts.android.reviwer.widget;

import java.util.Calendar;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RemoteViews;

import com.gmail.dailyefforts.android.reviwer.R;

public class CountdownTimerWidgetConfigActivity extends Activity implements
		OnClickListener {

	private static final String TAG = CountdownTimerWidgetConfigActivity.class
			.getSimpleName();
	private DatePicker mDatePicker;
	private Button mBtnDone;
	private EditText mEditTextName;
	private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setResult(RESULT_CANCELED);

		setContentView(R.layout.activity_countdown_timer_widget_config);

		mEditTextName = (EditText) findViewById(R.id.et_countdown_timer_name);

		mDatePicker = (DatePicker) findViewById(R.id.dp_countdown_timer);
		mDatePicker.updateDate(2014, Calendar.JANUARY, 4);

		mBtnDone = (Button) findViewById(R.id.btn_countdown_timer_set_done);

		mBtnDone.setOnClickListener(this);

		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		if (extras != null) {
			mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
					AppWidgetManager.INVALID_APPWIDGET_ID);
		}

		Log.i(TAG, "onCreate() mAppWidgetId: " + mAppWidgetId);

		if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
			finish();
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_countdown_timer_set_done:
			AppWidgetManager appWidgetManager = AppWidgetManager
					.getInstance(CountdownTimerWidgetConfigActivity.this);
			RemoteViews views = new RemoteViews(
					CountdownTimerWidgetConfigActivity.this.getPackageName(),
					R.layout.countdown_timer_widget);
			CharSequence name = mEditTextName.getText() == null
					|| mEditTextName.getText().length() <= 0 ? mEditTextName
					.getHint() : mEditTextName.getText();
			views.setTextViewText(R.id.tv_count_down_label, name);

			Calendar calTarget = Calendar.getInstance();
			calTarget.set(mDatePicker.getYear(), mDatePicker.getMonth(),
					mDatePicker.getDayOfMonth(), 0, 0, 0);
			int days = Math.round((calTarget.getTimeInMillis() - Calendar
					.getInstance().getTimeInMillis())
					/ WidgetConfig.MillSecondsOfDay + 0.5f);

			views.setTextViewText(R.id.tv_countdown_days, String.valueOf(days));

			savePref(name, calTarget);
			appWidgetManager.updateAppWidget(mAppWidgetId, views);
			Intent resultValue = new Intent();
			resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
					mAppWidgetId);
			setResult(RESULT_OK, resultValue);
			finish();
			break;
		}
	}

	private void savePref(CharSequence name, Calendar calTarget) {
		SharedPreferences prefs = CountdownTimerWidgetConfigActivity.this
				.getSharedPreferences(WidgetConfig.PrefFileName,
						Context.MODE_PRIVATE);
		if (prefs != null) {
			SharedPreferences.Editor editor = prefs.edit();
			editor.putString(String.format(
					WidgetConfig.CountdownTimerNameFormat, mAppWidgetId),
					String.valueOf(name));
			editor.putLong(String.format(
					WidgetConfig.CountdownTimerTargetTimeFormat, mAppWidgetId),
					calTarget.getTimeInMillis());
			editor.commit();
		}
	}

}
