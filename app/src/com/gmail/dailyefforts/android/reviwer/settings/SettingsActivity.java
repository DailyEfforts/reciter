package com.gmail.dailyefforts.android.reviwer.settings;

import com.gmail.dailyefforts.android.reviwer.R;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class SettingsActivity extends PreferenceActivity implements
		OnPreferenceChangeListener {
	public static final String KEY_OPTION_NUMBER = "option_number";
	public static final int DEFAULT_OPTION_NUMBER = 4;
	private ListPreference mListPref;
	private SharedPreferences mSharedPref;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
		mSharedPref = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());

		mListPref = (ListPreference) findPreference(getString(R.string.pref_key_options_number));
		if (mListPref != null && mSharedPref != null) {
			mListPref.setOnPreferenceChangeListener(this);
			String value = String.valueOf(mSharedPref.getInt(
					SettingsActivity.KEY_OPTION_NUMBER,
					SettingsActivity.DEFAULT_OPTION_NUMBER));
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
//			mListPref.setSummary(String.valueOf(mListPref.getSummary())
//					.replace("**", String.valueOf(newValue)));
			
			Editor editor = mSharedPref.edit();
			editor.putInt(KEY_OPTION_NUMBER,
					Integer.parseInt(String.valueOf(newValue)));
			editor.commit();
		}
		return false;
	}
}
