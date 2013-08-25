package com.gmail.dailyefforts.android.reciter.setting;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;

import com.gmail.dailyefforts.android.reviwer.R;

public class AnnounceActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_announce);
		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		}
		return false;
	}
}
