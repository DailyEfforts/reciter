package com.gmail.dailyefforts.android.reviwer;

import java.util.ArrayList;
import java.util.Random;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gmail.dailyefforts.android.reviwer.Launcher.Word;

public class TestPage extends Activity implements OnTouchListener {

	private static final String TAG = TestPage.class.getSimpleName();

	private TextView tv;

	private OptBtn btnOpt0;
	private OptBtn btnOpt1;
	private OptBtn btnOpt2;

	private TextView tvBingoRate;

	private String word;
	private String meaning;

	private SparseArray<Word> map;

	private SparseArray<Word> pageMap;

	private Drawable bgColorNormal;
	private Drawable bgColorPressedBingo;
	private Drawable bgColorPressedWarning;

	private int totalNum;
	private int bingoNum;

	private boolean isFirstTouch;

	private LinearLayout optCat;

	private ArrayList<OptBtn> mOptList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test_page);

		tv = (TextView) findViewById(R.id.tv_word);

		optCat = (LinearLayout) findViewById(R.id.opt_category);

		/*
		 * btnOpt0 = (Button) findViewById(R.id.btn_option_0); btnOpt1 =
		 * (Button) findViewById(R.id.btn_option_1); btnOpt2 = (Button)
		 * findViewById(R.id.btn_option_2);
		 */

		mOptList = new ArrayList<OptBtn>();

		btnOpt0 = new OptBtn(this, 0);
		btnOpt1 = new OptBtn(this, 1);
		btnOpt2 = new OptBtn(this, 2);

		mOptList.add(btnOpt0);
		mOptList.add(btnOpt1);
		mOptList.add(btnOpt2);

		for (OptBtn tmp : mOptList) {
			optCat.addView(tmp);
		}
		
		bgColorNormal = getResources()
				.getDrawable(R.drawable.opt_btn_bg_normal);
		bgColorPressedBingo = getResources().getDrawable(
				R.drawable.opt_btn_bg_pressed_bingo);
		bgColorPressedWarning = getResources().getDrawable(
				R.drawable.opt_btn_bg_pressed_warning);

		if (btnOpt0 != null) {
			btnOpt0.setOnTouchListener(this);
		}
		if (btnOpt1 != null) {
			btnOpt1.setOnTouchListener(this);
		}
		if (btnOpt2 != null) {
			btnOpt2.setOnTouchListener(this);
		}

		tvBingoRate = (TextView) findViewById(R.id.tv_bingo_rate);

		map = Launcher.getMap();

		buildTestCase();

	}

	private void buildTestCase() {
		Random random = new Random();

		int idx = random.nextInt(map.size());

		word = map.get(idx).getWord();
		meaning = map.get(idx).getMeaning();
		tv.setText(word);

		pageMap = new SparseArray<Word>();

		// two another different words' meaning
		ArrayList<Integer> arrList = new ArrayList<Integer>();
		while (arrList.size() < 2) {
			int tmp = random.nextInt(map.size());
			if (tmp != idx && !arrList.contains(tmp)) {
				arrList.add(tmp);
			}
		}

		int rand = random.nextInt(100);
		switch (rand % 3) {
		case 0:
			btnOpt0.setText(meaning);
			pageMap.put(btnOpt0.getId(), map.get(idx));

			btnOpt1.setText(getMeaningByIdx(arrList.get(0)));
			pageMap.put(btnOpt1.getId(), map.get(arrList.get(0)));

			btnOpt2.setText(getMeaningByIdx(arrList.get(1)));
			pageMap.put(btnOpt2.getId(), map.get(arrList.get(1)));
			break;
		case 1:
			btnOpt0.setText(getMeaningByIdx(arrList.get(0)));
			pageMap.put(btnOpt0.getId(), map.get(arrList.get(0)));

			btnOpt1.setText(meaning);
			pageMap.put(btnOpt1.getId(), map.get(idx));

			btnOpt2.setText(getMeaningByIdx(arrList.get(1)));
			pageMap.put(btnOpt2.getId(), map.get(arrList.get(1)));
			break;
		case 2:
			btnOpt0.setText(getMeaningByIdx(arrList.get(0)));
			pageMap.put(btnOpt0.getId(), map.get(arrList.get(0)));

			btnOpt1.setText(getMeaningByIdx(arrList.get(1)));
			pageMap.put(btnOpt1.getId(), map.get(arrList.get(1)));

			btnOpt2.setText(meaning);
			pageMap.put(btnOpt2.getId(), map.get(idx));
			break;
		default:
			break;
		}
		isFirstTouch = true;

		if (tvBingoRate != null) {
			if (totalNum <= 0) {
				tvBingoRate.setText("");
			} else {
				tvBingoRate.setText(String.format("%d / %d  %.0f%%", bingoNum,
						totalNum, bingoNum * 100.0f / totalNum));
			}
		}

		totalNum++;
	}

	private String getMeaningByIdx(int idx) {
		if (idx >= 0 && map != null && idx < map.size()) {
			return map.get(idx).getMeaning();
		}
		return null;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		boolean returnValue = false;
		boolean bingGo = false;
		if (v != null && event != null) {
			Word w = pageMap.get(v.getId());
			if (Debuger.DEBUG) {
				Log.d(TAG,
						"onTouch() id: " + v.getId() + " word: " + w.toString());
			}
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
				}
				returnValue = true;
				break;
			case MotionEvent.ACTION_UP:
				if (bingGo) {
					buildTestCase();
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
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
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
		return false;
	}
}
