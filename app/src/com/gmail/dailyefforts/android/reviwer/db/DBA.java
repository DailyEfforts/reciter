package com.gmail.dailyefforts.android.reviwer.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBA extends SQLiteOpenHelper {
	private static final String TAG = DBA.class.getSimpleName();
	private static final String DATABASE_NAME = "wot.db";
	private static final int DATABASE_VERSION = 1;
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
			+ " datetime default current_timestamp, "
			+ COLUMN_STAR
			+ " integer default 0, " + COLUMN_OTHER + " text);";

	private static DBA dba = null;

	public int update(String table, ContentValues values, String whereClause,
			String[] whereArgs) {
		return getWritableDatabase().update(table, values, whereClause,
				whereArgs);

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
