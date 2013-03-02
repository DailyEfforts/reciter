package com.gmail.dailyefforts.android.reviwer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.app.Activity;
import android.content.ContentValues;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

public class Launcher extends Activity {

	private static final String TAG = Launcher.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_luncher);
		new LoadWordsList().execute();
	}

	private class LoadWordsList extends AsyncTask<Void, Integer, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {
			AssetManager assetMngr = getAssets();
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
				while ((str = reader.readLine()) != null) {
					String[] arr = str.split("--");
					if (arr != null && arr.length == 2) {
						String word = arr[0].trim();
						String meanning = arr[1].trim();
						values.clear();
						values.put(DBA.COLUMN_WORD, word);
						values.put(DBA.COLUMN_MEANING, meanning);
						dba.insert(DBA.TABLE_NAME, null, values);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
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
