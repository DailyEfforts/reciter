package com.gmail.dailyefforts.android.reciter.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import android.app.DialogFragment;
import android.content.ContentValues;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gmail.dailyefforts.android.reciter.Config;
import com.gmail.dailyefforts.android.reciter.Word;
import com.gmail.dailyefforts.android.reciter.db.DBA;
import com.gmail.dailyefforts.android.reviwer.R;

public class SelectionTestActivity extends AbstractTestActivity implements
		View.OnClickListener {

	private static final String TAG = SelectionTestActivity.class
			.getSimpleName();

	private TextView mTextViewTestingItem;

	private String mMeaning;

	private SparseArray<Word> mWordsMeaningsMap;

	private int mBingoNum;

	private boolean isFirstTouch;

	private LinearLayout optCat;

	private ArrayList<OptionButton> mOptList;

	private int mDbCount;

	private static ArrayList<String> mWrongWordList = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_selection_test);
		mTextViewTestingItem = (TextView) findViewById(R.id.tv_word);

		mDbCount = mDba.getCount();
		optCat = (LinearLayout) findViewById(R.id.opt_category);
		optCat.setWeightSum(Config.DEFAULT_OPTION_COUNT);

		Bundle extras = getIntent().getExtras();
		int testType = extras.getInt(Config.INTENT_EXTRA_TEST_TYPE);
		if (Config.DEBUG) {
			Log.d(TAG, "testType: " + testType);
		}
		switch (testType) {
		case Config.RANDOM_TEST_ZH:
		case Config.MY_WORD_TEST_ZH:
			FLAG = true;
			mTextViewTestingItem
					.setTextSize(mTextViewTestingItem.getTextSize() / 2.0f);
			break;
		default:
			FLAG = false;
			break;
		}

		mOptList = new ArrayList<OptionButton>();

		for (int i = 0; i < Config.DEFAULT_OPTION_COUNT; i++) {
			OptionButton btn = new OptionButton(this, i);
			mOptList.add(btn);
		}

		for (OptionButton tmp : mOptList) {
			optCat.addView(tmp);
			// tmp.setOnTouchListener(this);
			tmp.setOnClickListener(this);
		}

		buildTestCase();

		getActionBar().setDisplayHomeAsUpEnabled(true);

		mStartTime = 0L;

		if (mWrongWordList != null) {
			mWrongWordList.clear();
		}

	}

	@Override
	protected void onPause() {
		super.onPause();
		if (!isFirstTouch && mDba != null && mDba.getStar(mWord) <= 0) {
			mDba.star(mWord);
		}
	}

	@Override
	protected void buildTestCase() {
		super.buildTestCase();
		Random random = new Random();

		mWord = mWordList.get(mWordIdx).getWord();
		mMeaning = mWordList.get(mWordIdx).getMeaning();
		if (FLAG) {
			mTextViewTestingItem.setText(mMeaning);
		} else {
			mTextViewTestingItem.setText(mWord);

		}
		invalidateOptionsMenu();

		for (Button btn : mOptList) {
			btn.setEnabled(true);
		}

		mWordsMeaningsMap = new SparseArray<Word>();

		// make sure the option is not duplicate.
		ArrayList<Integer> arrList = new ArrayList<Integer>();
		while (arrList.size() < Config.DEFAULT_OPTION_COUNT - 1) {
			int tmp = random.nextInt(mDbCount);
			if (tmp != 0 && tmp != mWordIdx && !arrList.contains(tmp)) {
				arrList.add(tmp);
			}
		}

		int answerIdx = random.nextInt(Config.DEFAULT_OPTION_COUNT);

		for (int i = 0; i < mOptList.size(); i++) {
			OptionButton btn = mOptList.get(i);
			if (i == answerIdx) {
				if (FLAG) {
					btn.setText(mWord);
				} else {
					btn.setText(mMeaning);
				}
				mWordsMeaningsMap.put(btn.getId(), mWordList.get(mWordIdx));
			} else {

				int tmp = 0;

				if (arrList != null && arrList.size() > 0) {
					tmp = arrList.get(0);
					arrList.remove(0);
				} else {
					tmp = random.nextInt(mWordList.size());
				}

				if (mDba != null) {
					Word word = mDba.getWordByIdx(tmp);
					if (FLAG) {
						btn.setText(word.getWord());
					} else {
						btn.setText(word.getMeaning());

					}
					mWordsMeaningsMap.put(btn.getId(), word);
				}
			}
		}

		isFirstTouch = true;
	}

	private boolean FLAG = false;

	@Override
	public void onClick(View v) {
		if (!(v instanceof Button)) {
			return;
		}
		Word w = mWordsMeaningsMap.get(v.getId());
		if (w != null && mWord != null) {
			if (mWord.equals(w.getWord())) {
				if (isFirstTouch) {
					mBingoNum++;
				}
				if (hasNext()) {
					forward();
				} else {
					showTestReport();
				}
			} else {
				if (mDba != null) {
					mDba.star(mWord);
				}
				if (isFirstTouch) {
					isFirstTouch = false;
				}
				remember();
				((Button) v).setText(w.getWord() + "\n" + w.getMeaning());
				((Button) v).setEnabled(false);
			}
		}
	}

	private void remember() {
		if (mWrongWordList != null && !mWrongWordList.contains(mWord)
				&& !mWrongWordList.contains(mMeaning)) {
			if (FLAG) {
				mWrongWordList.add(mMeaning);
			} else {
				mWrongWordList.add(mWord);
			}
		}
	}

	private void showTestReport() {
		setProgress(Window.PROGRESS_END);

		long elapsedTime = Math
				.round((System.currentTimeMillis() - mStartTime) / 1000.0);
		int accuracy = (int) (mBingoNum * 100.0f / mWordList.size());

		if (mDba != null) {
			ContentValues values = new ContentValues();
			values.put(DBA.TEST_TESTED_NUMBER, mWordList.size());
			values.put(DBA.TEST_CORRECT_NUMBER, mBingoNum);
			values.put(DBA.TEST_ELAPSED_TIME, elapsedTime);
			values.put(DBA.TEST_ACCURACY, accuracy);
			values.put(DBA.TEST_DB_SIZE, mDba.size());
			values.put(DBA.TEST_TIMESTAMP, System.currentTimeMillis());
			if (mWrongWordList != null) {
				Collections.sort(mWrongWordList);
				values.put(DBA.TEST_WRONG_WORD_LIST, mWrongWordList.toString());
			}
			mDba.insert(DBA.CURRENT_TEST_REPORT_TABLE, null, values);
		}

		String message = String.format(mTestReportStr, mWordList.size(),
				mBingoNum, elapsedTime, accuracy, mDba.size(),
				(int) (mDba.size() * (mBingoNum * 1.0f / mWordList.size())));
		DialogFragment newFragment = TestReportFragment.newInstance(
				getString(R.string.test_report), message);
		newFragment.show(getFragmentManager(), "dialog");
	}

}
