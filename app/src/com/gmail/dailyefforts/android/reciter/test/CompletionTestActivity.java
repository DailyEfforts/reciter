package com.gmail.dailyefforts.android.reciter.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import android.app.DialogFragment;
import android.content.ContentValues;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.gmail.dailyefforts.android.reciter.Config;
import com.gmail.dailyefforts.android.reciter.Language;
import com.gmail.dailyefforts.android.reciter.Word;
import com.gmail.dailyefforts.android.reciter.db.DBA;
import com.gmail.dailyefforts.android.reviwer.R;

public class CompletionTestActivity extends AbstractTestActivity implements
		OnClickListener {

	private static final String TAG = CompletionTestActivity.class
			.getSimpleName();
	private TextView mTextviewChinese;
	private LinearLayout mCandidatesContainer;
	private Button mBtnNext;
	private Button mBtnSkip;
	private Animation mAnimation;

	private boolean isFirstTouch;
	private ArrayList<String> mWrongWordList;

	private static final char[] OPTIONS_FR = "abcdefghijklmnopqrstuvwxyzàâçéèêëîïôûùüÿœ"
			.toCharArray();

	private List<Button> mOptionList;
	private LayoutParams mLayoutParams;
	private TextView mTextviewSpelling;
	private int mTestedSize;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_completion_test);

		if (Config.DEBUG) {
			for (int i = 0; i < mWordList.size(); i++) {
				Log.d(TAG, "word: " + i + ": " + mWordList.get(i).toString());
			}
		}

		mTextviewChinese = (TextView) findViewById(R.id.tv_spell_test_word_chinese);
		mTextviewSpelling = (TextView) findViewById(R.id.tv_spell_test_word_spelling);
		mCandidatesContainer = (LinearLayout) findViewById(R.id.ll_spell_test_word_candidates_container);

		mBtnSkip = (Button) findViewById(R.id.btn_spell_test_skip);
		mBtnNext = (Button) findViewById(R.id.btn_spell_test_next);

		if (mTextviewChinese == null || mTextviewSpelling == null
				|| mCandidatesContainer == null || mBtnSkip == null
				|| mBtnNext == null) {
			Log.e(TAG, "onCreate() null pointer issue.");
			return;
		}

		mBtnSkip.setOnClickListener(this);
		mBtnNext.setOnClickListener(this);

		mLayoutParams = new LayoutParams(mCandidatesContainer.getLayoutParams());
		mLayoutParams.weight = 1f;
		mLayoutParams.width = 0;
		mLayoutParams.gravity = Gravity.CENTER_VERTICAL;

		mOptionList = new ArrayList<Button>();

		for (int i = 0; i < 5; i++) {
			Button btn = new Button(this);
			btn.setId(i);
			btn.setLayoutParams(mLayoutParams);
			btn.setTextColor(Color.LTGRAY);
			mCandidatesContainer.addView(btn);
			mOptionList.add(btn);
			btn.setOnClickListener(this);
		}
		buildTestCase();

		mAnimation = AnimationUtils.loadAnimation(this, R.anim.wave_scale);
		// mBtnNext.setEnabled(false);
		mWrongWordList = new ArrayList<String>();

		mTestedSize = mWordList.size();
	}

	private void remember() {
		if (mWrongWordList != null && !mWrongWordList.contains(mWord)) {
			mWrongWordList.add(mWord);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case 0:
		case 1:
		case 2:
		case 3:
		case 4:
			if (v instanceof Button) {
				String str = String.valueOf(((Button) v).getText());
				if (str.equals(String.valueOf(mTestCase.letter))) {
					mTextviewSpelling.setText(mWord);
					mTextviewSpelling.startAnimation(mAnimation);
					mBtnNext.setEnabled(true);
					if (hasNext()) {
						startAutoForward();
					} else {
						showTestReport();
					}
				} else {
					if (isFirstTouch) {
						isFirstTouch = false;
						remember();
					}
					star(mWord);
					((Button) v).setEnabled(false);
				}
			}
			break;
		case R.id.btn_spell_test_next:
			if (hasNext()) {
				forward();
			} else {
				finish();
			}
			break;
		case R.id.btn_spell_test_skip:
			mTestedSize--;
			forward();
			break;
		}
	}

	private static List<Integer> mTestPointList = new ArrayList<Integer>();

	private List<TestCase> mTestCases = new ArrayList<CompletionTestActivity.TestCase>();

	private class TestCase {
		public int index;
		public char letter;

		public TestCase(int index, char letter) {
			super();
			this.index = index;
			this.letter = letter;
		}

		@Override
		public String toString() {
			return "TestCase [index=" + index + ", letter=" + letter + "]";
		}
	}

	private TestCase getTextCase(String spelling) {
		if (spelling == null) {
			return null;
		}
		Random random = new Random();

		mTestPointList.clear();
		mTestCases.clear();

		char[] arr = spelling.toCharArray();

		for (int i = 0; i < arr.length; i++) {
			if (Character.isLetter(arr[i])) {
				mTestCases.add(new TestCase(i, arr[i]));
			}
		}

		int len = mTestCases.size();

		return mTestCases.get(random.nextInt(len));
	}

	private TestCase mTestCase;

	@Override
	protected void buildTestCase() {
		super.buildTestCase();

		Word word = mWordList.get(mWordIdx);

		if (hasNext()) {
			mBtnNext.setEnabled(false);
		} else {
			mBtnNext.setText(R.string.done);
			mBtnSkip.setEnabled(false);
		}

		if (word == null) {
			return;
		}

		mTextviewChinese.setText(word.getMeaning());

		mWord = word.getWord();

		mTestCase = getTextCase(mWord);

		char[] arr = mWord.toCharArray();

		for (int i = 0; i < arr.length; i++) {
			if (i == mTestCase.index) {
				arr[i] = '?';
			}
		}
		mTextviewSpelling.setText(String.valueOf(arr));

		Random random = new Random();

		List<Character> options = new ArrayList<Character>();
		options.add(mTestCase.letter);

		int size = mOptionList.size();
		if (Language.English.equals(Config.CURRENT_LANGUAGE)) {
			while (options.size() < size) {
				char opt = (char) ('a' + random.nextInt(26));
				if (!options.contains(opt)) {
					options.add(opt);
				}
			}
		} else {
			while (options.size() < size) {
				char opt = OPTIONS_FR[random.nextInt(OPTIONS_FR.length)];
				if (!options.contains(opt)) {
					options.add(opt);
				}
			}
		}

		Collections.sort(options);

		for (int i = 0; i < size && i < options.size(); i++) {
			mOptionList.get(i).setEnabled(true);
			mOptionList.get(i).setText(String.valueOf(options.get(i)));
		}

		word = null; // Let GC do its work.
		isFirstTouch = true;
	}

	private void showTestReport() {
		setProgress(Window.PROGRESS_END);

		long elapsedTime = Math
				.round((System.currentTimeMillis() - mStartTime) / 1000.0);
		int bingoNum = mTestedSize - mWrongWordList.size();

		if (mTestedSize <= 0 || bingoNum < 0) {
			return;
		}

		int accuracy = (int) (bingoNum * 100.0f / mTestedSize);

		if (mDba != null) {
			ContentValues values = new ContentValues();
			values.put(DBA.TEST_TESTED_NUMBER, mTestedSize);
			values.put(DBA.TEST_CORRECT_NUMBER, bingoNum);
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

		String message = String.format(mTestReportStr, mTestedSize, bingoNum,
				elapsedTime, accuracy, mDba.size(),
				(int) (mDba.size() * (bingoNum * 1.0f / mTestedSize)));
		DialogFragment newFragment = TestReportFragment.newInstance(
				getString(R.string.test_report), message);
		newFragment.show(getFragmentManager(), "dialog");
	}
}
