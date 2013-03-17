package com.gmail.dailyefforts.android.reviwer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.TextView;

import com.gmail.dailyefforts.android.reviwer.Launcher.Word;

public class TestPage extends Activity implements OnTouchListener {

	private TextView tv;

	private Button btnOpt0;
	private Button btnOpt1;
	private Button btnOpt2;

	private TextView tvTip;

	private String word;

	private String meaning;

	private SparseArray<Word> map;

	private HashMap<Integer, Word> pageMap;
	
	private int bgColorNormal;
	private int bgColorPressedBingo;
	private int bgColorPressedWarning;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test_page);

		tv = (TextView) findViewById(R.id.tv_word);

		btnOpt0 = (Button) findViewById(R.id.btn_option_0);
		btnOpt1 = (Button) findViewById(R.id.btn_option_1);
		btnOpt2 = (Button) findViewById(R.id.btn_option_2);
		
		bgColorNormal = getResources().getColor(R.color.opt_btn_bg_normal);
		bgColorPressedBingo = getResources().getColor(R.color.opt_btn_bg_pressed_bingo);
		bgColorPressedWarning = getResources().getColor(R.color.opt_btn_bg_pressed_warning);

		if (btnOpt0 != null) {
			btnOpt0.setOnTouchListener(this);
		}
		if (btnOpt1 != null) {
			btnOpt1.setOnTouchListener(this);
		}
		if (btnOpt2 != null) {
			btnOpt2.setOnTouchListener(this);
		}

		tvTip = (TextView) findViewById(R.id.tv_tip);

		map = Launcher.getMap();

		buildTestCase();

	}

	private void buildTestCase() {
		Random random = new Random();

		int idx = random.nextInt(map.size());

		word = map.get(idx).getWord();
		meaning = map.get(idx).getMeaning();
		tv.setText(word);

		pageMap = new HashMap<Integer, Word>();

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

		if (tvTip != null) {
			tvTip.setText("");
		}
		
	}

	private String getMeaningByIdx(int idx) {
		if (idx >= 0 && map != null && idx < map.size()) {
			return map.get(idx).getMeaning();
		}
		return null;

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_test_page, menu);

		return true;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		boolean returnValue = false;
		boolean bingGo = false;
		if (v != null && event != null) {
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
					v.setBackgroundColor(bgColorPressedBingo);
				} else {
					v.setBackgroundColor(bgColorPressedWarning);
				}
				returnValue = true;
				break;
			case MotionEvent.ACTION_UP:
				if (bingGo) {
					buildTestCase();
				} else {
					((Button) v).setText(w.getMeaning());
				}
				v.setBackgroundColor(bgColorNormal);
				returnValue = true;
				break;
			default:
				break;
			}
		}
		return returnValue;
	}
}
