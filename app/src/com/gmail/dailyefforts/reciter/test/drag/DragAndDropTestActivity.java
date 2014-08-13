package com.gmail.dailyefforts.reciter.test.drag;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

import android.content.ClipData;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.view.View.OnClickListener;
import android.view.View.OnDragListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.gmail.dailyefforts.reciter.R;
import com.gmail.dailyefforts.reciter.Config;
import com.gmail.dailyefforts.reciter.Word;
import com.gmail.dailyefforts.reciter.test.AbstractTestActivity;

public class DragAndDropTestActivity extends AbstractTestActivity implements
		OnDragListener, OnClickListener {

	private static final String TAG = DragAndDropTestActivity.class
			.getSimpleName();

	private Button mBtnCurrentWord;

	private SparseArray<Word> pageMap;

	private ArrayList<Button> mOptList;

	private int mDbCount;

	private Button mBtnOptionTopLeft;

	private Button mBtnOptionTopRight;

	private Button mBtnOptionBottomLeft;

	private Button mBtnOptionBottomRight;

	private ImageButton mBtnArrowLeft;

	private ImageButton mBtnArrowRight;

	private int mColorError;

	private int mColorBingon;

	private CheckBox mCheckBox;

	private TextView mTextViewSample;

	private static ArrayList<String> mWrongWordList = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_drag_and_drop_test);

		mBtnCurrentWord = (Button) findViewById(R.id.btn_drop_word);
		mBtnOptionTopLeft = (Button) findViewById(R.id.btn_drop_meaning_top_left);
		mBtnOptionTopRight = (Button) findViewById(R.id.btn_drop_meaning_top_right);
		mBtnOptionBottomLeft = (Button) findViewById(R.id.btn_drop_meaning_bottom_left);
		mBtnOptionBottomRight = (Button) findViewById(R.id.btn_drop_meaning_bottom_right);
		mTextViewSample = (TextView) findViewById(R.id.tv_sample);

		mCheckBox = (CheckBox) findViewById(R.id.check_auto_speak);

		mBtnArrowLeft = (ImageButton) findViewById(R.id.btn_drop_arrow_left);
		mBtnArrowRight = (ImageButton) findViewById(R.id.btn_drop_arrow_right);

		if (mBtnCurrentWord == null || mBtnOptionTopLeft == null
				|| mBtnOptionTopRight == null || mBtnOptionBottomLeft == null
				|| mBtnOptionBottomRight == null || mBtnArrowLeft == null
				|| mBtnArrowRight == null) {
			Log.e(TAG, "onCreate null pointer");
		}

		mBtnArrowLeft.setOnClickListener(this);
		mBtnArrowRight.setOnClickListener(this);

		mBtnCurrentWord.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getActionMasked()) {
				case MotionEvent.ACTION_DOWN:
					if (Config.DEBUG) {
						Log.d(TAG, "onTouch() ACTION_DOWN");
					}
					ClipData dragData = ClipData.newPlainText("label", "text");
					DragShadowBuilder shadowBuilder = new DragShadowBuilder(v);
					v.clearAnimation();
					v.startDrag(dragData, shadowBuilder, v, 0);
					return true;
				case MotionEvent.ACTION_UP:
					if (Config.DEBUG) {
						Log.d(TAG, "onTouch() ACTION_UP");
					}
					break;
				default:
					break;
				}
				return false;
			}

		});

		mBtnOptionTopLeft.setOnDragListener(this);
		mBtnOptionTopRight.setOnDragListener(this);
		mBtnOptionBottomLeft.setOnDragListener(this);
		mBtnOptionBottomRight.setOnDragListener(this);

		mBtnCurrentWord.setOnDragListener(this);

		mDbCount = mDba.getCount();

		mOptList = new ArrayList<Button>();
		mOptList.add(mBtnOptionTopLeft);
		mOptList.add(mBtnOptionTopRight);
		mOptList.add(mBtnOptionBottomLeft);
		mOptList.add(mBtnOptionBottomRight);

		for (Button btn : mOptList) {
			btn.setOnClickListener(this);
		}

		Resources res = getResources();

		mColorError = res.getColor(R.color.orange_dark);
		mColorBingon = res.getColor(R.color.green);

		if (savedInstanceState == null) {
			if (mWrongWordList != null) {
				mWrongWordList.clear();
			}
		}

		Random random = new Random();
		int optNum = 4;

		mTestCases.clear();

		for (int i = 0; i < mWordList.size(); i++) {
			TestCase testCase = new TestCase();

			Word w = mWordList.get(i);

			if (w == null) {
				continue;
			}

			testCase.wordIdx = w.getId();
			int id = w.getId();
			if (Config.DEBUG) {
				Log.d(TAG, "onCreate() id: " + id);
			}
			arrList.clear();
			while (arrList.size() <= optNum) {
				int tmp = random.nextInt(mDbCount);
				if (tmp != 0 && tmp != id && !arrList.contains(tmp)) {
					arrList.add(tmp);
				}
			}

			int answerIdx = random.nextInt(optNum);

			for (int j = 0; j < optNum; j++) {
				if (j == answerIdx) {
					arrList.set(j, id);
				}

				switch (j) {
				case 0:
					testCase.topLeftIdx = arrList.get(j);
					break;
				case 1:
					testCase.topRightIdx = arrList.get(j);
					break;
				case 2:
					testCase.bottomLeftIdx = arrList.get(j);
					break;
				case 3:
					testCase.bottomRightIdx = arrList.get(j);
					break;
				}
			}

			mTestCases.add(testCase);

			if (Config.DEBUG) {
				Log.d(TAG,
						"onCreate() test case-" + i + ", "
								+ testCase.toString());
			}
		}
		buildTestCase();

	}

	private static ArrayList<TestCase> mTestCases = new ArrayList<DragAndDropTestActivity.TestCase>();

	private class TestCase {
		public int wordIdx;
		public int topLeftIdx;
		public int topRightIdx;
		public int bottomLeftIdx;
		public int bottomRightIdx;

		@Override
		public String toString() {
			return String.format(Locale.getDefault(),
					"testcase: %d, %d, %d, %d, %d", wordIdx, topLeftIdx,
					topRightIdx, bottomLeftIdx, bottomRightIdx);
		}
	}

	private boolean mBingo;

	private static ArrayList<Integer> arrList = new ArrayList<Integer>();

	@Override
	protected void buildTestCase() {
		super.buildTestCase();
		if (mWordIdx >= mWordList.size()) {
			Toast.makeText(getApplicationContext(), "Done.", Toast.LENGTH_SHORT)
					.show();
			finish();
			return;
		}

		mBingo = false;

		TestCase testCase = mTestCases.get(mWordIdx);

		if (mBtnCurrentWord.getVisibility() != View.VISIBLE) {
			mBtnCurrentWord.setVisibility(View.VISIBLE);
		}

		int color = Color.LTGRAY;

		for (Button btn : mOptList) {
			btn.setVisibility(View.VISIBLE);
			btn.setEnabled(true);
			btn.setTextColor(color);
		}

		pageMap = new SparseArray<Word>();
		int idxInDb = testCase.wordIdx;
		int topLeftIdx = testCase.topLeftIdx;
		int topRightIdx = testCase.topRightIdx;
		int bottomLeftIdx = testCase.bottomLeftIdx;
		int bottomRightIdx = testCase.bottomRightIdx;

		Word curentWord = mDba.getWordByIdx(idxInDb);
		Word topLeftWord = mDba.getWordByIdx(topLeftIdx);
		Word topRightWord = mDba.getWordByIdx(topRightIdx);
		Word bottomLeftWord = mDba.getWordByIdx(bottomLeftIdx);
		Word bottomRightWord = mDba.getWordByIdx(bottomRightIdx);
		pageMap.put(mBtnCurrentWord.getId(), curentWord);
		pageMap.put(mBtnOptionTopLeft.getId(), topLeftWord);
		pageMap.put(mBtnOptionTopRight.getId(), topRightWord);
		pageMap.put(mBtnOptionBottomLeft.getId(), bottomLeftWord);
		pageMap.put(mBtnOptionBottomRight.getId(), bottomRightWord);

		mWord = curentWord.getWord();
		mBtnCurrentWord.setText(mWord);

		mBtnOptionTopLeft.setText(topLeftWord.getMeaning());
		mBtnOptionTopRight.setText(topRightWord.getMeaning());
		mBtnOptionBottomLeft.setText(bottomLeftWord.getMeaning());
		mBtnOptionBottomRight.setText(bottomRightWord.getMeaning());
		if (mTextViewSample != null) {
			String sample = curentWord.getSample();
			if (sample != null) {
				mTextViewSample.setText(sample);
				if (mTextViewSample.getVisibility() != View.VISIBLE) {
					mTextViewSample.setVisibility(View.VISIBLE);
				}
			} else {
				if (mTextViewSample.getVisibility() != View.GONE) {
					mTextViewSample.setVisibility(View.GONE);
				}
			}
		}

		if (mWordIdx == 0) {
			mBtnArrowLeft.setVisibility(View.INVISIBLE);
		} else {
			mBtnArrowLeft.setVisibility(View.VISIBLE);
		}

		if (hasNext()) {
			mBtnArrowRight.setVisibility(View.VISIBLE);
		} else {
			mBtnArrowRight.setVisibility(View.INVISIBLE);
		}

		if (mCheckBox != null && mCheckBox.isChecked()) {
			read(mWord);
		}

		invalidateOptionsMenu();
	}

	@Override
	public boolean onDrag(View v, DragEvent event) {
		if (Config.DEBUG) {
			Log.d(TAG, "onDrag() event.getAction(): " + event.getAction());
		}
		switch (event.getAction()) {
		case DragEvent.ACTION_DRAG_STARTED:

			if (v.getId() == mBtnCurrentWord.getId()) {
				mBtnCurrentWord.clearAnimation();
				mBtnCurrentWord.setVisibility(View.INVISIBLE);
			}
			break;
		case DragEvent.ACTION_DRAG_LOCATION:
			break;
		case DragEvent.ACTION_DRAG_ENTERED:
			break;
		case DragEvent.ACTION_DRAG_EXITED:
			break;
		case DragEvent.ACTION_DROP:
			if (v.getId() == mBtnCurrentWord.getId()) {
				mBtnCurrentWord.setVisibility(View.VISIBLE);
			} else {
				judge(v);
			}
			break;
		case DragEvent.ACTION_DRAG_ENDED:
			if (v.getId() == mBtnCurrentWord.getId()) {
				if (!mBingo) {
					mBtnCurrentWord.setVisibility(View.VISIBLE);
				}
			} else {
				v.clearAnimation();
			}
			break;
		}
		if (v.getId() == mBtnCurrentWord.getId()) {
			return false;
		} else {
			return true;
		}
	}

	private void judge(View v) {
		if (pageMap != null) {
			Word word = pageMap.get(v.getId());
			if (word != null) {
				String w = word.getWord();
				String m = word.getMeaning();
				if (Config.DEBUG) {
					Log.d(TAG, "onDrag() w: " + w + ", m: " + m);
				}

				if (mWord != null) {
					if (v instanceof Button) {
						Button btn = (Button) v;
						if (mWord.equals(w)) {
							btn.setTextColor(mColorBingon);
							startAutoForward();
							mBingo = true;
							mDba.setPast(mWord);
						} else {
							btn.setTextColor(mColorError);
							if (mDba != null) {
								mDba.star(mWord);
							}
						}
						btn.setText(w + "\n" + m);
						btn.setEnabled(false);
					}
				}
			}
		}
		v.clearAnimation();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_drop_arrow_left:
			backward();
			break;
		case R.id.btn_drop_arrow_right:
			forward();
			break;
		case R.id.btn_drop_meaning_top_left:
		case R.id.btn_drop_meaning_top_right:
		case R.id.btn_drop_meaning_bottom_left:
		case R.id.btn_drop_meaning_bottom_right:
			judge(v);
			break;
		}
	}

}
