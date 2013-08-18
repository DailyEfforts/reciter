package com.gmail.dailyefforts.android.reciter.test.drag;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

import android.content.ClipData;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.SparseArray;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.view.View.OnClickListener;
import android.view.View.OnDragListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.Toast;

import com.gmail.dailyefforts.android.reciter.Config;
import com.gmail.dailyefforts.android.reciter.Word;
import com.gmail.dailyefforts.android.reciter.test.AbstractTestActivity;
import com.gmail.dailyefforts.android.reciter.R;

public class DragAndDropActivity extends AbstractTestActivity implements
		OnDragListener, OnClickListener {

	private static final int TIME_DELAY_TO_AUTO_FORWARD = 600;

	private static final String TAG = DragAndDropActivity.class.getSimpleName();

	private Button mBtnCurrentWord;

	private SparseArray<Word> pageMap;

	private ArrayList<Button> mOptList;

	private int mDbCount;

	private int mRate;

	private Button mBtnOptionTopLeft;

	private Button mBtnOptionTopRight;

	private Button mBtnOptionBottomLeft;

	private Button mBtnOptionBottomRight;

	private ImageButton mBtnArrowLeft;

	private ImageButton mBtnArrowRight;

	private int mColorError;

	private int mColorBingon;

	private AutoForwardHandler mAutoForwardHandler;

	private CheckBox mCheckBox;

	private static ArrayList<String> mWrongWordList = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_drag_and_drop);

		mBtnCurrentWord = (Button) findViewById(R.id.btn_drop_word);
		mBtnOptionTopLeft = (Button) findViewById(R.id.btn_drop_meaning_top_left);
		mBtnOptionTopRight = (Button) findViewById(R.id.btn_drop_meaning_top_right);
		mBtnOptionBottomLeft = (Button) findViewById(R.id.btn_drop_meaning_bottom_left);
		mBtnOptionBottomRight = (Button) findViewById(R.id.btn_drop_meaning_bottom_right);

		mCheckBox = (CheckBox) findViewById(R.id.check_auto_speak);

		mBtnArrowLeft = (ImageButton) findViewById(R.id.btn_drop_arrow_left);
		mBtnArrowRight = (ImageButton) findViewById(R.id.btn_drop_arrow_right);

		if (mBtnCurrentWord == null || mBtnOptionTopLeft == null
				|| mBtnOptionTopRight == null || mBtnOptionBottomLeft == null
				|| mBtnOptionBottomRight == null || mBtnArrowLeft == null
				|| mBtnArrowRight == null) {
			Log.e(TAG, "onCreate null pointer");
			finish();
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

		mRate = (Window.PROGRESS_END - Window.PROGRESS_START) / mWordArray.size();

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

		for (int i = 0; i < mWordArray.size(); i++) {
			TestCase testCase = new TestCase();

			Word w = mWordArray.get(i);
			testCase.wordIdx = w.getId();
			int id = -1;
			if (w != null) {
				id = w.getId();
			}
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

			for (int ii = 0; ii < optNum; ii++) {
				if (ii == answerIdx) {
					arrList.set(ii, id);
				}

				switch (ii) {
				case 0:
					testCase.topLeftIdx = arrList.get(ii);
					break;
				case 1:
					testCase.topRightIdx = arrList.get(ii);
					break;
				case 2:
					testCase.bottomLeftIdx = arrList.get(ii);
					break;
				case 3:
					testCase.bottomRightIdx = arrList.get(ii);
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

		mAutoForwardHandler = new AutoForwardHandler();
	}

	private class AutoForwardHandler extends Handler {
		public static final int MSG_MOVE_ON = 0;

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_MOVE_ON:
				forward();
				removeMessages(MSG_MOVE_ON);
				break;
			}
		}
	}

	private static ArrayList<TestCase> mTestCases = new ArrayList<DragAndDropActivity.TestCase>();

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

	private void buildTestCase() {

		if (mWordCounter >= mWordArray.size()) {
			Toast.makeText(getApplicationContext(), "Done.", Toast.LENGTH_SHORT)
					.show();
			finish();
			return;
		}

		mBingo = false;

		TestCase testCase = mTestCases.get(mWordCounter);

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
		int mWordIdx = testCase.wordIdx;
		int topLeftIdx = testCase.topLeftIdx;
		int topRightIdx = testCase.topRightIdx;
		int bottomLeftIdx = testCase.bottomLeftIdx;
		int bottomRightIdx = testCase.bottomRightIdx;

		Word curentWord = mDba.getWordByIdx(mWordIdx);
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

		setProgress((mWordCounter * mRate));

		if (mWordCounter == 0) {
			mBtnArrowLeft.setVisibility(View.INVISIBLE);
		} else {
			mBtnArrowLeft.setVisibility(View.VISIBLE);
		}

		if (mWordCounter == mWordArray.size() - 1) {
			setProgress(Window.PROGRESS_END);
			mBtnArrowRight.setVisibility(View.INVISIBLE);
		} else {
			mBtnArrowRight.setVisibility(View.VISIBLE);
		}

		if (mCheckBox != null && mCheckBox.isChecked()) {
			readIt(mWord);
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
							autoForward();
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

	private void autoForward() {
		if (mAutoForwardHandler != null) {
			mAutoForwardHandler.sendEmptyMessageDelayed(
					AutoForwardHandler.MSG_MOVE_ON, TIME_DELAY_TO_AUTO_FORWARD);
		}
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

	private void forward() {
		mWordCounter++;
		buildTestCase();
	}

	private void backward() {
		mWordCounter--;
		buildTestCase();
	}

}
