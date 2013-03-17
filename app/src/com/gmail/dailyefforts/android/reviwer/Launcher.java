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
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class Launcher extends Activity {

	private static final String TAG = Launcher.class.getSimpleName();
	private Button btnStart;
	private ProgressBar prograssBar;
	private TextView tvLoading;
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
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_luncher);
		
		prograssBar = (ProgressBar) findViewById(R.id.pb_loading);
		tvLoading = (TextView) findViewById(R.id.tv_loading);
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

	private class LoadWordsList extends AsyncTask<Void, Integer, Boolean> {

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);

			if (result) {
				if (prograssBar != null && tvLoading != null
						&& btnStart != null) {
					tvLoading.setVisibility(View.GONE);
					prograssBar.setVisibility(View.GONE);
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
							Log.d(TAG, "doInBackground() " + word + " - "
									+ meanning + ", exist: " + cursor.moveToFirst());
						}
						if (cursor != null && !cursor.moveToFirst()) {
							values.clear();
							values.put(DBA.COLUMN_WORD, word);
							values.put(DBA.COLUMN_MEANING, meanning);
							dba.insert(DBA.TABLE_NAME, null, values);
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
