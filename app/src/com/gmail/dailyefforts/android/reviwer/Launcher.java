package com.gmail.dailyefforts.android.reviwer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.RelativeLayout;

import com.gmail.dailyefforts.android.reviwer.book.WordBookActivity;
import com.gmail.dailyefforts.android.reviwer.db.DBA;
import com.gmail.dailyefforts.android.reviwer.debug.Debuger;
import com.gmail.dailyefforts.android.reviwer.setting.Settings;
import com.gmail.dailyefforts.android.reviwer.setting.SettingsActivity;
import com.gmail.dailyefforts.android.reviwer.unit.UnitView;
import com.gmail.dailyefforts.android.reviwer.word.Word;

public class Launcher extends Activity {

	private static final String TAG = Launcher.class.getSimpleName();
	private Button btnSetting;
	private RelativeLayout loadingTip;
	private GridView mGridView;
	private DBA dba;
	private Button btnWordBook;
	private Button btnExit;
	private SharedPreferences mSharedPref;
	private Animation mAnimation;

	private static int UNIT = Integer
			.valueOf(Settings.DEFAULT_WORD_COUNT_OF_ONE_UNIT);

	private class UnitAdapter extends BaseAdapter {

		private int mUnitCount;
		private int mDbSize;

		public UnitAdapter(int count, int dbSize) {
			mUnitCount = count;
			mDbSize = dbSize;
		}

		@Override
		public int getCount() {
			return mUnitCount;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup container) {
			View view = null;
			if (convertView == null) {
				// view = getLayoutInflater().inflate(R.layout.view_unit, null);
				view = new UnitView(Launcher.this);
			} else {
				view = convertView;
			}

			if (view instanceof UnitView) {
				// TODO dba non-null check
				UnitView tmp = ((UnitView) view);
				tmp.id = position;
				tmp.start = position * UNIT;
				tmp.end = position == mUnitCount - 1 ? mDbSize - 1
						: (position + 1) * UNIT - 1;

				if (Debuger.DEBUG) {
					Log.d(TAG, String.format("getView() id: %d, s: %d, e: %d ",
							tmp.id, tmp.start, tmp.end));
				}

				tmp.setText(String.format("Unit-%02d\n(%d)", position + 1,
						tmp.end - tmp.start + 1));
			}

			return view;
		}

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_luncher);

		loadingTip = (RelativeLayout) findViewById(R.id.rl_loading);
		btnSetting = (Button) findViewById(R.id.btn_setting);
		btnWordBook = (Button) findViewById(R.id.btn_word_book);
		btnExit = (Button) findViewById(R.id.btn_exit);

		dba = DBA.getInstance(getApplicationContext());

		mGridView = (GridView) findViewById(R.id.gv_unit);

		mAnimation = AnimationUtils.loadAnimation(this, R.anim.zoom_in);

		mSharedPref = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		if (mSharedPref != null) {
			UNIT = Integer.valueOf(mSharedPref.getString(
					getString(R.string.pref_key_word_count_in_one_unit),
					Settings.DEFAULT_WORD_COUNT_OF_ONE_UNIT));
		}

		if (btnSetting != null) {
			btnSetting.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(Launcher.this,
							SettingsActivity.class);
					startActivity(intent);
				}
			});
		}

		if (btnWordBook != null) {
			btnWordBook.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// dba.getStar();
					Intent intent = new Intent(Launcher.this,
							WordBookActivity.class);
					startActivity(intent);
				}
			});
		}

		if (btnExit != null) {
			btnExit.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					finish();
				}
			});
		}

		new LoadWordsList().execute();

		checkLatestVersion();
	}

	@Override
	protected void onRestart() {
		super.onRestart();

		if (Debuger.DEBUG) {
			Log.d(TAG, "onRestart()");
		}

		if (mGridView != null && dba != null) {
			if (Debuger.DEBUG) {
				Log.d(TAG, "onPostExecute() " + dba.getCount());
			}

			UNIT = Integer.valueOf(mSharedPref.getString(
					getString(R.string.pref_key_word_count_in_one_unit),
					Settings.DEFAULT_WORD_COUNT_OF_ONE_UNIT));

			int count = dba.getCount();

			int unitSize = count % UNIT == 0 ? count / UNIT : count / UNIT + 1;

			mGridView.setAdapter(new UnitAdapter(unitSize, count));

			mGridView.startAnimation(mAnimation);
		}
	}
	private void checkLatestVersion() {
		Intent intent = new Intent(Config.ACTION_NAME_CHECK_VERSION);
		if (Debuger.DEBUG) {
			Log.d(TAG, "checkLatestVersion()");
		}
		startService(intent);
	}
	private class LoadWordsList extends AsyncTask<Void, Integer, Boolean> {

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);

			if (result) {
				if (loadingTip != null && btnSetting != null
						&& btnWordBook != null && btnExit != null) {
					loadingTip.setVisibility(View.GONE);
					btnSetting.setEnabled(true);
					btnWordBook.setEnabled(true);
					btnExit.setEnabled(true);
				}
				if (mGridView != null && dba != null) {
					if (Debuger.DEBUG) {
						Log.d(TAG, "onPostExecute() " + dba.getCount());
					}

					int count = dba.getCount();

					int unitSize = count % UNIT == 0 ? count / UNIT : count
							/ UNIT + 1;

					mGridView.setAdapter(new UnitAdapter(unitSize, count));
					mGridView.setVisibility(View.VISIBLE);

					mGridView.startAnimation(mAnimation);
					
				}
			}
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			AssetManager assetMngr = getAssets();
			if (Debuger.DEBUG) {
				Log.d(TAG, "doInBackground() assetMngr: " + assetMngr);
			}
			if (assetMngr == null || dba == null) {
				return false;
			}
			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new InputStreamReader(
						assetMngr.open("mot.txt")));
				String str = null;
				str = reader.readLine();
				if (str != null && str.startsWith("total=")) {
					int total = Integer
							.valueOf(str.substring(str.indexOf("=") + 1));

					if (Debuger.DEBUG) {
						Log.d(TAG, "doInBackground() total mot.txt: " + total
								+ ", db: " + dba.getCount());
					}
					if (dba.getCount() > total - 100) {
						return true;
					}
				}
				ContentValues values = new ContentValues();
				dba.beginTransaction();
				while ((str = reader.readLine()) != null) {
					String[] arr = str.split(Word.WORD_MEANING_SPLIT);
					if (arr != null && arr.length == 2) {
						String word = arr[0].trim();
						String meanning = arr[1].trim();

						if (!dba.exist(word)) {
							values.clear();
							values.put(DBA.COLUMN_WORD, word);
							values.put(DBA.COLUMN_MEANING, meanning);
							values.put(DBA.COLUMN_TIMESTAMP,
									System.currentTimeMillis());
							dba.insert(DBA.TABLE_NAME, null, values);
						}

					}
				}
				dba.setTransactionSuccessful();
				dba.endTransaction();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (reader != null) {
					try {
						reader.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

			return true;
		}
	}

}
