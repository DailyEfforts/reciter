package com.gmail.dailyefforts.android.reviwer.book;

import android.app.ListFragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.util.TimeUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CursorAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.gmail.dailyefforts.android.reviwer.R;
import com.gmail.dailyefforts.android.reviwer.db.DBA;
import com.gmail.dailyefforts.android.reviwer.db.DbProvider;
import com.gmail.dailyefforts.android.reviwer.debug.Debuger;

public class WordBookList extends ListFragment implements
		LoaderCallbacks<Cursor> {
	private static final String TAG = WordBookList.class.getSimpleName();
	private Cursor mCursor = null;
	private SimpleCursorAdapter mAdapter;
	private ListAdapter mLisAdapter;
	private LayoutInflater mLayoutInflater;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		setEmptyText(getActivity().getText(R.string.tip_empty));

		mAdapter = new SimpleCursorAdapter(getActivity(),
				android.R.layout.simple_list_item_2, mCursor, new String[] {
						DBA.COLUMN_WORD, DBA.COLUMN_TIMESTAMP }, new int[] {
						android.R.id.text1, android.R.id.text2 },
				CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

		mLayoutInflater = getActivity().getLayoutInflater();

		setListShown(false);

		getLoaderManager().initLoader(0, null, this);
	}

	private class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mCursor == null ? 0 : mCursor.getCount();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = null;
			if (convertView == null) {
				String word = null;
				long time = 0L;
				if (mCursor != null && mCursor.moveToPosition(position)) {
					word = mCursor.getString(mCursor
							.getColumnIndex(DBA.COLUMN_WORD));
					time = mCursor.getLong(mCursor
							.getColumnIndex(DBA.COLUMN_TIMESTAMP));
				}

				view = mLayoutInflater.inflate(R.layout.book_item, null);

				TextView tv1 = (TextView) view
						.findViewById(R.id.tv_book_item_word);
				TextView tv2 = (TextView) view
						.findViewById(R.id.tv_book_item_timestamp);
				if (tv1 != null && tv2 != null) {
					tv1.setText(word);
					tv2.setText(DateUtils.getRelativeTimeSpanString(time));
				}
			} else {
				view = convertView;
			}
			
			if ((position & 0x01) == 0) {
				view.setAlpha(128);
			}
			return view;
		}

	}

	@Override
	public void onResume() {
		super.onResume();
		getLoaderManager().restartLoader(0, null, this);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		if (mCursor != null) {
			if (Debuger.DEBUG) {
				Log.d(TAG, "onListItemClick()");
			}

			if (mCursor.moveToPosition(position)) {
				System.out.println("WordBookList.onListItemClick() "
						+ mCursor.getString(mCursor
								.getColumnIndex(DBA.COLUMN_WORD)));
				System.out.println("WordBookList.onListItemClick() "
						+ mCursor.getString(mCursor
								.getColumnIndex(DBA.COLUMN_MEANING)));
				System.out.println("WordBookList.onListItemClick() "
						+ mCursor.getString(mCursor
								.getColumnIndex(DBA.COLUMN_TIMESTAMP)));
				String timeStr = mCursor.getString(mCursor
						.getColumnIndex(DBA.COLUMN_TIMESTAMP));
				Toast.makeText(
						getActivity(),
						mCursor.getString(mCursor
								.getColumnIndex(DBA.COLUMN_MEANING)),
						Toast.LENGTH_SHORT).show();
			}

		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mCursor != null) {
			mCursor.close();
		}
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		Uri uri = DbProvider.CONTENT_URI;
		String[] projection = null;
		String selection = DBA.COLUMN_STAR + ">?";
		String[] selectionArgs = new String[] { "0" };
		String sortOrder = DBA.COLUMN_TIMESTAMP + " desc ";
		CursorLoader loader = new CursorLoader(getActivity(), uri, projection,
				selection, selectionArgs, sortOrder);
		return loader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		this.mCursor = cursor;
		if (isResumed()) {
			mLisAdapter = new MyAdapter();
			setListAdapter(mLisAdapter);
			setListShown(true);
		} else {
			setListShownNoAnimation(true);
		}

	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		if (mCursor != null) {
			mCursor.close();
		}
	}
}