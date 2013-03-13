package com.gmail.dailyefforts.android.reviwer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;

public class TestPage extends Activity {

	private TextView tv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test_page);

		tv = (TextView) findViewById(R.id.tv_word);

		Map<Integer, Launcher.Word> map = Launcher.getMap();

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

		tv.setText(String.format("word: %s\nmeaning:\n1. %s\n2. %s\n3. %s",
				word, meaning, randArr.get(0), randArr.get(1)));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_test_page, menu);
		return true;
	}

}
