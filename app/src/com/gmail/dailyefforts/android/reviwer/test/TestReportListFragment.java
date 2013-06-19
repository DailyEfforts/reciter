package com.gmail.dailyefforts.android.reviwer.test;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.gmail.dailyefforts.android.reviwer.Config;
import com.gmail.dailyefforts.android.reviwer.R;
import com.gmail.dailyefforts.android.reviwer.db.DBA;
import com.gmail.dailyefforts.android.reviwer.db.TestReportsProvider;

public class TestReportListFragment extends ListFragment implements
		LoaderCallbacks<Cursor> {
	private static final String TAG = TestReportListFragment.class.getSimpleName();
	private Cursor mCursor = null;
	private ListAdapter mLisAdapter;
	private LayoutInflater mLayoutInflater;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		setEmptyText(getActivity().getText(R.string.test_record_is_empty));

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
			String words = null;
			long time = 0L;
			int accuracy = -1;
			if (mCursor != null && mCursor.moveToPosition(position)) {
				words = mCursor.getString(mCursor
						.getColumnIndex(DBA.TEST_WRONG_WORD_LIST));
				time = mCursor.getLong(mCursor
						.getColumnIndex(DBA.TEST_TIMESTAMP));

				accuracy = mCursor.getInt(mCursor
						.getColumnIndex(DBA.TEST_ACCURACY));

				if (Config.DEBUG) {
					Log.d(TAG,
							"getView() "
									+ String.format("%d - %s", position, words));
				}
			}
			if (convertView == null) {
				view = mLayoutInflater.inflate(R.layout.test_report_item, null);
			} else {
				view = convertView;
			}
			if (view != null) {
				TextView tv1 = (TextView) view
						.findViewById(R.id.tv_test_report_item_time);
				TextView tv2 = (TextView) view
						.findViewById(R.id.tv_test_report_item_wrong_word_list);
				if (tv1 != null && tv2 != null) {
					tv1.setText(String.format("%s (%d%%)",
							DateUtils.getRelativeTimeSpanString(time), accuracy));
					if (accuracy == 100) {
						tv2.setVisibility(View.GONE);
					} else {
						if (words != null && words.length() > 2) {
							tv2.setText(words.subSequence(1, words.length() - 1));
						} else {
							tv2.setVisibility(View.GONE);
						}
					}
				}
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
			if (Config.DEBUG) {
				Log.d(TAG, "onListItemClick()");
			}

			if (mCursor.moveToPosition(position)) {

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
		Uri uri = TestReportsProvider.CONTENT_URI;
		String[] projection = null;
		String selection = null;
		String[] selectionArgs = null;
		String sortOrder = DBA.TEST_TIMESTAMP + " DESC ";
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
