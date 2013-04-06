package com.gmail.dailyefforts.android.reviwer.setting;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;

import com.gmail.dailyefforts.android.reviwer.R;
import com.gmail.dailyefforts.android.reviwer.debug.Debuger;

public class SettingsActivity extends PreferenceActivity {

	public static final String TAG = SettingsActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Display the fragment as the main content.
		getFragmentManager().beginTransaction()
				.replace(android.R.id.content, new PrefsFragment()).commit();

	}

	public static class PrefsFragment extends PreferenceFragment implements
			OnSharedPreferenceChangeListener {

		private ListPreference mOptNumListPref;
		private ListPreference mWordCountInOneUnitPref;
		private String mOptNumSummary;
		private String mWordCountInOneUnitSummary;
		private SharedPreferences mSharedPref;
		private Preference mCurrentVersionPref;

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			// Load the preferences from an XML resource
			addPreferencesFromResource(R.xml.settings);

			mOptNumListPref = (ListPreference) findPreference(getString(R.string.pref_key_options_count));
			mWordCountInOneUnitPref = (ListPreference) findPreference(getString(R.string.pref_key_word_count_in_one_unit));
			mCurrentVersionPref = (Preference) findPreference(getString(R.string.pref_key_current_version));
			if (mCurrentVersionPref != null) {
				try {
					String versionName = getActivity().getPackageManager()
							.getPackageInfo(getActivity().getPackageName(),
									PackageManager.GET_SIGNATURES).versionName;
					mCurrentVersionPref.setSummary("v" + versionName);
				} catch (NameNotFoundException e) {
					e.printStackTrace();
				}
			}

			mSharedPref = PreferenceManager
					.getDefaultSharedPreferences(getActivity()
							.getApplicationContext());

			mOptNumSummary = String.valueOf(getResources().getText(
					R.string.pref_summary_options_count));
			mWordCountInOneUnitSummary = String.valueOf(getResources().getText(
					R.string.pref_summary_word_count));

			String value = mOptNumListPref.getValue();

			if (value == null && mOptNumListPref != null) {
				value = Settings.DEFAULT_OPTION_COUNT;
				mOptNumListPref.setValue(value);
			}

			value = mWordCountInOneUnitPref.getValue();

			if (value == null && mWordCountInOneUnitPref != null) {
				value = Settings.DEFAULT_WORD_COUNT_OF_ONE_UNIT;
				mWordCountInOneUnitPref.setValue(value);
			}

			setOptNumSummary();
			setWordCountSummary();
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
					&& mWordCountInOneUnitPref != null) {
				if (key.equals(mOptNumListPref.getKey())) {
					setOptNumSummary();
				} else if (key.equals(mWordCountInOneUnitPref.getKey())) {
					setWordCountSummary();
				}
			}
		}

		private void setOptNumSummary() {
			if (mOptNumListPref != null) {
				mOptNumListPref.setSummary(String.format(mOptNumSummary,
						mOptNumListPref.getValue()));

				if (Debuger.DEBUG) {
					Log.d(TAG,
							"setOptNumSummary() " + mOptNumListPref.getValue());
				}
			}
		}

		private void setWordCountSummary() {
			if (mWordCountInOneUnitPref != null) {
				mWordCountInOneUnitPref.setSummary(String.format(
						mWordCountInOneUnitSummary,
						mWordCountInOneUnitPref.getValue()));

				if (Debuger.DEBUG) {
					Log.d(TAG, "setWordCountSummary() "
							+ mWordCountInOneUnitPref.getValue());
				}
			}
		}
	}

}
