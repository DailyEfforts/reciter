package com.gmail.dailyefforts.android.reviwer.setting;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
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
		private String mOptNumSumm;
		private SharedPreferences mSharedPref;

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			// Load the preferences from an XML resource
			addPreferencesFromResource(R.xml.settings);

			mOptNumListPref = (ListPreference) findPreference(getString(R.string.pref_key_options_number));

			mSharedPref = mOptNumListPref.getSharedPreferences();

			mOptNumSumm = String.valueOf(getResources().getText(
					R.string.pref_summary_options_number));

			String value = mOptNumListPref.getValue();

			if (value == null && mOptNumListPref != null) {
				value = Settings.DEFAULT_OPTION_NUMBER;
				mOptNumListPref.setValue(value);
			}

			setOptNumSummary();
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

			if (key != null && mOptNumListPref != null) {
				if (key.equals(mOptNumListPref.getKey())) {
					setOptNumSummary();
				}
			}
		}

		private void setOptNumSummary() {
			if (mOptNumListPref != null) {
				mOptNumListPref.setSummary(String.format(mOptNumSumm,
						mOptNumListPref.getValue()));

				if (Debuger.DEBUG) {
					Log.d(TAG,
							"setOptNumSummary() " + mOptNumListPref.getValue());
				}
			}
		}
	}

}
