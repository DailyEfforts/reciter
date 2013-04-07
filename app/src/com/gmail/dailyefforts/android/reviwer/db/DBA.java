package com.gmail.dailyefforts.android.reviwer.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.gmail.dailyefforts.android.reviwer.debug.Debuger;
import com.gmail.dailyefforts.android.reviwer.word.Word;

public class DBA extends SQLiteOpenHelper {
	private static final String TAG = DBA.class.getSimpleName();
	private static final String DATABASE_NAME = "wot.db";
	private static final int DATABASE_VERSION = 2;
	public static final String TABLE_NAME = "wordlist";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_WORD = "word";
	public static final String COLUMN_MEANING = "meaning";
	public static final String COLUMN_SAMPLE = "sample";
	public static final String COLUMN_TIMESTAMP = "timestamp";
	public static final String COLUMN_STAR = "star";
	public static final String COLUMN_OTHER = "other";

	private static final String CREAT_TABLE_WORD_LIST = "create table if not exists "
			+ TABLE_NAME
			+ "("
			+ "_id integer primary key autoincrement, "
			+ COLUMN_WORD
			+ " text unique, "
			+ COLUMN_MEANING
			+ " text, "
			+ COLUMN_SAMPLE
			+ " text, "
			+ COLUMN_TIMESTAMP
			+ " datetime, "
			+ COLUMN_STAR + " integer default 0, " + COLUMN_OTHER + " text);";

	private static DBA dba = null;

	public void star(final String word) {
		Cursor cursor = query(TABLE_NAME, null, COLUMN_WORD + "=?",
				new String[] { word }, null, null, null);

		if (cursor != null && cursor.moveToFirst()) {
			int star = cursor.getInt(cursor.getColumnIndex(COLUMN_STAR));
			if (Debuger.DEBUG) {
				long timeStamp = cursor.getInt(cursor
						.getColumnIndex(COLUMN_TIMESTAMP));
				Log.d(TAG, "star() star: " + star);
				Log.d(TAG, "star() timeStamp: " + timeStamp);
			}
			ContentValues values = new ContentValues();
			values.put(COLUMN_TIMESTAMP, System.currentTimeMillis());
			values.put(COLUMN_STAR, ++star);
			getWritableDatabase().update(TABLE_NAME, values,
					COLUMN_WORD + "=?", new String[] { word });
		}

		if (cursor != null) {
			cursor.close();
		}
	}

	public int getStar(final String word) {
		int star = -1;
		Cursor cursor = query(TABLE_NAME, null, COLUMN_WORD + "=?",
				new String[] { word }, null, null, null);

		if (cursor != null && cursor.moveToFirst()) {
			star = cursor.getInt(cursor.getColumnIndex(COLUMN_STAR));
			cursor.close();
		}
		return star;

	}

	public boolean exist(final String word) {
		Cursor cursor = query(TABLE_NAME, null, COLUMN_WORD + "=?",
				new String[] { word }, null, null, null);

		if (cursor != null && cursor.moveToFirst()) {
			cursor.close();
			return true;
		}

		return false;
	}

	public void unStar(final String word) {

		ContentValues values = new ContentValues();
		values.put(DBA.COLUMN_STAR, 0);
		getWritableDatabase().update(TABLE_NAME, values, COLUMN_WORD + "=?",
				new String[] { word });

	}

	public Word getWordByIdx(int idx) {
		Cursor cursor = query(TABLE_NAME, null, COLUMN_ID + "=?",
				new String[] { String.valueOf(idx) }, null, null, null);

		String word = "";
		String meaning = "";

		if (cursor != null && cursor.moveToFirst()) {
			word = cursor.getString(cursor.getColumnIndex(COLUMN_WORD));
			meaning = cursor.getString(cursor.getColumnIndex(COLUMN_MEANING));
			cursor.close();
		}

		return new Word(word, meaning);
	}

	private DBA(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	public static DBA getInstance(Context context) {
		if (dba == null) {
			dba = new DBA(context.getApplicationContext());
		}
		return dba;
	}

	public Cursor query(String table, String[] columns, String selection,
			String[] selectionArgs, String groupBy, String having,
			String orderBy) {
		return getReadableDatabase().query(table, columns, selection,
				selectionArgs, groupBy, having, orderBy);
	}

	public Cursor rawQuery(String sql, String[] selectionArgs) {
		return getReadableDatabase().rawQuery(sql, selectionArgs);
	}

	public int getCount() {
		Cursor cursor = query(TABLE_NAME, null, null, null, null, null, null);
		int count = 0;
		if (cursor != null) {
			count = cursor.getCount();
			cursor.close();
		}
		return count;
	}
	
	public long insert(String table, String nullColumnHack, ContentValues values) {
		long result = -1L;
		try {
			result = getWritableDatabase()
					.insert(table, nullColumnHack, values);
		} catch (SQLiteException e) {
			// let go.
			Log.e(TAG, e.getMessage());
		}
		return result;
	}

	public void beginTransaction() {
		getWritableDatabase().beginTransaction();
	}

	public void setTransactionSuccessful() {
		getWritableDatabase().setTransactionSuccessful();
	}

	public int delete(String table, String whereClause, String[] whereArgs) {
		return getWritableDatabase().delete(table, whereClause, whereArgs);
	}

	public void endTransaction() {
		getWritableDatabase().endTransaction();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREAT_TABLE_WORD_LIST);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("drop table if exists " + TABLE_NAME);
		onCreate(db);
	}

}
