package com.gmail.dailyefforts.android.reciter.test;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.LinearLayout.LayoutParams;

import com.gmail.dailyefforts.android.reviwer.R;

public class OptionButton extends Button {

	public OptionButton(Context context, int id) {
		super(context);
		// TODO Auto-generated constructor stub
		setTextAppearance(context, android.R.style.TextAppearance_Medium);
		setTextColor(Color.LTGRAY);
		setBackgroundResource(R.drawable.btn_dark_bg);

		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		Resources res = getResources();
		if (res != null) {
			int leftMargin = res
					.getDimensionPixelSize(R.dimen.opt_btn_margin_left);
			int topMargin = res
					.getDimensionPixelSize(R.dimen.opt_btn_margin_top);
			int rightMargin = res
					.getDimensionPixelSize(R.dimen.opt_btn_margin_right);
			int bottomMargin = res
					.getDimensionPixelSize(R.dimen.opt_btn_margin_bottom);
			params.height = 0;
			params.weight = 1;
			params.leftMargin = leftMargin;
			params.topMargin = topMargin;
			params.rightMargin = rightMargin;
			params.bottomMargin = bottomMargin;
			setLayoutParams(params);
			setId(id);
		}

	}

	private OptionButton(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

}
