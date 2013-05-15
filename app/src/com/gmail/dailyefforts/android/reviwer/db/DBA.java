package com.gmail.dailyefforts.android.reviwer.db;

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

import com.gmail.dailyefforts.android.reviwer.debug.Debuger;
import com.gmail.dailyefforts.android.reviwer.word.Word;

public class DBA extends SQLiteOpenHelper {
	private static final String TAG = DBA.class.getSimpleName();
	private static final String DATABASE_NAME = "wot.db";
	private static final int DATABASE_VERSION = 5;
	public static final String TABLE_WORD_LIST = "wordlist";

	public static final String TABLE_WORD_LIST_NCE1 = "wordlist_nce1";
	public static final String TABLE_WORD_LIST_NCE2 = "wordlist_nce2";
	public static final String TABLE_WORD_LIST_NCE3 = "wordlist_nce3";
	public static final String TABLE_WORD_LIST_NCE4 = "wordlist_nce4";

	public static final String WORD_ID = "_id";
	public static final String WORD_WORD = "word";
	public static final String WORD_MEANING = "meaning";
	public static final String WORD_SAMPLE = "sample";
	public static final String WORD_TIMESTAMP = "timestamp";
	public static final String WORD_STAR = "star";
	public static final String WORD_OTHER = "other";

	private static final String CREATE_TABLE_WORD_LIST = "CREATE TABLE IF NOT EXISTS "
			+ TABLE_WORD_LIST
			+ "("
			+ "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ WORD_WORD
			+ " TEXT UNIQUE, "
			+ WORD_MEANING
			+ " TEXT, "
			+ WORD_SAMPLE
			+ " TEXT, "
			+ WORD_TIMESTAMP
			+ " DATETIME, "
			+ WORD_STAR + " INTEGER DEFAULT 0, " + WORD_OTHER + " TEXT);";

	private static final String CREATE_TABLE_WORD_LIST_NCE1 = "CREATE TABLE IF NOT EXISTS "
			+ TABLE_WORD_LIST_NCE1
			+ "("
			+ "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ WORD_WORD
			+ " TEXT UNIQUE, "
			+ WORD_MEANING
			+ " TEXT, "
			+ WORD_SAMPLE
			+ " TEXT, "
			+ WORD_TIMESTAMP
			+ " DATETIME, "
			+ WORD_STAR
			+ " INTEGER DEFAULT 0, " + WORD_OTHER + " TEXT);";

	private static final String CREATE_TABLE_WORD_LIST_NCE2 = "CREATE TABLE IF NOT EXISTS "
			+ TABLE_WORD_LIST_NCE2
			+ "("
			+ "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ WORD_WORD
			+ " TEXT UNIQUE, "
			+ WORD_MEANING
			+ " TEXT, "
			+ WORD_SAMPLE
			+ " TEXT, "
			+ WORD_TIMESTAMP
			+ " DATETIME, "
			+ WORD_STAR
			+ " INTEGER DEFAULT 0, " + WORD_OTHER + " TEXT);";

	private static final String CREATE_TABLE_WORD_LIST_NCE3 = "CREATE TABLE IF NOT EXISTS "
			+ TABLE_WORD_LIST_NCE3
			+ "("
			+ "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ WORD_WORD
			+ " TEXT UNIQUE, "
			+ WORD_MEANING
			+ " TEXT, "
			+ WORD_SAMPLE
			+ " TEXT, "
			+ WORD_TIMESTAMP
			+ " DATETIME, "
			+ WORD_STAR
			+ " INTEGER DEFAULT 0, " + WORD_OTHER + " TEXT);";

	private static final String CREATE_TABLE_WORD_LIST_NCE4 = "CREATE TABLE IF NOT EXISTS "
			+ TABLE_WORD_LIST_NCE4
			+ "("
			+ "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ WORD_WORD
			+ " TEXT UNIQUE, "
			+ WORD_MEANING
			+ " TEXT, "
			+ WORD_SAMPLE
			+ " TEXT, "
			+ WORD_TIMESTAMP
			+ " DATETIME, "
			+ WORD_STAR
			+ " INTEGER DEFAULT 0, " + WORD_OTHER + " TEXT);";

	public static final String TABLE_TEST_REPORT = "testreport";
	public static final String TABLE_TEST_REPORT_NCE1 = "testreport_nce1";
	public static final String TABLE_TEST_REPORT_NCE2 = "testreport_nce2";
	public static final String TABLE_TEST_REPORT_NCE3 = "testreport_nce3";
	public static final String TABLE_TEST_REPORT_NCE4 = "testreport_nce4";

	public static final String TEST_REPORT_ID = "_id";
	public static final String TEST_TESTED_NUMBER = "tested_number";
	public static final String TEST_CORRECT_NUMBER = "correct_number";
	public static final String TEST_ACCURACY = "accuracy";
	public static final String TEST_DB_SIZE = "db_size";
	public static final String TEST_ELAPSED_TIME = "elapsed_time";
	public static final String TEST_TIMESTAMP = "time_stamp";
	public static final String TEST_WRONG_WORD_LIST = "wrong_word_list";
	public static final String TEST_OTHER = "other";

	private static final String CREATE_TABLE_TEST_REPORT = "CREATE TABLE IF NOT EXISTS "
			+ TABLE_TEST_REPORT
			+ "("
			+ TEST_REPORT_ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ TEST_TESTED_NUMBER
			+ " INTEGER DEFAULT 0, "
			+ TEST_CORRECT_NUMBER
			+ " INTEGER DEFAULT 0, "
			+ TEST_ACCURACY
			+ " INTEGER DEFAULT 0, "
			+ TEST_DB_SIZE
			+ " INTEGER DEFAULT 0, "
			+ TEST_ELAPSED_TIME
			+ " INTEGER DEFAULT 0, "
			+ TEST_TIMESTAMP
			+ " DATETIME, "
			+ TEST_WRONG_WORD_LIST + " TEXT, " + TEST_OTHER + " TEXT);";

	private static final String CREATE_TABLE_TEST_REPORT_NCE1 = "CREATE TABLE IF NOT EXISTS "
			+ TABLE_TEST_REPORT_NCE1
			+ "("
			+ TEST_REPORT_ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ TEST_TESTED_NUMBER
			+ " INTEGER DEFAULT 0, "
			+ TEST_CORRECT_NUMBER
			+ " INTEGER DEFAULT 0, "
			+ TEST_ACCURACY
			+ " INTEGER DEFAULT 0, "
			+ TEST_DB_SIZE
			+ " INTEGER DEFAULT 0, "
			+ TEST_ELAPSED_TIME
			+ " INTEGER DEFAULT 0, "
			+ TEST_TIMESTAMP
			+ " DATETIME, "
			+ TEST_WRONG_WORD_LIST + " TEXT, " + TEST_OTHER + " TEXT);";

	private static final String CREATE_TABLE_TEST_REPORT_NCE2 = "CREATE TABLE IF NOT EXISTS "
			+ TABLE_TEST_REPORT_NCE2
			+ "("
			+ TEST_REPORT_ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ TEST_TESTED_NUMBER
			+ " INTEGER DEFAULT 0, "
			+ TEST_CORRECT_NUMBER
			+ " INTEGER DEFAULT 0, "
			+ TEST_ACCURACY
			+ " INTEGER DEFAULT 0, "
			+ TEST_DB_SIZE
			+ " INTEGER DEFAULT 0, "
			+ TEST_ELAPSED_TIME
			+ " INTEGER DEFAULT 0, "
			+ TEST_TIMESTAMP
			+ " DATETIME, "
			+ TEST_WRONG_WORD_LIST + " TEXT, " + TEST_OTHER + " TEXT);";

	private static final String CREATE_TABLE_TEST_REPORT_NCE3 = "CREATE TABLE IF NOT EXISTS "
			+ TABLE_TEST_REPORT_NCE3
			+ "("
			+ TEST_REPORT_ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ TEST_TESTED_NUMBER
			+ " INTEGER DEFAULT 0, "
			+ TEST_CORRECT_NUMBER
			+ " INTEGER DEFAULT 0, "
			+ TEST_ACCURACY
			+ " INTEGER DEFAULT 0, "
			+ TEST_DB_SIZE
			+ " INTEGER DEFAULT 0, "
			+ TEST_ELAPSED_TIME
			+ " INTEGER DEFAULT 0, "
			+ TEST_TIMESTAMP
			+ " DATETIME, "
			+ TEST_WRONG_WORD_LIST + " TEXT, " + TEST_OTHER + " TEXT);";

	private static final String CREATE_TABLE_TEST_REPORT_NCE4 = "CREATE TABLE IF NOT EXISTS "
			+ TABLE_TEST_REPORT_NCE4
			+ "("
			+ TEST_REPORT_ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ TEST_TESTED_NUMBER
			+ " INTEGER DEFAULT 0, "
			+ TEST_CORRECT_NUMBER
			+ " INTEGER DEFAULT 0, "
			+ TEST_ACCURACY
			+ " INTEGER DEFAULT 0, "
			+ TEST_DB_SIZE
			+ " INTEGER DEFAULT 0, "
			+ TEST_ELAPSED_TIME
			+ " INTEGER DEFAULT 0, "
			+ TEST_TIMESTAMP
			+ " DATETIME, "
			+ TEST_WRONG_WORD_LIST + " TEXT, " + TEST_OTHER + " TEXT);";

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
					if (Debuger.DEBUG) {
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

	public Word getWordByIdx(int idx) {
		Cursor cursor = query(CURRENT_WORD_TABLE, null, WORD_ID + "=?",
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

	public void buildRandomTest(final int size) {
		SparseArray<Word> map = Word.getMap();
		map.clear();

		HashSet<Integer> set = new HashSet<Integer>();
		int i = 0;
		Random random = new Random();
		int dbSize = getCount();

		while (set.size() < size) {
			set.add(random.nextInt(dbSize));
		}

		Iterator<Integer> it = set.iterator();

		while (it.hasNext()) {
			int idx = it.next();
			Word word = getWordByIdx(idx);
			map.put(i++, word);
		}
	}

	public void buildMyWordBookTest() {
		SparseArray<Word> map = Word.getMap();
		map.clear();

		Cursor cursor = query(CURRENT_WORD_TABLE, null, WORD_STAR + ">?",
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

		if (Debuger.DEBUG) {
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
				if (Debuger.DEBUG) {
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

			if (Debuger.DEBUG) {
				Log.d(TAG, "table: " + table + ", insert() " + result);
			}
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
		db.execSQL(CREATE_TABLE_WORD_LIST);
		db.execSQL(CREATE_TABLE_WORD_LIST_NCE1);
		db.execSQL(CREATE_TABLE_WORD_LIST_NCE2);
		db.execSQL(CREATE_TABLE_WORD_LIST_NCE3);
		db.execSQL(CREATE_TABLE_WORD_LIST_NCE4);

		db.execSQL(CREATE_TABLE_TEST_REPORT);
		db.execSQL(CREATE_TABLE_TEST_REPORT_NCE1);
		db.execSQL(CREATE_TABLE_TEST_REPORT_NCE2);
		db.execSQL(CREATE_TABLE_TEST_REPORT_NCE3);
		db.execSQL(CREATE_TABLE_TEST_REPORT_NCE4);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// db.execSQL("DROP TABLE IF EXISTS " + TABLE_TEST_REPORT);
		onCreate(db);
	}

}
