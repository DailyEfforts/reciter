package com.gmail.dailyefforts.android.reviwer.version;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.gmail.dailyefforts.android.reviwer.Config;
import com.gmail.dailyefforts.android.reviwer.R;

public class UpdateConfirm extends Activity {

	private String verInfo;
	private String verName;
	private int size;
	private String updatePromptTitle;
	private static DownloadManager dMgr;
	private static String MD5_SUM;

	private static final String TAG = UpdateConfirm.class.getSimpleName();

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
		showPromptDialog();
	}

	private void showPromptDialog() {
		DialogFragment newFragment = UpdatePromptDialogFragment.newInstance(
				updatePromptTitle, verInfo);
		newFragment.show(getFragmentManager(), "dialog");
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

	public static class UpdatePromptDialogFragment extends DialogFragment {

		public static UpdatePromptDialogFragment newInstance(String title,
				String message) {
			UpdatePromptDialogFragment frag = new UpdatePromptDialogFragment();
			Bundle args = new Bundle();
			args.putString("title", title);
			args.putString("message", message);
			frag.setArguments(args);
			return frag;
		}

		@Override
		public void onDismiss(DialogInterface dialog) {
			super.onDismiss(dialog);
			getActivity().finish();
		}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			String title = getArguments().getString("title");
			String message = getArguments().getString("message");

			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle(title);
			builder.setMessage(message);
			builder.setPositiveButton(android.R.string.yes,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							DownloadManager.Request request = new DownloadManager.Request(
									Uri.parse(Config.REMOTE_APK_FILE_URL));
							request.setTitle(getString(R.string.click_to_install_reciter));
							request.setDescription("Downloading the latest Reciter APK file...");
							request.setAllowedNetworkTypes(Request.NETWORK_WIFI
									| Request.NETWORK_WIFI);
							request.setDestinationInExternalPublicDir(
									Environment.DIRECTORY_DOWNLOADS,
									Config.APK_NAME);
							request.setNotificationVisibility(Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
							Log.i(TAG, "Environment.DIRECTORY_DOWNLOADS: "
									+ Environment.DIRECTORY_DOWNLOADS);
							Log.i(TAG, Environment
									.getExternalStorageDirectory().toString());
							File down = Environment
									.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
							Log.i(TAG, down.getAbsolutePath());

							File[] list = down.listFiles();
							if (list != null) {
								for (File f : list) {
									if (Config.APK_NAME.equals(f.getName())) {
										Log.i(TAG,
												"delete " + f.getAbsolutePath()
														+ ", " + f.delete());
									}
								}
							}
							dMgr.enqueue(request);

							Toast.makeText(
									getActivity(),
									R.string.click_notification_to_install_when_download_completed,
									Toast.LENGTH_LONG).show();
							try {
								File dir = new File(Environment
										.getExternalStorageDirectory(),
										Config.SDCARD_FOLDER_NAME);
								if (dir != null && dir.exists()) {
									File[] subFiles = dir.listFiles();
									if (subFiles != null) {
										for (File f : subFiles) {
											f.delete();
										}
									}
									dir.delete();
								}
							} catch (Exception e) {
								Log.e(TAG, e.getMessage());
							}

						}
					});
			builder.setNegativeButton(android.R.string.no, null);

			return builder.create();
		}
	}

}
