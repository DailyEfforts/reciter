package com.gmail.dailyefforts.android.reviwer;

import android.app.Activity;
import android.view.MenuItem;

public class BaseActivity extends Activity {

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		default:
			break;
		}
		return true;
	}
}
