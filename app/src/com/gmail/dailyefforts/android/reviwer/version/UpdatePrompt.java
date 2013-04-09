package com.gmail.dailyefforts.android.reviwer.version;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.gmail.dailyefforts.android.reviwer.Config;
import com.gmail.dailyefforts.android.reviwer.R;

public class UpdatePrompt extends Activity implements OnClickListener {

	private Button btnOk;
	private Button btnCancel;
	private TextView mTv;
	private File apk;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_update_prompt);

		btnOk = (Button) findViewById(R.id.btn_update_ok);
		btnCancel = (Button) findViewById(R.id.btn_update_cancel);

		mTv = (TextView) findViewById(R.id.tv_update_version_info);

		if (btnCancel != null && btnOk != null) {
			btnOk.setOnClickListener(this);
			btnCancel.setOnClickListener(this);
		}
		apk = new File(getIntent().getExtras().getString(
				Config.INTENT_APK_FILE_PATH));

		String versionName = getIntent().getExtras().getString(
				Config.INTENT_APK_VERSION_NAME);
		String info = getIntent().getExtras().getString(
				Config.INTENT_APK_VERSION_INFO);

		setTitle(String.format(
				String.valueOf(getResources().getText(R.string.update_prompt)),
				versionName));
		
		if (mTv != null) {
			if (info != null && info.length() > 0) {
				mTv.setText(info);
			} else {
				mTv.setVisibility(View.GONE);
			}
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_update_ok:
			Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
			intent.setData(Uri.fromFile(apk));
			intent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
			intent.putExtra(Intent.EXTRA_RETURN_RESULT, true);
			intent.putExtra(Intent.EXTRA_INSTALLER_PACKAGE_NAME,
					getApplicationInfo().packageName);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			break;
		case R.id.btn_update_cancel:
			finish();
			break;
		default:
			break;
		}
	}
}
