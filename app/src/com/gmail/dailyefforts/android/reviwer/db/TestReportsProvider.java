package com.gmail.dailyefforts.android.reviwer.db;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import com.gmail.dailyefforts.android.reviwer.debug.Debuger;

public class TestReportsProvider extends ContentProvider {
	private DBA dba;
	private static final String AUTHORITY = "com.gmail.dailyefforts.android.reviwer.testreport";
	private static final String BASE_PATH = DBA.CURRENT_TEST_REPORT_TABLE; // Test report
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
			+ "/" + BASE_PATH);
	private static final String TAG = TestReportsProvider.class.getSimpleName();

	@Override
	public boolean onCreate() {
		dba = DBA.getInstance(getContext().getApplicationContext());
		if (Debuger.DEBUG) {
			Log.d(TAG, "onCreate()" + (dba == null ? "null" : dba.getCount()));
		}
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		// Use SQLiteQueryBuilder instead of query() method
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

		// Set the table
		queryBuilder.setTables(DBA.CURRENT_TEST_REPORT_TABLE);

		SQLiteDatabase db = dba.getWritableDatabase();
		Cursor cursor = queryBuilder.query(db, projection, selection,
				selectionArgs, null, null, sortOrder);
		// Make sure that potential listeners are getting notified
		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		return cursor;
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		return null;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		SQLiteDatabase db = dba.getWritableDatabase();
		return db.delete(DBA.CURRENT_TEST_REPORT_TABLE, selection,
				selectionArgs);
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		SQLiteDatabase db = dba.getWritableDatabase();
		return db.update(DBA.CURRENT_TEST_REPORT_TABLE, values, selection,
				selectionArgs);
	}

}
