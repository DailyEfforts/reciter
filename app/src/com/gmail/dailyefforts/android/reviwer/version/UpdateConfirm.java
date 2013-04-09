package com.gmail.dailyefforts.android.reviwer.version;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.gmail.dailyefforts.android.reviwer.Config;
import com.gmail.dailyefforts.android.reviwer.R;

public class UpdateConfirm extends Activity implements OnClickListener {

	private Button btnOk;
	private Button btnCancel;
	private TextView mVersionInfo;
	private File apk;
	private TextView mUpdateConfirm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_LEFT_ICON);

		setContentView(R.layout.update_confirm);

		getWindow().setFeatureDrawableResource(Window.FEATURE_LEFT_ICON,
				R.drawable.ic_launcher);

		btnOk = (Button) findViewById(R.id.btn_update_yes);
		btnCancel = (Button) findViewById(R.id.btn_update_no);

		mVersionInfo = (TextView) findViewById(R.id.tv_update_version_info);
		mUpdateConfirm = (TextView) findViewById(R.id.tv_update_confirm);

		if (btnCancel != null && btnOk != null) {
			btnOk.setOnClickListener(this);
			btnCancel.setOnClickListener(this);
		}
		apk = new File(getIntent().getExtras().getString(
				Config.INTENT_APK_FILE_PATH));

		String verInfo = getIntent().getExtras().getString(
				Config.INTENT_APK_VERSION_INFO);
		String verName = getIntent().getExtras().getString(
				Config.INTENT_APK_VERSION_NAME);

		if (mVersionInfo != null) {
			if (verInfo != null && verInfo.length() > 0) {
				mVersionInfo.setText(verInfo);
			} else {
				mVersionInfo.setVisibility(View.GONE);
			}
		}

		if (mUpdateConfirm != null) {
			mUpdateConfirm.setText(String.format(
					String.valueOf(getResources().getText(
							R.string.update_confirm)), verName));
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_update_yes:
			Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
			intent.setData(Uri.fromFile(apk));
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			break;
		case R.id.btn_update_no:
			finish();
			break;
		default:
			break;
		}
	}
}
