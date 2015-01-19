package com.gmail.dailyefforts.reciter.test;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gmail.dailyefforts.reciter.Config;
import com.gmail.dailyefforts.android.reviwer.R;

public class TestFragment extends Fragment {
	private static final String TAG = TestFragment.class.getSimpleName();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_test, container, false);
		if (Config.DEBUG) {
			Log.d(TAG, "onCreateView() savedInstanceState: "
					+ savedInstanceState);
		}

		return view;
	}

}
