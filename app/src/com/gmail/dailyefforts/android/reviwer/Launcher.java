package com.gmail.dailyefforts.android.reviwer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.gmail.dailyefforts.android.reviwer.db.DBA;
import com.gmail.dailyefforts.android.reviwer.debug.Debuger;
import com.gmail.dailyefforts.android.reviwer.setting.SettingsActivity;

public class Launcher extends Activity {

	private static final String TAG = Launcher.class.getSimpleName();
	private Button btnStart;
	private RelativeLayout loadingTip;
	private static SparseArray<Word> map = new SparseArray<Word>();

	public class Word {
		private String word;
		private String meaning;

		public Word(String word, String meaning) {
			super();
			this.word = word;
			this.meaning = meaning;
		}

		public String getWord() {
			return word;
		}

		public String getMeaning() {
			return meaning;
		}

		@Override
		public String toString() {
			return "Word [word=" + word + ", meaning=" + meaning + "]";
		}

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_luncher);

		loadingTip = (RelativeLayout) findViewById(R.id.rl_loading);
		btnStart = (Button) findViewById(R.id.btn_start);

		if (btnStart != null) {
			btnStart.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					if (Debuger.DEBUG) {
						Log.d(TAG, "onClick() map.size()" + map.size());
					}
					Intent intent = new Intent(Launcher.this, TestPage.class);
					startActivity(intent);
				}
			});
		}

		new LoadWordsList().execute();
	}

	public static SparseArray<Word> getMap() {
		return map;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_luncher, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean isConsumed = false;
		switch (item.getItemId()) {
		case R.id.menu_settings:
			Intent intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
			isConsumed = true;
			break;
		default:
			break;
		}
		return isConsumed;
	}

	private class LoadWordsList extends AsyncTask<Void, Integer, Boolean> {

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);

			if (result) {
				if (loadingTip != null) {
					loadingTip.setVisibility(View.GONE);
					btnStart.setEnabled(true);
				}
			}
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			AssetManager assetMngr = getAssets();
			if (Debuger.DEBUG) {
				Log.d(TAG, "doInBackground() assetMngr: " + assetMngr);
			}
			if (assetMngr == null) {
				return false;
			}
			DBA dba = DBA.getInstance(getApplicationContext());
			try {
				InputStream in = assetMngr.open("mot.txt");
				InputStreamReader inReader = new InputStreamReader(in);
				BufferedReader reader = new BufferedReader(inReader);
				String str = null;
				ContentValues values = new ContentValues();
				dba.beginTransaction();
				while ((str = reader.readLine()) != null) {
					String[] arr = str.split("--");
					if (arr != null && arr.length == 2) {
						String word = arr[0].trim();
						String meanning = arr[1].trim();

						String sql = "select " + DBA.COLUMN_WORD + " from "
								+ DBA.TABLE_NAME + " where " + DBA.COLUMN_WORD
								+ "=?;";
						Cursor cursor = dba
								.rawQuery(sql, new String[] { word });
						if (Debuger.DEBUG) {
							Log.d(TAG,
									"doInBackground() " + word + " - "
											+ meanning + ", exist: "
											+ cursor.getCount());
						}
						if (cursor != null && cursor.getCount() == 0) {
							values.clear();
							values.put(DBA.COLUMN_WORD, word);
							values.put(DBA.COLUMN_MEANING, meanning);
							dba.insert(DBA.TABLE_NAME, null, values);
						}

						if (cursor != null) {
							cursor.close();
							cursor = null;
						}
					}
				}
				dba.setTransactionSuccessful();
				dba.endTransaction();
			} catch (IOException e) {
				e.printStackTrace();
			}

			Cursor cursor = dba.query(DBA.TABLE_NAME, new String[] {
					DBA.COLUMN_ID, DBA.COLUMN_WORD, DBA.COLUMN_MEANING }, null,
					null, null, null, null);
			if (Debuger.DEBUG) {
				Log.d(TAG, "doInBackground() cursor: " + cursor);
			}
			if (cursor != null && cursor.moveToFirst()) {
				map.clear();
				int idx = 0;
				while (!cursor.isAfterLast()) {
					String word = cursor.getString(cursor
							.getColumnIndex(DBA.COLUMN_WORD));
					String meanning = cursor.getString(cursor
							.getColumnIndex(DBA.COLUMN_MEANING));
					if (Debuger.DEBUG) {
						Log.d(TAG, "update() idx: " + idx + ", word: " + word);
					}
					Word newWord = new Word(word, meanning);
					map.put(idx++, newWord);
					cursor.moveToNext();
				}
			}
			if (cursor != null) {
				cursor.close();
			}
			return true;
		}
	}

	// @Override
	// public boolean onCreateOptionsMenu(Menu menu) {
	// // Inflate the menu; this adds items to the action bar if it is present.
	// getMenuInflater().inflate(R.menu.activity_luncher, menu);
	// return true;
	// }

}
