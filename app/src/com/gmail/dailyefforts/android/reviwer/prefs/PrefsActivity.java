package com.gmail.dailyefforts.android.reviwer.prefs;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import com.gmail.dailyefforts.android.reviwer.R;

public class PrefsActivity extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Display the fragment as the main content.
		getFragmentManager().beginTransaction()
				.replace(android.R.id.content, new PrefsFragment()).commit();

	}

	public static class PrefsFragment extends PreferenceFragment implements
			OnPreferenceChangeListener {

		private ListPreference mListPref;
		private Prefs mPrefs;

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			// Load the preferences from an XML resource
			addPreferencesFromResource(R.xml.preferences);
			SharedPreferences sharedPref = PreferenceManager
					.getDefaultSharedPreferences(getActivity()
							.getApplicationContext());

			mPrefs = Prefs.getInstance(sharedPref);

			mListPref = (ListPreference) findPreference(getString(R.string.pref_key_options_number));
			if (mListPref != null && mPrefs != null) {
				mListPref.setOnPreferenceChangeListener(this);
				String value = String.valueOf(mPrefs.getOptionNumber());
				mListPref.setValue(value);
				mListPref.setSummary(String.valueOf(mListPref.getSummary())
						.replace("**", value));
			}
		}

		@Override
		public boolean onPreferenceChange(Preference preference, Object newValue) {
			if (preference == null) {
				return false;
			}
			
			String key = preference.getKey();

			if (key == null || mListPref == null) {
				return false;
			}

			if (key.equals(mListPref.getKey())) {
				if (mPrefs != null) {
					mPrefs.setOptionNumber(Integer.valueOf(String
							.valueOf(newValue)));
				}

			}
			return false;
		}
	}

}
