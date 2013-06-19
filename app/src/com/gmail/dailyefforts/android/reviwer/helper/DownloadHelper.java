package com.gmail.dailyefforts.android.reviwer.helper;

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

import android.os.Environment;
import android.util.Log;

import com.gmail.dailyefforts.android.reviwer.Config;

public class DownloadHelper {
	private static final String TAG = DownloadHelper.class.getSimpleName();

	public static InputStream getInStreamFromUrl(final String remoteUrlStr) {
		InputStream in = null;
		URL url = null;
		try {
			url = new URL(remoteUrlStr);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(10000);
			conn.setConnectTimeout(15000);
			conn.setRequestMethod("GET");
			conn.setDoInput(true);
			// Starts the query
			conn.connect();
			int response = conn.getResponseCode();
			if (Config.DEBUG) {
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

	public static String downloadJsonStr(final String urlStr) {
		InputStream in = getInStreamFromUrl(urlStr);

		return downloadJsonStr(in);
	}

	private static String downloadJsonStr(final InputStream in) {
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

	public static File downloadApkFile(final String urlStr) {
		InputStream in = getInStreamFromUrl(urlStr);
		return downloadApkFile(in);
	}

	private static File downloadApkFile(final InputStream in) {
		if (in == null) {
			Log.e(TAG, "downLoadApk() InputStream is null.");
			return null;
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
				if (Config.DEBUG) {
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
}
