package com.gmail.dailyefforts.reciter.test;

import java.lang.ref.WeakReference;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import com.gmail.dailyefforts.reciter.R;
import com.gmail.dailyefforts.reciter.Config;
import com.gmail.dailyefforts.reciter.Word;
import com.gmail.dailyefforts.reciter.db.DBA;

public abstract class AbstractTestActivity extends Activity implements
		OnInitListener {
	private static final String TAG = AbstractTestActivity.class
			.getSimpleName();

	// 当前正在测试的外语单词	
	protected String mWord;

	// 此番测试中要测试的单词列表
	protected SparseArray<Word> mWordList;

	// 当前正在测试的单词的索引
	protected int mWordIdx = 0;

	// 数据库管理负责人
	protected DBA mDba;

	// Speech管理者
	protected TextToSpeech mTts;

	private String mStrRemoveFromCorrectionBook;
	private String mStrAddToCorrectionBook;

	protected AudioManager mAudioMngr;

	protected int mProgressStep;

	protected AutoForwardHandler mAutoForwardHandler;

	protected long mStartTime;

	protected String mTestReportStr;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setWindowFeatures();

		mDba = DBA.getInstance(getApplicationContext());
		mWordList = Word.getMap();

		if (mWordList == null || mWordList.size() <= 0) {
			Log.e(TAG, "onCreate() mWordArray: " + mWordList);
			return;
		}

		mProgressStep = (Window.PROGRESS_END - Window.PROGRESS_START)
				/ mWordList.size();

		mTts = new TextToSpeech(getApplicationContext(), this);
		mAudioMngr = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

		perpareTipStr();

		mAutoForwardHandler = new AutoForwardHandler(this);
		mStartTime = System.currentTimeMillis();
	}

	private static class AutoForwardHandler extends Handler {
		public static final int MSG_MOVE_ON = 0;
		private final WeakReference<AbstractTestActivity> mRef;

		public AutoForwardHandler(AbstractTestActivity activity) {
			mRef = new WeakReference<AbstractTestActivity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			AbstractTestActivity ac = mRef.get();
			if (ac != null) {
				switch (msg.what) {
				case MSG_MOVE_ON:
					ac.forward();
					removeMessages(MSG_MOVE_ON);
					break;
				}
			}
		}
	}

	private void setWindowFeatures() {
		requestWindowFeature(Window.FEATURE_PROGRESS);
		getActionBar().setDisplayShowTitleEnabled(false);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		setProgressBarVisibility(true);
	}

	private void perpareTipStr() {
		mStrAddToCorrectionBook = String.valueOf(getResources().getText(
				R.string.tip_add_to_word_book));
		mStrRemoveFromCorrectionBook = String.valueOf(getResources().getText(
				R.string.tip_remove_from_word_book));
		mTestReportStr = String.valueOf(getResources().getText(
				R.string.test_report_content));

	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if (menu != null) {
			menu.clear();
		}

		getMenuInflater().inflate(R.menu.action, menu);

		if (mDba == null || menu == null) {
			Log.e(TAG, "onPrepareOptionsMenu() mDba: " + mDba + ", menu: "
					+ menu);
			return false;
		}

		MenuItem starMenu = menu.findItem(R.id.menu_star);
		if (starMenu != null) {
			Log.i(TAG, "onPrepareOptionsMenu() mWord: " + mWord + ", star: "
					+ mDba.getStar(mWord));
			if (mDba.getStar(mWord) <= 0) {
				starMenu.setIcon(android.R.drawable.star_off);
				starMenu.setTitle(R.string.add_to_word_book);
			} else {
				starMenu.setIcon(android.R.drawable.star_on);
				starMenu.setTitle(R.string.remove_from_word_book);
			}
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
			read(mWord);
			return true;
		case R.id.menu_star:
			if (mDba == null) {
				return false;
			}
			if (mDba.getStar(mWord) <= 0) {
				mDba.star(mWord);
				toast(String.format(mStrAddToCorrectionBook, mWord));
				invalidateOptionsMenu();
			} else {
				mDba.unStar(mWord);
				toast(String.format(mStrRemoveFromCorrectionBook, mWord));
				invalidateOptionsMenu();
			}
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	protected void star(String word) {
		if (mDba != null && mDba.getStar(word) <= 0) {
			mDba.star(word);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (mTts != null) {
			mTts.shutdown();
		}
	}

	protected void read(String word) {
		if (mTts != null) {
			int result = mTts.speak(word, TextToSpeech.QUEUE_FLUSH, null);
			if (result != TextToSpeech.SUCCESS) {
				Log.e(TAG, "speak failed");
			}
		} else {
			Log.e(TAG, "read() mTts: " + mTts);
		}
	}

	private void toast(String msg) {
		Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onInit(int status) {
		if (status == TextToSpeech.SUCCESS) {
			int result = -1;

			switch (Config.CURRENT_LANGUAGE) {
			case French:
				result = mTts.setLanguage(Locale.FRANCE);
				break;
			case English:
			default:
				result = mTts.setLanguage(Locale.ENGLISH);
				break;
			}
			if (result == TextToSpeech.LANG_MISSING_DATA
					|| result == TextToSpeech.LANG_NOT_SUPPORTED) {
				Log.e(TAG, "Language is not available.");
			} else {
				if (Config.DEBUG) {
					Log.d(TAG, "TTS works fine.");
				}
			}
		} else {
			Log.e(TAG, "Could not initialize TextToSpeech.");
		}
	}

	protected void forward() {
		mWordIdx++;

		final int size = mWordList.size();

		if (mWordIdx > size) {
			mWordIdx = size;
		}

		buildTestCase();
	}

	protected void backward() {
		mWordIdx--;
		if (mWordIdx < 0) {
			mWordIdx = 0;
		}
		buildTestCase();
	}

	protected void buildTestCase() {
		setProgress((mWordIdx * mProgressStep));
		cancelAutoForward();
	}

	private void cancelAutoForward() {
		if (mAutoForwardHandler != null
				&& mAutoForwardHandler
						.hasMessages(AutoForwardHandler.MSG_MOVE_ON)) {
			mAutoForwardHandler.removeMessages(AutoForwardHandler.MSG_MOVE_ON);
		}
	}

	protected boolean hasNext() {
		if (Config.DEBUG) {
			Log.d(TAG, "hasNext() mWordIdx / mWordList.size() : " + mWordIdx
					+ " / " + mWordList.size());
		}
		return mWordIdx < mWordList.size() - 1;
	}

	protected void startAutoForward() {
		if (mAutoForwardHandler != null) {
			mAutoForwardHandler.sendEmptyMessageDelayed(
					AutoForwardHandler.MSG_MOVE_ON,
					Config.TIME_DELAY_TO_AUTO_FORWARD);
		}
	}
}
