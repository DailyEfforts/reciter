package com.gmail.dailyefforts.android.reviwer.version;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
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
	private DownloadManager dMgr;
	private static String MD5_SUM;

	private static final int DIALOG_DOWNLOAD_YES_NO = 0;
	private static final int DIALOG_DOWNLOADING = 1;
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
	protected void onResume() {
		super.onResume();
		dMgr = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
	}

	@Override
	protected void onPause() {
		super.onPause();
		dMgr = null;
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
							DownloadManager.Request request = new DownloadManager.Request(
									Uri.parse(Config.REMOTE_APK_FILE_URL));
							request.setTitle(getString(R.string.app_name));
							request.setDescription("Downloading the latest Reciter APK file...");
							request.setAllowedNetworkTypes(Request.NETWORK_WIFI
									| Request.NETWORK_WIFI);
							request.setDestinationInExternalPublicDir(
									Environment.DIRECTORY_DOWNLOADS,
									Config.APK_NAME);
							request.setNotificationVisibility(Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
							Log.i(TAG, "Environment.DIRECTORY_DOWNLOADS: "
									+ Environment.DIRECTORY_DOWNLOADS);
							Log.i(TAG, Environment.getExternalStorageDirectory().toString());
							File down = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
							Log.i(TAG, down.getAbsolutePath());
							
							File[] list = down.listFiles();
							if (list != null) {
								for (File f : list) {
/*									if (Config.APK_NAME.equals(f.getName())) {
										Log.i(TAG, "delete old file " + f.delete());
									}*/
									if (f.getName().endsWith(".apk")) {
										Log.i(TAG, "delete old file " + f.delete());
									}
								}
							}
							  dMgr.enqueue(request);
						}
					});
			builder.setNegativeButton(android.R.string.no,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
						}
					});

			AlertDialog dialog = builder.create();
			dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {

				@Override
				public void onDismiss(DialogInterface dialog) {
					finish();
				}
			});
			return dialog;
		}
		return null;
	}

}
