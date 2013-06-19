package com.gmail.dailyefforts.android.reviwer.setting;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;

import com.gmail.dailyefforts.android.reviwer.Config;
import com.gmail.dailyefforts.android.reviwer.R;

public class SettingsActivity extends PreferenceActivity {

	public static final String TAG = SettingsActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Display the fragment as the main content.
		getFragmentManager().beginTransaction()
				.replace(android.R.id.content, new PrefsFragment()).commit();

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

	public static class PrefsFragment extends PreferenceFragment implements
			OnSharedPreferenceChangeListener, OnPreferenceClickListener {

		private static ListPreference mOptNumListPref;
		private static ListPreference mWordCountInOneUnitPref;
		private static String mOptNumSummary;
		private static String mWordCountInOneUnitSummary;
		private static SharedPreferences mSharedPref;
		private Preference mCurrentVersionPref;
		private static CheckBoxPreference mReviewNotification;
		private static ListPreference mRandomTestSizePref;
		private static String mRandomTestSizeSummary;
		private static Preference mResetPref;

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			// Load the preferences from an XML resource
			addPreferencesFromResource(R.xml.settings);
			mSharedPref = PreferenceManager
					.getDefaultSharedPreferences(getActivity()
							.getApplicationContext());
			mOptNumListPref = (ListPreference) findPreference(getString(R.string.pref_key_options_count));
			mWordCountInOneUnitPref = (ListPreference) findPreference(getString(R.string.pref_key_word_count_in_one_unit));
			mCurrentVersionPref = (Preference) findPreference(getString(R.string.pref_key_version));
			mResetPref = (Preference) findPreference(getString(R.string.pref_key_reset));
			mReviewNotification = (CheckBoxPreference) findPreference(getString(R.string.pref_key_review_notification));
			mRandomTestSizePref = (ListPreference) findPreference(getString(R.string.pref_key_random_test_question_number));

			if (mSharedPref == null || mOptNumListPref == null
					|| mCurrentVersionPref == null
					|| mCurrentVersionPref == null || mResetPref == null
					|| mReviewNotification == null
					|| mRandomTestSizePref == null) {
				return;
			}

			try {
				String versionName = getActivity().getPackageManager()
						.getPackageInfo(getActivity().getPackageName(),
								PackageManager.GET_SIGNATURES).versionName;
				mCurrentVersionPref.setSummary(versionName);
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}

			mResetPref.setOnPreferenceClickListener(this);

			Resources res = getResources();
			if (res == null) {
				return;
			}

			mOptNumSummary = String.valueOf(res
					.getText(R.string.pref_summary_options_count));
			mWordCountInOneUnitSummary = String.valueOf(res
					.getText(R.string.pref_summary_word_count));

			mRandomTestSizeSummary = String.valueOf(res
					.getText(R.string.build_x_question_in_each_test_case));

			String value = mOptNumListPref.getValue();

			if (value == null) {
				value = Config.DEFAULT_OPTION_COUNT;
				mOptNumListPref.setValue(value);
			}

			value = mWordCountInOneUnitPref.getValue();

			if (value == null) {
				value = Config.DEFAULT_WORD_COUNT_OF_ONE_UNIT;
				mWordCountInOneUnitPref.setValue(value);
			}

			value = mRandomTestSizePref.getValue();
			if (value == null) {
				value = Config.DEFAULT_RANDOM_TEST_SIZE;
				mRandomTestSizePref.setValue(value);
			}

			mReviewNotification.setChecked(mSharedPref.getBoolean(
					getString(R.string.pref_key_review_notification),
					Config.DEFAULT_ALLOW_REVIEW_NOTIFICATION));

			setOptNumSummary();
			setWordCountSummary();
			setRandomTestSizeSummary();
		}

		private static void setRandomTestSizeSummary() {
			if (mRandomTestSizePref != null) {
				mRandomTestSizePref
						.setSummary(String.format(mRandomTestSizeSummary,
								mRandomTestSizePref.getValue()));
			}
		}

		@Override
		public void onResume() {
			super.onResume();
			if (mSharedPref != null) {
				mSharedPref.registerOnSharedPreferenceChangeListener(this);
			}
		}

		@Override
		public void onPause() {
			super.onPause();
			if (mSharedPref != null) {
				mSharedPref.unregisterOnSharedPreferenceChangeListener(this);
			}
		}

		@Override
		public void onSharedPreferenceChanged(
				SharedPreferences sharedPreferences, String key) {

			if (key != null && mOptNumListPref != null
					&& mWordCountInOneUnitPref != null
					&& mReviewNotification != null
					&& mRandomTestSizePref != null) {
				if (key.equals(mOptNumListPref.getKey())) {
					setOptNumSummary();
				} else if (key.equals(mWordCountInOneUnitPref.getKey())) {
					setWordCountSummary();
				} else if (key.equals(mRandomTestSizePref.getKey())) {
					setRandomTestSizeSummary();
				}
			}
		}

		private static void setOptNumSummary() {
			if (mOptNumListPref != null) {
				mOptNumListPref.setSummary(String.format(mOptNumSummary,
						mOptNumListPref.getValue()));

				if (Config.DEBUG) {
					Log.d(TAG,
							"setOptNumSummary() " + mOptNumListPref.getValue());
				}
			}
		}

		private static void setWordCountSummary() {
			if (mWordCountInOneUnitPref != null) {
				mWordCountInOneUnitPref.setSummary(String.format(
						mWordCountInOneUnitSummary,
						mWordCountInOneUnitPref.getValue()));

				if (Config.DEBUG) {
					Log.d(TAG, "setWordCountSummary() "
							+ mWordCountInOneUnitPref.getValue());
				}
			}
		}

		@Override
		public boolean onPreferenceClick(Preference preference) {
			String key = preference.getKey();
			if (key == null) {
				return false;
			}

			if (mResetPref != null && key.equals(mResetPref.getKey())) {

				DialogFragment newFragment = ResetAlertDialogFragment
						.newInstance(R.string.reset_to_default);
				newFragment.show(getFragmentManager(), "dialog");

				return true;
			}
			return false;
		}

		private static void reset() {
			if (mOptNumListPref != null) {
				mOptNumListPref.setValue(Config.DEFAULT_OPTION_COUNT);
			}

			if (mWordCountInOneUnitPref != null) {
				mWordCountInOneUnitPref
						.setValue(Config.DEFAULT_WORD_COUNT_OF_ONE_UNIT);
			}

			if (mRandomTestSizePref != null) {
				mRandomTestSizePref.setValue(Config.DEFAULT_RANDOM_TEST_SIZE);
			}

			setOptNumSummary();
			setWordCountSummary();
			setRandomTestSizeSummary();

			if (mReviewNotification != null) {
				mReviewNotification
						.setChecked(Config.DEFAULT_ALLOW_REVIEW_NOTIFICATION);
			}
		}

		public static class ResetAlertDialogFragment extends DialogFragment {

			public static ResetAlertDialogFragment newInstance(int title) {
				ResetAlertDialogFragment frag = new ResetAlertDialogFragment();
				Bundle args = new Bundle();
				args.putInt("title", title);
				args.putInt("message", R.string.reset_sumarry);
				frag.setArguments(args);
				return frag;
			}

			@Override
			public Dialog onCreateDialog(Bundle savedInstanceState) {
				int title = getArguments().getInt("title");
				int message = getArguments().getInt("message");

				return new AlertDialog.Builder(getActivity())
						.setIcon(android.R.drawable.ic_dialog_alert)
						.setTitle(title)
						.setMessage(message)
						.setPositiveButton(android.R.string.yes,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) {
										reset();
									}
								}).setNegativeButton(android.R.string.no, null)
						.create();
			}
		}
	}

}
