package com.gmail.dailyefforts.android.reviwer.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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
		private String verName;
		private int verCode;
		private String verInfo;
		private String md5;

		public Version(String name, int code, String info, String md5) {
			super();
			this.verName = name;
			this.verCode = code;
			this.verInfo = info;
			this.md5 = md5;
		}

		public String getVerName() {
			return verName;
		}

		public int getVerCode() {
			return verCode;
		}

		public String getInfo() {
			return verInfo;
		}

		public String getMd5() {
			return md5;
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
					String info = jsonObj.getString(Config.JSON_VERSION_INFO);
					String md5 = null;
					
					if (jsonObj.has(Config.JSON_VERSION_MD5)) {
						md5 = jsonObj.getString(Config.JSON_VERSION_MD5);
					}

					ver = new Version(name, code, info, md5);
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
		intent.putExtra(Config.INTENT_APK_VERSION_NAME, ver.getVerName());
		intent.putExtra(Config.INTENT_APK_VERSION_INFO, ver.getInfo());
		startActivity(intent);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		if (Debuger.DEBUG) {
			File apk;
			try {

				Version serverVer = new Version("1.7.0", 9, "1. a\n2.b1. a",
						"ca5fd267ff1f2d0b074d8127fc0f86e4");
				apk = downLoadApk(new FileInputStream(
						new File(Environment.getExternalStorageDirectory(),
								"/Mot/a.apk")));

				if (!getMd5Sum(apk).equalsIgnoreCase(
						"ca5fd267ff1f2d0b074d8127fc0f86e4")) {
					apk = downLoadApk(new FileInputStream(new File(
							Environment.getExternalStorageDirectory(),
							"/Mot/a.apk")));
				}
				launchUpdatePrompt(apk, serverVer);
				Log.d(TAG, "md5: " + String.valueOf(getMd5Sum(apk)));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
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
								+ serverVer.getVerCode());
				Log.d(TAG,
						"onHandleIntent() serverVersionName: "
								+ serverVer.getVerName());
			}

			if (serverVer.getVerCode() > currentVersionCode
					&& currentVersionCode > 0) {
				File apk = downLoadApk(getInStream(Config.URL_APK));
				String md5 = null;
				try {
					md5 = getMd5Sum(apk);
				} catch (NoSuchAlgorithmException e) {
					Log.e(TAG, e.getMessage());
				} catch (IOException e) {
					Log.e(TAG, e.getMessage());
				}
				if (Debuger.DEBUG) {
					Log.d(TAG, "onHandleIntent() md5: " + md5);
				}
				if (md5 != null && !md5.equalsIgnoreCase(serverVer.getMd5())) {
					apk = downLoadApk(getInStream(Config.URL_APK));
				}
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

		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(apk);
			byte[] buf = new byte[1024];
			int len = 0;
			while ((len = in.read(buf)) > 0) {
				fos.write(buf, 0, len);
				if (Debuger.DEBUG) {
					Log.d(TAG, "downLoadApk() downloading..." + buf.length
							+ ", " + len);
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

	private static char[] DIGITS = { '0', '1', '2', '3', '4', '5', '6', '7',
			'8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	private static String getMd5Sum(File file) throws NoSuchAlgorithmException,
			IOException {

		InputStream in = new FileInputStream(file);

		MessageDigest digester = MessageDigest.getInstance("MD5");
		byte[] bytes = new byte[8192];
		int byteCount;
		while ((byteCount = in.read(bytes)) > 0) {
			digester.update(bytes, 0, byteCount);
		}
		byte[] digest = digester.digest();

		BigInteger bigInt = new BigInteger(1, digest);
		String out = bigInt.toString(16);

		return out;
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
