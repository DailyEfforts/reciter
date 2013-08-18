package com.gmail.dailyefforts.android.reciter;

import java.util.Locale;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.gmail.dailyefforts.android.reciter.Config.TestType;
import com.gmail.dailyefforts.android.reciter.db.DBA;
import com.gmail.dailyefforts.android.reciter.test.SpellTestActivity;
import com.gmail.dailyefforts.android.reciter.test.TestFragment;
import com.gmail.dailyefforts.android.reciter.test.TestPage;
import com.gmail.dailyefforts.android.reciter.unit.MistakeCollectionBookFragment;
import com.gmail.dailyefforts.android.reciter.unit.UnitSetFragment;
import com.gmail.dailyefforts.android.reciter.R;

public class Sessions extends FragmentActivity implements ActionBar.TabListener {

	private static final String TAG = Sessions.class.getSimpleName();

	SectionsPagerAdapter mSectionsPagerAdapter;
	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	private static Integer mTestWordsSize;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sessions);

		if (Config.DEBUG) {
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

		RUNNING = true;

		Intent intent = getIntent();
		if (intent != null && intent.getExtras() != null) {
			int titleId = intent.getExtras().getInt(
					Config.INTENT_EXTRA_BOOK_NAME_RES_ID);
			if (titleId != -1) {
				setTitle(titleId);
			}
		}

		getActionBar().setDisplayHomeAsUpEnabled(true);

		mTestWordsSize = Config.DEFAULT_RANDOM_TEST_SIZE;
	}

	public static boolean RUNNING;

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (Config.DEBUG) {
			Log.d(TAG, "onDestroy()");
		}
		RUNNING = false;
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
				fragment = new MistakeCollectionBookFragment();
				break;
			case 2:
				if (Config.DEBUG) {
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
				return getString(R.string.unit).toUpperCase(l);
			case 1:
				return getString(R.string.my_word_book).toUpperCase(l);
			case 2:
				return getString(R.string.test).toUpperCase(l);
			}
			return null;
		}
	}

	public void onStartTesting(View view) {
		switch (view.getId()) {
		case R.id.btn_start_testing:
			showPromptDialog();
			break;
		}
	}

	private void showPromptDialog() {
		DialogFragment newFragment = TestTypeListDialogFragment
				.newInstance(R.string.select_test_type);
		newFragment.show(getSupportFragmentManager(),
				"testtypelistdialogfragment");
	}

	public static class TestTypeListDialogFragment extends DialogFragment {

		public static TestTypeListDialogFragment newInstance(int title) {
			TestTypeListDialogFragment frag = new TestTypeListDialogFragment();
			Bundle args = new Bundle();
			args.putInt("title", title);
			frag.setArguments(args);
			return frag;
		}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			int title = getArguments().getInt("title");

			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle(title);
			builder.setItems(R.array.test_types,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							new TestCaseBuilder(getActivity())
									.execute(getTestType(which));
						}
					});
			builder.setNegativeButton(android.R.string.cancel, null);
			return builder.create();
		}
	}

	private static TestType getTestType(int which) {
		TestType ret = null;
		TestType[] types = TestType.values();
		for (TestType type : types) {
			if (type.ordinal() == which) {
				ret = type;
				break;
			}
		}

		return ret;
	}

	public static class TestCaseBuilder extends
			AsyncTask<TestType, Void, TestType> {
		private DBA dba;
		private TestType mTestType;

		private Context mContext;

		public TestCaseBuilder(Context context) {
			mContext = context;
		}

		@Override
		protected void onPreExecute() {
			dba = DBA.getInstance(mContext);
		}

		@Override
		protected void onPostExecute(TestType type) {

			if (type == null || mContext == null) {
				Log.e(TAG, "TestCaseBuilder.onPostExecute() type: " + type);
				Log.e(TAG, "TestCaseBuilder.onPostExecute() mContext: "
						+ mContext);
				return;
			}
			Intent intent = null;
			switch (type) {
			case RANDOM_FROM_ZH:
			case RANDOM_TO_ZH:
			case MY_WORD_FROM_ZH:
			case MY_WORD_TO_ZH:
				intent = new Intent(mContext, TestPage.class);
				break;
			case RANDOM_SPELL:
			case MY_WORD_SPELL:
				intent = new Intent(mContext, SpellTestActivity.class);
				break;
			case UNKNOWN:
				Toast.makeText(mContext, R.string.tip_word_book_is_empty,
						Toast.LENGTH_LONG).show();
			default:
				break;
			}

			if (intent != null) {
				intent.putExtra(Config.INTENT_EXTRA_TEST_TYPE, type.ordinal());
				mContext.startActivity(intent);
				intent = null; // Let GC do its work.
			}

		}

		@Override
		protected TestType doInBackground(TestType... params) {
			mTestType = params[0];
			if (dba == null) {
				return null;
			}

			if (Config.DEBUG) {
				Log.d(TAG, "mTestType: " + mTestType);
			}

			switch (mTestType) {
			case RANDOM_FROM_ZH:
			case RANDOM_TO_ZH:
			case RANDOM_SPELL:
				dba.buildRandomTest(DBA.CURRENT_WORD_TABLE, mTestWordsSize);
				break;
			case MY_WORD_FROM_ZH:
			case MY_WORD_TO_ZH:
			case MY_WORD_SPELL:
				dba.buildMyWordBookTest(DBA.CURRENT_WORD_TABLE);
				break;
			default:
				break;
			}

			if (Config.DEBUG) {
				Log.d(TAG, "size: " + Word.getMap().size());
			}

			return Word.getMap().size() > 0 ? mTestType : TestType.UNKNOWN;
		}

	}
}
