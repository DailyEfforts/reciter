package com.gmail.dailyefforts.android.reviwer.version;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import com.gmail.dailyefforts.android.reviwer.Config;
import com.gmail.dailyefforts.android.reviwer.R;
import com.gmail.dailyefforts.android.reviwer.debug.Debuger;
import com.gmail.dailyefforts.android.reviwer.helper.DownloadHelper;
import com.gmail.dailyefforts.android.reviwer.helper.FileChecker;

@SuppressWarnings("deprecation")
public class UpdateConfirm extends Activity {

	private String verInfo;
	private String verName;
	private int size;
	private String updatePromptTitle;
	private static String MD5_SUM;

	private static final int DIALOG_DOWNLOAD_YES_NO = 0;
	private static final int DIALOG_DOWNLOADING = 1;
	private static final int INTENT_START_APK_INSTALLER_REQUEST_CODE = 0;
	private static final String TAG = UpdateConfirm.class.getSimpleName();
	private static ProgressDialog mDownloadingProgressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle extras = getIntent().getExtras();
		verInfo = extras.getString(Config.INTENT_APK_VERSION_INFO);

		verName = extras.getString(Config.INTENT_APK_VERSION_NAME);

		size = extras.getInt(Config.INTENT_APK_VERSION_SIZE);

		MD5_SUM = extras.getString(Config.INTENT_APK_VERSION_MD5);

		updatePromptTitle = String.format(
				getString(R.string.update_to_latest_version), verName);

		showDialog(DIALOG_DOWNLOAD_YES_NO, null);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		finish();
		return true;
	}

	@Override
	protected Dialog onCreateDialog(int id, Bundle args) {
		switch (id) {
		case DIALOG_DOWNLOAD_YES_NO:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);

			builder.setTitle(updatePromptTitle);
			builder.setMessage(verInfo);
			builder.setPositiveButton(android.R.string.yes,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							showDialog(DIALOG_DOWNLOADING, null);
						}
					});
			builder.setNegativeButton(android.R.string.no,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							finish();
						}
					});

			return builder.create();
		case DIALOG_DOWNLOADING:
			mDownloadingProgressDialog = new ProgressDialog(this);
			mDownloadingProgressDialog.setTitle("Downloading...");
			mDownloadingProgressDialog
					.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			mDownloadingProgressDialog.setMax(size);

			// mDownloadingProgressDialog.setButton(
			// DialogInterface.BUTTON_NEGATIVE,
			// getText(android.R.string.no),
			// new DialogInterface.OnClickListener() {
			// public void onClick(DialogInterface dialog,
			// int whichButton) {
			//
			// /* User clicked No so do some stuff */
			// }
			// });
			mDownloadingProgressDialog.setProgressNumberFormat("%d KB");
			mDownloadingProgressDialog
					.setOnShowListener(new DialogInterface.OnShowListener() {

						@Override
						public void onShow(DialogInterface dialog) {
							new DownLoadTask(UpdateConfirm.this)
									.execute(Uri.EMPTY);
						}
					});

			return mDownloadingProgressDialog;
		}
		return null;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (Debuger.DEBUG) {
			Log.d(TAG, String.format(
					"onActivityResult() requestCode: %d, resultCode: %d",
					requestCode, requestCode));
		}
		if (requestCode == INTENT_START_APK_INSTALLER_REQUEST_CODE) {
			if (resultCode == Activity.RESULT_CANCELED) {
				finish();
			}
		}
	}

	public static class DownLoadTask extends AsyncTask<Uri, Integer, File> {

		private static final int ONE_KB = 1024;
		private static final String TAG = null;

		private Activity mActivity;

		public DownLoadTask(Activity context) {
			this.mActivity = context;
		}

		@Override
		protected void onPostExecute(File apkFile) {
			super.onPostExecute(apkFile);
			if (Debuger.DEBUG) {
				Log.d(TAG,
						"onPostExecute()"
								+ FileChecker.isValid(apkFile, MD5_SUM));
			}
			if (apkFile != null && apkFile.exists()
					&& FileChecker.isValid(apkFile, MD5_SUM)) {
				Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
				intent.setData(Uri.fromFile(apkFile));
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				// startActivity(intent);

				if (mActivity != null) {
					mDownloadingProgressDialog.dismiss();
					// mActivity.startActivity(intent);
					mActivity.startActivityForResult(intent,
							INTENT_START_APK_INSTALLER_REQUEST_CODE);
				} else {
					Log.e(TAG, "onPostExecute() context it null: " + mActivity);
				}
			} else {
				if (mActivity != null) {
					Toast.makeText(mActivity.getApplicationContext(),
							"Download failed, please try again later.",
							Toast.LENGTH_SHORT).show();
				}
			}
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);

			if (mDownloadingProgressDialog != null) {
				mDownloadingProgressDialog.setProgress(values[0]);
			}
		}

		@Override
		protected File doInBackground(Uri... params) {
			File apk = null;

			InputStream in = DownloadHelper
					.getInStreamFromUrl(Config.REMOTE_APK_FILE_URL);

			if (in != null) {
				File dir = new File(Environment.getExternalStorageDirectory(),
						Config.SDCARD_FOLDER_NAME);

				if (!dir.exists()) {
					dir.mkdirs();
				}

				apk = new File(dir, Config.APK_NAME);

				if (apk.exists()) {
					if (FileChecker.isValid(apk, MD5_SUM)) {
						// have downloaded the latest apk.
						return apk;
					} else {
						apk.delete();
					}
				}

				FileOutputStream fos = null;

				int iKB = 0;

				try {
					fos = new FileOutputStream(apk);
					byte[] buf = new byte[ONE_KB];
					int len = 0;
					while ((len = in.read(buf)) > 0) {
						fos.write(buf, 0, len);
						iKB++;
						publishProgress(iKB);
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
			return null;
		}

	}

}
