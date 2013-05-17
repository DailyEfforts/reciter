package com.gmail.dailyefforts.android.reviwer.service;

import java.io.File;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Log;

import com.gmail.dailyefforts.android.reviwer.Config;
import com.gmail.dailyefforts.android.reviwer.debug.Debuger;
import com.gmail.dailyefforts.android.reviwer.helper.DownloadHelper;
import com.gmail.dailyefforts.android.reviwer.version.UpdateConfirm;

public class VersionChecker extends IntentService {

	private static final String TAG = VersionChecker.class.getSimpleName();
	public VersionChecker() {
		super(TAG);
	}
	private int getCurrentVersionCode() {
		int versionCode = -1;
		try {
			versionCode = getPackageManager().getPackageInfo(getPackageName(),
					PackageManager.GET_SIGNATURES).versionCode;
		} catch (NameNotFoundException e) {
			Log.e(TAG, e.getMessage());
		}

		return versionCode;
	}
	private class Version {
		public String name;
		public int code;
		public int size;
		public String info;
		public String md5;

		public Version(final String name, final int code, final int size,
				final String info, final String md5) {
			this.name = name;
			this.code = code;
			this.size = size;
			this.info = info;
			this.md5 = md5;
		}
		@Override
		public String toString() {
			return "Version [name=" + name + ", code=" + code + ", size="
					+ size + ", info=" + info + ", md5=" + md5 + "]";
		}
	}

	private Version getLatestVerInfo(final String urlStr) {
		Version ver = null;
		String jsonStr = DownloadHelper.downloadJsonStr(urlStr);
		if (jsonStr == null) {
			return null;
		}
		JSONArray array;
		try {
			array = new JSONArray(jsonStr);
			if (array.length() > 0) {
				JSONObject jsonObj;
				try {
					jsonObj = array.getJSONObject(0);

					int code = jsonObj.getInt(Config.JSON_VERSION_CODE);
					String name = jsonObj.getString(Config.JSON_VERSION_NAME);
					String info = jsonObj.getString(Config.JSON_VERSION_INFO);
					String md5 = null;
					int size = 0; // KB

					if (jsonObj.has(Config.JSON_VERSION_MD5)) {
						md5 = jsonObj.getString(Config.JSON_VERSION_MD5);
					}

					if (jsonObj.has(Config.JSON_VERSION_SIZE)) {
						size = jsonObj.getInt(Config.JSON_VERSION_SIZE);
					}

					ver = new Version(name, code, size, info, md5);
				} catch (JSONException e) {
					Log.e(TAG, e.getMessage());
				}
			}
		} catch (JSONException e) {
			Log.e(TAG, e.getMessage());
		}
		Log.i(TAG, "remote version: " + ver.toString());
		return ver;

	}

	private void launchUpdatePrompt(final Version ver) {
		if (ver == null) {
			Log.e(TAG, "ver: " + ver);
			return;
		}
		Intent intent = new Intent(getApplicationContext(), UpdateConfirm.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra(Config.INTENT_APK_VERSION_NAME, ver.name);
		intent.putExtra(Config.INTENT_APK_VERSION_INFO, ver.info);
		intent.putExtra(Config.INTENT_APK_VERSION_SIZE, ver.size);
		intent.putExtra(Config.INTENT_APK_VERSION_SIZE, ver.size);
		intent.putExtra(Config.INTENT_APK_VERSION_MD5, ver.md5);
		startActivity(intent);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		if (Debuger.DEBUG) {
			File apk = new File(Environment.getExternalStorageDirectory(),
					"/Mot/Mot.apk");

			Version serverVer = new Version("1.7.0", 9, 234, "1. a\n2.b1. a",
					"658cd1d2a92f58e6d289266222dff37d");
			/*
			 * apk = downLoadApk(new FileInputStream( new
			 * File(Environment.getExternalStorageDirectory(), "/Mot/a.apk")));
			 * 
			 * if (!getMd5Sum(apk).equalsIgnoreCase(
			 * "ca5fd267ff1f2d0b074d8127fc0f86e4")) { apk = downLoadApk(new
			 * FileInputStream(new File(
			 * Environment.getExternalStorageDirectory(), "/Mot/a.apk"))); }
			 */
			Log.d(TAG, "onHandleIntent: " + apk.exists());
			launchUpdatePrompt(serverVer);
			return;
		}
		Log.i(TAG, "start to download ver.json...");
		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			int currentVersionCode = getCurrentVersionCode();
			Version newVersion = getLatestVerInfo(Config.URL_VER_JSON);

			if (newVersion == null) {
				Log.e(TAG, "onHandleIntent() serverVer is null");
				return;
			}

			if (Debuger.DEBUG) {
				Log.d(TAG, "onHandleIntent() currentVersionCode: "
						+ currentVersionCode);
				Log.d(TAG, "onHandleIntent() serverVersionCode: "
						+ newVersion.code);
				Log.d(TAG, "onHandleIntent() serverVersionName: "
						+ newVersion.name);
			}

			if (newVersion.code > currentVersionCode && currentVersionCode > 0) {
				/*
				 * File apk =
				 * DownloadHelper.downloadApkFile(Config.REMOTE_APK_FILE_URL);
				 * 
				 * if (FileChecker.isValid(apk, newVersion.md5)) { //
				 * re-download apk =
				 * DownloadHelper.downloadApkFile(Config.REMOTE_APK_FILE_URL); }
				 */
				launchUpdatePrompt(newVersion);
			}
		} else {
			Log.e(TAG, "network is not available now.");
		}

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		stopSelf();
		if (Debuger.DEBUG) {
			Log.d(TAG, "onDestroy()");
		}
	}
}
