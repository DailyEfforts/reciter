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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.RelativeLayout;

import com.gmail.dailyefforts.android.reviwer.db.DBA;
import com.gmail.dailyefforts.android.reviwer.debug.Debuger;
import com.gmail.dailyefforts.android.reviwer.setting.SettingsActivity;
import com.gmail.dailyefforts.android.reviwer.unit.UnitView;

public class Launcher extends Activity {

	private static final String TAG = Launcher.class.getSimpleName();
	private Button btnSetting;
	private RelativeLayout loadingTip;
	private GridView mGridView;
	private DBA dba;
	private Button btnWordBook;

	private static final int UNIT = 30;

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
				tmp.end = position == mUnitCount - 1 ? mDbSize - 1 : (position + 1)
						* UNIT - 1;

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

		dba = DBA.getInstance(getApplicationContext());

		mGridView = (GridView) findViewById(R.id.gv_unit);

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

		new LoadWordsList().execute();
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
		case R.id.menu_exit:
			finish();
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
				if (loadingTip != null && btnSetting != null
						&& btnWordBook != null) {
					loadingTip.setVisibility(View.GONE);
					btnSetting.setEnabled(true);
					btnWordBook.setEnabled(true);
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
			try {
				InputStream in = assetMngr.open("mot.txt");
				InputStreamReader inReader = new InputStreamReader(in);
				BufferedReader reader = new BufferedReader(inReader);
				String str = null;
				ContentValues values = new ContentValues();
				dba.beginTransaction();
				while ((str = reader.readLine()) != null) {
					String[] arr = str.split(Word.WORD_MEANING_SPLIT);
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

			return true;
		}
	}

}
