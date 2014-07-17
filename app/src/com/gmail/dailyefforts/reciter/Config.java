package com.gmail.dailyefforts.reciter;

import com.gmail.dailyefforts.android.reviwer.R;

public class Config {
	private Config() {
	}

	public static final boolean DEBUG = false;

	// Actions
	public static final String ACTION_NAME_CHECK_VERSION = "com.gmail.dailyefforts.reciter.CheckVersion";
	public static final String ACTION_REVIEW = "com.gmail.dailyefforts.reciter.review";
	public static final String ACTION_LAUNCH_ANNOUNCEACTIVITY = "com.gmail.dailyefforts.reciter.LAUNCH_ANNOUNCE_ACTIVITY";

	public static final String URL_VER_JSON = "https://raw.github.com/DailyEfforts/mot/master/ver.json";
	public static final String REMOTE_APK_FILE_URL = "https://raw.github.com/DailyEfforts/mot/master/Mot.apk";
	public static final String APK_NAME = "Mot.apk";
	public static final String TOTAL = "total=";

	// JSON elements
	public static final String JSON_VERSION_NAME = "name";
	public static final String JSON_VERSION_CODE = "code";
	public static final String JSON_VERSION_INFO = "info";
	public static final String JSON_VERSION_SIZE = "size";
	public static final String JSON_VERSION_MD5 = "md5";

	public static final String INTENT_APK_FILE_PATH = "apk_file_path";
	public static final String INTENT_APK_VERSION_NAME = "version_name";
	public static final String INTENT_APK_VERSION_INFO = "version_info";
	public static final String INTENT_APK_VERSION_SIZE = "version_size";
	public static final String INTENT_APK_VERSION_MD5 = "version_md5";

	public static final long INTERVAL_TIME_TO_TIP_REVIEW = 3 * 60 * 60 * 1000;

	// Default values
	public static final int DEFAULT_OPTION_COUNT = 5;
	public static final int DEFAULT_WORD_COUNT_OF_ONE_UNIT = 20;
	public static final int DEFAULT_RANDOM_TEST_SIZE = 20;
	public static final boolean DEFAULT_ALLOW_REVIEW_NOTIFICATION = true;

	// Book names
	public static final int BOOK_NAME_MOT = R.raw.mot;
	public static final int BOOK_NAME_NCE1 = R.raw.nce1;
	public static final int BOOK_NAME_NCE2 = R.raw.nce2;
	public static final int BOOK_NAME_NCE3 = R.raw.nce3;
	public static final int BOOK_NAME_NCE4 = R.raw.nce4;
	public static final int BOOK_NAME_REFLETS1U = R.raw.reflets1;
	public static final int BOOK_NAME_LINGUISTICS_GLOSSARY = R.raw.linguistics_glossary;
	public static final int BOOK_NAME_PRO_EN_CORE = R.raw.pro_en_core;
	public static final int BOOK_NAME_PRO_LIUYI_5000 = R.raw.liuyi_5000;
	public static final int BOOK_NAME_OGDEN = R.raw.ogden;
	public static int CURRENT_BOOK_NAME = BOOK_NAME_REFLETS1U;

	// Languages
	public static Language CURRENT_LANGUAGE = Language.English;

	public static final String INTENT_EXTRA_BOOK_NAME_RES_ID = "book_name_res_id";
	public static final String INTENT_EXTRA_TEST_TYPE = "test_type";

	public static final int MY_WORD_TEST = 0;
	public static final int MY_WORD_TEST_ZH = 1;
	public static final int MY_WORD_SPELL = 2;
	public static final int RANDOM_TEST = 3;
	public static final int RANDOM_TEST_ZH = 4;
	public static final int RANDOM_SPELL = 5;

	public static final String WORD_MEANING_SPLIT = "@";

	public static final String TITLE = "title";
	public static final String MESSAGE = "message";
	public static final String LAST_TIME_CHECKED_FOR_UPDATE = "last_time_checked_for_update";
	public static final long ZERO = 0L;
	public static final long ONE_DAY = 24 * 60 * 60 * 1000L; // MS
	public static final int TIME_DELAY_TO_AUTO_FORWARD = 100;

	public static enum TestType {
		MY_WORD_TO_ZH, MY_WORD_FROM_ZH, MY_WORD_SPELL, RANDOM_TO_ZH, RANDOM_FROM_ZH, RANDOM_SPELL, UNKNOWN
	}

	public static boolean IS_RUNNING;

	public static final char MISSED_CHAR = '*';
}
