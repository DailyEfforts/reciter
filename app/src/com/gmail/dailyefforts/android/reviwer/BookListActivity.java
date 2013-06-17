package com.gmail.dailyefforts.android.reviwer;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.gmail.dailyefforts.android.reviwer.db.DBA;
import com.gmail.dailyefforts.android.reviwer.setting.SettingsActivity;

public class BookListActivity extends ListActivity implements OnClickListener {

	private Button mBtnExit;
	private Button mBtnSettings;

	private static final String TAG = BookListActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_book_list);
		String[] books = getResources().getStringArray(R.array.books);

		ListAdapter adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, books);
		setListAdapter(adapter);

		findViewsAndSetListeners();

		launchVersionChecker();
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
	protected void onListItemClick(ListView l, View v, int position, long id) {
		int bookNameResId = -1;
		switch (position) {
		case 0:
			DBA.CURRENT_WORD_TABLE = DBA.TABLE_WORD_LIST;
			DBA.CURRENT_TEST_REPORT_TABLE = DBA.TABLE_TEST_REPORT;
			Config.CURRENT_BOOK_NAME = Config.BOOK_NAME_MOT;
			Config.CURRENT_LANGUAGE = Config.LANG_FR;
			bookNameResId = R.string.mot;
			break;
		case 1:
			DBA.CURRENT_WORD_TABLE = DBA.TABLE_WORD_LIST_NCE1;
			DBA.CURRENT_TEST_REPORT_TABLE = DBA.TABLE_TEST_REPORT_NCE1;
			Config.CURRENT_BOOK_NAME = Config.BOOK_NAME_NCE1;
			Config.CURRENT_LANGUAGE = Config.LANG_EN;
			bookNameResId = R.string.nce1;
			break;
		case 2:
			DBA.CURRENT_WORD_TABLE = DBA.TABLE_WORD_LIST_NCE2;
			DBA.CURRENT_TEST_REPORT_TABLE = DBA.TABLE_TEST_REPORT_NCE2;
			Config.CURRENT_BOOK_NAME = Config.BOOK_NAME_NCE2;
			Config.CURRENT_LANGUAGE = Config.LANG_EN;
			bookNameResId = R.string.nce2;
			break;
		case 3:
			DBA.CURRENT_WORD_TABLE = DBA.TABLE_WORD_LIST_NCE3;
			DBA.CURRENT_TEST_REPORT_TABLE = DBA.TABLE_TEST_REPORT_NCE3;
			Config.CURRENT_BOOK_NAME = Config.BOOK_NAME_NCE3;
			Config.CURRENT_LANGUAGE = Config.LANG_EN;
			bookNameResId = R.string.nce3;
			break;
		case 4:
			DBA.CURRENT_WORD_TABLE = DBA.TABLE_WORD_LIST_NCE4;
			DBA.CURRENT_TEST_REPORT_TABLE = DBA.TABLE_TEST_REPORT_NCE4;
			Config.CURRENT_BOOK_NAME = Config.BOOK_NAME_NCE4;
			Config.CURRENT_LANGUAGE = Config.LANG_EN;
			bookNameResId = R.string.nce4;
			break;
		case 5:
			DBA.CURRENT_WORD_TABLE = DBA.TABLE_WORD_LIST_REFLETS1U;
			DBA.CURRENT_TEST_REPORT_TABLE = DBA.TABLE_TEST_REPORT_REFLETS1U;
			Config.CURRENT_BOOK_NAME = Config.BOOK_NAME_REFLETS1U;
			Config.CURRENT_LANGUAGE = Config.LANG_FR;
			bookNameResId = R.string.reflets_1_uppper;
			break;
		}

		Intent intent = new Intent(getApplicationContext(), Sessions.class);
		intent.putExtra(Config.INTENT_EXTRA_BOOK_NAME_RES_ID, bookNameResId);
		startActivity(intent);
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
