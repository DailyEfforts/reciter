package com.gmail.dailyefforts.android.reviwer;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.LinearLayout.LayoutParams;

public class OptBtn extends Button {

	public OptBtn(Context context, int id) {
		super(context);
		// TODO Auto-generated constructor stub
		setBackgroundDrawable(getResources().getDrawable(
				R.drawable.opt_btn_bg_normal));
		setTextAppearance(context,
				android.R.style.TextAppearance_DeviceDefault_Medium);
		setTextColor(getResources().getColor(R.color.opt_btn_txt_color));
		
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		params.height = 0;
		params.weight = 1;
		setLayoutParams(params);
		setId(id);
	}

	private OptBtn(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

}
