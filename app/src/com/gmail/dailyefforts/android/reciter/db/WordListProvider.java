package com.gmail.dailyefforts.android.reciter.db;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import com.gmail.dailyefforts.android.reciter.Config;

public class WordListProvider extends ContentProvider {
	private DBA mDba;
	private static final String AUTHORITY = "com.gmail.dailyefforts.android.reciter.wordlist";
	private static final String BASE_PATH = DBA.CURRENT_WORD_TABLE; // wordlist
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
			+ "/" + BASE_PATH);
	private static final String TAG = WordListProvider.class.getSimpleName();

	@Override
	public boolean onCreate() {
		mDba = DBA.getInstance(getContext().getApplicationContext());
		if (Config.DEBUG) {
			Log.d(TAG, "onCreate()" + (mDba == null ? "null" : mDba.getCount()));
		}
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		// Uisng SQLiteQueryBuilder instead of query() method
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

		// Set the table
		queryBuilder.setTables(DBA.CURRENT_WORD_TABLE);

		SQLiteDatabase db = mDba.getWritableDatabase();
		Cursor cursor = queryBuilder.query(db, projection, selection,
				selectionArgs, null, null, sortOrder);
		// Make sure that potential listeners are getting notified
		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		return cursor;
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		SQLiteDatabase db = mDba.getWritableDatabase();
		return db.delete(DBA.CURRENT_WORD_TABLE, selection, selectionArgs);
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		SQLiteDatabase db = mDba.getWritableDatabase();
		return db.update(DBA.CURRENT_WORD_TABLE, values, selection,
				selectionArgs);
	}

}
