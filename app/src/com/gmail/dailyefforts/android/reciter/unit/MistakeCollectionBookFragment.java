package com.gmail.dailyefforts.android.reciter.unit;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.format.DateUtils;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gmail.dailyefforts.android.reciter.Config;
import com.gmail.dailyefforts.android.reviwer.R;
import com.gmail.dailyefforts.android.reciter.db.DBA;
import com.gmail.dailyefforts.android.reciter.db.WordListProvider;

public class MistakeCollectionBookFragment extends ListFragment implements
		LoaderCallbacks<Cursor> {
	private static final String TAG = MistakeCollectionBookFragment.class
			.getSimpleName();
	private Cursor mCursor = null;
	private ListAdapter mLisAdapter;
	private LayoutInflater mLayoutInflater;
	private DBA dba;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		setEmptyText(getActivity().getText(R.string.tip_word_book_is_empty));

		mLayoutInflater = getActivity().getLayoutInflater();

		setListShown(false);

		dba = DBA.getInstance(getActivity());

		ListView lv = getListView();
		lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
		lv.setMultiChoiceModeListener(new ModeCallback());

		getLoaderManager().initLoader(0, null, this);
	}

	private class ModeCallback implements ListView.MultiChoiceModeListener {

		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			MenuInflater inflater = getActivity().getMenuInflater();
			inflater.inflate(R.menu.word_list_select, menu);
			mode.setTitle(R.string.tip_select_items_to_unstar);
			return true;
		}

		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return true;
		}

		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			switch (item.getItemId()) {
			case R.id.unstar:
				ListView listView = getListView();
				if (listView != null) {
					SparseBooleanArray arr = listView.getCheckedItemPositions();
					if (arr != null && arr.size() > 0) {
						for (int i = 0; i < arr.size(); i++) {
							if (Config.DEBUG) {
								Log.d(TAG,
										"onActionItemClicked() " + arr.keyAt(i)
												+ " - " + arr.valueAt(i));
							}

							int position = arr.keyAt(i);

							if (mCursor != null) {
								if (mCursor.moveToPosition(position)) {
									String word = mCursor.getString(mCursor
											.getColumnIndex(DBA.WORD_WORD));
									if (Config.DEBUG) {
										Log.d(TAG,
												"onActionItemClicked() position: "
														+ position + ", word: "
														+ word);
									}

									if (dba != null) {
										dba.unStar(word);
									}
								}
							}

						}
					}

				}
				getLoaderManager().restartLoader(0, null,
						MistakeCollectionBookFragment.this);
				mode.finish();
				break;
			default:
				Toast.makeText(getActivity(), "Clicked " + item.getTitle(),
						Toast.LENGTH_SHORT).show();
				break;
			}
			return true;
		}

		public void onDestroyActionMode(ActionMode mode) {
		}

		public void onItemCheckedStateChanged(ActionMode mode, int position,
				long id, boolean checked) {
		}

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
			String word = null;
			long time = 0L;

			ViewHolder viewHolder = null;

			if (mCursor != null && mCursor.moveToPosition(position)) {
				word = mCursor.getString(mCursor.getColumnIndex(DBA.WORD_WORD));
				time = mCursor.getLong(mCursor
						.getColumnIndex(DBA.WORD_TIMESTAMP));
				if (Config.DEBUG) {
					Log.d(TAG,
							"getView() "
									+ String.format("%d - %s", position, word));
				}
			}

			if (convertView == null) {

				convertView = mLayoutInflater.inflate(R.layout.book_item, null);

				viewHolder = new ViewHolder();
				viewHolder.word = (TextView) convertView
						.findViewById(R.id.tv_book_item_word);
				viewHolder.timestamp = (TextView) convertView
						.findViewById(R.id.tv_book_item_timestamp);
				convertView.setTag(viewHolder);

			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

			if (viewHolder != null) {
				viewHolder.word.setText(word);
				viewHolder.timestamp.setText(DateUtils
						.getRelativeTimeSpanString(time));
			}

			return convertView;
		}

	}

	private static class ViewHolder {
		TextView word;
		TextView timestamp;
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
				Toast.makeText(
						getActivity(),
						mCursor.getString(mCursor
								.getColumnIndex(DBA.WORD_MEANING)),
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
		Uri uri = WordListProvider.CONTENT_URI;
		String[] projection = null;
		String selection = DBA.WORD_STAR + ">?";
		String[] selectionArgs = new String[] { "0" };
		String sortOrder = DBA.WORD_TIMESTAMP + " desc ";
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
