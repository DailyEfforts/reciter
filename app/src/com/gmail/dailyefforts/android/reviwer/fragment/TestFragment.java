package com.gmail.dailyefforts.android.reviwer.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.gmail.dailyefforts.android.reviwer.Config;
import com.gmail.dailyefforts.android.reviwer.R;
import com.gmail.dailyefforts.android.reviwer.debug.Debuger;

public class TestFragment extends Fragment {
	private static final String TAG = TestFragment.class.getSimpleName();
	private Button mBtnRandom;
	private Button mBtnMyWordBook;
	private SharedPreferences mSharedPref;
	private int mTestWordsSize;

	private static final int RANDOM_TEST = 0;
	private static final int MY_WORD_TEST = 1;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_test, container, false);
		if (Debuger.DEBUG) {
			Log.d(TAG, "onCreateView() savedInstanceState: "
					+ savedInstanceState);
		}

		if (view != null) {
//			mBtnRandom = (Button) view.findViewById(R.id.btn_test_random);
//			mBtnMyWordBook = (Button) view
//					.findViewById(R.id.btn_test_my_word_book);
//
//			if (mBtnMyWordBook != null) {
//				mBtnMyWordBook.setOnClickListener(this);
//			}
//
//			if (mBtnRandom != null) {
//				mBtnRandom.setOnClickListener(this);
//			}
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
				mTestWordsSize = Integer
						.valueOf(Config.DEFAULT_RANDOM_TEST_SIZE);
			}
		}
	}

	
	

}
