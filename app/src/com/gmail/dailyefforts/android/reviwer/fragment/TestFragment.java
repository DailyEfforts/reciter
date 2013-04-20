package com.gmail.dailyefforts.android.reviwer.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.gmail.dailyefforts.android.reviwer.Config;
import com.gmail.dailyefforts.android.reviwer.R;
import com.gmail.dailyefforts.android.reviwer.db.DBA;
import com.gmail.dailyefforts.android.reviwer.debug.Debuger;
import com.gmail.dailyefforts.android.reviwer.test.TestPage;
import com.gmail.dailyefforts.android.reviwer.word.Word;

public class TestFragment extends Fragment implements View.OnClickListener {
	private static final String TAG = TestFragment.class.getSimpleName();
	private Button mBtnRandom;
	private Button mBtnMyWordBook;
	private SharedPreferences mSharedPref;
	private int mTestWordsSize;

	private static final int RANDOM_TEST = 0;
	private static final int MY_WORD_TEST = 1;
/*
 	@Override
	public void onDestroyView() {
		super.onDestroyView();

		Fragment fragment = getFragmentManager().findFragmentById(
				R.id.fragment_test_report_record);
		if (fragment != null) {
			int result = getFragmentManager().beginTransaction()
					.remove(fragment).commit();
			if (Debuger.DEBUG) {
				Log.d(TAG,
						"onDestroyView() remove old test report fragment, result: "
								+ result);
			}
		}
	} */

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_test, container, false);
		if (Debuger.DEBUG) {
			Log.d(TAG, "onCreateView() savedInstanceState: " + savedInstanceState);
		}
	
		if (view != null) {
			mBtnRandom = (Button) view.findViewById(R.id.btn_test_random);
			mBtnMyWordBook = (Button) view
					.findViewById(R.id.btn_test_my_word_book);

			if (mBtnMyWordBook != null) {
				mBtnMyWordBook.setOnClickListener(this);
			}

			if (mBtnRandom != null) {
				mBtnRandom.setOnClickListener(this);
			}
		}

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		try {
			mSharedPref = PreferenceManager
					.getDefaultSharedPreferences(getActivity());
			String tmp = mSharedPref.getString(getActivity().getResources()
					.getString(R.string.pref_key_random_test_question_number),
					Config.DEFAULT_RANDOM_TEST_SIZE);
			mTestWordsSize = Integer.valueOf(tmp);
		} catch (Exception e) {
		} finally {
			if (mTestWordsSize <= 0) {
				mTestWordsSize = Integer.valueOf(Config.DEFAULT_RANDOM_TEST_SIZE);
			}
		}
	}

	public class TestCaseBuilder extends AsyncTask<Integer, Void, Boolean> {
		private DBA dba;
		private int mTestKind;

		@Override
		protected void onPreExecute() {
			dba = DBA.getInstance(getActivity());
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				Intent intent = new Intent(getActivity(), TestPage.class);
				startActivity(intent);
			} else {
				if (mTestKind == MY_WORD_TEST) {
					Toast.makeText(getActivity(),
							R.string.tip_word_book_is_empty, Toast.LENGTH_LONG)
							.show();
				}
			}
		}

		@Override
		protected Boolean doInBackground(Integer... params) {
			mTestKind = params[0];
			if (dba == null) {
				return null;
			}
			switch (mTestKind) {
			case RANDOM_TEST:
				dba.buildRandomTest(mTestWordsSize);
				break;
			case MY_WORD_TEST:
				dba.buildMyWordBookTest();
				break;
			}
			return Word.getMap().size() > 0;
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_test_my_word_book:
			new TestCaseBuilder().execute(MY_WORD_TEST);
			break;
		case R.id.btn_test_random:
			new TestCaseBuilder().execute(RANDOM_TEST);
			break;
		}
	}

}
