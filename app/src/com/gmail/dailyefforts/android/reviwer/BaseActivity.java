package com.gmail.dailyefforts.android.reviwer;

import java.util.Locale;

import android.app.Activity;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import com.gmail.dailyefforts.android.reviwer.db.DBA;

public class BaseActivity extends Activity implements OnInitListener {
	public static final String TAG = BaseActivity.class.getSimpleName();
	protected String mWord;
	protected DBA mDba;
	protected TextToSpeech mTts;
	public String mRmFromBook;
	public String mAddToBook;
	protected int mWordCounter = 0;
	protected SparseArray<Word> mWordArray;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_PROGRESS);
		getActionBar().setDisplayShowTitleEnabled(false);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		setProgressBarVisibility(true);
		mDba = DBA.getInstance(getApplicationContext());
		mWordArray = Word.getMap();
		if (mWordArray == null || mWordArray.size() <= 0) {
			return;
		}

		mTts = new TextToSpeech(getApplicationContext(), this);

		mAddToBook = String.valueOf(getResources().getText(
				R.string.tip_add_to_word_book));
		mRmFromBook = String.valueOf(getResources().getText(
				R.string.tip_remove_from_word_book));
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if (menu != null) {
			menu.clear();
		}

		getMenuInflater().inflate(R.menu.action, menu);

		if (mDba == null || menu == null) {
			return false;
		}

		MenuItem star = menu.findItem(R.id.menu_star);
		if (star != null) {
			Log.i(TAG, "onPrepareOptionsMenu() mWord: " + mWord + ", star: "
					+ mDba.getStar(mWord));
			if (mDba.getStar(mWord) <= 0) {
				star.setIcon(android.R.drawable.star_off);
				star.setTitle(R.string.add_to_word_book);
			} else {
				star.setIcon(android.R.drawable.star_on);
				star.setTitle(R.string.remove_from_word_book);
			}
		}

		if (Config.DEBUG) {
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

	protected void readIt(final String word) {
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
			if (mDba == null) {
				return false;
			}
			if (mDba.getStar(mWord) <= 0) {
				mDba.star(mWord);
				toast(String.format(mAddToBook, mWord));
				invalidateOptionsMenu();
			} else {
				mDba.unStar(mWord);
				toast(String.format(mRmFromBook, mWord));
				invalidateOptionsMenu();
			}
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void toast(String msg) {
		Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onInit(int status) {
		if (status == TextToSpeech.SUCCESS) {
			// Set preferred language to US english.
			// Note that a language may not be available, and the result will
			// indicate this.
			int result = -1;

			switch (Config.CURRENT_LANGUAGE) {
			case French:
				result = mTts.setLanguage(Locale.FRANCE);
				break;
			case English:
				result = mTts.setLanguage(Locale.ENGLISH);
				break;
			default:
				// TODO: unknown
				break;
			}
			// Try this someday for some interesting results.
			// int result mTts.setLanguage(Locale.FRANCE);
			if (result == TextToSpeech.LANG_MISSING_DATA
					|| result == TextToSpeech.LANG_NOT_SUPPORTED) {
				// language data is missing or the language is not supported.
				Log.e(TAG, "Language is not available.");
			} else {
				if (Config.DEBUG) {
					Log.d(TAG, "TTS works fine.");
				}
			}
		} else {
			// Initialization failed.
			Log.e(TAG, "Could not initialize TextToSpeech.");
		}
	}
}
