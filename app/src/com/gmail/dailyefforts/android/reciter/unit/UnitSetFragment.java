package com.gmail.dailyefforts.android.reciter.unit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.ContentValues;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gmail.dailyefforts.android.reciter.Config;
import com.gmail.dailyefforts.android.reciter.db.DBA;
import com.gmail.dailyefforts.android.reciter.test.drag.DragAndDropTestActivity;
import com.gmail.dailyefforts.android.reviwer.R;

public class UnitSetFragment extends Fragment implements OnItemClickListener {
	private static final String TAG = UnitSetFragment.class.getSimpleName();
	private RelativeLayout loadingTip;
	private GridView mGridView;
	private DBA dba;
	private Animation mAnimation;

	private static int UNIT = Config.DEFAULT_WORD_COUNT_OF_ONE_UNIT;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_unit_grid, container,
				false);
		loadingTip = (RelativeLayout) view.findViewById(R.id.rl_loading);

		dba = DBA.getInstance(getActivity());

		mGridView = (GridView) view.findViewById(R.id.gv_unit);

		mAnimation = AnimationUtils
				.loadAnimation(getActivity(), R.anim.zoom_in);

		mGridView.setOnItemClickListener(this);

		new LoadWordsList().execute();
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		if (mGridView != null) {
			mGridView.invalidateViews();
			mGridView.startAnimation(mAnimation);
		}
	}

	private UnitAdapter mUnitAdapter;

	private class LoadWordsList extends AsyncTask<Void, Integer, Boolean> {

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);

			if (result) {
				if (loadingTip != null) {
					loadingTip.setVisibility(View.GONE);
				}
				if (mGridView != null && dba != null) {
					if (Config.DEBUG) {
						Log.d(TAG, "onPostExecute() " + dba.getCount());
					}
					int count = dba.getCount();

					int unitSize = count % UNIT == 0 ? count / UNIT : count
							/ UNIT + 1;

					mUnitAdapter = new UnitAdapter(unitSize, count);
					mGridView.setAdapter(mUnitAdapter);
					mGridView.setVisibility(View.VISIBLE);

					mGridView.startAnimation(mAnimation);
				}

			}
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			AssetManager assetMngr = getActivity().getAssets();
			if (Config.DEBUG) {
				Log.d(TAG, "doInBackground() assetMngr: " + assetMngr
						+ ", dba: " + dba);
				Log.d(TAG, "doInBackground() CURRENT_BOOK_NAME: "
						+ Config.CURRENT_BOOK_NAME);
			}
			if (assetMngr == null || dba == null) {
				return false;
			}
			SQLiteDatabase db = null;
			try {
				db = dba.getWritableDatabase();
			} catch (SQLiteException e) {
				Log.d(TAG, "doInBackground() " + e.getMessage());
			}

			if (db == null) {
				return false;
			}

			db.beginTransaction();
			BufferedReader reader = null;
			try {
				InputStream in = getResources().openRawResource(
						Config.CURRENT_BOOK_NAME);
				reader = new BufferedReader(new InputStreamReader(in));
				// getResources().openRawResource(R.)
				String str = null;
				str = reader.readLine();
				if (str != null && str.contains(Config.TOTAL)) {
					int total = Integer
							.valueOf(str.substring(str.indexOf("=") + 1));

					if (Config.DEBUG) {
						Log.d(TAG, "doInBackground() total : "
								+ Config.CURRENT_BOOK_NAME + total + ", db: "
								+ dba.getCount());
					}
					if (total > 200 && dba.getCount() > total - 200) {
						return true;
					}
				}
				ContentValues values = new ContentValues();

				String word = null;
				String meaning = null;
				String example = null;

				while ((str = reader.readLine()) != null) {
					String[] arr = str.split(Config.WORD_MEANING_SPLIT);
					if (arr != null && arr.length == 2) {
						word = arr[0].trim();
						meaning = arr[1].trim();

						if (dba.exist(word, meaning)) {
							if (Config.DEBUG) {
								Log.d(TAG, "exist: " + word + " - " + meaning);
							}
							continue;
						}

						values.clear();
						if (Config.CURRENT_BOOK_NAME == Config.BOOK_NAME_PRO_LIUYI_5000) {
							if (meaning != null) {
								int indexOf = meaning.indexOf("#");
								if (indexOf != -1) {
									example = meaning.substring(indexOf + 1);
									meaning = meaning.substring(0, indexOf);
									if (meaning != null) {
										int idx = meaning.indexOf(".");
										if (idx != -1) {
											meaning = meaning
													.substring(idx + 1);
										}
									}
									values.put(DBA.WORD_SAMPLE, example);
								}
							}
						}
						values.put(DBA.WORD_WORD, word);
						values.put(DBA.WORD_MEANING, meaning);
						values.put(DBA.WORD_TIMESTAMP,
								System.currentTimeMillis());

						db.insert(DBA.CURRENT_WORD_TABLE, null, values);
					}
				}
				db.setTransactionSuccessful();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				db.endTransaction();
				if (reader != null) {
					try {
						reader.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

			return true;
		}

	}

	static class UnitViewHolder {
		TextView unit_id;
		TextView unit_contents_num;
		ImageView status;
	}

	private class UnitAdapter extends BaseAdapter {

		private int mUnitCount;
		private int mDbSize;

		public UnitAdapter(int count, int dbSize) {
			mUnitCount = count;
			mDbSize = dbSize;
		}

		@Override
		public int getCount() {
			return mUnitCount;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup container) {
			UnitViewHolder holder;
			if (convertView == null) {
				convertView = getActivity().getLayoutInflater().inflate(
						R.layout.unit_view, null);
				holder = new UnitViewHolder();
				holder.unit_id = (TextView) convertView
						.findViewById(R.id.unit_id);
				holder.unit_contents_num = (TextView) convertView
						.findViewById(R.id.unit_contents_num);
				holder.status = (ImageView) convertView
						.findViewById(R.id.unit_status);

				convertView.setTag(holder);
			} else {
				holder = (UnitViewHolder) convertView.getTag();
			}

			if (convertView instanceof UnitView) {
				// TODO dba non-null check
				UnitView tmp = ((UnitView) convertView);
				tmp.id = position;
				tmp.start = position * UNIT + 1;
				tmp.end = position == mUnitCount - 1 ? mDbSize : (position + 1)
						* UNIT;

				if (Config.DEBUG) {
					Log.d(TAG, String.format("getView() id: %d, s: %d, e: %d ",
							tmp.id, tmp.start, tmp.end));
				}

				if (holder.unit_id != null) {
					holder.unit_id.setText(getResources().getString(
							R.string.unit_id, position + 1));
				}

				if (holder.unit_contents_num != null) {
					holder.unit_contents_num.setText(getResources().getString(
							R.string.unit_contents_num, tmp.start, tmp.end));
				}

				if (holder.status != null) {
					if (dba != null && dba.isPass(tmp.start, tmp.end)) {
						holder.status
								.setImageResource(android.R.drawable.presence_online);
					} else {
						holder.status
								.setImageResource(android.R.drawable.presence_away);
					}
				}
			}

			return convertView;
		}

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		int start = position * UNIT + 1;
		int end = (position + 1) * UNIT;
		dba.loadUnitWords(start, end);
		Intent intent = new Intent(getActivity(), DragAndDropTestActivity.class);
		getActivity().startActivity(intent);
	}
}
