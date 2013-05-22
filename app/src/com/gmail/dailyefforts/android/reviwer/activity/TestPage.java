package com.gmail.dailyefforts.android.reviwer.activity;

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
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.util.SparseArray;
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
			readIt(mWord);
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

		mTts = new TextToSpeech(getApplicationContext(), this);

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
		invalidateOptionsMenu();

		for (Button btn : mOptList) {
			btn.setEnabled(true);
		}

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
				if (v instanceof Button) {
					((Button) v).setText(w.getWord());
				}
				if (bingGo) {
					if (isFirstTouch) {
						mBingoNum++;
					}
				} else {
					if (isFirstTouch && dba != null && dba.getStar(mWord) <= 0) {
						dba.star(mWord);
						if (Debuger.DEBUG) {
							Log.d(TAG, "onTouch() down: mWord: " + mWord + ", ");
						}
					}
					if (mWrongWordList != null
							&& !mWrongWordList.contains(mWord)) {
						mWrongWordList.add(mWord);
						if (Debuger.DEBUG) {
							Log.d(TAG, "onTouch() mWord: " + mWord + ", set: "
									+ mWrongWordList.toString());
						}
					}
					if (v instanceof Button) {
						((Button) v).setEnabled(false);
					}
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
							dba.insert(DBA.CURRENT_TEST_REPORT_TABLE, null,
									values);
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

}
