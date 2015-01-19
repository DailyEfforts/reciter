package com.gmail.dailyefforts.reciter;

import android.app.AlarmManager;
import android.app.ExpandableListActivity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.gmail.dailyefforts.reciter.db.DBA;
import com.gmail.dailyefforts.reciter.setting.SettingsActivity;
import com.gmail.dailyefforts.android.reviwer.R;

public class MainActivity extends ExpandableListActivity implements
		OnClickListener {

	private Button mBtnExit;
	private Button mBtnSettings;

	private static final String TAG = MainActivity.class.getSimpleName();
	private SharedPreferences mSharedPref;

	private static class ViewHolder {
		TextView tv;
	}

	private class BooksAdapter extends BaseExpandableListAdapter {
		int[] groups = { R.string.english, R.string.french,
				R.string.linguistics, R.string.literature };
		int[][] children = {
				{ R.string.nce1, R.string.nce2, R.string.nce3, R.string.nce4,
						R.string.ogden, R.string.liuyi_5000 },
				{ R.string.mot, R.string.reflets_1 },
				{ R.string.linguistics_glossary },
				{ R.string.american_literature } };

		@Override
		public int getGroupCount() {
			return groups.length;
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			return children[groupPosition].length;
		}

		@Override
		public Object getGroup(int groupPosition) {
			return groups[groupPosition];
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {

			return children[groupPosition][childPosition];
		}

		@Override
		public long getGroupId(int groupPosition) {

			return groupPosition;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {

			return childPosition;
		}

		@Override
		public boolean hasStableIds() {

			return false;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = (TextView) getLayoutInflater().inflate(
						R.layout.item, null);
				holder = new ViewHolder();
				holder.tv = (TextView) convertView;
				convertView.setAlpha(0.6F);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.tv.setText(groups[groupPosition]);
			return convertView;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = (TextView) getLayoutInflater().inflate(
						R.layout.item, null);
				holder = new ViewHolder();
				holder.tv = (TextView) convertView;
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			final int resId = children[groupPosition][childPosition];
			holder.tv.setText(resId);
			holder.tv.setId(resId);
			return convertView;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		final ExpandableListAdapter adapter = new BooksAdapter();
		setListAdapter(adapter);

		findViewsAndSetListeners();
		mSharedPref = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		Config.IS_RUNNING = true;
		setReviewAlarm();

		getExpandableListView().expandGroup(0);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Config.IS_RUNNING = false;
	}

	private void setReviewAlarm() {
		AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		PendingIntent sender = PendingIntent.getBroadcast(this, 0, new Intent(
				Config.ACTION_REVIEW), PendingIntent.FLAG_CANCEL_CURRENT);
		am.setRepeating(AlarmManager.RTC, System.currentTimeMillis(),
				Config.INTERVAL_TIME_TO_TIP_REVIEW, sender);
	}

	private boolean needToCheckForUpdate(long lastTime) {
		boolean ret = false;

		long currentTimeMillis = System.currentTimeMillis();
		if (currentTimeMillis - lastTime > Config.ONE_DAY) {
			ret = true;
			updateLastCheckForUpdateTime(currentTimeMillis);
		}

		if (Config.DEBUG) {
			Log.d(TAG, "needToCheckForUpdate() ret: " + ret);
		}
		return ret;
	}

	private void updateLastCheckForUpdateTime(long currentTimeMillis) {
		Editor editor = mSharedPref.edit();
		editor.putLong(Config.LAST_TIME_CHECKED_FOR_UPDATE, currentTimeMillis);
		editor.commit();
	}

	private void findViewsAndSetListeners() {
		mBtnExit = (Button) findViewById(R.id.btn_exit);
		mBtnSettings = (Button) findViewById(R.id.btn_settings);

		if (mBtnExit != null) {
			mBtnExit.setOnClickListener(this);
		}

		if (mBtnSettings != null) {
			mBtnSettings.setOnClickListener(this);
		}
	}

	private void launchVersionChecker() {
		Log.i(TAG, "checkLatestVersion()");
		Intent intent = new Intent(Config.ACTION_NAME_CHECK_VERSION);
		startService(intent);
	}

	@Override
	public boolean onChildClick(ExpandableListView parent, View v,
			int groupPosition, int childPosition, long id) {

		final int viewId = v.getId();
		switch (viewId) {
		case R.string.mot:
			DBA.CURRENT_WORD_TABLE = DBA.TABLE_WORD_LIST;
			DBA.CURRENT_TEST_REPORT_TABLE = DBA.TABLE_TEST_REPORT;
			Config.CURRENT_BOOK_NAME = Config.BOOK_NAME_MOT;
			Config.CURRENT_LANGUAGE = Language.French;
			break;
		case R.string.nce1:
			DBA.CURRENT_WORD_TABLE = DBA.TABLE_WORD_LIST_NCE1;
			DBA.CURRENT_TEST_REPORT_TABLE = DBA.TABLE_TEST_REPORT_NCE1;
			Config.CURRENT_BOOK_NAME = Config.BOOK_NAME_NCE1;
			Config.CURRENT_LANGUAGE = Language.English;
			break;
		case R.string.nce2:
			DBA.CURRENT_WORD_TABLE = DBA.TABLE_WORD_LIST_NCE2;
			DBA.CURRENT_TEST_REPORT_TABLE = DBA.TABLE_TEST_REPORT_NCE2;
			Config.CURRENT_BOOK_NAME = Config.BOOK_NAME_NCE2;
			Config.CURRENT_LANGUAGE = Language.English;
			break;
		case R.string.nce3:
			DBA.CURRENT_WORD_TABLE = DBA.TABLE_WORD_LIST_NCE3;
			DBA.CURRENT_TEST_REPORT_TABLE = DBA.TABLE_TEST_REPORT_NCE3;
			Config.CURRENT_BOOK_NAME = Config.BOOK_NAME_NCE3;
			Config.CURRENT_LANGUAGE = Language.English;
			break;
		case R.string.nce4:
			DBA.CURRENT_WORD_TABLE = DBA.TABLE_WORD_LIST_NCE4;
			DBA.CURRENT_TEST_REPORT_TABLE = DBA.TABLE_TEST_REPORT_NCE4;
			Config.CURRENT_BOOK_NAME = Config.BOOK_NAME_NCE4;
			Config.CURRENT_LANGUAGE = Language.English;
			break;
		case R.string.reflets_1:
			DBA.CURRENT_WORD_TABLE = DBA.TABLE_WORD_LIST_REFLETS1U;
			DBA.CURRENT_TEST_REPORT_TABLE = DBA.TABLE_TEST_REPORT_REFLETS1U;
			Config.CURRENT_BOOK_NAME = Config.BOOK_NAME_REFLETS1U;
			Config.CURRENT_LANGUAGE = Language.French;
			break;
		case R.string.linguistics_glossary:
			DBA.CURRENT_WORD_TABLE = DBA.TABLE_WORD_LIST_LINGUISTICS_GLOSSARY;
			DBA.CURRENT_TEST_REPORT_TABLE = DBA.TABLE_TEST_REPORT_INGUISTICS_GLOSSARY;
			Config.CURRENT_BOOK_NAME = Config.BOOK_NAME_LINGUISTICS_GLOSSARY;
			Config.CURRENT_LANGUAGE = Language.English;
			break;
		case R.string.liuyi_5000:
			DBA.CURRENT_WORD_TABLE = DBA.TABLE_WORD_LIST_LIUYI_5000;
			DBA.CURRENT_TEST_REPORT_TABLE = DBA.TABLE_TEST_REPORT_LIUYI_5000;
			Config.CURRENT_BOOK_NAME = Config.BOOK_NAME_PRO_LIUYI_5000;
			Config.CURRENT_LANGUAGE = Language.English;
			break;
		case R.string.ogden:
			DBA.CURRENT_WORD_TABLE = DBA.TABLE_WORD_LIST_OGDEN;
			DBA.CURRENT_TEST_REPORT_TABLE = DBA.TABLE_TEST_REPORT_OGDEN;
			Config.CURRENT_BOOK_NAME = Config.BOOK_NAME_OGDEN;
			Config.CURRENT_LANGUAGE = Language.English;
			break;
		case R.string.american_literature:
			DBA.CURRENT_WORD_TABLE = DBA.TABLE_WORD_LIST_LITERATURE;
			DBA.CURRENT_TEST_REPORT_TABLE = DBA.TABLE_TEST_REPORT_LITERATURE;
			Config.CURRENT_BOOK_NAME = Config.BOOK_NAME_LITERATURE;
			Config.CURRENT_LANGUAGE = Language.English;
			break;
		}

		Intent intent = new Intent(getApplicationContext(),
				SessionsActivity.class);
		intent.putExtra(Config.INTENT_EXTRA_BOOK_NAME_RES_ID, viewId);
		startActivity(intent);

		return super.onChildClick(parent, v, groupPosition, childPosition, id);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_exit:
			finish();
			break;
		case R.id.btn_settings:
			startActivity(new Intent(this, SettingsActivity.class));
			break;
		}
	}

}
