package com.gmail.dailyefforts.android.reviwer.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.Random;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gmail.dailyefforts.android.reviwer.Config;
import com.gmail.dailyefforts.android.reviwer.R;
import com.gmail.dailyefforts.android.reviwer.db.DBA;
import com.gmail.dailyefforts.android.reviwer.debug.Debuger;
import com.gmail.dailyefforts.android.reviwer.option.OptionButton;
import com.gmail.dailyefforts.android.reviwer.word.Word;

public class TestPage extends Activity implements OnTouchListener,
		OnInitListener {

	private static final String TAG = TestPage.class.getSimpleName();

	private TextView tv;

	private String mWord;
	private String mMeaning;

	private SparseArray<Word> map;

	private SparseArray<Word> pageMap;

	private Drawable bgColorNormal;
	private Drawable bgColorPressedBingo;
	private Drawable bgColorPressedWarning;

	private int mBingoNum;

	private boolean isFirstTouch;

	private LinearLayout optCat;

	private ArrayList<OptionButton> mOptList;

	private SharedPreferences mSharedPref;

	int optNum;

	private DBA dba;

	private int mDbCount;

	private int mRate;

	private String mAddToBook;

	private String mRmFromBook;

	private TextToSpeech mTts;

	private String mTestReport;

	private boolean isSpeaking;

	private static ArrayList<String> mWrongWordList = new ArrayList<String>();

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if (menu != null) {
			menu.clear();
		}

		getMenuInflater().inflate(R.menu.action, menu);

		if (dba == null || menu == null) {
			return false;
		}

		MenuItem star = menu.findItem(R.id.menu_star);
		if (star != null) {
			if (dba.getStar(mWord) <= 0) {
				star.setIcon(android.R.drawable.star_off);
				star.setTitle(R.string.add_to_word_book);
			} else {
				star.setIcon(android.R.drawable.star_on);
				star.setTitle(R.string.remove_from_word_book);
			}
		}

		MenuItem read = menu.findItem(R.id.menu_read);

		if (read != null) {
			if (isSpeaking) {
				read.setIcon(R.drawable.read);
			} else {
				read.setIcon(R.drawable.mute);
			}
		}

		if (Debuger.DEBUG) {
			Log.d(TAG, "onPrepareOptionsMenu()");
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		case R.id.menu_read:
			isSpeaking = !isSpeaking;
			invalidateOptionsMenu();
			return true;
		case R.id.menu_star:
			if (dba == null) {
				return false;
			}
			if (dba.getStar(mWord) <= 0) {
				dba.star(mWord);
				toast(String.format(mAddToBook, mWord));
				invalidateOptionsMenu();
			} else {
				dba.unStar(mWord);
				toast(String.format(mRmFromBook, mWord));
				invalidateOptionsMenu();
			}
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onStart() {
		super.onStart();
		isSpeaking = false;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		requestWindowFeature(Window.FEATURE_PROGRESS);
		setContentView(R.layout.activity_test_page);
		setProgressBarVisibility(true);
		tv = (TextView) findViewById(R.id.tv_word);
		getActionBar().setDisplayShowTitleEnabled(false);
		mSharedPref = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());

		optNum = Integer.valueOf(mSharedPref.getString(
				getString(R.string.pref_key_options_count),
				Config.DEFAULT_OPTION_COUNT));

		dba = DBA.getInstance(getApplicationContext());

		mDbCount = dba.getCount();
		optCat = (LinearLayout) findViewById(R.id.opt_category);
		optCat.setWeightSum(optNum);

		mOptList = new ArrayList<OptionButton>();

		for (int i = 0; i < optNum; i++) {
			OptionButton btn = new OptionButton(this, i);
			mOptList.add(btn);
		}

		for (OptionButton tmp : mOptList) {
			optCat.addView(tmp);
			tmp.setOnTouchListener(this);
		}

		Resources res = getResources();
		bgColorNormal = res.getDrawable(R.drawable.opt_btn_bg_normal);
		bgColorPressedBingo = res
				.getDrawable(R.drawable.opt_btn_bg_pressed_bingo);
		bgColorPressedWarning = res
				.getDrawable(R.drawable.opt_btn_bg_pressed_warning);

		map = Word.getMap();

		if (map == null || map.size() <= 0) {
			return;
		}

		mRate = (Window.PROGRESS_END - Window.PROGRESS_START) / map.size();

		mTestReport = String.valueOf(res.getText(R.string.test_report_content));

		mAddToBook = String.valueOf(res.getText(R.string.tip_add_to_word_book));
		mRmFromBook = String.valueOf(res
				.getText(R.string.tip_remove_from_word_book));

		buildTestCase(optNum);

		mTts = new TextToSpeech(this, this);

		getActionBar().setDisplayHomeAsUpEnabled(true);

		mStartTime = 0L;

		if (mWrongWordList != null) {
			mWrongWordList.clear();
		}

	}

	@Override
	protected void onPause() {
		super.onPause();
		if (!isFirstTouch && dba != null && dba.getStar(mWord) <= 0) {
			dba.star(mWord);
		}
	}

	private void readIt(final String word) {
		if (mTts != null && isSpeaking) {
			mTts.speak(word, TextToSpeech.QUEUE_FLUSH, null);
		}
	}

	private void toast(String msg) {
		Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
	}

	int mWordCounter = 0;

	private long mStartTime;

	private void buildTestCase(int optNum) {
		Random random = new Random();

		mWord = map.get(mWordCounter).getWord();
		mMeaning = map.get(mWordCounter).getMeaning();
		tv.setText(mWord);
		readIt(mWord);
		invalidateOptionsMenu();

		pageMap = new SparseArray<Word>();

		// make sure the option is not duplicate.
		ArrayList<Integer> arrList = new ArrayList<Integer>();
		while (arrList.size() < optNum - 1) {
			int tmp = random.nextInt(mDbCount);
			if (tmp != mWordCounter && !arrList.contains(tmp)) {
				arrList.add(tmp);
			}
		}

		int answerIdx = random.nextInt(optNum);

		for (int i = 0; i < mOptList.size(); i++) {
			OptionButton btn = mOptList.get(i);
			if (i == answerIdx) {
				btn.setText(mMeaning);
				pageMap.put(btn.getId(), map.get(mWordCounter));
			} else {

				int tmp = 0;

				if (arrList != null && arrList.size() > 0) {
					tmp = arrList.get(0);
					arrList.remove(0);
				} else {
					tmp = random.nextInt(map.size());
				}

				if (dba != null) {
					Word word = dba.getWordByIdx(tmp);
					btn.setText(word.getMeaning());
					pageMap.put(btn.getId(), word);
				}
			}
		}

		isFirstTouch = true;

		setProgress((mWordCounter * mRate));
		mWordCounter++;
	}

	/*
	 * private String getMeaningByIdx(int idx) { if (idx >= 0 && map != null &&
	 * idx < map.size()) { return map.get(idx).getMeaning(); } return null; }
	 */

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		boolean returnValue = false;
		boolean bingGo = false;
		if (v != null && (v instanceof Button) && event != null) {
			if (Debuger.DEBUG) {
				Log.d(TAG, "onTouch() id: " + v.getId());
				for (int i = 0; i < pageMap.size(); i++) {
					Log.d(TAG, String.format("onTouch() %d: %s", i, pageMap
							.get(i).toString()));
				}
			}
			Word w = pageMap.get(v.getId());
			if (w != null) {
				if (mWord != null && mWord.equals(w.getWord())) {
					bingGo = true;
				}
			}
			switch (event.getActionMasked()) {
			case MotionEvent.ACTION_DOWN:
				((Button) v).setText(w.getWord());
				if (bingGo) {
					if (isFirstTouch) {
						mBingoNum++;
					}
					v.setBackgroundDrawable(bgColorPressedBingo);
				} else {
					if (isFirstTouch && dba != null
							&& dba.getStar(mWord) <= 0) {
						dba.star(mWord);
						if (Debuger.DEBUG) {
							Log.d(TAG, "onTouch() down: mWord: " + mWord + ", ");
						}
					}
					if (mWrongWordList != null && !mWrongWordList.contains(mWord)) {
						mWrongWordList.add(mWord);
						if (Debuger.DEBUG) {
							Log.d(TAG, "onTouch() mWord: " + mWord + ", set: " + mWrongWordList.toString());
						}
					}
					v.setBackgroundDrawable(bgColorPressedWarning);
				}
				returnValue = true;
				break;
			case MotionEvent.ACTION_UP:
				((Button) v).playSoundEffect(SoundEffectConstants.CLICK);
				if (mStartTime == 0) {
					mStartTime = System.currentTimeMillis();
				}
				if (bingGo) {
					// Tested number: %1$d
					// Correct number: %2$d
					// Elapsed time: %3$d
					// Accuracy rating: %4$d
					// Database Size: %5$d
					// You may have mastered:%6$d

					if (mWordCounter == map.size()) {
						setProgress(Window.PROGRESS_END);

						long elapsedTime = Math.round((System
								.currentTimeMillis() - mStartTime) / 1000.0);
						int accuracy = (int) (mBingoNum * 100.0f / mWordCounter);

						if (dba != null) {
							ContentValues values = new ContentValues();
							values.put(DBA.TEST_TESTED_NUMBER, mWordCounter);
							values.put(DBA.TEST_CORRECT_NUMBER, mBingoNum);
							values.put(DBA.TEST_ELAPSED_TIME, elapsedTime);
							values.put(DBA.TEST_ACCURACY, accuracy);
							values.put(DBA.TEST_DB_SIZE, dba.size());
							values.put(DBA.TEST_TIMESTAMP,
									System.currentTimeMillis());
							if (mWrongWordList != null) {
								Collections.sort(mWrongWordList);
								values.put(DBA.TEST_WRONG_WORD_LIST,
										mWrongWordList.toString());
							}
							dba.insert(DBA.TABLE_TEST_REPORT, null, values);
						}

						String message = String
								.format(mTestReport,
										mWordCounter,
										mBingoNum,
										elapsedTime,
										accuracy,
										dba.size(),
										(int) (dba.size() * (mBingoNum * 1.0f / mWordCounter)));
						showDialog(getString(R.string.test_report), message);

					} else {
						buildTestCase(optNum);
					}
				} else {
					isFirstTouch = false;
					((Button) v).setText(w.getMeaning());
				}

				v.setBackgroundDrawable(bgColorNormal);
				returnValue = true;
				break;
			default:
				break;
			}
		}
		return returnValue;
	}

	void showDialog(String title, String message) {
		DialogFragment newFragment = TestReportFragment.newInstance(title,
				message);
		newFragment.show(getFragmentManager(), "dialog");
	}

	public static class TestReportFragment extends DialogFragment {

		public static TestReportFragment newInstance(String title,
				String message) {
			TestReportFragment frag = new TestReportFragment();
			Bundle args = new Bundle();
			args.putString("title", title);
			args.putString("message", message);
			frag.setArguments(args);
			return frag;
		}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			String title = getArguments().getString("title");
			String message = getArguments().getString("message");

			Builder builder = new AlertDialog.Builder(getActivity());
			builder.setIcon(android.R.drawable.ic_dialog_alert);
			builder.setTitle(title);
			builder.setMessage(message);
			builder.setPositiveButton(android.R.string.ok,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							getActivity().finish();
						}
					});
			return builder.create();
		}
	}

	private long lastPressedTime;
	private static final int PERIOD = 2000;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (false && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			switch (event.getAction()) {
			case KeyEvent.ACTION_DOWN:
				if (event.getDownTime() - lastPressedTime < PERIOD) {
					finish();
				} else {
					Toast.makeText(getApplicationContext(),
							"Press again to exit.", Toast.LENGTH_SHORT).show();
					lastPressedTime = event.getEventTime();
				}
				break;
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onInit(int status) {
		if (status == TextToSpeech.SUCCESS) {
			// Set preferred language to US english.
			// Note that a language may not be available, and the result will
			// indicate this.
			int result = mTts.setLanguage(Locale.FRANCE);
			// Try this someday for some interesting results.
			// int result mTts.setLanguage(Locale.FRANCE);
			if (result == TextToSpeech.LANG_MISSING_DATA
					|| result == TextToSpeech.LANG_NOT_SUPPORTED) {
				// Lanuage data is missing or the language is not supported.
				Log.e(TAG, "Language is not available.");
			} else {

			}
		} else {
			// Initialization failed.
			Log.e(TAG, "Could not initialize TextToSpeech.");
		}
	}

}
