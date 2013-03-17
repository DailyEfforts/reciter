package com.gmail.dailyefforts.android.reviwer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import android.app.Activity;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.gmail.dailyefforts.android.reviwer.Launcher.Word;

public class TestPage extends Activity implements OnClickListener {

	private TextView tv;

	private Button btnOpt0;
	private Button btnOpt1;
	private Button btnOpt2;

	private TextView tvTip;

	private String word;

	private String meaning;

	private SparseArray<Word> map;

	private HashMap<String, String> pageMap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test_page);

		tv = (TextView) findViewById(R.id.tv_word);

		btnOpt0 = (Button) findViewById(R.id.btn_option_0);
		btnOpt1 = (Button) findViewById(R.id.btn_option_1);
		btnOpt2 = (Button) findViewById(R.id.btn_option_2);

		if (btnOpt0 != null) {
			btnOpt0.setOnClickListener(this);
		}
		if (btnOpt1 != null) {
			btnOpt1.setOnClickListener(this);
		}
		if (btnOpt2 != null) {
			btnOpt2.setOnClickListener(this);
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

		pageMap = new HashMap<String, String>();
		pageMap.put(meaning, word);

		ArrayList<String> randArr = new ArrayList<String>();

		// two another different words' meaning
		Set<Integer> intSet = new HashSet<Integer>();
		while (intSet.size() < 2) {
			int tmp = random.nextInt(map.size());
			if (tmp != idx) {
				if (intSet.add(tmp)) {
					randArr.add(map.get(tmp).getMeaning());
					pageMap.put(map.get(tmp).getMeaning(), map.get(tmp)
							.getWord());
				}
			}
		}

		int rand = random.nextInt(100);
		switch (rand % 3) {
		case 0:
			btnOpt0.setText(meaning);
			btnOpt1.setText(randArr.get(0));
			btnOpt2.setText(randArr.get(1));
			break;
		case 1:
			btnOpt0.setText(randArr.get(0));
			btnOpt1.setText(meaning);
			btnOpt2.setText(randArr.get(1));
			break;
		case 2:
			btnOpt0.setText(randArr.get(0));
			btnOpt1.setText(randArr.get(1));
			btnOpt2.setText(meaning);
			break;
		default:
			break;
		}
		
		if (tvTip != null) {
			tvTip.setText("");
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_test_page, menu);

		return true;
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.btn_option_0:
		case R.id.btn_option_1:
		case R.id.btn_option_2:
			CharSequence text = ((Button) v).getText();
			if (meaning != null && meaning.equals(text)) {
				buildTestCase();
			} else {
				if (tvTip != null) {
					tvTip.setText(pageMap.get(text));
				}
			}
			break;

		}
	}
}
