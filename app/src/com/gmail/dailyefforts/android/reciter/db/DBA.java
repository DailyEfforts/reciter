package com.gmail.dailyefforts.android.reciter.db;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.util.SparseArray;

import com.gmail.dailyefforts.android.reciter.Config;
import com.gmail.dailyefforts.android.reciter.Word;

public class DBA extends SQLiteOpenHelper {
	private static final String TAG = DBA.class.getSimpleName();
	private static final String DATABASE_NAME = "wot.db";
	private static final int DATABASE_VERSION = 9;
	public static final String TABLE_WORD_LIST = "wordlist";

	public static final String TABLE_WORD_LIST_NCE1 = "wordlist_nce1";
	public static final String TABLE_WORD_LIST_NCE2 = "wordlist_nce2";
	public static final String TABLE_WORD_LIST_NCE3 = "wordlist_nce3";
	public static final String TABLE_WORD_LIST_NCE4 = "wordlist_nce4";
	public static final String TABLE_WORD_LIST_REFLETS1U = "wordlist_reflets1u";
	public static final String TABLE_WORD_LIST_LINGUISTICS_GLOSSARY = "linguistics_glossary";

	public static final String WORD_ID = "_id";
	public static final String WORD_WORD = "word";
	public static final String WORD_MEANING = "meaning";
	public static final String WORD_SAMPLE = "sample";
	public static final String WORD_TIMESTAMP = "timestamp";
	public static final String WORD_STAR = "star";
	public static final String WORD_OTHER = "other";
	public static final String WORD_MARKER = "marker";
	public static final String WORD_TYPE = "type";

	public static String getCreateWordListSql(final String tableName) {
		return "CREATE TABLE IF NOT EXISTS " + tableName + "("
				+ "_id INTEGER PRIMARY KEY AUTOINCREMENT, " + WORD_WORD
				+ " TEXT, " + WORD_MEANING + " TEXT, " + WORD_SAMPLE
				+ " TEXT, " + WORD_TIMESTAMP + " DATETIME, " + WORD_STAR
				+ " INTEGER DEFAULT 0, " + WORD_OTHER + " TEXT);";
	}

	public static final String TABLE_TEST_REPORT = "testreport";
	public static final String TABLE_TEST_REPORT_NCE1 = "testreport_nce1";
	public static final String TABLE_TEST_REPORT_NCE2 = "testreport_nce2";
	public static final String TABLE_TEST_REPORT_NCE3 = "testreport_nce3";
	public static final String TABLE_TEST_REPORT_NCE4 = "testreport_nce4";
	public static final String TABLE_TEST_REPORT_REFLETS1U = "testreport_reflets1u";
	public static final String TABLE_TEST_REPORT_INGUISTICS_GLOSSARY = "testreport_linguistics_glossary";

	public static final String TEST_REPORT_ID = "_id";
	public static final String TEST_TESTED_NUMBER = "tested_number";
	public static final String TEST_CORRECT_NUMBER = "correct_number";
	public static final String TEST_ACCURACY = "accuracy";
	public static final String TEST_DB_SIZE = "db_size";
	public static final String TEST_ELAPSED_TIME = "elapsed_time";
	public static final String TEST_TIMESTAMP = "time_stamp";
	public static final String TEST_WRONG_WORD_LIST = "wrong_word_list";
	public static final String TEST_OTHER = "other";

	public static String getCreateTestTableSql(final String tableName) {
		return "CREATE TABLE IF NOT EXISTS " + tableName + "(" + TEST_REPORT_ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + TEST_TESTED_NUMBER
				+ " INTEGER DEFAULT 0, " + TEST_CORRECT_NUMBER
				+ " INTEGER DEFAULT 0, " + TEST_ACCURACY
				+ " INTEGER DEFAULT 0, " + TEST_DB_SIZE
				+ " INTEGER DEFAULT 0, " + TEST_ELAPSED_TIME
				+ " INTEGER DEFAULT 0, " + TEST_TIMESTAMP + " DATETIME, "
				+ TEST_WRONG_WORD_LIST + " TEXT, " + TEST_OTHER + " TEXT);";
	}

	private static DBA dba = null;

	public static String CURRENT_WORD_TABLE = TABLE_WORD_LIST;
	public static String CURRENT_TEST_REPORT_TABLE = TABLE_TEST_REPORT;

	public void star(final String word) {

		if (word == null) {
			return;
		}

		Cursor cursor = query(CURRENT_WORD_TABLE, null, WORD_WORD + "=?",
				new String[] { word }, null, null, null);

		if (cursor != null && cursor.getCount() > 0) {
			try {
				if (cursor.moveToFirst()) {
					int star = cursor.getInt(cursor.getColumnIndex(WORD_STAR));
					if (Config.DEBUG) {
						long timeStamp = cursor.getInt(cursor
								.getColumnIndex(WORD_TIMESTAMP));
						Log.d(TAG, "star() star: " + star);
						Log.d(TAG, "star() timeStamp: " + timeStamp);
					}
					ContentValues values = new ContentValues();
					values.put(WORD_TIMESTAMP, System.currentTimeMillis());
					values.put(WORD_STAR, ++star);
					getWritableDatabase().update(CURRENT_WORD_TABLE, values,
							WORD_WORD + "=?", new String[] { word });
				}
			} finally {
				cursor.close();
			}
		}
	}

	public boolean isPass(int start, int end) {

		for (int i = start; i < end; i++) {
			String star = getPassValueByIdx(i);
			if (Config.DEBUG) {
				Log.d(TAG, "isPass() i: " + i + ", star: " + star);
			}

			if (!FLAG_PASS.equals(star)) {
				return false;
			}
		}

		return true;
	}

	public String FLAG_PASS = "p";

	private String getPassValueByIdx(int idx) {
		String star = null;
		Cursor cursor = query(CURRENT_WORD_TABLE, null, WORD_ID + "=?",
				new String[] { String.valueOf(idx) }, null, null, null);
		if (cursor != null && cursor.getCount() == 1) {
			if (cursor.moveToFirst()) {
				star = cursor.getString(cursor.getColumnIndex(WORD_OTHER));
			}
			cursor.close();
		} else {
			Log.e(TAG, "getStarByIdx: idx: " + idx + ", cursor: " + cursor);
		}
		return star;
	}

	public void setPast(final String word) {
		ContentValues values = new ContentValues();
		values.put(DBA.WORD_OTHER, FLAG_PASS);
		getWritableDatabase().update(CURRENT_WORD_TABLE, values,
				WORD_WORD + "=?", new String[] { word });
	}

	public int getStar(final String word) {

		int star = -1;

		if (word == null) {
			return -1;
		}
		Cursor cursor = query(CURRENT_WORD_TABLE, null, WORD_WORD + "=?",
				new String[] { word }, null, null, null);

		if (cursor != null && cursor.getCount() > 0) {
			if (cursor.moveToFirst()) {
				star = cursor.getInt(cursor.getColumnIndex(WORD_STAR));
			}
			cursor.close();
		}
		return star;

	}

	public boolean exist(final String word) {
		Cursor cursor = query(CURRENT_WORD_TABLE, null, WORD_WORD + "=?",
				new String[] { word }, null, null, null);
		boolean exist = false;
		if (cursor != null) {

			if (cursor.getCount() > 0) {
				exist = true;
			}

			cursor.close();
		}

		return exist;
	}

	public void unStar(final String word) {

		ContentValues values = new ContentValues();
		values.put(DBA.WORD_STAR, 0);
		getWritableDatabase().update(CURRENT_WORD_TABLE, values,
				WORD_WORD + "=?", new String[] { word });

	}

	public boolean exist(String word, String meaning) {
		boolean ret = false;
		Cursor cursor = query(CURRENT_WORD_TABLE, null, WORD_WORD + "=?"
				+ " AND " + WORD_MEANING + "=?",
				new String[] { word, meaning }, null, null, null);
		if (cursor != null) {
			if (cursor.getCount() > 0) {
				ret = true;
			}
			cursor.close();
		}

		if (Config.DEBUG) {
			Log.d(TAG, "exist()" + word + " - " + meaning + ", " + ret);
		}

		return ret;
	}

	private Word getWordByIdx(String table, int idx) {

		Cursor cursor = query(table, null, WORD_ID + "=?",
				new String[] { String.valueOf(idx) }, null, null, null);

		String word = "";
		String meaning = "";
		int id = -1;
		if (cursor != null) {
			if (cursor.moveToFirst()) {
				id = cursor.getInt(cursor.getColumnIndex(WORD_ID));

				word = cursor.getString(cursor.getColumnIndex(WORD_WORD));
				meaning = cursor.getString(cursor.getColumnIndex(WORD_MEANING));
			}
			cursor.close();
		}

		return new Word(id, word, meaning);

	}

	public Word getWordByIdx(int idx) {
		return getWordByIdx(CURRENT_WORD_TABLE, idx);
	}

	public void buildRandomTest(String table, int size) {
		SparseArray<Word> map = Word.getMap();
		map.clear();

		HashSet<Integer> set = new HashSet<Integer>();
		int i = 0;
		Random random = new Random();
		int dbSize = getCount();

		while (set.size() < size) {
			int id = random.nextInt(dbSize);
			if (id != 0) {
				set.add(id);
			}
		}

		Iterator<Integer> it = set.iterator();

		while (it.hasNext()) {
			int idx = it.next();
			Word word = getWordByIdx(table, idx);
			map.put(i++, word);
		}
	}

	public String getOneWordToReview() {
		Random random = new Random();

		String word = null;
		int size = buildMyWordBookTest(DBA.TABLE_WORD_LIST_REFLETS1U);
		int idx = -1;
		if (size > 0) {
			idx = random.nextInt(size);
		} else {
			buildRandomTest(DBA.TABLE_WORD_LIST_REFLETS1U, 1);
			idx = 0;
		}

		if (idx != -1) {
			try {
				word = Word.getMap().valueAt(idx).getWord();
			} catch (Exception e) {
				Log.e(TAG, "getOneWordToReview() failed.");
			}
		}
		return word;
	}

	public int buildMyWordBookTest(String table) {
		SparseArray<Word> map = Word.getMap();
		map.clear();

		Cursor cursor = query(table, null, WORD_STAR + ">?",
				new String[] { "0" }, null, null, null);

		int i = 0;
		String word = "";
		String meaning = "";
		int id = -1;
		if (cursor != null) {
			while (cursor.moveToNext()) {
				id = cursor.getInt(cursor.getColumnIndex(WORD_ID));
				word = cursor.getString(cursor.getColumnIndex(WORD_WORD));
				meaning = cursor.getString(cursor.getColumnIndex(WORD_MEANING));
				Word value = new Word(id, word, meaning);
				map.put(i++, value);
			}
			cursor.close();
		}
		return map.size();
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

	public void loadUnitWords(int start, int end) {
		String sql = "select " + DBA.WORD_ID + ", " + DBA.WORD_WORD + ", "
				+ DBA.WORD_MEANING + " from " + DBA.CURRENT_WORD_TABLE
				+ " where " + DBA.WORD_ID + ">=? AND " + DBA.WORD_ID + "<=?;";

		Cursor cursor = dba.rawQuery(sql, new String[] { String.valueOf(start),
				String.valueOf(end) });

		if (Config.DEBUG) {
			Log.d(TAG, "onClick() start: " + start + ", end: " + end);
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
				if (Config.DEBUG) {
					Log.d(TAG, String.format("id: %d, word: %s, meanning: %s",
							id, word, meanning));
				}
				Word newWord = new Word(id, word, meanning);
				map.put(idx++, newWord);
			}
			cursor.close();
		}
	}

	public int getCount() {
		Cursor cursor = query(CURRENT_WORD_TABLE, null, null, null, null, null,
				null);
		int count = 0;
		if (cursor != null) {
			count = cursor.getCount();
			cursor.close();
		}
		return count;
	}

	public int size() {
		return getCount();
	}

	public long insert(String table, String nullColumnHack, ContentValues values) {
		long result = -1L;
		try {
			result = getWritableDatabase()
					.insert(table, nullColumnHack, values);

			if (Config.DEBUG) {
				Log.d(TAG, "table: " + table + ", insert() " + result);
			}
		} catch (SQLiteException e) {
			// let go.
			Log.e(TAG, e.getMessage());
		}
		return result;
	}

	public void update(String table, String word, ContentValues values) {
		try {
			if (Config.DEBUG) {
				Log.d(TAG, "table: " + table + ", update(1) " + word + " - "
						+ values.getAsString(WORD_MEANING));
			}
			getWritableDatabase().update(table, values, WORD_WORD + "=?",
					new String[] { word });
		} catch (Exception e) {
			// let go.
			Log.e(TAG, e.getMessage());
		}
	}

	/*
	 * public void update(String table, String word, String meaning) { try {
	 * ContentValues values = new ContentValues(); values.put(WORD_MEANING,
	 * meaning); if (Config.DEBUG) { Log.d(TAG, "table: " + table +
	 * ", update() " + word + " - " + meaning); }
	 * getWritableDatabase().update(table, values, WORD_WORD + "=?", new
	 * String[] { word }); } catch (Exception e) { // let go. Log.e(TAG,
	 * e.getMessage()); } }
	 */

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
		db.execSQL(getCreateWordListSql(TABLE_WORD_LIST));
		db.execSQL(getCreateWordListSql(TABLE_WORD_LIST_NCE1));
		db.execSQL(getCreateWordListSql(TABLE_WORD_LIST_NCE2));
		db.execSQL(getCreateWordListSql(TABLE_WORD_LIST_NCE3));
		db.execSQL(getCreateWordListSql(TABLE_WORD_LIST_NCE4));
		db.execSQL(getCreateWordListSql(TABLE_WORD_LIST_REFLETS1U));
		db.execSQL(getCreateWordListSql(TABLE_WORD_LIST_LINGUISTICS_GLOSSARY));

		db.execSQL(getCreateTestTableSql(TABLE_TEST_REPORT));
		db.execSQL(getCreateTestTableSql(TABLE_TEST_REPORT_NCE1));
		db.execSQL(getCreateTestTableSql(TABLE_TEST_REPORT_NCE2));
		db.execSQL(getCreateTestTableSql(TABLE_TEST_REPORT_NCE3));
		db.execSQL(getCreateTestTableSql(TABLE_TEST_REPORT_NCE4));
		db.execSQL(getCreateTestTableSql(TABLE_TEST_REPORT_REFLETS1U));
		db.execSQL(getCreateTestTableSql(TABLE_TEST_REPORT_INGUISTICS_GLOSSARY));
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (newVersion == 9) {
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_WORD_LIST_NCE3);
			onCreate(db);
		}
	}

}
