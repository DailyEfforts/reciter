package com.gmail.dailyefforts.android.reviwer.fragment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

import com.gmail.dailyefforts.android.reviwer.Config;
import com.gmail.dailyefforts.android.reviwer.R;
import com.gmail.dailyefforts.android.reviwer.db.DBA;
import com.gmail.dailyefforts.android.reviwer.drag.DragAndDropActivity;
import com.gmail.dailyefforts.android.reviwer.unit.UnitView;

public class UnitSetFragment extends Fragment implements OnItemClickListener {
	private static final String TAG = UnitSetFragment.class.getSimpleName();
	private RelativeLayout loadingTip;
	private GridView mGridView;
	private DBA dba;
	private SharedPreferences mSharedPref;
	private Animation mAnimation;

	private static int UNIT = Integer
			.valueOf(Config.DEFAULT_WORD_COUNT_OF_ONE_UNIT);

	private boolean UPDATE_NCE1_DONE;
	private boolean UPDATE_NCE2_DONE;
	private boolean UPDATE_NCE4_DONE;
	private static final String UpdateNce1Done = "update_nce1_done";
	private static final String UpdateNce2Done = "update_nce2_done";
	private static final String UpdateNce4Done = "update_nce4_done";

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

		mSharedPref = PreferenceManager
				.getDefaultSharedPreferences(getActivity());
		if (mSharedPref != null) {
			UNIT = Integer.valueOf(mSharedPref.getString(
					getString(R.string.pref_key_word_count_in_one_unit),
					Config.DEFAULT_WORD_COUNT_OF_ONE_UNIT));

			UPDATE_NCE1_DONE = mSharedPref.getBoolean(UpdateNce1Done, false);
			UPDATE_NCE2_DONE = mSharedPref.getBoolean(UpdateNce2Done, false);
			UPDATE_NCE4_DONE = mSharedPref.getBoolean(UpdateNce4Done, false);
		}

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
				Log.d(TAG, "doInBackground() assetMngr: " + assetMngr);
			}
			if (assetMngr == null || dba == null) {
				return false;
			}
			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new InputStreamReader(
						assetMngr.open(Config.CURRENT_BOOK_NAME)));
				String str = null;
				str = reader.readLine();
				if (str != null && str.contains("total=")) {
					int total = Integer
							.valueOf(str.substring(str.indexOf("=") + 1));

					if (Config.DEBUG) {
						Log.d(TAG, "doInBackground() total : "
								+ Config.CURRENT_BOOK_NAME + total + ", db: "
								+ dba.getCount());
						Log.d(TAG, "doInBackground() UPDATE_NCE1_DONE : "
								+ UPDATE_NCE1_DONE + ", " + UPDATE_NCE2_DONE
								+ ", " + UPDATE_NCE4_DONE);
					}
					if (total > 200 && dba.getCount() > total - 200) {
						if ((Config.CURRENT_BOOK_NAME
								.equals(Config.BOOK_NAME_NCE1) && !UPDATE_NCE1_DONE)
								|| (Config.CURRENT_BOOK_NAME
										.equals(Config.BOOK_NAME_NCE2) && !UPDATE_NCE2_DONE)
								|| (Config.CURRENT_BOOK_NAME
										.equals(Config.BOOK_NAME_NCE4) && !UPDATE_NCE4_DONE)) {
							ContentValues values = new ContentValues();
							dba.beginTransaction();
							while ((str = reader.readLine()) != null) {
								String[] arr = str
										.split(Config.WORD_MEANING_SPLIT);
								if (arr != null && arr.length == 2) {
									String word = arr[0].trim();
									String meanning = arr[1].trim();
									values.clear();
									values.put(DBA.WORD_MEANING, meanning);
									dba.update(DBA.CURRENT_WORD_TABLE, word,
											values);
								}
							}
							dba.setTransactionSuccessful();
							dba.endTransaction();

							if (Config.CURRENT_BOOK_NAME
									.equals(Config.BOOK_NAME_NCE1)) {
								UPDATE_NCE1_DONE = true;
								commit(UpdateNce1Done);
							} else if (Config.CURRENT_BOOK_NAME
									.equals(Config.BOOK_NAME_NCE2)) {
								UPDATE_NCE2_DONE = true;
								commit(UpdateNce2Done);
							} else if (Config.CURRENT_BOOK_NAME
									.equals(Config.BOOK_NAME_NCE4)) {
								UPDATE_NCE4_DONE = true;
								commit(UpdateNce4Done);
							}

						}
						return true;
					}
				}
				ContentValues values = new ContentValues();
				dba.beginTransaction();
				while ((str = reader.readLine()) != null) {
					String[] arr = str.split(Config.WORD_MEANING_SPLIT);
					if (arr != null && arr.length == 2) {
						String word = arr[0].trim();
						String meanning = arr[1].trim();

						values.clear();
						values.put(DBA.WORD_WORD, word);
						values.put(DBA.WORD_MEANING, meanning);
						values.put(DBA.WORD_TIMESTAMP,
								System.currentTimeMillis());
						dba.insert(DBA.CURRENT_WORD_TABLE, null, values);

					}
				}
				dba.setTransactionSuccessful();
				dba.endTransaction();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
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

	private void commit(String key) {
		if (mSharedPref != null) {
			Editor editor = mSharedPref.edit();
			editor.putBoolean(key, true);
			editor.commit();
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
		Intent intent = new Intent(getActivity(), DragAndDropActivity.class);
		getActivity().startActivity(intent);
	}
}
