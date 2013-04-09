package com.gmail.dailyefforts.android.reviwer.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

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

	private String getVerJsonStr(final InputStream in) {
		if (in == null) {
			Log.e(TAG, "getVerJsonStr() in is NULL.");
			return null;
		}
		String jsonStr = null;
		BufferedReader reader = null;
		reader = new BufferedReader(new InputStreamReader(in));
		StringBuffer sb = new StringBuffer();
		String str = null;
		try {
			while ((str = reader.readLine()) != null) {
				sb.append(str);
			}
			jsonStr = sb.toString();
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
		}

		return jsonStr;
	}

	private class Version {
		private String name;
		private int code;
		private String info;

		public Version(String name, int code, String info) {
			super();
			this.name = name;
			this.code = code;
			this.info = info;
		}

		public String getName() {
			return name;
		}

		public int getCode() {
			return code;
		}

		public String getInfo() {
			return info;
		}

	}

	private Version getServerVersionCode(final String urlStr) {
		Version ver = null;
		String jsonStr = getVerJsonStr(getInStream(urlStr));
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
					int code = Integer.parseInt(jsonObj
							.getString(Config.JSON_VERSION_CODE));
					String name = jsonObj.getString(Config.JSON_VERSION_NAME);
					String info = jsonObj.getString(Config.JSON_VERSION_info);

					ver = new Version(name, code, info);
				} catch (JSONException e) {
					Log.e(TAG, e.getMessage());
				}
			}
		} catch (JSONException e) {
			Log.e(TAG, e.getMessage());
		}

		return ver;

	}

	private InputStream getInStream(final String urlStr) {
		InputStream in = null;
		URL url = null;
		try {
			url = new URL(urlStr);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(10000 /* milliseconds */);
			conn.setConnectTimeout(15000 /* milliseconds */);
			conn.setRequestMethod("GET");
			conn.setDoInput(true);
			// Starts the query
			conn.connect();
			int response = conn.getResponseCode();
			if (Debuger.DEBUG) {
				Log.d(TAG, "onHandleIntent() The response is: " + response);
			}
			if (response == HttpURLConnection.HTTP_OK) {
				in = conn.getInputStream();
			}
		} catch (MalformedURLException e) {
			Log.e(TAG, e.getMessage());
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
		}

		return in;
	}

	private void launchUpdatePrompt(final File apk, final Version ver) {
		if (apk == null || !apk.exists() || ver == null) {
			return;
		}
		Intent intent = new Intent(getApplicationContext(), UpdateConfirm.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra(Config.INTENT_APK_FILE_PATH, apk.getAbsolutePath());
		intent.putExtra(Config.INTENT_APK_VERSION_NAME, ver.getName());
		intent.putExtra(Config.INTENT_APK_VERSION_INFO, ver.getInfo());
		startActivity(intent);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		if (Debuger.DEBUG) {
			File apk = new File(Environment.getExternalStorageDirectory(), "/Mot/Mot.apk");
			Version serverVer = new Version("1.7.0", 9, "1. a\n2.b");
			launchUpdatePrompt(apk, serverVer );
			return;
		}
		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			int currentVersionCode = getCurrentVersionCode();
			Version serverVer = getServerVersionCode(Config.URL_VER_JSON);

			if (serverVer == null) {
				Log.e(TAG, "onHandleIntent() serverVer is null");
				return;
			}

			if (Debuger.DEBUG) {
				Log.d(TAG, "onHandleIntent() currentVersionCode: "
						+ currentVersionCode);
				Log.d(TAG,
						"onHandleIntent() serverVersionCode: "
								+ serverVer.getCode());
				Log.d(TAG,
						"onHandleIntent() serverVersionName: "
								+ serverVer.getName());
			}

			if (serverVer.getCode() > currentVersionCode
					&& currentVersionCode > 0) {
				File apk = downLoadApk(getInStream(Config.URL_APK));
				launchUpdatePrompt(apk, serverVer);
			}
		} else {
			Log.e(TAG, "network is not available now.");
		}

	}

	private File downLoadApk(final InputStream in) {
		if (in == null) {
			Log.e(TAG, "downLoadApk() InputStream is null.");
		}
		File dir = new File(Environment.getExternalStorageDirectory(),
				Config.SDCARD_FOLDER_NAME);

		if (!dir.exists()) {
			dir.mkdir();
		}

		File apk = new File(dir, Config.APK_NAME);

		if (apk.exists()) {
			apk.delete();
		}

		File sky = new File(Environment.getExternalStorageDirectory(),
				"SkyDrive");
		if (sky != null && sky.exists()) {
			File downloads = new File(sky, "downloads");
			if (downloads != null && downloads.exists()) {
				File[] files = downloads.listFiles();
				if (files != null) {
					for (File f : files) {
						if (f.getName().toLowerCase().startsWith("mot")
								&& f.getName().endsWith(".apk")) {
							f.delete();
						}
					}
				}
			}
		}

		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(apk);
			int oneByte = -1;
			while ((oneByte = in.read()) != -1) {
				fos.write(oneByte);
				if (Debuger.DEBUG) {
					Log.d(TAG, "downLoadApk() downloading...");
				}
			}

			fos.flush();
		} catch (FileNotFoundException e) {
			Log.e(TAG, e.getMessage());
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					Log.e(TAG, e.getMessage());
				}
			}

			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					Log.e(TAG, e.getMessage());
				}
			}
		}

		return apk;
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
