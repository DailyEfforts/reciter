package com.gmail.dailyefforts.android.reviwer.book;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;

public class WordBookActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Display the fragment as the main content.
		/*
		 * getFragmentManager().beginTransaction()
		 * .replace(android.R.id.content, new WordBookFragment()).commit();
		 */
		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		getActionBar().setSubtitle("Long press to start selection");
	}

}
