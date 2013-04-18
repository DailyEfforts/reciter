package com.gmail.dailyefforts.android.reviwer.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.gmail.dailyefforts.android.reviwer.Config;
import com.gmail.dailyefforts.android.reviwer.R;
import com.gmail.dailyefforts.android.reviwer.db.DBA;
import com.gmail.dailyefforts.android.reviwer.setting.Settings;
import com.gmail.dailyefforts.android.reviwer.test.TestPage;
import com.gmail.dailyefforts.android.reviwer.word.Word;

public class TestFragment extends Fragment implements View.OnClickListener {
	private static final String TAG = TestFragment.class.getSimpleName();
	private Button btnRandom;
	private Button btnMyWordBook;
	private SharedPreferences mSharedPref;
	private int size;

	private static final int RANDOM_TEST = 0;
	private static final int MY_WORD_TEST = 1;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_test, container, false);
		mSharedPref = PreferenceManager
				.getDefaultSharedPreferences(getActivity());
		try {
			String tmp = mSharedPref.getString(getActivity().getResources()
					.getString(R.string.pref_key_random_test_question_number),
					Settings.DEFAULT_RANDOM_TEST_SIZE);
			size = Integer.valueOf(tmp);
		} catch (Exception e) {
		} finally {
			if (size <= 0) {
				size = Integer.valueOf(Settings.DEFAULT_RANDOM_TEST_SIZE);
			}
		}
		if (view != null) {
			btnRandom = (Button) view.findViewById(R.id.btn_test_random);
			btnMyWordBook = (Button) view
					.findViewById(R.id.btn_test_my_word_book);

			if (btnMyWordBook != null) {
				btnMyWordBook.setOnClickListener(this);
			}

			if (btnRandom != null) {
				btnRandom.setOnClickListener(this);
			}
		}

		return view;
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
				dba.buildRandomTest(size);
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
