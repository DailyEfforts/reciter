package com.gmail.dailyefforts.android.reviwer.test;

import java.util.ArrayList;
import java.util.Random;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.Window;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gmail.dailyefforts.android.reviwer.R;
import com.gmail.dailyefforts.android.reviwer.db.DBA;
import com.gmail.dailyefforts.android.reviwer.debug.Debuger;
import com.gmail.dailyefforts.android.reviwer.option.OptionButton;
import com.gmail.dailyefforts.android.reviwer.setting.Settings;
import com.gmail.dailyefforts.android.reviwer.word.Word;

public class TestPage extends Activity implements OnTouchListener {

	private static final String TAG = TestPage.class.getSimpleName();

	private TextView tv;

	private TextView tvBingoRate;

	private String word;
	private String meaning;

	private SparseArray<Word> map;

	private SparseArray<Word> pageMap;

	private Drawable bgColorNormal;
	private Drawable bgColorPressedBingo;
	private Drawable bgColorPressedWarning;

	private int bingoNum;

	private boolean isFirstTouch;

	private LinearLayout optCat;

	private ArrayList<OptionButton> mOptList;

	private SharedPreferences mSharedPref;

	int optNum;

	private DBA dba;

	private int mDbCount;

	private int mRate;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_PROGRESS);
		setContentView(R.layout.activity_test_page);
		setProgressBarVisibility(true);
		tv = (TextView) findViewById(R.id.tv_word);

		mSharedPref = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());

		optNum = Integer.valueOf(mSharedPref.getString(
				getString(R.string.pref_key_options_count),
				Settings.DEFAULT_OPTION_COUNT));

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

		bgColorNormal = getResources()
				.getDrawable(R.drawable.opt_btn_bg_normal);
		bgColorPressedBingo = getResources().getDrawable(
				R.drawable.opt_btn_bg_pressed_bingo);
		bgColorPressedWarning = getResources().getDrawable(
				R.drawable.opt_btn_bg_pressed_warning);

		tvBingoRate = (TextView) findViewById(R.id.tv_bingo_rate);

		map = Word.getMap();

		mRate = (Window.PROGRESS_END - Window.PROGRESS_START) / map.size();

		buildTestCase(optNum);

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	int mWordCounter = 0;

	private void buildTestCase(int optNum) {
		Random random = new Random();

		word = map.get(mWordCounter).getWord();
		meaning = map.get(mWordCounter).getMeaning();
		tv.setText(word);

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
				btn.setText(meaning);
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

		if (tvBingoRate != null) {
			if (mWordCounter <= 0) {
				tvBingoRate.setText("");
			} else {
				tvBingoRate.setText(String.format("%d / %d  %.0f%%", bingoNum,
						mWordCounter, bingoNum * 100.0f / mWordCounter));
			}
		}
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
				if (word != null && word.equals(w.getWord())) {
					bingGo = true;
				}
			}
			switch (event.getActionMasked()) {
			case MotionEvent.ACTION_DOWN:
				((Button) v).setText(w.getWord());
				if (bingGo) {
					if (isFirstTouch) {
						bingoNum++;
					}
					v.setBackgroundDrawable(bgColorPressedBingo);
				} else {
					v.setBackgroundDrawable(bgColorPressedWarning);
					if (dba != null) {
						dba.star(word);
					}
				}
				returnValue = true;
				break;
			case MotionEvent.ACTION_UP:
				((Button) v).playSoundEffect(SoundEffectConstants.CLICK);
				if (bingGo) {
					if (mWordCounter == map.size()) {
						Toast.makeText(getApplicationContext(), "Done.",
								Toast.LENGTH_SHORT).show();
						finish();
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
}
