package com.gmail.dailyefforts.android.reviwer.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.gmail.dailyefforts.android.reviwer.BaseActivity;
import com.gmail.dailyefforts.android.reviwer.Config;
import com.gmail.dailyefforts.android.reviwer.R;
import com.gmail.dailyefforts.android.reviwer.Word;

public class SpellTestActivity extends BaseActivity implements OnClickListener {

	private static final String TAG = SpellTestActivity.class.getSimpleName();
	private TextView mTextviewChinese;
	private LinearLayout mCandidatesContainer;
	private Button mBtnNext;
	private Button mBtnSkip;
	private Animation mAnimation;

	private static final char[] OPTIONS = "àâçéèêëîïôûùüÿœabcdefghijklmnopqrstuvwxyz"
			.toCharArray();

	private int mCounter;

	private List<Button> mOptionList;
	private LayoutParams mLayoutParams;
	private TextView mTextviewSpelling;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_spell_test);

		if (Config.DEBUG) {
			for (int i = 0; i < mWordArray.size(); i++) {
				Log.d(TAG, "word: " + i + ": " + mWordArray.get(i).toString());
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
		build();

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
					mTextviewSpelling.setText(mCurrentWord.getWord());
					mTextviewSpelling.startAnimation(mAnimation);
					mBtnNext.setEnabled(true);
				} else {
					((Button) v).setEnabled(false);
				}
			}
			break;
		case R.id.btn_spell_test_next:
			if (noMore()) {
				finish();
			} else {
				build();
			}
			break;
		case R.id.btn_spell_test_skip:
			build();
			break;
		}
	}

	private boolean noMore() {
		return mCounter >= mWordArray.size();
	}

	private static List<Integer> mTestPointList = new ArrayList<Integer>();

	private List<TestCase> mTestCases = new ArrayList<SpellTestActivity.TestCase>();

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
	private Word mCurrentWord;

	private void build() {

		mCurrentWord = mWordArray.get(mCounter++);

		if (noMore()) {
			mBtnNext.setText("Done");
			mBtnSkip.setEnabled(false);
		} else {
			mBtnNext.setEnabled(false);
		}

		if (mCurrentWord == null) {
			return;
		}

		mTextviewChinese.setText(mCurrentWord.getMeaning());

		mWord = mCurrentWord.getWord();

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

	}

}
