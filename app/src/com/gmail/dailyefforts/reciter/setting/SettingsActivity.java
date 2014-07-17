package com.gmail.dailyefforts.reciter.setting;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.view.MenuItem;

import com.gmail.dailyefforts.android.reviwer.R;
import com.gmail.dailyefforts.reciter.Config;

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
		return false;
	}

	public static class PrefsFragment extends PreferenceFragment {

		private SharedPreferences mSharedPref;
		private Preference mVersionPref;
		private CheckBoxPreference mAllowReviewNotifyPref;
		private Preference mResetPref;

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			// Load the preferences from an XML resource
			addPreferencesFromResource(R.xml.settings);
			setPreferences();
		}

		private void setPreferences() {
			mSharedPref = PreferenceManager
					.getDefaultSharedPreferences(getActivity()
							.getApplicationContext());
			mVersionPref = (Preference) findPreference(getString(R.string.pref_key_version));
			mResetPref = (Preference) findPreference(getString(R.string.pref_key_reset));
			mAllowReviewNotifyPref = (CheckBoxPreference) findPreference(getString(R.string.pref_key_allow_review_notification));

			if (mSharedPref == null || mVersionPref == null
					|| mResetPref == null || mAllowReviewNotifyPref == null) {
				return;
			}

			try {
				String versionName = getActivity().getPackageManager()
						.getPackageInfo(getActivity().getPackageName(),
								PackageManager.GET_SIGNATURES).versionName;
				mVersionPref.setSummary(versionName);
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}

			mAllowReviewNotifyPref.setChecked(mSharedPref.getBoolean(
					getString(R.string.pref_key_allow_review_notification),
					Config.DEFAULT_ALLOW_REVIEW_NOTIFICATION));
		}

		@Override
		public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
				Preference preference) {
			if (preference == mResetPref) {
				DialogFragment newFragment = new ResetAlertDialogFragment();
				newFragment.show(getFragmentManager(), "dialog");
			}

			return super.onPreferenceTreeClick(preferenceScreen, preference);
		}

		private void reset() {
			if (mSharedPref != null) {
				SharedPreferences.Editor editor = mSharedPref.edit();
				if (editor != null) {
					editor.clear().apply();
					setPreferenceScreen(null);
					addPreferencesFromResource(R.xml.settings);
					setPreferences();
				} else {
					Log.e(TAG, "reset() editor is null : " + editor);
				}
			} else {
				Log.e(TAG, "reset() mSharedPref is null : " + mSharedPref);
			}
		}

		public class ResetAlertDialogFragment extends DialogFragment {

			@Override
			public Dialog onCreateDialog(Bundle savedInstanceState) {
				return new AlertDialog.Builder(getActivity())
						.setIcon(android.R.drawable.ic_dialog_alert)
						.setTitle(R.string.reset_to_default)
						.setMessage(R.string.reset_sumarry)
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
