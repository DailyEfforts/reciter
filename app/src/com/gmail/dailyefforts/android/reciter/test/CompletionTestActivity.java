package com.gmail.dailyefforts.android.reciter.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

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
import com.gmail.dailyefforts.android.reviwer.R;
import com.gmail.dailyefforts.android.reciter.Word;

public class CompletionTestActivity extends AbstractTestActivity implements
		OnClickListener {

	private static final String TAG = CompletionTestActivity.class
			.getSimpleName();
	private TextView mTextviewChinese;
	private LinearLayout mCandidatesContainer;
	private Button mBtnNext;
	private Button mBtnSkip;
	private Animation mAnimation;

	private static final char[] OPTIONS = "abcdefghijklmnopqrstuvwxyzàâçéèêëîïôûùüÿœ"
			.toCharArray();

	private List<Button> mOptionList;
	private LayoutParams mLayoutParams;
	private TextView mTextviewSpelling;

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
			mCandidatesContainer.addView(btn);
			mOptionList.add(btn);
			btn.setOnClickListener(this);
		}
		buildTestCase();

		mAnimation = AnimationUtils.loadAnimation(this, R.anim.wave_scale);
		// mBtnNext.setEnabled(false);

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
						setProgress(Window.PROGRESS_END);
					}
				} else {
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

		while (options.size() < mOptionList.size()) {
			char opt = OPTIONS[random.nextInt(OPTIONS.length)];
			if (!options.contains(opt)) {
				options.add(opt);
			}
		}

		Collections.sort(options);

		for (int i = 0; i < mOptionList.size() && i < options.size(); i++) {
			mOptionList.get(i).setEnabled(true);
			mOptionList.get(i).setText(String.valueOf(options.get(i)));
		}

		word = null; // Let GC do its work.
	}

}
