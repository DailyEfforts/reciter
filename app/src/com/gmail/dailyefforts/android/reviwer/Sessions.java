package com.gmail.dailyefforts.android.reviwer;

import java.util.Locale;

import android.app.ActionBar;
import android.app.AlarmManager;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.gmail.dailyefforts.android.reviwer.debug.Debuger;
import com.gmail.dailyefforts.android.reviwer.fragment.TestFragment;
import com.gmail.dailyefforts.android.reviwer.fragment.UnitSetFragment;
import com.gmail.dailyefforts.android.reviwer.fragment.WordBookFragment;
import com.gmail.dailyefforts.android.reviwer.setting.SettingsActivity;

public class Sessions extends FragmentActivity implements ActionBar.TabListener {

	private static final String TAG = Sessions.class.getSimpleName();
	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;
	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sessions);

		if (Debuger.DEBUG) {
			Log.d(TAG, "onCreate() savedInstanceState: " + savedInstanceState);
		}

		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
		launchVersionChecker();

		RUNNING = true;

		AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		PendingIntent sender = PendingIntent.getBroadcast(this, 0, new Intent(
				Config.ACTION_REVIEW), PendingIntent.FLAG_CANCEL_CURRENT);
		am.setRepeating(AlarmManager.RTC, System.currentTimeMillis(),
				Config.INTERVAL_TIME_TO_TIP_REVIEW, sender);

		Intent intent = getIntent();
		if (intent != null && intent.getExtras() != null) {
			int titleId = intent.getExtras().getInt(
					Config.INTENT_EXTRA_BOOK_NAME_RES_ID);
			if (titleId != -1) {
				setTitle(titleId);
			}
		}

		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	public static boolean RUNNING;

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (Debuger.DEBUG) {
			Log.d(TAG, "onDestroy()");
		}
		RUNNING = false;
	}

	private void launchVersionChecker() {
		Intent intent = new Intent(Config.ACTION_NAME_CHECK_VERSION);
		if (Debuger.DEBUG) {
			Log.d(TAG, "checkLatestVersion()");
		}
		startService(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.sessions, menu);
		return false;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		case R.id.settings:
			startActivity(new Intent(this, SettingsActivity.class));
			return true;
		case R.id.exit:
			finish();
			return true;
		}
		return false;
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a DummySectionFragment (defined as a static inner class
			// below) with the page number as its lone argument.
			Fragment fragment = null;

			switch (position) {
			case 0:
				fragment = new UnitSetFragment();
				break;
			case 1:
				fragment = new WordBookFragment();
				break;
			case 2:
				if (Debuger.DEBUG) {
					Log.d(TAG, "getItem() TestFragment: ");
				}
				fragment = new TestFragment();
				break;
			}
			return fragment;
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.recite).toUpperCase(l);
			case 1:
				return getString(R.string.my_word_book).toUpperCase(l);
			case 2:
				return getString(R.string.test).toUpperCase(l);
			}
			return null;
		}
	}

}
