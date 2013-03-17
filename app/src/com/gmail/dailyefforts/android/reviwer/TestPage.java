package com.gmail.dailyefforts.android.reviwer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import android.app.Activity;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.Menu;
import android.widget.Button;
import android.widget.TextView;

import com.gmail.dailyefforts.android.reviwer.Launcher.Word;

public class TestPage extends Activity {

	private TextView tv;

	private Button btnOpt0;
	private Button btnOpt1;
	private Button btnOpt2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test_page);

		tv = (TextView) findViewById(R.id.tv_word);

		btnOpt0 = (Button) findViewById(R.id.btn_option_0);
		btnOpt1 = (Button) findViewById(R.id.btn_option_1);
		btnOpt2 = (Button) findViewById(R.id.btn_option_2);

		SparseArray<Word> map = Launcher.getMap();

		Random random = new Random();

		int idx = random.nextInt(map.size());

		String word = map.get(idx).getWord();
		String meaning = map.get(idx).getMeaning();

		ArrayList<String> randArr = new ArrayList<String>();

		Set<Integer> intSet = new HashSet<Integer>();

		while (intSet.size() < 2) {
			int tmp = random.nextInt(map.size());
			if (tmp != idx) {
				if (intSet.add(tmp)) {
					randArr.add(map.get(tmp).getMeaning());
				}
			}
		}

		tv.setText(word);
		btnOpt0.setText(meaning);
		btnOpt1.setText(randArr.get(0));
		btnOpt2.setText(randArr.get(1));
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_test_page, menu);
		return true;
	}

}
