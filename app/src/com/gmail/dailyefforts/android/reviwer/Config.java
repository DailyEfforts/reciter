package com.gmail.dailyefforts.android.reviwer;

public class Config {
	public static final String ACTION_NAME_CHECK_VERSION = "com.gmail.dailyefforts.android.reviwer.CheckVersion";
	public static final String ACTION_REVIEW = "com.gmail.dailyefforts.android.reviwer.review";
	public static final String SDCARD_FOLDER_NAME = "Mot";
	public static final String URL_VER_JSON = "https://raw.github.com/DailyEfforts/mot/master/ver.json";
	public static final String REMOTE_APK_FILE_URL = "https://raw.github.com/DailyEfforts/mot/master/Mot.apk";
	public static final String APK_NAME = "Mot.apk";
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
	public static final long INTERVAL_TIME_TO_TIP_REVIEW = 5 * 60 * 60 * 1000;
	public static final String DEFAULT_OPTION_COUNT = "4";
	public static final String DEFAULT_WORD_COUNT_OF_ONE_UNIT = "20";
	public static final String DEFAULT_TIME_GAP = "3";
	public static final String DEFAULT_RANDOM_TEST_SIZE = "30";
	public static final boolean DEFAULT_ALLOW_REVIEW_NOTIFICATION = true;

	public static final String BOOK_NAME_MOT = "mot.txt";
	public static final String BOOK_NAME_NCE1 = "nce1.txt";
	public static final String BOOK_NAME_NCE2 = "nce2.txt";
	public static final String BOOK_NAME_NCE3 = "nce3.txt";
	public static final String BOOK_NAME_NCE4 = "nce4.txt";
	public static String CURRENT_BOOK_NAME = BOOK_NAME_MOT;
	public static final String LANG_EN = "en";
	public static final String LANG_FR = "fr";
	public static String CURRENT_LANGUAGE = LANG_EN;

	public static final String INTENT_EXTRA_BOOK_NAME_RES_ID = "book_name_res_id";

}
