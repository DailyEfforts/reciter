package com.gmail.dailyefforts.android.reviwer.unit;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

import com.gmail.dailyefforts.android.reviwer.R;
import com.gmail.dailyefforts.android.reviwer.db.DBA;
import com.gmail.dailyefforts.android.reviwer.drag.DragAndDropActivity;

public class UnitButton extends Button implements View.OnClickListener {

	// private static final String TAG = UnitButton.class.getSimpleName();
	public int id;
	public int start;
	public int end;
	private DBA dba;

	public UnitButton(Context context) {
		this(context, null);
	}

	@SuppressWarnings("deprecation")
	public UnitButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		setOnClickListener(this);
		setBackgroundDrawable(getResources().getDrawable(
				R.drawable.unit_view_bg));
		// setTextColor(getResources().getColor(R.color.gray_light));
		setTextAppearance(context,
				android.R.style.TextAppearance_DeviceDefault_Small);
	}

	@Override
	public String toString() {
		return "UnitView [id=" + id + ", start=" + start + ", end=" + end + "]";
	}

	@Override
	public void onClick(View v) {
		dba = DBA.getInstance(this.getContext().getApplicationContext());
		dba.loadUnitWords(this.start, this.end);

		Intent intent = new Intent(getContext(), DragAndDropActivity.class);
		this.getContext().startActivity(intent);
	}

}
