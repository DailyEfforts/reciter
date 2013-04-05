package com.gmail.dailyefforts.android.reviwer.book;

import android.R.array;
import android.app.Activity;
import android.app.ListFragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.CheckBox;
import android.widget.CheckedTextView;
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

public class WordBookActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Display the fragment as the main content.
		getFragmentManager().beginTransaction()
				.replace(android.R.id.content, new WordBookList()).commit();

	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		getActionBar().setSubtitle("Long press to start selection");
	}

	public static class WordBookList extends ListFragment implements
			LoaderCallbacks<Cursor> {
		private static final String TAG = WordBookList.class.getSimpleName();
		private Cursor mCursor = null;
		private SimpleCursorAdapter mAdapter;
		private ListAdapter mLisAdapter;
		private LayoutInflater mLayoutInflater;
		private DBA dba;

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
				setSubtitle(mode);
				return true;
			}

			public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
				return true;
			}

			public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
				switch (item.getItemId()) {
				case R.id.unstar:
					String tip = String.format(String.valueOf(getResources()
							.getText(R.string.tip_unstar_items)), getListView()
							.getCheckedItemCount());
					Toast.makeText(getActivity(), tip, Toast.LENGTH_SHORT)
							.show();
					ListView listView = getListView();
					if (listView != null) {
						SparseBooleanArray arr = listView
								.getCheckedItemPositions();
						if (arr != null && arr.size() > 0) {
							getLoaderManager().restartLoader(0, null,
									WordBookList.this);
							for (int i = 0; i < arr.size(); i++) {
								if (Debuger.DEBUG) {
									Log.d(TAG,
											"onActionItemClicked() "
													+ arr.keyAt(i) + " - "
													+ arr.valueAt(i));
								}

								int position = arr.keyAt(i);

								if (mCursor != null) {
									if (mCursor.moveToPosition(position)) {
										String word = mCursor
												.getString(mCursor
														.getColumnIndex(DBA.COLUMN_WORD));
										if (Debuger.DEBUG) {
											Log.d(TAG,
													"onActionItemClicked() position: "
															+ position
															+ ", word: " + word);
										}

										if (dba != null) {
											dba.resetStar(word);
										}
									}
								}

							}
						}

					}
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
				ListView listView = getListView();
				if (listView != null) {
					SparseBooleanArray arr = listView.getCheckedItemPositions();
					for (int i = 0; i < arr.size(); i++) {
						if (arr != null && arr.size() > 0) {
							int position = arr.keyAt(i);
							View view = listView.getChildAt(position);
							if (view != null) {
								CheckBox cb = (CheckBox) view
										.findViewById(R.id.cb_book_item);
								if (cb != null) {
									cb.setChecked(false);
								}
							}
						}
					}
				}
			}

			public void onItemCheckedStateChanged(ActionMode mode,
					int position, long id, boolean checked) {
				View v = getListView().getChildAt(position);
				if (v != null) {
					CheckBox checkBox = (CheckBox) v
							.findViewById(R.id.cb_book_item);
					TextView tv = (TextView) v
							.findViewById(R.id.tv_book_item_word);
					if (checkBox != null) {
						checkBox.setChecked(checked);
					}

					if (Debuger.DEBUG) {
						Log.d(TAG, "onItemCheckedStateChanged() position: "
								+ position);
						Log.d(TAG,
								"onItemCheckedStateChanged() word: "
										+ tv.getText());
					}
				}

				setSubtitle(mode);
			}

			private void setSubtitle(ActionMode mode) {
				final int checkedCount = getListView().getCheckedItemCount();
				switch (checkedCount) {
				case 0:
					mode.setSubtitle(null);
					break;
				case 1:
					mode.setSubtitle(R.string.tip_1_item_is_selected);
					break;
				default:
					String tip = String.format(
							String.valueOf(getResources().getText(
									R.string.tip_items_are_selected)),
							checkedCount);
					mode.setSubtitle(tip);
					break;
				}
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
				View view = null;
				String word = null;
				long time = 0L;
				if (mCursor != null && mCursor.moveToPosition(position)) {
					word = mCursor.getString(mCursor
							.getColumnIndex(DBA.COLUMN_WORD));
					time = mCursor.getLong(mCursor
							.getColumnIndex(DBA.COLUMN_TIMESTAMP));
					if (Debuger.DEBUG) {
						Log.d(TAG,
								"getView() "
										+ String.format("%d - %s", position,
												word));
					}
				}
				if (convertView == null) {

					view = mLayoutInflater.inflate(R.layout.book_item, null);

				} else {
					view = convertView;
				}
				if (view != null) {
					TextView tv1 = (TextView) view
							.findViewById(R.id.tv_book_item_word);
					TextView tv2 = (TextView) view
							.findViewById(R.id.tv_book_item_timestamp);
					if (tv1 != null && tv2 != null) {
						tv1.setText(word);
						tv2.setText(DateUtils.getRelativeTimeSpanString(time));
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
			CursorLoader loader = new CursorLoader(getActivity(), uri,
					projection, selection, selectionArgs, sortOrder);
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
}
