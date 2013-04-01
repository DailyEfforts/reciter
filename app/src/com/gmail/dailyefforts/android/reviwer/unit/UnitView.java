package com.gmail.dailyefforts.android.reviwer.unit;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;

import com.gmail.dailyefforts.android.reviwer.TestPage;
import com.gmail.dailyefforts.android.reviwer.Word;
import com.gmail.dailyefforts.android.reviwer.db.DBA;
import com.gmail.dailyefforts.android.reviwer.debug.Debuger;

public class UnitView extends Button implements View.OnClickListener {

	private static final String TAG = UnitView.class.getSimpleName();
	public int id;
	public int start;
	public int end;
	private DBA dba;

	private static SparseArray<Word> map = new SparseArray<Word>();

	public UnitView(Context context) {
		super(context);

		setOnClickListener(this);
	}

	public static SparseArray<Word> getMap() {
		return map;
	}

	@Override
	public String toString() {
		return "UnitView [id=" + id + ", start=" + start + ", end=" + end + "]";
	}

	@Override
	public void onClick(View v) {
		String sql = "select " + DBA.COLUMN_ID + ", " + DBA.COLUMN_WORD + ", "
				+ DBA.COLUMN_MEANING + " from " + DBA.TABLE_NAME + " where "
				+ DBA.COLUMN_ID + ">=? AND " + DBA.COLUMN_ID + "<=?;";

		dba = DBA.getInstance(this.getContext().getApplicationContext());

		Cursor cursor = dba.rawQuery(
				sql,
				new String[] { String.valueOf(this.start),
						String.valueOf(this.end) });

		if (cursor != null && cursor.moveToFirst()) {
			map.clear();
			int idx = 0;
			while (!cursor.isAfterLast()) {
				int id = cursor.getInt(cursor.getColumnIndex(DBA.COLUMN_ID));
				String word = cursor.getString(cursor
						.getColumnIndex(DBA.COLUMN_WORD));
				String meanning = cursor.getString(cursor
						.getColumnIndex(DBA.COLUMN_MEANING));
				if (Debuger.DEBUG) {
					Log.d(TAG, String.format("id: %d, word: %s, meanning: %s",
							id, word, meanning));
				}
				Word newWord = new Word(word, meanning);
				map.put(idx++, newWord);
				cursor.moveToNext();
			}
		}

		if (cursor != null) {
			cursor.close();
		}

		Intent intent = new Intent(getContext(), TestPage.class);
		this.getContext().startActivity(intent);
	}

}
