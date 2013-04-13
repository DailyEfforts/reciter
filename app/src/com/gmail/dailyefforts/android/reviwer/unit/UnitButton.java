package com.gmail.dailyefforts.android.reviwer.unit;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;

import com.gmail.dailyefforts.android.reviwer.R;
import com.gmail.dailyefforts.android.reviwer.activity.WordPager;
import com.gmail.dailyefforts.android.reviwer.db.DBA;
import com.gmail.dailyefforts.android.reviwer.debug.Debuger;
import com.gmail.dailyefforts.android.reviwer.word.Word;

public class UnitButton extends Button implements View.OnClickListener {

	private static final String TAG = UnitButton.class.getSimpleName();
	public int id;
	public int start;
	public int end;
	private DBA dba;

	public UnitButton(Context context) {
		this(context, null);
	}

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
		String sql = "select " + DBA.WORD_ID + ", " + DBA.WORD_WORD + ", "
				+ DBA.WORD_MEANING + " from " + DBA.TABLE_WORD_LIST + " where "
				+ DBA.WORD_ID + ">=? AND " + DBA.WORD_ID + "<=?;";

		dba = DBA.getInstance(this.getContext().getApplicationContext());

		Cursor cursor = dba.rawQuery(
				sql,
				new String[] { String.valueOf(this.start),
						String.valueOf(this.end) });
		
		if (Debuger.DEBUG) {
			Log.d(TAG, "onClick() start: " + this.start + ", end: " + this.end);
		}

		if (cursor != null) {
			SparseArray<Word> map = Word.getMap();
			map.clear();
			int idx = 0;
			while (cursor.moveToNext()) {
				int id = cursor.getInt(cursor.getColumnIndex(DBA.WORD_ID));
				String word = cursor.getString(cursor
						.getColumnIndex(DBA.WORD_WORD));
				String meanning = cursor.getString(cursor
						.getColumnIndex(DBA.WORD_MEANING));
				if (Debuger.DEBUG) {
					Log.d(TAG, String.format("id: %d, word: %s, meanning: %s",
							id, word, meanning));
				}
				Word newWord = new Word(word, meanning);
				map.put(idx++, newWord);
			}
			cursor.close();
		}

		Intent intent = new Intent(getContext(), WordPager.class);
		this.getContext().startActivity(intent);
	}

}
