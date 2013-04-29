package com.gmail.dailyefforts.android.reviwer.helper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.util.Log;

public class FileChecker {

	private static final String TAG = FileChecker.class.getSimpleName();
	private static char[] DIGITS = { '0', '1', '2', '3', '4', '5', '6', '7',
			'8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	public static boolean isValid(final File file, final String md5Sum) {
		if (file == null) {
			Log.e(TAG, "apk file is null: " + file);
			return false;
		}

		if (md5Sum == null) {
			Log.e(TAG, "md5Sum is null: " + md5Sum);
			return false;
		}

		if (md5Sum.equals(getMd5Sum(file))) {
			return true;
		}

		return false;
	}

	private static String getMd5Sum(final File file) {
		String md5 = null;

		try {
			MessageDigest digester = MessageDigest.getInstance("MD5");

			InputStream in = new FileInputStream(file);

			byte[] bytes = new byte[8192];

			int byteCount;

			while ((byteCount = in.read(bytes)) > 0) {
				digester.update(bytes, 0, byteCount);
			}

			byte[] digest = digester.digest();

			md5 = String.valueOf(encodeHex(digest));
		} catch (FileNotFoundException e) {
			Log.e(TAG, e.getMessage());
		} catch (NoSuchAlgorithmException e) {
			Log.e(TAG, e.getMessage());
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
		}

		// BigInteger bigInt = new BigInteger(1, digest);
		// String out = bigInt.toString(16);

		return md5;
	}

	private static char[] encodeHex(byte[] data) {
		int len = data.length;

		char[] out = new char[len << 1];

		// two characters form the hex value.
		for (int i = 0, j = 0; i < len; i++) {
			out[j++] = DIGITS[(0xF0 & data[i]) >>> 4];
			out[j++] = DIGITS[0x0F & data[i]];
		}

		return out;
	}
}
