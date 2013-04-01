package com.gmail.dailyefforts.android.reviwer.setting;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class Settings {
	public static final String KEY_OPTION_NUMBER = "option_number";
	public static final int DEFAULT_OPTION_NUMBER = 4;

	private static SharedPreferences mSharedPref;

	private static Settings PREFS = new Settings();

	private static int OPTION_NUMBER;

	public int getOptionNumber() {
		return OPTION_NUMBER;
	}

	public void setOptionNumber(int optionNumber) {
		OPTION_NUMBER = optionNumber;
		Editor editor = mSharedPref.edit();
		editor.putInt(Settings.KEY_OPTION_NUMBER,
				Integer.parseInt(String.valueOf(optionNumber)));
		editor.commit();
	}

	private Settings() {
	}

	public static Settings getInstance(SharedPreferences sharePref) {
		mSharedPref = sharePref;
		initValues();
		return PREFS;
	}

	private static void initValues() {
		if (mSharedPref == null) {
			return;
		}

		OPTION_NUMBER = mSharedPref.getInt(Settings.KEY_OPTION_NUMBER,
				DEFAULT_OPTION_NUMBER);
	}

}
