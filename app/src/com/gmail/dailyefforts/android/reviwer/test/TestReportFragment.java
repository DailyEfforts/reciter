package com.gmail.dailyefforts.android.reviwer.test;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class TestReportFragment extends DialogFragment {

	public static TestReportFragment newInstance(String title,
			String message) {
		TestReportFragment frag = new TestReportFragment();
		Bundle args = new Bundle();
		args.putString("title", title);
		args.putString("message", message);
		frag.setArguments(args);
		return frag;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		String title = getArguments().getString("title");
		String message = getArguments().getString("message");

		Builder builder = new AlertDialog.Builder(getActivity());
		builder.setIcon(android.R.drawable.ic_dialog_alert);
		builder.setTitle(title);
		builder.setMessage(message);
		builder.setPositiveButton(android.R.string.ok,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						getActivity().finish();
					}
				});
		return builder.create();
	}
}
