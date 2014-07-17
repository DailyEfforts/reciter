package com.gmail.dailyefforts.reciter.version;

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

import com.gmail.dailyefforts.android.reviwer.R;
import com.gmail.dailyefforts.reciter.Config;

public class UpdateConfirm extends Activity {

	private String mVerInfo;
	private String mVerName;
	private String mUpdatePromptTitle;
	private static DownloadManager dMgr;

	private static final String TAG = UpdateConfirm.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle extras = getIntent().getExtras();
		mVerInfo = extras.getString(Config.INTENT_APK_VERSION_INFO);
		mVerName = extras.getString(Config.INTENT_APK_VERSION_NAME);
		mUpdatePromptTitle = String.format(
				getString(R.string.update_to_latest_version), mVerName);
		showPromptDialog();
	}

	private void showPromptDialog() {
		DialogFragment newFragment = UpdatePromptDialogFragment.newInstance(
				mUpdatePromptTitle, mVerInfo);
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
			args.putString(Config.TITLE, title);
			args.putString(Config.MESSAGE, message);
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
			String title = getArguments().getString(Config.TITLE);
			String message = getArguments().getString(Config.MESSAGE);

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
							request.setDescription(getString(R.string.downloading_the_latest_reciter_apk_file));
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
								File dir = new File(
										Environment.DIRECTORY_DOWNLOADS);
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
