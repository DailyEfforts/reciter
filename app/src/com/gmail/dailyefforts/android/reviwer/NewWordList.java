package com.gmail.dailyefforts.android.reviwer;

import android.app.ListFragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.gmail.dailyefforts.android.reviwer.db.DBA;
import com.gmail.dailyefforts.android.reviwer.db.DbProvider;

public class NewWordList extends ListFragment implements
		LoaderCallbacks<Cursor> {
	private static final String TAG = NewWordList.class.getSimpleName();
	private Cursor mCursor = null;
	private SimpleCursorAdapter mAdapter;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		setEmptyText(getActivity().getText(R.string.tip_empty));

		mAdapter = new SimpleCursorAdapter(getActivity(),
				android.R.layout.simple_list_item_2, mCursor, new String[] {
						DBA.COLUMN_WORD, DBA.COLUMN_TIMESTAMP }, new int[] {
						android.R.id.text1, android.R.id.text2 },
				CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

		setListAdapter(mAdapter);
		setListShown(false);

		getLoaderManager().initLoader(0, null, this);
	}

	@Override
	public void onResume() {
		super.onResume();
		getLoaderManager().restartLoader(0, null, this);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		// Intent intent = new Intent(getActivity(), ImageViewer.class);
		if (mCursor != null) {
			/*
			 * Images.update(mCursor); Intent intent = new Intent(getActivity(),
			 * ImgPager.class); intent.putExtra(Images.POSITION, position);
			 * startActivity(intent);
			 */
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
		mAdapter.swapCursor(cursor);
		if (isResumed()) {
			setListShown(true);
		} else {
			setListShownNoAnimation(true);
		}

	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		mAdapter.swapCursor(null);
	}
}