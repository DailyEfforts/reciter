package com.gmail.dailyefforts.reciter.test.drag;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

import com.gmail.dailyefforts.android.reviwer.R;

public class DragOptBtn extends Button {

	public DragOptBtn(Context context, AttributeSet attrs) {
		super(context, attrs);
		setBackgroundResource(R.drawable.btn_dark_bg);
		setTextAppearance(context,
				android.R.style.TextAppearance_DeviceDefault_Small);
		setMinHeight(getResources().getDimensionPixelSize(
				R.dimen.activity_drag_drop_opt_mini_height));
	}

}
