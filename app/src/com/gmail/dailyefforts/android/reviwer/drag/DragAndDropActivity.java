package com.gmail.dailyefforts.android.reviwer.drag;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.util.SparseArray;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.view.View.OnClickListener;
import android.view.View.OnDragListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.gmail.dailyefforts.android.reviwer.Config;
import com.gmail.dailyefforts.android.reviwer.R;
import com.gmail.dailyefforts.android.reviwer.db.DBA;
import com.gmail.dailyefforts.android.reviwer.debug.Debuger;
import com.gmail.dailyefforts.android.reviwer.draw.Paper;
import com.gmail.dailyefforts.android.reviwer.word.Word;

public class DragAndDropActivity extends Activity implements OnDragListener,
		OnClickListener, OnInitListener {

	private static final int TIME_DELAY_TO_AUTO_FORWARD = 600;

	private static final String TAG = DragAndDropActivity.class.getSimpleName();

	private Button mBtnCurrentWord;

	private String mWord;

	private SparseArray<Word> map;

	private SparseArray<Word> pageMap;

	private ArrayList<Button> mOptList;

	private DBA mDBA;

	private int mDbCount;

	private int mRate;

	private String mAddToBook;

	private String mRmFromBook;

	private TextToSpeech mTts;

	private Button mBtnOptionTopLeft;

	private Button mBtnOptionTopRight;

	private Button mBtnOptionBottomLeft;

	private Button mBtnOptionBottomRight;

	private ImageButton mBtnArrowLeft;

	private ImageButton mBtnArrowRight;

	private int mColorError;

	private int mColorBingon;

	private Animation animation;

	private Paper mPaper;

	private AutoForwardHandler mAutoForwardHandler;

	private static ArrayList<String> mWrongWordList = new ArrayList<String>();

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if (menu != null) {
			menu.clear();
		}

		getMenuInflater().inflate(R.menu.action, menu);

		if (mDBA == null || menu == null) {
			return false;
		}

		MenuItem star = menu.findItem(R.id.menu_star);
		if (star != null) {
			Log.i(TAG, "onPrepareOptionsMenu() mWord: " + mWord + ", star: "
					+ mDBA.getStar(mWord));
			if (mDBA.getStar(mWord) <= 0) {
				star.setIcon(android.R.drawable.star_off);
				star.setTitle(R.string.add_to_word_book);
			} else {
				star.setIcon(android.R.drawable.star_on);
				star.setTitle(R.string.remove_from_word_book);
			}
		}

		if (Debuger.DEBUG) {
			Log.d(TAG, "onPrepareOptionsMenu()");
		}
		return true;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (mTts != null) {
			mTts.shutdown();
		}
	}

	private void readIt(final String word) {
		if (mTts != null) {
			int result = mTts.speak(word, TextToSpeech.QUEUE_FLUSH, null);
			if (result != TextToSpeech.SUCCESS) {
				Log.e(TAG, "speak failed");
			}
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		case R.id.menu_read:
			readIt(mWord);
			return true;
		case R.id.menu_star:
			if (mDBA == null) {
				return false;
			}
			if (mDBA.getStar(mWord) <= 0) {
				mDBA.star(mWord);
				toast(String.format(mAddToBook, mWord));
				invalidateOptionsMenu();
			} else {
				mDBA.unStar(mWord);
				toast(String.format(mRmFromBook, mWord));
				invalidateOptionsMenu();
			}
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		requestWindowFeature(Window.FEATURE_PROGRESS);
		setContentView(R.layout.activity_drag_and_drop);
		setProgressBarVisibility(true);
		getActionBar().setDisplayShowTitleEnabled(false);

		mBtnCurrentWord = (Button) findViewById(R.id.btn_drop_word);
		mBtnOptionTopLeft = (Button) findViewById(R.id.btn_drop_meaning_top_left);
		mBtnOptionTopRight = (Button) findViewById(R.id.btn_drop_meaning_top_right);
		mBtnOptionBottomLeft = (Button) findViewById(R.id.btn_drop_meaning_bottom_left);
		mBtnOptionBottomRight = (Button) findViewById(R.id.btn_drop_meaning_bottom_right);
		mBtnArrowLeft = (ImageButton) findViewById(R.id.btn_drop_arrow_left);
		mBtnArrowRight = (ImageButton) findViewById(R.id.btn_drop_arrow_right);
		mPaper = (Paper) findViewById(R.id.draw_paper);

		if (mBtnCurrentWord == null || mBtnOptionTopLeft == null
				|| mBtnOptionTopRight == null || mBtnOptionBottomLeft == null
				|| mBtnOptionBottomRight == null || mBtnArrowLeft == null
				|| mBtnArrowRight == null || mPaper == null) {
			// TODO: handle error here.
			finish();
		}
		animation = AnimationUtils.loadAnimation(getApplicationContext(),
				R.anim.shake);

		mBtnArrowLeft.setOnClickListener(this);
		mBtnArrowRight.setOnClickListener(this);

		mBtnCurrentWord.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getActionMasked()) {
				case MotionEvent.ACTION_DOWN:
					if (Debuger.DEBUG) {
						Log.d(TAG, "onTouch() ACTION_DOWN");
					}
					ClipData dragData = ClipData.newPlainText("label", "text");
					DragShadowBuilder shadowBuilder = new DragShadowBuilder(v);
					v.clearAnimation();
					v.startDrag(dragData, shadowBuilder, v, 0);
					return true;
				case MotionEvent.ACTION_UP:
					if (Debuger.DEBUG) {
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

		mDBA = DBA.getInstance(getApplicationContext());

		mDbCount = mDBA.getCount();

		mOptList = new ArrayList<Button>();
		mOptList.add(mBtnOptionTopLeft);
		mOptList.add(mBtnOptionTopRight);
		mOptList.add(mBtnOptionBottomLeft);
		mOptList.add(mBtnOptionBottomRight);

		Resources res = getResources();

		map = Word.getMap();

		if (map == null || map.size() <= 0) {
			return;
		}

		mRate = (Window.PROGRESS_END - Window.PROGRESS_START) / map.size();

		mAddToBook = String.valueOf(res.getText(R.string.tip_add_to_word_book));
		mRmFromBook = String.valueOf(res
				.getText(R.string.tip_remove_from_word_book));

		mColorError = res.getColor(R.color.orange_dark);
		mColorBingon = res.getColor(R.color.green);

		getActionBar().setDisplayHomeAsUpEnabled(true);

		if (savedInstanceState == null) {
			if (mWrongWordList != null) {
				mWrongWordList.clear();
			}
		}

		Random random = new Random();
		int optNum = 4;

		mTestCases.clear();

		for (int i = 0; i < map.size(); i++) {
			TestCase testCase = new TestCase();

			Word w = map.get(i);
			testCase.wordIdx = w.getId();
			int id = -1;
			if (w != null) {
				id = w.getId();
			}
			if (Debuger.DEBUG) {
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

			if (Debuger.DEBUG) {
				Log.d(TAG,
						"onCreate() test case-" + i + ", "
								+ testCase.toString());
			}
		}
		buildTestCase();

		mTts = new TextToSpeech(getApplicationContext(), this);

		mAutoForwardHandler = new AutoForwardHandler();
	}

	@SuppressLint("HandlerLeak")
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

	@Override
	public void onInit(int status) {
		if (status == TextToSpeech.SUCCESS) {
			// Set preferred language to US english.
			// Note that a language may not be available, and the result will
			// indicate this.
			int result = -1;
			if (Config.CURRENT_LANGUAGE.equals(Config.LANG_FR)) {
				result = mTts.setLanguage(Locale.FRANCE);
			} else {
				result = mTts.setLanguage(Locale.ENGLISH);
			}
			// Try this someday for some interesting results.
			// int result mTts.setLanguage(Locale.FRANCE);
			if (result == TextToSpeech.LANG_MISSING_DATA
					|| result == TextToSpeech.LANG_NOT_SUPPORTED) {
				// Lanuage data is missing or the language is not supported.
				Log.e(TAG, "Language is not available.");
			} else {
				if (Debuger.DEBUG) {
					Log.d(TAG, "TTS works fine.");
				}
			}
		} else {
			// Initialization failed.
			Log.e(TAG, "Could not initialize TextToSpeech.");
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

	@Override
	protected void onPause() {
		super.onPause();
	}

	private void toast(String msg) {
		Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
	}

	int mWordCounter = 0;

	private boolean mBingo;

	private static ArrayList<Integer> arrList = new ArrayList<Integer>();

	private void buildTestCase() {

		if (mWordCounter >= map.size()) {
			Toast.makeText(getApplicationContext(), "Done.", Toast.LENGTH_SHORT)
					.show();
			finish();
			return;
		}

		mBingo = false;

		mPaper.clear();

		TestCase testCase = mTestCases.get(mWordCounter);

		mBtnCurrentWord.clearAnimation();
		mBtnCurrentWord.startAnimation(animation);
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

		Word curentWord = mDBA.getWordByIdx(mWordIdx);
		Word topLeftWord = mDBA.getWordByIdx(topLeftIdx);
		Word topRightWord = mDBA.getWordByIdx(topRightIdx);
		Word bottomLeftWord = mDBA.getWordByIdx(bottomLeftIdx);
		Word bottomRightWord = mDBA.getWordByIdx(bottomRightIdx);
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

		if (mWordCounter == map.size() - 1) {
			setProgress(Window.PROGRESS_END);
			mBtnArrowRight.setVisibility(View.INVISIBLE);
		} else {
			mBtnArrowRight.setVisibility(View.VISIBLE);
		}

		invalidateOptionsMenu();
	}

	@Override
	public boolean onDrag(View v, DragEvent event) {
		if (Debuger.DEBUG) {
			Log.d(TAG, "onDrag() event.getAction(): " + event.getAction());
		}
		switch (event.getAction()) {
		case DragEvent.ACTION_DRAG_STARTED:

			if (v.getId() == mBtnCurrentWord.getId()) {
				mBtnCurrentWord.clearAnimation();
				mBtnCurrentWord.setVisibility(View.INVISIBLE);
			} else {
				v.clearAnimation();
				v.startAnimation(animation);
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
				if (pageMap != null) {
					Word word = pageMap.get(v.getId());
					if (word != null) {
						String w = word.getWord();
						String m = word.getMeaning();
						if (Debuger.DEBUG) {
							Log.d(TAG, "onDrag() w: " + w + ", m: " + m);
						}

						if (mWord != null) {
							if (v instanceof Button) {
								Button btn = (Button) v;
								if (mWord.equals(w)) {
									btn.setTextColor(mColorBingon);
									if (mAutoForwardHandler != null) {
										mAutoForwardHandler
												.sendEmptyMessageDelayed(
														AutoForwardHandler.MSG_MOVE_ON,
														TIME_DELAY_TO_AUTO_FORWARD);
									}
									mBingo = true;
									mDBA.setPast(mWord);
								} else {
									btn.setTextColor(mColorError);
									if (mDBA != null) {
										mDBA.star(mWord);
									}
								}
								btn.setText(m + "\n" + w);
								btn.setEnabled(false);
							}
						}
					}
				}
				v.clearAnimation();
			}
			break;
		case DragEvent.ACTION_DRAG_ENDED:
			if (v.getId() == mBtnCurrentWord.getId()) {
				if (!mBingo) {
					mBtnCurrentWord.setVisibility(View.VISIBLE);
					mBtnCurrentWord.startAnimation(animation);
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_drop_arrow_left:
			backward();
			break;
		case R.id.btn_drop_arrow_right:
			forward();
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
